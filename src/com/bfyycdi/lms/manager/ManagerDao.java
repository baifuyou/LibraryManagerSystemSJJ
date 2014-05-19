package com.bfyycdi.lms.manager;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.bfyycdi.lms.reader.Book;
import com.bfyycdi.lms.reader.UserService;

public class ManagerDao {

	private final static ManagerDao managerDao = new ManagerDao();
	public final static int PENALTY_PER_DAY = 20;
	public final static long MILLISECOND_OF_DAY = 24 * 60 * 60 * 1000;
	private DataSource dataSource;

	private ManagerDao() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/lms");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public String validateManager(String username, String password)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select manager_password from managers where manager_name = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ResultSet result = ps.executeQuery();
		if (!result.first()) {
			return "userNoExist";
		}
		String rightPassword = result.getString(1);
		if (!rightPassword.equals(password)) {
			return "passwordError";
		}
		result.close();
		ps.close();
		conn.close();
		return "success";
	}

	public static ManagerDao newInstance() {
		return managerDao;
	}

	public long getManagerIdByUsername(String username) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select manager_id from managers where manager_name = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ResultSet result = ps.executeQuery();
		result.first();
		long managerId = result.getLong(1);
		result.close();
		ps.close();
		conn.close();
		return managerId;
	}

	public List<ExtendedBookItem> searchBookItemsByBookItemId(long bookItemId)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select books.book_id, books.book_name, books.isbn,"
				+ " book_items.book_item_id, readers.reader_id, readers.reader_name,"
				+ " borrowed_books.borrowed_time, borrowed_books.borrowed_id, borrowed_books.state from books,"
				+ " book_items, readers, borrowed_books where books.book_id "
				+ "= book_items.book_id and book_items.book_item_id = "
				+ "borrowed_books.book_item_id and (borrowed_books.state = "
				+ "'未还' or borrowed_books.state = '未取') and borrowed_books.reader_id "
				+ "= readers.reader_id and book_items.book_item_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookItemId);
		ResultSet result = ps.executeQuery();
		List<ExtendedBookItem> bookItems = new ArrayList<ExtendedBookItem>();
		while (result.next()) {
			ExtendedBookItem bookItem = new ExtendedBookItem();
			bookItem.setBookId(result.getLong("book_id"));
			bookItem.setBookItemId(result.getLong("book_item_id"));
			bookItem.setBookName(result.getString("book_name"));
			bookItem.setBorrowedDate(result.getDate("borrowed_time"));
			bookItem.setIsbn(result.getString("isbn"));
			bookItem.setBorrowedId(result.getLong("borrowed_id"));
			bookItem.setPenalty(calculatePenalty(bookItem.getBorrowedDate()));
			bookItem.setReaderId(result.getLong("reader_id"));
			bookItem.setReaderName(result.getString("reader_name"));
			bookItem.setState(result.getString("state"));
			bookItems.add(bookItem);
		}
		result.close();
		ps.close();
		conn.close();
		return bookItems;
	}

	private int calculatePenalty(Date borrowedDate) {
		long borrowedTimestamp = borrowedDate.getTime();
		long nowTimestamp = System.currentTimeMillis();
		int gapOfDay = (int) ((int) (nowTimestamp - borrowedTimestamp) / MILLISECOND_OF_DAY);
		int penalty = (gapOfDay - UserService.MAX_BORROWED_DAYS)
				* PENALTY_PER_DAY;
		penalty = penalty < 0 ? 0 : penalty;
		return penalty;
	}

	public List<ExtendedBookItem> searchBookItemsByUsername(String username)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select books.book_id, books.book_name, books.isbn,"
				+ " book_items.book_item_id, readers.reader_id, readers.reader_name,"
				+ " borrowed_books.borrowed_time, borrowed_books.borrowed_id, borrowed_books.state from books,"
				+ " book_items, readers, borrowed_books where books.book_id "
				+ "= book_items.book_id and book_items.book_item_id = "
				+ "borrowed_books.book_item_id and (borrowed_books.state = "
				+ "'未还' or borrowed_books.state = '未取') and borrowed_books.reader_id "
				+ "= readers.reader_id and readers.reader_name = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ResultSet result = ps.executeQuery();
		List<ExtendedBookItem> bookItems = new ArrayList<ExtendedBookItem>();
		while (result.next()) {
			ExtendedBookItem bookItem = new ExtendedBookItem();
			bookItem.setBookId(result.getLong("book_id"));
			bookItem.setBookItemId(result.getLong("book_item_id"));
			bookItem.setBookName(result.getString("book_name"));
			bookItem.setBorrowedDate(result.getDate("borrowed_time"));
			bookItem.setIsbn(result.getString("isbn"));
			bookItem.setBorrowedId(result.getLong("borrowed_id"));
			bookItem.setPenalty(calculatePenalty(bookItem.getBorrowedDate()));
			bookItem.setReaderId(result.getLong("reader_id"));
			bookItem.setReaderName(result.getString("reader_name"));
			bookItem.setState(result.getString("state"));
			bookItems.add(bookItem);
		}
		result.close();
		ps.close();
		conn.close();
		return bookItems;
	}

	public void takeBook(long borrowedId) throws SQLException {
		setBorrowedState(borrowedId, "未还");
	}

	private void setBorrowedState(long borrowedId, String state)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "update borrowed_books set state = ? where borrowed_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, state);
		ps.setLong(2, borrowedId);
		ps.executeUpdate();
	}

	public void returnBook(long borrowedId, long bookItemId)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		try {
			conn.setAutoCommit(false);
			setBorrowedStateAndReturnTime(conn, borrowedId, "已还");
			setBookItemState(conn, bookItemId, "在库");
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		conn.commit();
		conn.close();
	}

	private void setBorrowedStateAndReturnTime(Connection conn, long borrowedId, String state)
			throws SQLException {
		String sql = "update borrowed_books set state = ?, returned_time = ? where borrowed_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, state);
		ps.setDate(2, new Date(System.currentTimeMillis()));
		ps.setLong(3, borrowedId);
		ps.executeUpdate();
		ps.close();
	}

	private void setBookItemState(Connection conn, long bookItemId, String state)
			throws SQLException {
		String sql = "update book_items set book_state = ? where book_item_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, state);
		ps.setLong(2, bookItemId);
		ps.executeUpdate();
		ps.close();
	}

	public long getBookIdByBookItemId(long bookItemId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select book_id from book_items where book_item_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookItemId);
		ResultSet result = ps.executeQuery();
		result.first();
		long bookId = result.getLong(1);
		ps.close();
		result.close();
		conn.close();
		return bookId;
	}

	public long getOrderedReaderIdIfExist(long bookId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select reader_id from order_books where book_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookId);
		ResultSet result = ps.executeQuery();
		if (!result.first()) {
			return -1;
		}
		long readerId = result.getLong(1);
		result.close();
		ps.close();
		conn.close();
		return readerId;
	}

	public void notifyReader(long readerId, String message) throws SQLException {
		notifyReader(readerId, "新消息", message);
	}

	public void notifyReader(long readerId, String title, String message)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "insert into messages(is_readed, title, content, reader_id) values(0, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, title);
		ps.setString(2, message);
		ps.setLong(3, readerId);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

	public long getBookIdByIsbnIfExist(String isbn) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select book_id from books where isbn = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, isbn);
		ResultSet result = ps.executeQuery();
		if (!result.first()) {
			result.close();
			ps.close();
			return -1;
		} 
		long bookId = result.getLong(1);
		result.close();
		ps.close();
		conn.close();
		return bookId;
	}

	public long addBook(Book book) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "insert into books(book_name, isbn, press, author, category)"
				+ " values(?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, book.getBookName());
		ps.setString(2, book.getIsbn());
		ps.setString(3, book.getPress());
		ps.setString(4, book.getAuthor());
		ps.setString(5, book.getBookType());
		ps.executeUpdate();
		long bookId = this.getBookIdByIsbnIfExist(book.getIsbn());
		ps.close();
		conn.close();
		return bookId;
	}

	public void addBookItems(long bookId, int addNumber) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "insert into book_items(book_id, join_time, book_state)"
				+ " values(?, now(), '在库')";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookId);
		for(int i = 0; i < addNumber; i++) {
			ps.executeUpdate();
		}
		ps.close();
		conn.close();
	}
}
