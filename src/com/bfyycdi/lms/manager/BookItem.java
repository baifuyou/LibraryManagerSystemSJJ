package com.bfyycdi.lms.manager;

public class BookItem {
	private long bookId;
	private long bookItemId;
	private String bookName;
	private String isbn;

	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public long getBookItemId() {
		return bookItemId;
	}

	public void setBookItemId(long bookItemId) {
		this.bookItemId = bookItemId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
}
