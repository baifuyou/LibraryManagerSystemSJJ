package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/deleteMessage")
public class DeleteMessage extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static UserService userService = UserService.newInstance();

	public DeleteMessage() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long messageId = Long.parseLong(request.getParameter("messageId"));
		String url = "";
		try {
			userService.deleteMessage(messageId);
			url = "jsp/deleteSuccess.jsp";
		} catch (SQLException e) {
			url = "jsp/deleteFailure.jsp";
			e.printStackTrace();
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
