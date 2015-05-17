from django.db import models

# Create your models here.

class Courses(models.Model):
	cname = models.CharField(max_length = 100)
	description = models.CharField(max_length = 2000)
	def __unicode__(self):
		return "Course name : " + cname

class Books(models.Model):
	bname = models.CharField(max_length = 100)
	isbn = models.CharField(max_length = 20)
	author = models.CharField(max_length = 100)
	publisher = models.CharField(max_length = 100)
	pic = models.CharField(max_length = 100)
	url = models.CharField(max_length = 60)
	def __unicode__(self):
		return "Book name : " + bname + "\nauthor : " + author + "\npublisher : " + publisher

class Relation(models.Model):
	bid = models.IntegerField(default =-0)
	cid = models.IntegerField(default = 0)
	click = models.IntegerField(default = 0)