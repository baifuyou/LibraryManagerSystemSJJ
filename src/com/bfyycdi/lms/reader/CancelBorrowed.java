package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/cancelBorrowed")
public class CancelBorrowed extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserService userService = UserService.newInstance();

	public CancelBorrowed() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long bookId = Long.parseLong(request.getParameter("bookId"));
		long readerId = (Long) request.getSession().getAttribute("readerId");
		String url = "";
		try {
			userService.cancelBorrowed(readerId, bookId);
			url = "jsp/cancelSuccess.jsp";
		} catch (SQLException e) {
			url = "jsp/cancelFailure.jsp";
			e.printStackTrace();
		}
		request.setAttribute("tip", "取消");
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
