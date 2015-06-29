package com.example.library_helper.model;

public class Book {
	
	private String name;
	private String pic;
	private String author;
	private String publisher;
	private String isbn;

	public Book() {
		// TODO Auto-generated constructor stub
		name = "";
		pic = "";
		author = "";
		publisher = "";
		isbn = "";
	}
	
	public Book(String _name, String _pic, String _author, String _publisher, String _isbn) {
		// TODO Auto-generated constructor stub
		name = _name;
		pic = _pic;
		author = _author;
		publisher = _publisher;
		isbn = _isbn;
	}
	
	public void setName(String _name) {
		name = _name;
	}
	
	public void setPic(String _pic) {
		pic = _pic;
	}
	
	public void setAuthor(String _author) {
		author = _author;
	}
	
	public void setPublisher(String _publisher) {
		publisher = _publisher;
	}
	
	public void setIsbn(String _isbn) {
		isbn = _isbn;
	}

	public String getName() {
		return name;
	}
	
	public String getPic() {
		return pic;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getPublisher() {
		return publisher;
	}
	
	public String getIsbn() {
		return isbn;
	}
	
	public String ChangeToString() {
		String str = name + "@#@" + pic + "@#@" + author + "@#@" + publisher + "@#@" + isbn;
		return str;
	}
	
	public void setByString(String s) {
		int pos1 = 0, pos2 = 0;
		pos2 = s.indexOf("@#@", pos1);
		if(pos1 == pos2) {
			name = "";
		} else {
			name = s.substring(pos1, pos2);
		}
		
		pos1 = pos2 + "@#@".length();
		pos2 = s.indexOf("@#@", pos1);
		if(pos1 == pos2) {
			pic = "";
		} else {
			pic = s.substring(pos1, pos2);
		}
		
		pos1 = pos2 + "@#@".length();
		pos2 = s.indexOf("@#@", pos1);
		if(pos1 == pos2) {
			author = "";
		} else {
			author = s.substring(pos1, pos2);
		}
		
		pos1 = pos2 + "@#@".length();
		pos2 = s.indexOf("@#@", pos1);
		if(pos1 == pos2) {
			publisher = "";
		} else {
			publisher = s.substring(pos1, pos2);
		}
		
		pos1 = pos2 + "@#@".length();
		pos2 = s.length();
		if(pos1 == pos2) {
			isbn = "";
		} else {
			isbn = s.substring(pos1, pos2);
		}
		
		   
	}
}
