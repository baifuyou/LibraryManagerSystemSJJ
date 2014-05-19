package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/history")
public class History extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private UserService userService = UserService.newInstance();

	public History() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long readerId = (Long) request.getSession().getAttribute("readerId");
		String label = request.getParameter("label");
		String url = "";
		try {
			if (label.equals("reading")) {
				List<ExtendedBook> books = userService.getReadingBooks(readerId);
				url = "jsp/historyExtended.jsp";
				request.setAttribute("books", books);
			} else if (label.equals("noTake")) {
				List<Book> books = userService.getBorrowedBooks(readerId);
				url = "jsp/historyNoTake.jsp";
				request.setAttribute("books", books);
			} else if (label.equals("ordered")) {
				List<Book> books = userService.getOrderedBooks(readerId);
				url = "jsp/historyOrdered.jsp";
				request.setAttribute("books", books);
			} else if (label.equals("returned")) {
				List<ExtendedBook> books = userService.getReturnedBooks(readerId);
				url = "jsp/historyExtended.jsp";
				request.setAttribute("books", books);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
