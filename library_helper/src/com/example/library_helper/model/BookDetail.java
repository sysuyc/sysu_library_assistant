package com.example.library_helper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class BookDetail {
	
	private String theme;
	private String name;
	private String author;
	private String pic;
	private String publisher;
	private String isbn;
	private String digest;
	private String collect;
	private String language;
	private String classification;
	private String sysNumber;
	private List<Map<String, String> > bookStates;
	
	public BookDetail() {
		// TODO Auto-generated constructor stub
		theme = "";
		name = "";
		author = "";
		pic = "";
		publisher = "";
		isbn = "";
		digest = "";
		collect = "";
		language = "";
		classification = "";
		sysNumber = "";
		bookStates = new ArrayList<Map<String, String>>();
	}
	
	public BookDetail(String t, String n, String a, String pi, String pub,
			String i, String d, String coll, String l, String cla) {
		theme = t;
		name = n;
		author = a;
		pic = pi;
		publisher = pub;
		isbn = i;
		digest = d;
		collect = coll;
		language = l;
		classification = cla;
	}
	
	public void setTheme(String _theme) {
		theme = _theme;
	}
	
	public void setName(String _name) {
		name = _name;
	}
	
	public void setAuthor(String _author) {
		author = _author;
	}
	
	public void setPic(String _pic) {
		pic = _pic;
	}

	public void setPublisher(String _publisher) {
		publisher = _publisher;
	}
	
	public void setIsbn(String _isbn) {
		isbn = _isbn;
	}
	
	public void setDigest(String _digest) {
		digest = _digest;
	}
	
	public void setCollect(String _collect) {
		collect = _collect;
	}
	
	public void setLanguage(String _language) {
		language = _language;
	}
	
	public void setClassification(String _classification) {
		classification = _classification;
	}
	
	public void setSysNumber(String _sysNumber) {
		sysNumber = _sysNumber;	
	}
	
	public void addBookStates(Map<String, String> _state) {
		bookStates.add(_state);
	}
	
	
	
	public String getTheme() {
		return theme;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getPic() {
		return pic;
	}
	
	public String getPublisher() {
		return publisher;
	}
	
	public String getIsbn() {
		return isbn;
	}
	
	public String getDigest() {
		return digest;
	}
	
	public String getCollect() {
		return collect;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getClassification() {
		return classification;
	}
	
	public String getSysNumber() {
		return sysNumber;
	}
	
	public List<Map<String, String> > getBookStates() {
		return bookStates;
	}
	
}
