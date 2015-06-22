#*- coding:utf-8 -*-
from django.shortcuts import render
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.conf import settings
from django.core.cache import cache
from library.models import *
from reptile import CourseReptile
from solaSpider import solaSpider
import correct
import Levenshtein
import time
import re

'''
------------------------------------
search books by couse name
return the result as xml
service for android application
------------------------------------
'''
def searchByCourse(requset):
    course = requset.GET.get("course", "")
    if course == "":
        return HttpResponse("Request error")
    print type(course)
    course = correct.correct(course)
    print type(course)
    xml = cache.get("C_" + course)
    # check whether has some data in redis
    if xml:
        return HttpResponse(xml, content_type="application/xml")
    try:
        # check whether this couse is in database
        c = Courses.objects.get(cname = course)
        # return the historic result
        xml =  getExistCourseRecord(c)
        cache.set("C_" + course, xml, 60 * 60 * 24)
        return HttpResponse(xml, content_type="application/xml")
    except Courses.DoesNotExist:
        # this course has not been searched before
        # search it, and store the result in database
        cr = CourseReptile()
        t1 = time.time()
        booksNames = cr.course_search(course)
        print "search books by course cost : " + repr(time.time() - t1) + "s"
        # if not correlated book for this course
        if not len(booksNames):
            return HttpResponse("No relative book for this course!")
        c = Courses.objects.create(cname = course, description = "")
        c.save()
        # count the similar of bookname and course
        similarNames = []
        for bookName in booksNames:
            p = Levenshtein.ratio(bookName, course)
            similarNames.append((bookName, p))
        # sort the book names by the similar
        booksNames = sorted(similarNames, key = lambda x : x[1], reverse = True)
        print booksNames
        xml = ""
        for bookName, p in booksNames:
            # to be implement. this operation should return a list of dictionary
            sola = solaSpider()
            t1 = time.time()
            books = sola.getBookList(bookName, True)
            print "search books by book cost : " + repr(time.time() - t1) + "s"
            # some database operation
            for book in books:
                print book
                # book is a dictionary
                bookid = storeBookItem(book)
                # construct the return xml
                xml += getBookItemXml(bookid)
                # create the relation for this new course and the the relative book
                r = Relation.objects.create(course = c, bid = bookid, click = 0)
                r.save()
        if xml == "":
            return HttpResponse("No relative book for this course!")
        xml = packXml(xml, c.id, "course")
        # write in cache
        cache.set("C_" + course, xml, 60 * 60 * 24)
        return HttpResponse(xml, content_type="application/xml")

# service for function searchByCourse
def getExistCourseRecord(course_object):
    relations = course_object.relation_set.all()
    bookids = [r.bid for r in relations]
    xml = ""
    for bookid in bookids:
        xml += getBookItemXml(bookid)
    return packXml(xml, course_object.id, "course")

'''
--------------------------------------------------------------------------
the following three function search for both course search and book search
--------------------------------------------------------------------------
'''

# add the parent node to the xml
def packXml(xml, sid, search_type):
    head = '''<?xml version="1.0" encoding="UTF-8"?>\n''' +\
           '''<bookList>\n'''
    tail = '''</bookList>'''
    resXml = head + xml + tail
    return resXml

# create an xml for a book
def getBookItemXml(bookid):
    try:
        b = Books.objects.get(id = bookid)
    except Books.DoesNotExist:
        return ""
    item_xml = '<item>\n' +\
                   '<name><![CDATA[%s]]></name>\n' +\
                   '<pic><![CDATA[%s]]></pic>\n' +\
                   '<author><![CDATA[%s]]></author>\n' +\
                   '<publisher><![CDATA[%s]]></publisher>\n' +\
                   '<isbn><![CDATA[%s]]></isbn>\n' +\
                   '<url><![CDATA[%s]]></url>\n' +\
               '</item>\n'
    bname = b.bname.replace("&nbsp;:&nbsp;", " ")
    print b.url + "    in get"
    item_xml = item_xml % (bname, b.pic, b.author, b.publisher, b.isbn, b.url)
    return item_xml

# store a book item into database
# item is a dictionary, has keys : bname, pic, author, publisher, isbn, detail
def storeBookItem(item):
    try:
        b = Books.objects.get(bname = item["bname"], author = item["author"], num = item["num"])
        return b.id
    except:
        b = Books.objects.create(bname = item["bname"], publisher = item["publisher"],
                            author = item["author"], pic = item["img"], num = item["num"],
                            isbn = item["isbn"], url = item["link"])
    b.save()
    return b.id


