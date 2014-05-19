package com.bfyycdi.lms.manager;

import java.sql.Date;

public class ExtendedBookItem extends BookItem {
	private long readerId;
	private Date borrowedDate;
	private String readerName;
	private int penalty; //罚款，角为单位
	private long borrowedId;
	private String state;

	public long getReaderId() {
		return readerId;
	}

	public void setReaderId(long readerId) {
		this.readerId = readerId;
	}

	public Date getBorrowedDate() {
		return borrowedDate;
	}

	public void setBorrowedDate(Date borrowedDate) {
		this.borrowedDate = borrowedDate;
	}

	public String getReaderName() {
		return readerName;
	}

	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}

	public int getPenalty() {
		return penalty;
	}

	public void setPenalty(int penalty) {
		this.penalty = penalty;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getBorrowedId() {
		return borrowedId;
	}

	public void setBorrowedId(long borrowedId) {
		this.borrowedId = borrowedId;
	}
}
