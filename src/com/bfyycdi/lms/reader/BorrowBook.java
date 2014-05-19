package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/borrowBook")
public class BorrowBook extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserService userService = UserService.newInstance();

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long bookId = Long.parseLong(request.getParameter("bookId"));
		long readerId = (Long) request.getSession().getAttribute("readerId");
		String url = "";
		try {
			userService.borrowBook(bookId, readerId);
			url = "jsp/success.jsp";
		} catch (SQLException | NoAvailableBookException e) {
			url = "jsp/failure.jsp";
			e.printStackTrace();
		}
		request.setAttribute("tip", "借阅");
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
