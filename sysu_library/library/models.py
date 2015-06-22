from django.db import models

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
    num = models.CharField(max_length = 100)
    pic = models.CharField(max_length = 100)
    url = models.CharField(max_length = 1024)
    def __unicode__(self):
        return "Book name : " + bname + "\nauthor : " + author + "\npublisher : " + publisher

class Relation(models.Model):
    # one course has several correlated books
    course = models.ForeignKey(Courses)
    # not every book has relation with course, eg : search book directly by book name
    bid = models.IntegerField(default = 0)
    click = models.IntegerField(default = 0)
