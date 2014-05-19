package com.bfyycdi.lms.manager;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.bfyycdi.lms.reader.Book;
import com.bfyycdi.lms.reader.NoAvailableBookException;
import com.bfyycdi.lms.reader.UserDao;

public class ManagerService {
	
	private static final ManagerService managerService = new ManagerService();
	private static final String MESSAGE_OF_TAKE_BOOK = "您预订的图书已经有了库存，请您尽快取走";
	
	public final static ManagerDao managerDao = ManagerDao.newInstance();
	public final static UserDao userDao = UserDao.newInstance();

	private ManagerService() {
	}

	public String validateManager(String username, String password) {
		try {
			return managerDao.validateManager(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public void rememberUser(String username, String password,
			HttpServletResponse response) {
		addCookie("username", username, 7, response);
		addCookie("password", password, 7, response);
	}

	public void forgiveUser(String username, String password,
			HttpServletResponse response) {
		addCookie("username", username, 0, response);
		addCookie("password", password, 0, response);
	}

	private void addCookie(String name, String value, int validTime,
			HttpServletResponse response) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(validTime * 24 * 60 * 60);
		response.addCookie(cookie);
	}

	public static ManagerService newInstance() {
		return managerService;
	}

	public long getManagerIdByUsername(String username) throws SQLException {
		return managerDao.getManagerIdByUsername(username); 
	}

	public List<ExtendedBookItem> searchBookItemsByBookItemId(long bookItemId) throws SQLException {
		return managerDao.searchBookItemsByBookItemId(bookItemId);
	}

	public List<ExtendedBookItem> searchBookItemsByUsername(String username) throws SQLException {
		return managerDao.searchBookItemsByUsername(username);
	}

	public void takeBook(long borrowedId) throws SQLException {
		managerDao.takeBook(borrowedId);
	}

	public void returnBook(long borrowedId, long bookItemId) throws SQLException {
		managerDao.returnBook(borrowedId, bookItemId);
	}

	public void notifyOrderIfExist(long bookItemId) throws SQLException, NoAvailableBookException {
		long bookId = managerDao.getBookIdByBookItemId(bookItemId);
		long readerId = managerDao.getOrderedReaderIdIfExist(bookId);
		if (readerId == -1) {
			return;
		}
		managerDao.notifyReader(readerId, MESSAGE_OF_TAKE_BOOK);
		userDao.cancelOrdered(readerId, bookId);
		userDao.borrowBook(bookId, readerId);
	}

	public void addBook(Book book, int addNumber) throws SQLException {
		long bookId = managerDao.getBookIdByIsbnIfExist(book.getIsbn());
		if (bookId == -1) {
			bookId = managerDao.addBook(book);
		}
		managerDao.addBookItems(bookId, addNumber);
	}
}
