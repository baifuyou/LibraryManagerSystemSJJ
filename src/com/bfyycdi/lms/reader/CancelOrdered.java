package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/cancelOrdered")
public class CancelOrdered extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private UserService userService = UserService.newInstance();

	public CancelOrdered() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long bookId = Long.parseLong(request.getParameter("bookId"));
		long readerId = (Long) request.getSession().getAttribute("readerId");
		String url = "";
		try {
			userService.cancelOrdered(readerId, bookId);
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
