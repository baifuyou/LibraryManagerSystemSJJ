package com.bfyycdi.lms.reader;

import java.sql.*;
import java.util.*;
import java.sql.Date;

import javax.naming.*;
import javax.sql.DataSource;

public class UserDao {

	private static UserDao userDao = new UserDao();

	private DataSource dataSource;

	private UserDao() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/lms");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public String validate(String username, String password)
			throws SQLException {
		String sql = "select reader_password from readers where reader_name = ?";
		Connection conn = dataSource.getConnection();
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

	public static UserDao newInstance() {
		return userDao;
	}

	public int getUnreadMessageCount(long readerId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select count(messageNo) from messages where reader_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ResultSet result = ps.executeQuery();
		result.first();
		result.close();
		ps.close();
		conn.close();
		return result.getInt(1);
	}

	public List<Book> searchBooksByName(String name, int pageIndex, int pageSize)
			throws SQLException {
		String sql = "select * from books where books.book_name like '%" + name
				+ "%' limit " + (pageIndex - 1) * pageSize + "," + pageSize;
		List<Book> books = searchBookBySQL(sql);
		return books;
	}

	public List<Book> searchBooksByAuthor(String author, int pageIndex,
			int pageSize) throws SQLException {
		String sql = "select * from books where books.author like '%" + author
				+ "%' limit " + (pageIndex - 1) * pageSize + "," + pageSize;
		return searchBookBySQL(sql);
	}

	public List<Book> getBooksByCategory(String category, int pageIndex,
			int pageSize) throws SQLException {
		String sql = "select * from books where books.category = '" + category
				+ "' limit " + (pageIndex - 1) * pageSize + "," + pageSize;
		return searchBookBySQL(sql);
	}

	private List<Book> searchBookBySQL(String sql) throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement stat = conn.createStatement();
		ResultSet result = stat.executeQuery(sql);
		List<Book> searchResult = new ArrayList<>();
		while (result.next()) {
			Book book = new Book();
			long bookId = result.getLong("book_id");
			book.setBookId(result.getLong("book_id"));
			book.setBookName(result.getString("book_name"));
			book.setAuthor(result.getString("author"));
			book.setBookType(result.getString("category"));
			book.setIsbn(result.getString("isbn"));
			book.setMargin(getMargin(bookId));
			book.setBorrowed_number(getBorrowedNumber(bookId));
			book.setPress(result.getString("press"));
			searchResult.add(book);
		}
		result.close();
		stat.close();
		conn.close();
		return searchResult;
	}

