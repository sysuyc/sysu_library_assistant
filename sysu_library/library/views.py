from django.shortcuts import render
from django.http import HttpResponse
from django.shortcuts import render_to_response
from library.models import *
from course_reptile.reptile import CourseReptile
# from library.bookCrawler import getBooksListByName

'''
search books by couse name
return the result as xml
service for android application
'''
def searchByCourse(requset):
    try:
        course = requset.GET["course"]
    except:
        return HttpResponse("Request error") 
    try:
        # check whether this couse is in database
        c = Course.objects.get(cname = course)
        # return the historic result
        xml =  getExistCourseRecord(c)
        return xml
    except Course.DoesNotExist:
        # this course has not been searched before
        # search it, and store the result in database
        cr = CourseReptile()
        booksNames = cr.course_search(course)
        # if not correlated book for this course
        if not len(booksNames):
            return HttpResponse("No correlated book")
        c = Coures.objects.create(cname = course, description = "")
        c.save()
        xml = ""
        for bookName in booksNames:
            # to be implement. this operation should return a list of dictionary
            books = getBooksListByName(bookName = bookName, num = 3)
            # some database operation
            for book in books:
                # book is a dictionary
                bookid = storeBookItem(book)
                # construct the return xml
                xml += getBookItemXml(bookid)
                # create the relation for this new course and the the relative book
                r = Relation.objects.create(course = c, bid = bookid, click = 0)
                r.save()
        return xml

# service for function searchByCourse
def getExistCourseRecord(course_object):
    relations = c.relation_set.all()
    bookids = [r.bid for r in relations]
    xml = '''<?xml version="1.0" encoding="UTF-8"?>\n'''
    xml += '''<bookList>\n'''
    for bookid in bookids:
        xml += getBookItemXml(bookid)
    xml += '''</bookList>'''
    return xml

# create a xml for a book
def getBookItemXml(bookid):
    try:
        b = Books.objects.get(id = bookid)
    except Books.DoesNotExist:
        return ""
    item_xml = '<item>\n' +\
               '<name>%s</name>\n' +\
               '<pic>%s</pic>\n' +\
               '<author>%s</author>\n' +\
               '<publisher>%s</publisher>\n' +\
               '<num>%s</num>\n' +\
               '<detail>%s</detail>\n' +\
               '</item>\n'
    item_xml = item_xml % (b.name, b.pic, b.author, b.publisher, b.num, b.detail)
    return item_xml

# store a book item into database
# item is a dictionary, has keys : name, pic, author, publisher, isbn, detail
def storeBookItem(item):
    try:
        b = Books.objects.get(bname = item.name, author = item.author, num = item.num)
        return b.id
    except:
    b = Books.objects.create(bname = item.name, publisher = item.publisher,
                            author = item.author, pic = item.pic,
                            isbn = item.isbn, url = item.url)
    b.save()
    return b.id

def getExistBookRecord():
    pass

def searchByBook(requset):
    pass

def getBookDetail(requset):
    pass

def clickIncrement(requset):
    pass