'''
----------------------------------------------------------
search books directly by book name
return the result as xml
service for android application
will not store the relation in database!
----------------------------------------------------------
'''
def searchByBook(requset):
    bookName = requset.GET.get("book", "")
    if bookName == "":
        return HttpResponse("Request error") 
    # read cache
    xml = cache.get("B_" + bookName)
    if xml:
        return HttpResponse(xml, content_type="application/xml")
    sola = solaSpider()
    t1 = time.time()
    books = sola.getBookList(bookName, False)
    print "search books by book cost : " + repr(time.time() - t1) + "s"
    xml = ""
    for book in books:
        # book is a dictionary
        bookid = storeBookItem(book)
        # construct the return xml
        xml += getBookItemXml(bookid)
    if xml == "":
        return HttpResponse("No relative records for this book!")
    xml = packXml(xml, 0, "book")
    cache.set("B_" + bookName, xml, 60 * 60 * 24)
    return HttpResponse(xml, content_type="application/xml")


'''
-----------------------------------------------------------------
return the detail message for a book by xml
the augument is isbn, which is the primary key for a book records
-----------------------------------------------------------------
'''
def getBookDetail(request):
    isbn = request.GET.get("isbn", "")
    bname = request.GET.get("bname", "")
    author = request.GET.get("author", "")
    if isbn == "":
        return HttpResponse("Request error")
    try:
        b = Books.objects.get(isbn = isbn, bname = bname, author = author)
        url = b.url
    except Books.DoesNotExist:
        return HttpResponse("ISBN error")
    # url = 'http://202.116.64.108:8991/F/X2U3STMSYHI5UJI52C9JHPI2LLSUHD8KPXDNLRMEER4QS4LCTQ-44681?func=full-set-set&set_number=097749&set_entry=000006&format=999'
    # url = 'http://202.116.64.108:8991/F/IL2GHX2DB6D3N31QJK2B8XXI15QXAJYJIEGQN42F84HSUGJQJG-25196?func=full-set-set&set_number=101560&set_entry=000005&format=999'
    # detail is a dictionary
    sola = solaSpider()
    detail = sola.getDetail(url)
    print detail
    if len(detail) == 0:
        books = sola.getBookList(bname, False)
        for book in books:
            if book["bname"] == bname and book["author"] == author:
                url = book["link"]
                break
        print "new url"
        print url
        b.url = url
        b.save()
        detail = sola.getDetail(url)
    xml = ""
    # compare witb key in an unknown code...
    filt_word = ["形态", "全部馆藏"]
    collect = "全部馆藏"
    htmlfilter = re.compile('<.*?>')
    signfilter = re.compile('[ :-]')
    for key in detail:
        flag = 0
        for f in filt_word:
            # but it does work well
            if f in key:
                flag = 1
                break
        if flag:
            continue
        # remove the html part like <span> </span>
        contain = htmlfilter.sub('', detail[key])
        # remove : and space and -
        parsekey = signfilter.sub('', key)
        xml += '<%s><![CDATA[%s]]></%s>\n' % (parsekey, contain, parsekey)
    for key in detail:
        if collect in key:
            msg = detail[key]
            collect_xml = "<馆藏状态>\n"
            for m in msg:
                item = "<子项>\n" +\
                        "<应还日期><![CDATA[%s]]></应还日期>\n" +\
                        "<馆藏地><![CDATA[%s]]></馆藏地>\n" +\
                        "<架位><![CDATA[%s]]></架位>\n" +\
                        "<doc_number><![CDATA[%s]]></doc_number>\n" +\
                        "<item_sequence><![CDATA[%s]]></item_sequence>\n" +\
                        "</子项>\n"
                item = item % (m["应还日期"], m["馆藏地"], m["架位"], m["doc_number"], m["item_sequence"])
                collect_xml += item
            collect_xml = collect_xml + "</馆藏状态>\n"
    xml += collect_xml
    xml = packXml(xml, 0, "book")
    return HttpResponse(xml, content_type="application/xml")

def clickIncrement(requset):
    # cid means course id
    cid = requset.GET.get("cid", "")
    isbn = requset.GET.get("isbn", "")
    try:
        couse = CourseNames.objects.get(id = sid)
        book = Books.objects.get(isbn = isbn)
        r = Relation.objects.get(course = course, bid = book.id)
        r.click += 1
        r.save()
        return HttpResponse("Succeed")
    except:
        return HttpResponse("Relation error")
    return HttpResponse("Request error")

def login(request):
    username = request.GET.get('username')
    password = request.GET.get('password')
    if not username or not password:
        return HttpResponse('Failed: username and password are needed')
    sola = solaSpider()
    unique_code = sola.login(username, password)
    if not unique_code:
        return HttpResponse("Failed: check your account")
    return HttpResponse("Success: " + unique_code)
    

def bookAppointment(request):
    username = request.GET.get('username')
    password = request.GET.get('password')
    doc_number = request.GET.get('doc_number')
    item_sequence = request.GET.get('item_sequence')
    pickup = request.GET.get('pickup')
    end_time = request.GET.get('end_time')
    sola = solaSpider()
    unique_code = sola.login(username, password)
    if not unique_code:
        return HttpResponse('Failed: check your account')
    success = sola. book_appointment(unique_code, doc_number, item_sequence, pickup, end_time)
    if not success:
        return HttpResponse('Failed: counld not make an appointment')
    return HttpResponse('Success: make the appointment successful')

