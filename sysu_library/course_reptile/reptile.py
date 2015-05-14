#!/usr/bin/env python
# -*- encoding: utf-8 -*-

"""
该文档定义了课程爬虫，爬虫通过课程名称爬取相关的书籍信息。
采用的方式是，百度搜索“课程名 相关书籍”，确保右侧出现推荐信息，然后爬下来。

How-to-use:
    创建一个CourseReptile的实例 cr = CourseReptile()
    然后使用 cr.course_search(course_name)，该返回一个书籍名称列表(空或非空)

额外库依赖：
    BeautifulSoup:
    http://www.crummy.com/software/BeautifulSoup/bs4/doc/index.zh.html
额外库安装：
    如果使用Debian系系统，可以使用包管理软件下载：
    $ apt-get install Python-bs4
    BeautifulSoup通过Pypi发布，所以也可以用easy_install或pip安装：
    $ easy_install beautifulsoup4
    或
    $ pip install beautifulsoup4
"""

__author__ = 'sysuycc'

import re
import sys
import urllib2
import bs4
from bs4 import BeautifulSoup

Tag = bs4.element.Tag

class CourseReptile(object):
    """
    定义百度课程爬虫
    """
    def __init__(self):
        self.opener = urllib2.build_opener()
        self.ResultList = list()

    def _get_content(self, url):
        """
        简单地打开url，返回页面内容
        """
        response = self.opener.open(url)
        return response.read()

    def course_search(self, course_name):
        """
        通过课程名称搜索相关书籍。
        如果搜索成功，则返回一个包含书籍名称的列表
        如果搜索失败，则返回一个空列表
        """
        self.ResultList = list()
        if not course_name:
            return []
        url = ''.join(['http://www.baidu.com/s?wd=', course_name, '+相关书籍'])
        content = self._get_content(url)
        soup = BeautifulSoup(content.decode('utf-8'))
        title = soup.find('span', title='相关书籍')
        if not title:
            return []
        books = title.parent.next_sibling
        while not isinstance(books, Tag):
            books = books.next_sibling
        for div in books:
            if not isinstance(div, Tag):
                continue
            if 'c-row' in div['class']:
                self._get_books_on_panel(div)
                continue
            elif 'textarea' == div.name:
                div = div.find('div')
            self._get_books_hidden(div)
        return self.ResultList
    
    def _get_books_on_panel(self, node):
        """
        私有函数
        返回一行之内的书籍列表
        """
        for div in node.children:
            # print type(div)
            if not isinstance(div, Tag):
                continue
            alist = div.find_all('a')
            for a in alist:
                book_title = a.get('title')
                if book_title:
                    self.ResultList.append(book_title)
                    # print book_title
                    break

    def _get_books_hidden(self, node):
        """
        私有函数
        返回隐藏块内的书籍列表
        """
        for div in node:
            if not isinstance(div, Tag):
                continue
            self._get_books_on_panel(div)

if __name__ == '__main__':
    cr = CourseReptile()
    if len(sys.argv) > 1:
        course_name = sys.argv[1]
    else:
        course_name = '系统分析与设计'
    cr.course_search(course_name)
