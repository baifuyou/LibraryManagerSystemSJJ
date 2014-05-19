package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/orderBook")
public class OrderBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserService userService = UserService.newInstance();

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long bookId = Long.parseLong(request.getParameter("bookId"));
		long readerId = (Long) request.getSession().getAttribute("readerId");
		String url = "";
		try {
			userService.orderBook(bookId, readerId);
			url = "jsp/success.jsp";
		} catch (SQLException e) {
			e.printStackTrace();
			url = "jsp/success.jsp";
		}
		request.setAttribute("tpi", "预订");
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
