package com.bfyycdi.lms.reader;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.*;

public class UserService {
	
	public final static int MAX_BORROWED_DAYS = 45;

	private UserDao userDao = UserDao.newInstance();

	private final static UserService userService = new UserService();

	private UserService() {
	}

	public String validate(String username, String password) {
		try {
			return userDao.validate(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public long getReaderIdByUsername(String username) throws SQLException {
		return userDao.getReaderIdByUsername(username);
	}

	public void orderBook(long bookId, long readerId) throws SQLException {
		userDao.orderBook(bookId, readerId);
	}

	public void borrowBook(long bookId, long readerId) throws SQLException,
			NoAvailableBookException {
		userDao.borrowBook(bookId, readerId);
	}

	public List<ExtendedBook> getReadingBooks(long readerId) throws SQLException {
		return userDao.getReadingBooks(readerId);
	}
	
	public List<ExtendedBook> getReturnedBooks(long readerId) throws SQLException {
		return userDao.getReturnedBooks(readerId);
	}
	
	public List<Book> getOrderedBooks(long readerId) throws SQLException {
		return userDao.getOrderedBooks(readerId);
	}
	
	public List<Book> getBorrowedBooks(long readerId) throws SQLException {
		return userDao.getBorrowedBook(readerId);
	}
	
	public int getUnreadMessageCount(long readerId) throws SQLException {
		return userDao.getUnreadMessageCount(readerId);
	}

	public List<Book> searchBooksByName(String bookName, int pageIndex,
			int pageSize) throws SQLException {
		return userDao.searchBooksByName(bookName, pageIndex, pageSize);
	}

	public List<Book> searchBooksByAuthor(String author, int pageIndex,
			int pageSize) throws SQLException {
		return userDao.searchBooksByAuthor(author, pageIndex, pageSize);
	}

	public List<Book> getBooksByCategory(String category, int pageIndex,
			int pageSize) throws SQLException {
		return userDao.getBooksByCategory(category, pageIndex, pageSize);
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
	
	public List<String> getAllBookCategories() throws SQLException {
		return userDao.getAllBookCategories();
	}
	
	public List<Book> getTopBooks() throws SQLException {
		return userDao.getTopBooks();
	}

	public static UserService newInstance() {
		return userService;
	}

	public int getSearchBooksByNamePageCount(String keyword, int pageSize) throws SQLException {
		int bookCount =  userDao.getSearchBooksByNameBookCount(keyword);
		int pageCount = bookCount/pageSize;
		if (bookCount % pageSize != 0) {
			pageCount++;
		}
		return pageCount;
	}

	public int getSearchBooksByAuthorPageCount(String keyword, int pageSize) throws SQLException {
		int bookCount = userDao.getSearchBooksByAuthorBookCount(keyword);
		int pageCount = bookCount/pageSize;
		if (bookCount % pageSize != 0) {
			pageCount++;
		}
		return pageCount;
	}

	public int getBooksByCategoryPageCount(String keyword, int pageSize) throws SQLException {
		int bookCount = userDao.getBooksByCategoryPageCount(keyword);
		int pageCount = bookCount/pageSize;
		if (bookCount % pageSize != 0) {
			pageCount++;
		}
		return pageCount;
	}

	public void cancelOrdered(long readerId, long bookId) throws SQLException {
		userDao.cancelOrdered(readerId, bookId);
	}

	public void cancelBorrowed(long readerId, long bookId) throws SQLException {
		userDao.cancelBorrowed(readerId, bookId);
	}

	public void markReaded(long readerId) throws SQLException {
		userDao.markReaded(readerId);
	}
	
	public List<Message> getMessagesByReaderId(long readerId) throws SQLException {
		List<Message> messages = userDao.getMessagesByReaderId(readerId);
		return messages;
	}

	public void deleteMessage(long messageId) throws SQLException {
		userDao.deleteMessage(messageId);
	}

}
