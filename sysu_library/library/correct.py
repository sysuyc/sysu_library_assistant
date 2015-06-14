# -*- coding: utf-8 -*-
import re
import urllib

def correct(search_str):
	url_string = "http://www.baidu.com/s?wd="+search_str
	res = urllib.urlopen(url_string).read()
	check = re.search('以下为您显示“</strong><strong>(.*?)</strong><strong class="c-gray">”的搜索结果',res)
	if (check == None):
		return search_str
	else: 
		return check.group(1)


print correct("系统分xi")
