# -*- coding:utf-8 -*-

"""
该模块定义了图书馆爬虫solaSpider.
接口提供:
    sola = solSpider
    books = sola.getBookList(bookName, accurate): 返回指定数目的搜索结果列表, accurate指定是否精确匹配
    item = sola.getDetail(url): 返回指定url的书籍详细信息,link可由上一个接口获得
    item = sola.getDetailByISBN(isbn): 返回指定isbn的书籍的详细信息
    result = sola.getAllCollections(href): 返回所有馆藏信息，是一个以字典为元素的列表
    sola.getFromAPI(url): 使用图书馆提供api返回书籍的作者简介，由getDetail调用，未完成

"""

import urllib
import urllib2
import re
import json
import thread
import Levenshtein
from datetime import date
from django.utils.http import urlquote


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
        #self.start_url += bookName + "&local_base=ZSU01"
        self.container = []

        try:
            response = urllib2.urlopen(self.start_url)
        except:
            return []
        body = response.read()
        result = re.findall('<table class=items.*?>(.*?)<hr class=itemsep size=1>', body, re.S)
        for r in result:
            try:
                item = {}
                info = re.findall('<img.*?src="(.*?)">', r, re.S)  # get url of the cover image
                item['img'] = self.deal_string(info[0])
                pat = re.compile("isbn=(.*?)/cover")
                item['isbn'] = pat.search(r).group(1)
                info = re.findall('<div class=itemtitle><a href=(.*?)>(.*?)</a>.*?</div>', r, re.S)
                item['bname'] = self.deal_string(info[0][1])
                pat = re.compile("<div class=itemtitle><a href=(.*?)>")
                # item['link'] = self.deal_string(info[0][0])
                item['link'] = pat.search(r).group(1)
                info = re.findall('<td class=content valign=top>(.*?)<', r, re.S)
                item['author'] = self.deal_string(info[0])
                item['num'] = self.deal_string(info[1])
                item['publisher'] = self.deal_string(info[2])
                item['year'] = self.deal_string(info[3])
                info = re.findall('<b>(.*?)</b>', r, re.S)
                data = info[0].strip(' ').replace('，', ':').split(':')
                item['total'] = int(data[1])
                item['lent'] = int(data[3])
                self.container.append(item)
            except:
                continue

        if accurate == True:
            self.exact_match(bookName.encode("utf-8"))

        """
        f = open('books.json', 'a')
        for each_item in self.container:
            tar = json.dumps(each_item, ensure_ascii=False)
            f.write(tar+'\n')
        f.close()
        """
        return self.container



    def getDetail(self, cururl):
        """
        Get the detail of a specified book (link)
        """
        try:
            response = urllib2.urlopen(cururl)
        except:
            return {}
        body = response.read()
        result = re.findall('<div class=tabcontent id=details2>(.*?)</div>', body, re.S)
        if len(result) == 0:
            return []
        result = re.findall('<tr>(.*?)</tr>', result[0], re.S)
        item = {}
        pre = None
        for r in result:
            info = re.findall('<td.*?>(.*?)</td>', r, re.S)
            info[0] = self.deal_string(info[0])
            info[1] = self.deal_string(info[1])

            temp = self.deal_exp(info[1], '<img src=.*?>(.*)')
            # 单独处理全部馆藏
            if info[0] == '全部馆藏':
                href = (re.findall('<A HREF=(.*?)>.*?</A>', temp, re.S))[0]
                item[info[0]] = self.getAllCollections(href)
                continue

            right = self.deal_exp(temp, '<A HREF=.*?>(.*?)</A>')
            if info[0] == "" and pre != None:
                if right == "":
                    pre = None
                else:
                    item[pre] = self.deal_string(item[pre] + '，' + right)
            else:
                if item.has_key(info[0]):
                    item[info[0]] += ', ' + right
                else:
                    item[info[0]] = self.deal_string(right)
                pre = info[0]

        if item.has_key('馆藏地:索书'):
            del item['馆藏地:索书']

        """
        f = open('details.json', 'a')
        tar = json.dumps(item, ensure_ascii=False)
        f.write(tar+'\n')
        f.close()
        """
        return item

    def getDetailByISBN(self, isbn):
        link = 'http://202.116.64.108:8991/F/93TMKDTYCMCYUXHM2T8\
                HPD3MSVS9K1GUI3RFE8AJU8H69HY35S-05843?func=find-\
                b&find_code=ISB&request='
        link += isbn + '&local_base=ZSU01'
        return self.getDetail(link)

    def getDetailBySYS(self, SYS):
        link = 'http://202.116.64.108:8991/F/APVLETD873K7NDX2S1F\
                TNSXY2RN4FS3TM37C7D4MT8NJTU4BB7-11189?func=find-b\
                &find_code=SYS&request='
        link += SYS + '+&local_base=ZSU01'
        return self.getDetail(link)

    def getFromAPI(self, isbn):
        """
        get infos about author from api
        not finished yet!
        """
        link = 'http://202.112.150.126/indexc.php?client=sysu&isbn=9787111407010&callback=libtool'
        response = urllib2.urlopen(link)
        body = response.read()
        print len(body), ' ', body


    def getAllCollections(self, href):
        """
        get all collections of a book in the library according to the specified href,
        return a list of dictionary : [{}, {}, {}...]
        """
        try:
            response = urllib2.urlopen(href)
        except:
            return []
        body = response.read()
        table = re.findall('<table border=0 cellspacing=2 width=99%>(.*?)</table>', body, re.S)
        if len(table) == 0:
            return []
        table[0] = self.undo_the_comment(table[0])
        firstRow = re.findall('<tr class=tr1>(.*?)</tr>', table[0], re.S)
        title = re.findall('<th class="text3">(.*?)</th>', firstRow[0], re.S)

        rows  = re.findall('<tr>(.*?)</tr>', table[0], re.S)
        result = []
        for row in rows:
            cols = re.findall('<td class=td1.*?>(.*?)</td>', row, re.S)
            dic = {}
            for idx, col in enumerate(cols):
                if idx == 0:
                    book_info = re.findall('<a href=.*doc_number=(.*?)&item_sequence=(.*?)&.*>', col.lower(), re.S) 
                    # print col.lower()
                    # print book_info
                    available = re.findall('(预约)', col.lower(), re.S)
                    if available:
                        dic['available'] = "Y"
                    else:
                        dic['available'] = "N"
                    dic['doc_number'] = book_info[0][0]
                    dic['item_sequence'] = book_info[0][1]
                    continue
                dic[title[idx]] = col
            result.append(dic)
        return result

    def login(self, bid, password):
        """
        Login by the id and password.
        If successful login, return the characteristic code on the url.
        If login failed, return None.
        """
        if not bid or not password:
            return None
        login_data = {
            'func' : 'login-session',
            'login_source' : 'bor_info',
            'bor_id' : bid,
            'bor_verification' : password,
            'bor_library' : 'ZSU50'
        }
        unique_code = self.get_unique_code()
        login_url = 'http://202.116.64.108:8991/F/'+unique_code
        response = urllib2.urlopen(login_url, urllib.urlencode(login_data))
        html = response.read()
        match_result = re.findall('(退出)', html, re.S)
        if not match_result:
            return None
        else:
            return unique_code

    def book_appointment(self, unique_code, doc_number, item_sequence, pickup, end_time):
        """
        预约书籍。
        """
        if not unique_code or not doc_number or not item_sequence or not location or not end_time:
            return None
        appointment_url = 'http://202.116.64.108:8991/F/%s-?func=item-hold-request&doc_library=ZSU50&adm_doc_number=%s&item_sequence=%s&year=&volume=&sub_library=&type=&no_loaned=N&start_rec_key=&end_rec_key=' % (unique_code, doc_number, item_sequence)
        today = date.today().strftime('%Y%m%d')
        post_data = {
            'func' : 'item-hold-request-b',
            'doc_library' : 'ZSU50',
            'adm_doc_number' : doc_number,
            'item_sequence' : item_sequence,
            'bib_request' : 'N',
            'PICKUP' : pickup,
            'from' : today,
            'to' : end_time,
            'rush_request' : 'N'
        }
        response = urllib2.urlopen(appointment_url, urllib.urlencode(post_data))
        html = response.read()
        res = re.findall('(预约请求已发送)', html, re.S)
        if not res:
            print 'Failed!'
            return None
        else:
            print 'Success'
            return 'Success'

    def get_unique_code(self):
        login_page_url = 'http://202.116.64.108:8991/F/?func=file&file_name=login-session'
        response = urllib2.urlopen(login_page_url)
        html = response.read()
        res = re.findall(r'<form .*action=.*/F/(.*?)-.*>', html, re.S)
        if not res:
            return None
        unique_code = res[0]
        return unique_code

    def exact_match(self, bookName):
        """
        If the book name searced is similar to the target, add to container
        """
        tmpContainer = []
        for item in self.container:
            """
            if item['bname'].upper() == bookName.upper():
                tmpContainer.append(item)
            """
            b1 = self.deal_string(item['bname']).replace(' ', '')
            b2 = bookName
            p = Levenshtein.ratio(b1, b2)
            if p > 0.90:
                tmpContainer.append(item)
        self.container = tmpContainer


    def undo_the_comment(self, s):
        idx1 = 0
        idx2 = s.find('<!--')
        newStr = ""
        while idx2 != -1:
            newStr += s[idx1:idx2]
            idx1 = idx2 + s[idx2:].find('-->') + 3
            if idx1 == len(s):
                break
            idx2 = idx1 + s[idx1:].find('<!--')
        newStr += s[idx1:]
        return newStr



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

def beautiful_print(items):
    for item in items:
        for k in item:
            print k, ':', item[k]
        print 

if __name__ == '__main__':

    sola = solaSpider()
    #href = 'http://202.116.64.108:8991/F/KGLKUAIDFFUTUAF2TE9C1YAF8DIVLPYHTAK1UMXB43JAR2DEJ1-06944?func=item-global&doc_library=ZSU01&doc_number=000705779&year=&volume=&sub_library='
    href = 'http://202.116.64.108:8991/F/?func=item-global&doc_library=ZSU01&doc_number=000818425&year=&volume=&sub_library='
    item = sola.getAllCollections(href)
    beautiful_print(item)
    # unique_code = sola.login('12330049', 'yingcong')
    # if unique_code:
    #     sola.book_appointment(unique_code, '000818425', '000060', 'DXLT', '20150723')
    """
    print('-----------------Start crawl----------------------')
    bookName = '系统分析'
    sola = solaSpider()
    books = sola.getBookList(bookName, True)
    for book in books:
        print book
    print '<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
    book = sola.getDetail(books[0]['link'])
    print book
    print('-----------------End crawl------------------------')
    """


