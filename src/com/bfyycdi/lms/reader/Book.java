package com.bfyycdi.lms.reader;

public class Book {
	private long bookId;
	private String isbn;
	private String bookName;
	private String bookType;
	private String author;
	private String press;
	private int margin;
	private int borrowed_number;

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPress() {
		return press;
	}

	public void setPress(String press) {
		this.press = press;
	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public int getBorrowed_number() {
		return borrowed_number;
	}

	public void setBorrowed_number(int borrowed_number) {
		this.borrowed_number = borrowed_number;
	}
}
