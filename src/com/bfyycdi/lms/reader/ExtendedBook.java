package com.bfyycdi.lms.reader;

import java.sql.Date;

public class ExtendedBook extends Book{
	private Date borrowedDate;
	private Date returnedDate;

	public Date getBorrowedDate() {
		return borrowedDate;
	}

	public void setBorrowedDate(Date borrowedDate) {
		this.borrowedDate = borrowedDate;
	}

	public Date getReturnedDate() {
		return returnedDate;
	}

	public void setReturnedDate(Date returnedDate) {
		this.returnedDate = returnedDate;
	}
}
