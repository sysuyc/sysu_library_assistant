package com.example.library_helper.controller;

import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import com.example.library_helper.search_activity;
import com.example.library_helper.model.Book;


import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class InternetSearchController {
	//private final String search_by_course_url = "http://172.18.187.83:8000/lib/course/?course=";
	//private final String search_by_bookname_url = "http://172.18.187.83:8000/lib/book/?book=";
	private final String search_by_course_url = "http://172.18.187.107:8005/lib/course/?course=";
	private final String search_by_bookname_url = "http://172.18.187.107:8005/lib/book/?book=";
	private List<Book> result_list;
	
	public InternetSearchController() {
		// TODO Auto-generated constructor stub
		result_list = new ArrayList<Book>();
	}
	
	private void clearResult() {
		result_list.clear();
	}
	
	public Map<String, List<Book>> SearchByCourse(String course) {
		//clear last search result
		return getCourseResultByInternet(course);
	}
	
	public Map<String, List<Book>> SearchByBookName(String bookname) {
		//clear last search result
		return getBookNameResultByInternet(bookname);
		
	}
	
	private Map<String, List<Book>> getCourseResultByInternet(String course) {
		
		String statusCode = "-1";
		List<Book> books = null;
		Map<String, List<Book>> result = new HashMap<String, List<Book>>();
		
		// TODO Auto-generated method stub
		try {
			//执行网络连接以及解析代码
			//get方法
			//设置url和对应的参数对
			String curUrl = search_by_course_url + URLEncoder.encode(course, "utf-8");
			//String curUrl = search_by_course_url + course.replaceAll(" ", "%20");
			Log.i("url", curUrl);
			HttpGet httpget = new HttpGet(curUrl);
			HttpClient defaultHttpClient = new DefaultHttpClient();
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//使用HttpResponse获取反馈
			HttpResponse response = defaultHttpClient.execute(httpget);
			//记录取得的反馈值
			//getEntity（）获取返回的流，EntityUtils.toString把得到的Entity流转换为字符串
			String str = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			//Log.i("return ", str);
			statusCode = "1";
			books = ParserSearchResult(str);
			
			
		}
		catch (SocketTimeoutException e) {
			//传输数据超时处理
			statusCode = "2";
			Log.i("Request Error:", e.toString());
		}
		catch (ConnectTimeoutException e) {
			//连接超时处理
			statusCode = "3";
			Log.i("Request Error:", e.toString());
		}
		catch (Exception e) {
			//异常处理
			statusCode = "4";
			Log.i("Request Error:", e.toString());
		}
		
		result.put(statusCode, books);
		return result;
		
	}
	
	private Map<String, List<Book> > getBookNameResultByInternet(String bookname) {
		
		String statusCode = "-1";
		List<Book> books = null;
		Map<String, List<Book>> result = new HashMap<String, List<Book>>();
		
		// TODO Auto-generated method stub
		try {
			//执行网络连接以及解析代码
			//get方法
			//设置url和对应的参数对
			String curUrl = search_by_bookname_url + URLEncoder.encode(bookname, "utf-8");
			//String curUrl = search_by_bookname_url + bookname.replaceAll(" ", "%20");
			Log.i("url", curUrl);
			HttpGet httpget = new HttpGet(curUrl);
			HttpClient defaultHttpClient = new DefaultHttpClient();
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//使用HttpResponse获取反馈
			HttpResponse response = defaultHttpClient.execute(httpget);
			//记录取得的反馈值
			//getEntity（）获取返回的流，EntityUtils.toString把得到的Entity流转换为字符串
			String str = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			//Log.i("return ", str);
			statusCode = "1";
			books = ParserSearchResult(str);
			
			
		}  
		catch (SocketTimeoutException e) {
			//传输数据超时处理
			statusCode = "2";
			Log.i("Request Error:", e.toString());
		}
		catch (ConnectTimeoutException e) {
			//连接超时处理
			statusCode = "3";
			Log.i("Request Error:", e.toString());
		}
		catch (Exception e) {
			//异常处理
			statusCode = "4";
			Log.i("Request Error:", e.toString());
		}
		
		result.put(statusCode, books);
		return result;
		
	}
	
	private List<Book> ParserSearchResult(String input) {
		
		List<Book> books_list = new ArrayList<Book>();
		
		Book bookitem = null;
		
		try {
			//使用XmlPullParser来解析取得的xml格式的字符串
			XmlPullParser parser = Xml.newPullParser();
			//传入解析内容，规定传入Reader类型参数
			parser.setInput(new StringReader(input));
			
			
			//设置文件标记
			int event = parser.getEventType();
			
			//如果标记不为文件尾，则开始记录
			while(event != XmlPullParser.END_DOCUMENT) {
				switch(event) {
				    case XmlPullParser.START_TAG:
				    	//get tag name
				    	String tagName = parser.getName();
				    	
				    	if("item".equals(tagName)) {
				    		
				    		if(bookitem == null) {
				    			bookitem = new Book();
				    		}
				    		
				    	} else if("name".equals(tagName)) {
				    		
				    		String str = parser.nextText();
				    		int pos1 = 0, pos2 = str.length(), temp = 0;
				    		
				    		temp = str.indexOf("<![CDATA[");
				    		if(temp != -1) {
				    			pos1 = temp + "<![CDATA[".length();
				    		}
				    		
				    		temp = str.lastIndexOf("]]");
				    		if(temp != -1) {
				    			pos2 = temp;
				    		}
				    		
				    		String _name = str.substring(pos1, pos2);
				    		if(bookitem != null) {
					    		//Log.i("name:", _name);
				    			bookitem.setName(_name);
				    		}
				    		
				    	} else if("pic".equals(tagName)) {

				    		String str = parser.nextText();
				    		int pos1 = 0, pos2 = str.length(), temp = 0;
				    		
				    		temp = str.indexOf("<![CDATA[");
				    		if(temp != -1) {
				    			pos1 = temp + "<![CDATA[".length();
				    		}
				    		
				    		temp = str.lastIndexOf("]]");
				    		if(temp != -1) {
				    			pos2 = temp;
				    		}
				    		
				    		String _pic = str.substring(pos1, pos2);
				    		if(bookitem != null) {
				    			//Log.i("pic:", _pic);
				    			bookitem.setPic(_pic);
				    		}
				    		
				    	} else if("author".equals(tagName)) {

				    		String str = parser.nextText();
				    		int pos1 = 0, pos2 = str.length(), temp = 0;
				    		
				    		temp = str.indexOf("<![CDATA[");
				    		if(temp != -1) {
				    			pos1 = temp + "<![CDATA[".length();
				    		}
				    		
				    		temp = str.lastIndexOf("]]");
				    		if(temp != -1) {
				    			pos2 = temp;
				    		}
				    		
				    		String _author = str.substring(pos1, pos2);
				    		if(bookitem != null) {
				    			//Log.i("author:", _author);
				    			bookitem.setAuthor(_author);
				    		}
				    		
				    	} else if("publisher".equals(tagName)) {

				    		String str = parser.nextText();
				    		int pos1 = 0, pos2 = str.length(), temp = 0;
				    		
				    		temp = str.indexOf("<![CDATA[");
				    		if(temp != -1) {
				    			pos1 = temp + "<![CDATA[".length();
				    		}
				    		
				    		temp = str.lastIndexOf("]]");
				    		if(temp != -1) {
				    			pos2 = temp;
				    		}
				    		
				    		String _publisher = str.substring(pos1, pos2);
				    		if(bookitem != null) {
				    			//Log.i("publisher:", _publisher);
				    			bookitem.setPublisher(_publisher);
				    		}
				    		
				    	} else if("isbn".equals(tagName)) {
				    		String str = parser.nextText();
				    		int pos1 = 0, pos2 = str.length(), temp = 0;
				    		
				    		temp = str.indexOf("<![CDATA[");
				    		if(temp != -1) {
				    			pos1 = temp + "<![CDATA[".length();
				    		}
				    		
				    		temp = str.lastIndexOf("]]");
				    		if(temp != -1) {
				    			pos2 = temp;
				    		}
				    		
				    		String _isbn = str.substring(pos1, pos2);
				    		if(bookitem != null) {
					    		//Log.i("isbn:", _isbn);
				    			bookitem.setIsbn(_isbn);
				    		}
				    		
				    	}
				    	break;
				    	
				    case XmlPullParser.END_TAG:
				    	// 单节点完成
				    	String endTagName = parser.getName();
				    	if("item".equals(endTagName)) {
				            if(bookitem != null) {
				            	books_list.add(bookitem);
				            }
				            bookitem = null;
				    	}
				    	break;
				    	
				    case XmlPullParser.END_DOCUMENT:
				    	break;
				    
				}
				event = parser.next();
			}
			
			return books_list;

		} catch(Exception e) {
			Log.i("xml parser error: ", e.toString());
			return null;
		}
		
	}

}
