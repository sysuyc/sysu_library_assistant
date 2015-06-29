package com.example.library_helper.controller;

import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.example.library_helper.model.Book;
import com.example.library_helper.model.BookDetail;

public class GetBookDetailController {
	
	//private final String bookdetail_url = "http://172.18.187.83:8000/lib/detail/?";
	private final String bookdetail_url = "http://172.18.187.107:8005/lib/detail/?";
	private final String bookdetailByIsbn_url = "http://172.18.187.107:8005/lib/detailbyisbn/?isbn=";
	
	public GetBookDetailController() {
		// TODO Auto-generated constructor stub
	}
	
	public Map<String, BookDetail> getBookDetail(String bname, String author, String isbn) {
		return getBookDetailByInternet(bname, author, isbn);
	}
	
	public Map<String, BookDetail> getBookDetailByIsbn(String isbn) {
		return getBookDetailWithIsbnByInternet(isbn);
	}
	
    private Map<String, BookDetail> getBookDetailByInternet(String bname, String author, String isbn) {
		
		String statusCode = "-1";
		BookDetail bookDetail = null;
		Map<String, BookDetail> result = new HashMap<String, BookDetail>();
		
		// TODO Auto-generated method stub
		try {
			//执行网络连接以及解析代码
			//get方法
			//设置url和对应的参数对
			//String curUrl = search_by_course_url + URLEncoder.encode(course, "utf-8");
			String curUrl = bookdetail_url;
			if(isbn != null && !isbn.equals("")) {
				//curUrl = curUrl + "isbn=" + isbn.replaceAll(" ", "%20") + "&";
				curUrl = curUrl + "isbn=" + URLEncoder.encode(isbn, "utf-8") + "&";
			}
			if(bname == null) {
				bname = "";
			}
			if(author == null) {
				author = "";
			}
			//curUrl = curUrl + "bname=" + bname.replaceAll(" ", "%20") + "&" + "author=" + author.replaceAll(" ", "%20");
			curUrl = curUrl + "bname=" + URLEncoder.encode(bname, "utf-8")  + "&" + "author=" + URLEncoder.encode(author, "utf-8");
			
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
			
			Log.i("return ", str);
			
			statusCode = "1";
			bookDetail = ParserSearchResult(str);
			
			
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
		
		result.put(statusCode, bookDetail);
		return result;
		
	}
    
    private Map<String, BookDetail> getBookDetailWithIsbnByInternet(String isbn) {
		
		String statusCode = "-1";
		BookDetail bookDetail = null;
		Map<String, BookDetail> result = new HashMap<String, BookDetail>();
		
		// TODO Auto-generated method stub
		try {
			//执行网络连接以及解析代码
			//get方法
			//设置url和对应的参数对
			//String curUrl = search_by_course_url + URLEncoder.encode(course, "utf-8");
			String curUrl = bookdetailByIsbn_url;
			curUrl = curUrl + URLEncoder.encode(isbn, "utf-8");
			
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
			
			Log.i("return ", str);
			
			statusCode = "1";
			bookDetail = ParserSearchResult(str);
			
			
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
		
		result.put(statusCode, bookDetail);
		return result;
		
	}
    
    private BookDetail ParserSearchResult(String input) {
		
		BookDetail bd = null;
		Map<String, String> _state = null;
		
		try {
			//使用XmlPullParser来解析取得的xml格式的字符串
			XmlPullParser parser = Xml.newPullParser();
			//传入解析内容，规定传入Reader类型参数
			parser.setInput(new StringReader(input));
			
			
			//设置文件标记
			int event = parser.getEventType();
			
			bd = new BookDetail();
			//如果标记不为文件尾，则开始记录
			while(event != XmlPullParser.END_DOCUMENT) {
				switch(event) {
				    case XmlPullParser.START_TAG:
				    	//get tag name
				    	String tagName = parser.getName();
				    	
				    	if("主题".equals(tagName)) {
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
				    		
				    		String _theme = str.substring(pos1, pos2);
				    		bd.setTheme(_theme);
				    		
				    	} else if("题名".equals(tagName)) {
				    		
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
				    		bd.setName(_name);
				    		
				    	} else if("个人著者".equals(tagName)) {

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
				    		if(bd != null) {
				    			//Log.i("author:", _author);
				    			bd.setAuthor(_author);
				    		}
				    		
				    	} else if("出版发行".equals(tagName)) {

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
				    		if(bd != null) {
				    			//Log.i("publisher:", _publisher);
				    			bd.setPublisher(_publisher);
				    		}
				    		
				    	} else if("ISBN".equals(tagName)) {
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
				    		if(bd != null) {
					    		//Log.i("isbn:", _isbn);
				    			bd.setIsbn(_isbn);
				    		}
				    		
				    	} else if("摘要".equals(tagName)) {
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
				    		
				    		String _digest = str.substring(pos1, pos2);
				    		if(bd != null) {
					    		//Log.i("isbn:", _isbn);
				    			bd.setDigest(_digest);
				    		}
				    		
				    	} else if("丛编".equals(tagName)) {
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
				    		
				    		String _collect = str.substring(pos1, pos2);
				    		if(bd != null) {
					    		//Log.i("isbn:", _isbn);
				    			bd.setCollect(_collect);
				    		}
				    		
				    	} else if("分类号".equals(tagName)) {
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
				    		
				    		String _classification = str.substring(pos1, pos2);
				    		if(bd != null) {
					    		//Log.i("isbn:", _isbn);
				    			bd.setClassification(_classification);
				    		}
				    		
				    	}  else if("作品语种".equals(tagName)) {
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
				    		
				    		String _language = str.substring(pos1, pos2);
				    		if(bd != null) {
					    		//Log.i("isbn:", _isbn);
				    			bd.setLanguage(_language);
				    		}
				    		
				    	} else if("子项".equals(tagName)) {
				    		if(_state == null) {
				    			_state = new HashMap<String, String>();
				    		} else if(_state.size() > 0) {
				    			_state.clear();
				    		}
				    	} else if("应还日期".equals(tagName)) {
				    		String str = parser.nextText();
				    		if(_state != null) {
				    			_state.put("BorrowDate", str);
				    		}
				    	} else if("馆藏地".equals(tagName)) {
				    		String str = parser.nextText();
				    		if(_state != null) {
				    			_state.put("place", str);
				    		}
				    	} else if("架位".equals(tagName)) {
				    		String str = parser.nextText();
				    		if(_state != null) {
				    			_state.put("bookPosition", str);
				    		}
				    	} else if("available".equals(tagName)) {
				    		String str = parser.nextText();
				    		if(_state != null) {
				    			_state.put("available", str);
				    		}
				    	} else if("doc_number".equals(tagName)) {
				    		String str = parser.nextText();
				    		if(_state != null) {
				    			_state.put("doc_number", str);
				    		}
				    	} else if("item_sequence".equals(tagName)) {
				    		String str = parser.nextText();
				    		if(_state != null) {
				    			_state.put("item_sequence", str);
				    		}
				    	}
				    	
				    	break;
				    	
				    case XmlPullParser.END_TAG:
				    	// 单节点完成
				    	String endTagName = parser.getName();
				    	if("子项".equals(endTagName)) {
				    		if(_state != null) {
				    			bd.addBookStates(_state);
				    		}
				    		_state = null;
				    	}
				    	break;
				    	
				    case XmlPullParser.END_DOCUMENT:
				    	break;
				    
				}
				event = parser.next();
			}
			
			return bd;

		} catch(Exception e) {
			Log.i("xml parser error: ", e.toString());
			return null;
		}
		
	}


}
