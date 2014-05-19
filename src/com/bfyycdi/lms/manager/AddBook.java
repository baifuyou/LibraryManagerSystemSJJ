package com.bfyycdi.lms.manager;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.bfyycdi.lms.reader.Book;

@WebServlet("/addBook")
public class AddBook extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final ManagerService managerService = ManagerService.newInstance();

	public AddBook() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String bookName = request.getParameter("bookName");
		String isbn = request.getParameter("isbn");
		String category = request.getParameter("category");
		String press = request.getParameter("press");
		String author = request.getParameter("author");
		int addNumber = Integer.parseInt(request.getParameter("number"));
		Book book = new Book();
		book.setAuthor(author);
		book.setBookName(bookName);
		book.setIsbn(isbn);
		book.setBookType(category);
		book.setPress(press);
		String url = "";
		try {
			managerService.addBook(book, addNumber);
			url = "jsp/addBookSuccess.jsp";
		} catch (SQLException e) {
			url = "jsp/addBookFailure.jsp";
			e.printStackTrace();
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