	private int getBorrowedNumber(long bookId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select count(book_id) from borrowed_books where book_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookId);
		ResultSet result = ps.executeQuery();
		result.first();
		int borrowedNumber = result.getInt(1);
		result.close();
		ps.close();
		conn.close();
		return borrowedNumber;
	}

	public void orderBook(long bookId, long readerId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "insert into order_books(book_id, reader_id, order_time) values(?,?, now())";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookId);
		ps.setLong(2, readerId);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

	// 获取一个状态为在库的指定bookId的book_item的id
	private long getAvailableBookItemId(long bookId) throws SQLException,
			NoAvailableBookException {
		Connection conn = dataSource.getConnection();
		String sql = "select book_item_id from book_items where book_id = ? and book_state = '在库' limit 0,1";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookId);
		ResultSet result = ps.executeQuery();
		long availableBookItemId;
		if (result.first()) {
			availableBookItemId = result.getLong(1);
		} else {
			throw new NoAvailableBookException("选中图书全部被借出");
		}
		result.close();
		ps.close();
		conn.close();
		return availableBookItemId;
	}

	public void borrowBook(long bookId, long readerId)
			throws NoAvailableBookException, SQLException {
		Connection conn = dataSource.getConnection();
		try {
			conn.setAutoCommit(false);
			long availableBookItemId = getAvailableBookItemId(bookId);
			setBookState(conn, availableBookItemId, "借出");
			addBorrowedBook(conn, bookId, availableBookItemId, readerId);
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		conn.commit();
		conn.close();
	}

	private void addBorrowedBook(Connection conn, long bookId, long bookItemId,
			long readerId) throws SQLException {
		String sql = "insert into borrowed_books(reader_id, book_id, book_item_id, "
				+ "state, borrowed_time) values(?, ?, ?, '未取', ?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ps.setLong(2, bookId);
		ps.setLong(3, bookItemId);
		ps.setDate(4, new Date(System.currentTimeMillis()));
		ps.executeUpdate();
		ps.close();
	}

	private void setBookState(Connection conn, long bookItemId, String state)
			throws SQLException {
		String sql = "update book_items set book_state = ? where book_item_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, state);
		ps.setLong(2, bookItemId);
		ps.executeUpdate();
		conn.close();
		ps.close();
	}

	public List<ExtendedBook> getReadingBooks(long readerId)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select books.*,borrowed_books.borrowed_time from books, borrowed_books where borrowed_books.book_id"
				+ "=books.book_id and state = '未还' and reader_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ResultSet result = ps.executeQuery();
		List<ExtendedBook> books = new ArrayList<>();
		while (result.next()) {
			ExtendedBook book = new ExtendedBook();
			book.setBookId(result.getLong("book_id"));
			book.setBookName(result.getString("book_name"));
			book.setAuthor(result.getString("author"));
			book.setBookType(result.getString("category"));
			book.setIsbn(result.getString("isbn"));
			book.setPress(result.getString("press"));
			Date borrowedDate = result.getDate("borrowed_time");
			book.setBorrowedDate(borrowedDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(borrowedDate);
			cal.add(Calendar.DAY_OF_MONTH, UserService.MAX_BORROWED_DAYS);
			Date returnedDate = new Date(cal.getTimeInMillis());
			book.setReturnedDate(returnedDate);
			books.add(book);
		}
		result.close();
		ps.close();
		conn.close();
		return books;
	}

	public List<ExtendedBook> getReturnedBooks(long readerId)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select books.*,borrowed_time,returned_time from books, borrowed_books where borrowed_books.book_id"
				+ "=books.book_id and state = '已还' and reader_id = ? and books.book_id =borrowed_books.book_id";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ResultSet result = ps.executeQuery();
		List<ExtendedBook> books = new ArrayList<>();
		while (result.next()) {
			ExtendedBook book = new ExtendedBook();
			book.setBookId(result.getLong("book_id"));
			book.setBookName(result.getString("book_name"));
			book.setAuthor(result.getString("author"));
			book.setBookType(result.getString("category"));
			book.setIsbn(result.getString("isbn"));
			book.setPress(result.getString("press"));
			book.setBorrowedDate(result.getDate("borrowed_time"));
			book.setReturnedDate(result.getDate("returned_time"));
			books.add(book);
		}
		result.close();
		ps.close();
		conn.close();
		return books;
	}

	public List<Book> getOrderedBooks(long readerId) throws SQLException {
		String sql = "select books.* from books, order_books where order_books.book_id = "
				+ "books.book_id and reader_id = ?";
		return getBooksBySQL(sql, readerId);
	}

	public List<Book> getBorrowedBook(long readerId) throws SQLException {
		String sql = "select books.* from books, borrowed_books where borrowed_books.book_id"
				+ "= books.book_id and state = '未取' and reader_id = ?";
		return getBooksBySQL(sql, readerId);
	}

	private List<Book> getBooksBySQL(String sql, long argument)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, argument);
		ResultSet result = ps.executeQuery();
		List<Book> books = new ArrayList<>();
		while (result.next()) {
			Book book = new Book();
			book.setBookId(result.getLong("book_id"));
			book.setBookName(result.getString("book_name"));
			book.setAuthor(result.getString("author"));
			book.setBookType(result.getString("category"));
			book.setIsbn(result.getString("isbn"));
			book.setPress(result.getString("press"));
			books.add(book);
		}
		result.close();
		ps.close();
		conn.close();
		return books;
	}

	public long getReaderIdByUsername(String username) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select reader_id from readers where reader_name = ?";
		long reader_id;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, username);
		ResultSet result = ps.executeQuery();
		if (!result.first())
			return -1;
		reader_id = result.getLong("reader_id");
		result.close();
		ps.close();
		conn.close();
		return reader_id;
	}

	public List<String> getAllBookCategories() throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select category from books group by category";
		Statement stat = conn.createStatement();
		ResultSet result = stat.executeQuery(sql);
		List<String> categories = new ArrayList<>();
		while (result.next()) {
			categories.add(result.getString(1));
		}
		return categories;
	}

	public List<Book> getTopBooks() throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select books.*, count(borrowed_books.book_id) as borrowed_number"
				+ " from books, borrowed_books where books.book_id = borrowed_books.book_id "
				+ " group by book_id order by count(books.book_id) desc limit 10";
		Statement stat = conn.createStatement();
		ResultSet result = stat.executeQuery(sql);
		List<Book> books = new ArrayList<>();
		while (result.next()) {
			Book book = new TopBook();
			long bookId = result.getLong("book_id");
			int margin = getMargin(bookId);
			book.setBookId(bookId);
			book.setBookName(result.getString("book_name"));
			book.setAuthor(result.getString("author"));
			book.setBookType(result.getString("category"));
			book.setIsbn(result.getString("isbn"));
			book.setPress(result.getString("press"));
			book.setMargin(margin);
			book.setBorrowed_number(result.getInt("borrowed_number"));
			books.add(book);
		}
		result.close();
		stat.close();
		conn.close();
		return books;
	}

	private int getMargin(long bookId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select count(book_id) from book_items where book_id = ? and book_state = '在库' group by book_id";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, bookId);
		ResultSet result = ps.executeQuery();
		if (!result.first()) {
			return 0;
		}
		int margin = result.getInt(1);
		result.close();
		ps.close();
		conn.close();
		return margin;
	}

	public int getSearchBooksByNameBookCount(String keyword)
			throws SQLException {
		String sql = "select count(book_id) from books where book_name like '%"
				+ keyword + "%'";
		int bookCount = getBookCountBySql(sql, keyword);
		return bookCount;
	}

	private int getBookCountBySql(String sql, String keyword)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement state = conn.createStatement();
		ResultSet result = state.executeQuery(sql);
		result.first();
		int bookCount = result.getInt(1);
		result.close();
		state.close();
		conn.close();
		return bookCount;
	}

	public int getSearchBooksByAuthorBookCount(String keyword)
			throws SQLException {
		String sql = "select count(book_id) from books where author like '%"
				+ keyword + "%'";
		int bookCount = getBookCountBySql(sql, keyword);
		return bookCount;
	}

	public int getBooksByCategoryPageCount(String keyword) throws SQLException {
		String sql = "select count(book_id) from books where category='"
				+ keyword + "'";
		int bookCount = getBookCountBySql(sql, keyword);
		return bookCount;
	}

	public void cancelOrdered(long readerId, long bookId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "delete from order_books where reader_id = ? and book_id = ? limit 1";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ps.setLong(2, bookId);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

	public void cancelBorrowed(long readerId, long bookId) throws SQLException {
		Connection conn = dataSource.getConnection();
		try {
			conn.setAutoCommit(false);
			removeBorrowedBook(conn, readerId, bookId);
			setBookStateByBookId(conn, bookId, "在库");
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		conn.commit();
		conn.close();
	}

	private void setBookStateByBookId(Connection conn, long bookId, String state)
			throws SQLException {
		String sql = "update book_items set book_state = ? where book_id = ? limit 1";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, state);
		ps.setLong(2, bookId);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

	private void removeBorrowedBook(Connection conn, long readerId, long bookId)
			throws SQLException {
		String sql = "delete from borrowed_books where reader_id = ? and book_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ps.setLong(2, bookId);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

	public List<Message> getMessagesByReaderId(long readerId)
			throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "select * from messages where reader_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ResultSet result = ps.executeQuery();
		List<Message> messages = new ArrayList<>();
		while (result.next()) {
			Message message = new Message();
			message.setContent(result.getString("content"));
			message.setMessageId(result.getLong("message_id"));
			message.setReaded(result.getBoolean("is_readed"));
			message.setReaderId(result.getLong("reader_id"));
			message.setTitle(result.getString("title"));
			message.setAddTime(result.getDate("add_time"));
			messages.add(message);
		}
		result.close();
		ps.close();
		conn.close();
		return messages;
	}

	public void markReaded(long readerId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "update messages set is_readed = 1 where reader_id = ? and is_readed = 0";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, readerId);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

	public void deleteMessage(long messageId) throws SQLException {
		Connection conn = dataSource.getConnection();
		String sql = "delete from messages where message_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, messageId);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

}
