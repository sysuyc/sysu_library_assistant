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
    if requset.method == 'GET':
        course = requset.GET["course"]
        try:
            # check whether this couse is in database
            c = Course.objects.get(cname = course)
            # return the historic result
            return getExistCourseRecord(c)
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
            for bookName in booksNames:
                # to be implement. this operation should return a list of dictionary
                books = getBooksListByName(bookName)
                # some database operation

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

def getExistBookRecord():
    pass

def searchByBook(requset):
    pass

def getBookDetail(requset):
    pass

def clickIncrement(requset):
    pass