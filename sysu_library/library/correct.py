# -*- coding: utf-8 -*-
import re
import urllib
from django.utils.http import urlquote

def correct(search_str):
    url_string = "http://www.baidu.com/s?wd=" + urlquote(search_str)
    res = urllib.urlopen(url_string).read()
    check = re.search('以下为您显示“</strong><strong>(.*?)</strong><strong class="c-gray">”的搜索结果',res)
    if not check:
        return search_str
    else: 
        res = check.group(1)
        return res.decode("utf-8")

#Example:print correct("系统分xi")
