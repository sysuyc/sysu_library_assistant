# -*- coding:utf-8 -*-

"""
该模块定义了图书馆爬虫solaSpider.
接口提供:
    sola = solSpider
    books = sola.getBookList(bookName, accurate): 返回指定数目的搜索结果列表, accurate指定是否精确匹配
    item = sola.getDetail(url): 返回指定url的书籍详细信息,link可由上一个接口获得
额外库依赖:
    Levenshtein 用于判断字符串相似度

"""

__author__ ='patrick'


import urllib
import urllib2
import re
import json
import thread
from django.utils.http import urlquote
#import Levenshtein


class solaSpider(object):
    def __init__(self):
        pass

    def getBookList(self, bookName, accurate):
        """
        Get the list of books named bookName
        If accurate is true, get the exactly matching book
        """

        self.start_url = "http://202.116.64.108:8991/F/-?func=find-b&find_code=WRD&request="
        # solve the encode question
        # but sometime this function will throw exception
        # for example, search "计算机网络技术"
        self.start_url += urlquote(bookName) + "&local_base=ZSU01"
        self.container = []

        response = urllib2.urlopen(self.start_url)
        body = response.read()
        result = re.findall('<table class=items.*?>(.*?)</table>', body, re.S)
        for r in result:
            item = {}
            info = re.findall('<div class=itemtitle><a href=(.*?)>(.*?)</a>.*?</div>', r, re.S)
            item['bname'] = info[0][1]
            item['link'] = info[0][0]
            info = re.findall('<td class=content valign=top>(.*?)<', r, re.S)
            item['author'] = info[0].strip(' \n\r')
            item['num'] = info[1].strip(' \r\n')
            item['publisher'] = info[2].strip(' \r\n')
            item['year'] = info[3].strip(' \r\n')
            info = re.findall('<b>(.*?)</b>', r, re.S)
            data = info[0].strip(' ').replace('，', ':').split(':')
            item['total'] = int(data[1])
            item['lent'] = int(data[3])
            self.container.append(item)

        if accurate == True:
            self.exactMatch(bookName)

        f = open('books.json', 'a')
        for each_item in self.container:
            tar = json.dumps(each_item, ensure_ascii=False)
            f.write(tar+'\n')
        f.close()
        return self.container



    def getDetail(self, cururl):
        """
        Get the detail of a specified book (link)
        """
        response = urllib2.urlopen(cururl)
        body = response.read()
        result = re.findall('<div class=tabcontent id=details2>(.*?)</div>', body, re.S)
        result = re.findall('<tr>(.*?)</tr>', result[0], re.S)
        item = {}
        pre = None
        for r in result:
            info = re.findall('<td.*?>(.*?)</td>', r, re.S)
            info[0] = self.deal_string(info[0])
            info[1] = self.deal_string(info[1])

            temp = self.deal_exp(info[1], '<img src=.*?>(.*)')
            right = self.deal_exp(temp, '<A HREF=.*?>(.*?)</A>')

            if info[0] == "" and pre != None:
                if right == "":
                    pre = None
                else:
                    item[pre] = item[pre] + '，' + right
            else:
                if item.has_key(info[0]):
                    item[info[0]] += ', ' + right
                else:
                    item[info[0]] = right
                pre = info[0]

        f = open('details.json', 'a')
        tar = json.dumps(item, ensure_ascii=False)
        f.write(tar+'\n')
        f.close()
        return item

    def exactMatch(self, bookName):
        tmpContainer = []
        for item in self.container:
            #p = Levenshtein.ratio(item['bname'].upper(), bookName.upper())
            p = 0.95
            if p >= 0.95:
                tmpContainer.append(item)
        self.container = tmpContainer



    def deal_exp(self, s, exp):
        R1 = re.findall(exp, s, re.S)
        if len(R1) > 0:
            return R1[0]
        return s


    def deal_string(self, s):

        s = s.replace('&nbsp;', ' ')
        s = s.replace('&lt;', '<')
        s = s.replace('&gt;', '>')
        s = s.replace('&amp;', '&')
        s = s.replace('&quot;', '"')
        s = s.replace('&apos;', "'")
        s = s.replace('<br>', "\n")
        s = s.replace('<b>', "")
        s = s.replace('</b>', "")
        s = s.strip(' \r\n');
        return s


if __name__ == '__main__':

    bookName = 'Unix环境高级编程'
    print('-----------------Start crawl----------------------')
    sola = solaSpider()
    books = sola.getBookList(bookName, True)
    for book in books:
        print book
    print '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
    book = sola.getDetail(books[0]['link'])
    print book
    print('-----------------End crawl------------------------')



