from django.shortcuts import render
from django.http import HttpResponse
from django.shortcuts import render_to_response
from library.models import *
# from library.courseCrawler import getBooksByCourse
# from library.bookCrawler import getBooksListByName

def searchByCourse(requset):
	if requset.method == 'GET':
		course = requset.GET["course"]
		try:
			# check whether this couse is in database
			c = Course.objects.get(cname = course)
			# return the historic result
			return getExistCourseRecord(c)
		except Course.DoesNotExist:
			booksNames = getBooksByCourse(course)
			if not len(booksNames):
				return HttpResponse("No correlated book")

def getExistCourseRecord(course_object):
	relations = c.relation_set.all()
	bookids = [r.bid for r in relations]

def getExistBookRecord():

def searchByBook(requset):
	pass

def getBookDetail(requset):
	pass

def clickIncrement(requset):
	pass