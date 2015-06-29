package com.example.library_helper.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.provider.OpenableColumns;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.example.library_helper.result_activity;
import com.example.library_helper.model.Book;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.KeyIterator;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

public class DBSearchController {
	
	private String courseDBName = "course";
	private String booknameDBName = "bookname";
	private DB courseDB;
	private DB booknameDB;
	private Context mContext;
	private int itemMaxNum = 100;
	
	public DBSearchController(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		courseDB = null;
		booknameDB = null;

	}
	
	public Boolean isContainCourseSearchResult(String course) {
		
		Boolean isContain = false;
		
		try {
			courseDB = DBFactory.open(mContext, courseDBName);
			isContain = courseDB.exists(course);
			courseDB.close();
			return isContain;
			
		} catch (SnappydbException e) {
			Log.i("Course DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
		return null;
	}
	
	public List<Book> getCourseSearchResult(String course) {
		List<Book> search_result = null;
		try {
			courseDB = DBFactory.open(mContext, courseDBName);
		    Book[] books = courseDB.getObjectArray(course, Book.class);
			search_result = Arrays.asList(books);
			
			courseDB.close();
			
			return search_result;
			
		} catch (SnappydbException e) {
			Log.i("Course DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
		return null;
	}
	
	public Boolean isContainBookNameSearchResult(String bookname) {
		
		Boolean isContain = false;
		
		try {
			booknameDB = DBFactory.open(mContext, booknameDBName);
			isContain = booknameDB.exists(bookname);
			booknameDB.close();
			
			return isContain;
			
		} catch (SnappydbException e) {
			Log.i("BookName DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
		return null;
	}
	
	public List<Book> getBookNameSearchResult(String bookname) {
		List<Book> search_result = null;
		try {
			booknameDB = DBFactory.open(mContext, booknameDBName);
		    Book[] books = booknameDB.getObjectArray(bookname, Book.class);
			search_result = Arrays.asList(books);
			
			booknameDB.close();
			
			return search_result;
			
		} catch (SnappydbException e) {
			Log.i("BookName DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
		return null;
	}
	
	public Boolean insertCourseSearchResult(String course, List<Book> books_list) {
		Boolean isInsert = false;
		try {
			
			Boolean fullFlag = isFull(courseDBName);
			if(fullFlag != null && fullFlag) {
				clear(courseDBName);
			}
			
			courseDB = DBFactory.open(mContext, courseDBName);
			int size = books_list.size();
			Book[] books = (Book[])books_list.toArray(new Book[size]);
			
			//Log.i("course DB", books.toString());
			
			courseDB.put(course, books);
			
			courseDB.close();
			
			isInsert = true;
			return isInsert;
		} catch (SnappydbException e) {
			Log.i("Course DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
		return false;
	}
	
	public Boolean insertBookNameSearchResult(String bookname, List<Book> books_list) {
		Boolean isInsert = false;
		try {
			
			Boolean fullFlag = isFull(booknameDBName);
			if(fullFlag != null && fullFlag) {
				clear(booknameDBName);
			}
			
			booknameDB = DBFactory.open(mContext, booknameDBName);
			int size = books_list.size();
			Book[] books = (Book[])books_list.toArray(new Book[size]);
			
			//Log.i("bookname DB", books.toString());
			
			booknameDB.put(bookname, books);
			
			booknameDB.close();
			
			isInsert = true;			
			return isInsert;
			
		} catch (SnappydbException e) {
			Log.i("bookname DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
		return false;
	}
	
	public Boolean isFull(String dbName) {
		Boolean full = false;
		
		if(!dbName.equals(courseDBName) && !dbName.equals(booknameDBName)) {
			return null;
		}
		
		try {
			DB db = DBFactory.open(mContext, dbName);
			KeyIterator it = db.allKeysIterator();
			int s = 0;
			if(it.hasNext()) {
				s = 1;
			}
			while(it.hasNext()) {
				s++;
				it.next(1);
			}
			if(s >= 100) {
				full = true;
			}
			
			it.close();
			db.close();
			
			return full;
			
		} catch (SnappydbException e) {
			Log.i("DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
		return null;
		
	}
	
	public void clear(String dbName) {
		if(!dbName.equals(courseDBName) && !dbName.equals(booknameDBName)) {
			return;
		}
		try {
			DB db = DBFactory.open(mContext, dbName);
			
			db.destroy();
			//db.close();
			
			Log.i("DB State", "Clear DB " + dbName + " Success");
			
		} catch (SnappydbException e) {
			Log.i("DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
		
	}
	
	public void clearAll() {
		try {
			courseDB = DBFactory.open(mContext, courseDBName);
			courseDB.destroy();
			//courseDB.close();
			
			booknameDB = DBFactory.open(mContext, booknameDBName);
			booknameDB.destroy();
			//booknameDB.close();
			
			Log.i("DB State", "Clear DB Success");
			
		} catch (SnappydbException e) {
			Log.i("DB Error", e.toString());
		} catch (Exception e) {
			Log.i("Unknown DB Error", e.toString());
		}
	}
	
}
