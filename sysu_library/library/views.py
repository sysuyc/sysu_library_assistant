# -*- coding:utf-8 -*-

from django.shortcuts import render
from django.http import HttpResponse
from django.shortcuts import render_to_response
from library.models import *
from reptile import CourseReptile
from solaSpider import solaSpider
import time
import re
# from library.bookCrawler import getBooksListByName

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
    try:
        # check whether this couse is in database
        c = Courses.objects.get(cname = course)
        # return the historic result
        xml =  getExistCourseRecord(c)
        return HttpResponse(xml, mimetype="application/xml")
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
        xml = ""
        for bookName in booksNames:
            # to be implement. this operation should return a list of dictionary
            sola = solaSpider()
            t1 = time.time()
            books = sola.getBookList(bookName, True)
            print "search books by book cost : " + repr(time.time() - t1) + "s"
            # some database operation
            for book in books:
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
        return HttpResponse(xml, mimetype="application/xml")

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
               '</item>\n'
    item_xml = item_xml % (b.bname, b.pic + "what", b.author, b.publisher, b.isbn + "what")
    return item_xml

# store a book item into database
# item is a dictionary, has keys : bname, pic, author, publisher, isbn, detail
def storeBookItem(item):
    try:
        b = Books.objects.get(bname = item["bname"], author = item["author"], num = item["num"])
        return b.id
    except:
        b = Books.objects.create(bname = item["bname"], publisher = item["publisher"],
                            author = item["author"], pic = "", num = item["num"],
                            isbn = "", url = item["link"])
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
    # the function getBooksListByName is waiting
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
    return HttpResponse(xml, mimetype="application/xml")


'''
-----------------------------------------------------------------
return the detail message for a book by xml
the augument is isbn, which is the primary key for a book records
-----------------------------------------------------------------
'''
def getBookDetail(requset):
    isbn = requset.GET.get("isbn", "")
    if isbn == "":
        return HttpResponse("Request error")
    try:
        b = Books.objects.get(isbn = isbn)
        url = b.url
    except Books.DoesNotExist:
        return HttpResponse("ISBN error")
    # url = 'http://202.116.64.108:8991/F/X2U3STMSYHI5UJI52C9JHPI2LLSUHD8KPXDNLRMEER4QS4LCTQ-44681?func=full-set-set&set_number=097749&set_entry=000006&format=999'
    # url = 'http://202.116.64.108:8991/F/IL2GHX2DB6D3N31QJK2B8XXI15QXAJYJIEGQN42F84HSUGJQJG-25196?func=full-set-set&set_number=101560&set_entry=000005&format=999'
    # detail is a dictionary
    sola = solaSpider()
    detail = sola.getDetail(url)
    xml = ""
    # compare witb key in an unknown code...
    filt_word = ["形态", "全部馆藏"]
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
    xml = packXml(xml, 0, "book")
    return HttpResponse(xml, mimetype="application/xml")

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