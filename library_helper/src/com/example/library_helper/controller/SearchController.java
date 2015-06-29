package com.example.library_helper.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.example.library_helper.model.Book;

public class SearchController implements Runnable {

	private Activity activity;
	private int searchType; //1:course 2:bookname
	private InternetSearchController my_InternetSearchController;
	private DBSearchController my_DbSearchController;
	private String searchItem;
	private Handler mHandler;
	
	public SearchController(Activity a, Handler h) {
		// TODO Auto-generated constructor stub
		activity = a;
		mHandler = h;
		searchType = 0;
		searchItem = "";
		my_InternetSearchController = new InternetSearchController();
		my_DbSearchController = new DBSearchController(activity.getApplicationContext());
	}
		
	public void setSearchType(int type) {
		searchType = type;	
	}
		
	public void setSearchItem(String item) {
		searchItem = item;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//Log.i("searchType", String.valueOf(searchType));
		
		if(searchType != 0) {
			
			Log.i("step", "step1");
			
			if(searchType == 1) {
				//Seartch By Course
				
				//Log.i("course", searchItem);

				String status_code = null;
				List<Book> books = null;
				
				Boolean isContain = my_DbSearchController.isContainCourseSearchResult(searchItem);
				if(isContain != null && isContain) {
					Log.i("DB Request", "course DB");
					books = my_DbSearchController.getCourseSearchResult(searchItem);
					if(books != null) {
						status_code = "1";
						
						//Log.i("books", books.toString());
						
					} else {
						status_code = "6";
					}
					
				} else {
					Map<String, List<Book>> searchResult = my_InternetSearchController.SearchByCourse(searchItem);

					for (java.util.Map.Entry<String, List<Book>> entry: searchResult.entrySet()) {
						status_code = entry.getKey();
						books = entry.getValue();
					}
					
					if(status_code != null && books != null) {
						System.out.println(status_code);
						
						//Log.i("books", books.toString());
						
						if(status_code.equals("1") && books.size() > 0) {
							my_DbSearchController.insertCourseSearchResult(searchItem, books);
						}
					} else {
						status_code = "5";
						books = null;
					}
				}
				
				mHandler.obtainMessage(Integer.parseInt(status_code), books).sendToTarget();
				
			} else if(searchType == 2) {
				//Seartch By BookName
				
				//Log.i("bookname", searchItem);
				
				String status_code = null;
				List<Book> books = null;
				
				Boolean isContain = my_DbSearchController.isContainBookNameSearchResult(searchItem);
				
				//Log.i("isContain", isContain.toString());
				
				if(isContain != null && isContain) {
					
					//Log.i("DB Request", "bookname DB");
					
					books = my_DbSearchController.getBookNameSearchResult(searchItem);
					if(books != null) {
						status_code = "1";
						
						//Log.i("books", books.toString());
						
					} else {
						status_code = "6";
					}
				} else {
					Map<String, List<Book>> searchResult = my_InternetSearchController.SearchByBookName(searchItem);

					for (java.util.Map.Entry<String, List<Book>> entry: searchResult.entrySet()) {
						status_code = entry.getKey();
						books = entry.getValue();
					}
					if(status_code != null && books != null) {
						System.out.println(status_code);
						
						//Log.i("books", books.toString());
						
						if(status_code.equals("1") && books.size() > 0) {
							my_DbSearchController.insertBookNameSearchResult(searchItem, books);
						}

					} else {
						status_code = "5";
						books = null;
					}
				}
				
				mHandler.obtainMessage(Integer.parseInt(status_code), books).sendToTarget();
				
			}
		}
		
	}
}
