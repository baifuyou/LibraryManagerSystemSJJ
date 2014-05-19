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

@WebServlet("/showMessage")
public class ShowMessage extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserService userService = UserService.newInstance();

	public ShowMessage() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long readerId = (Long) request.getSession().getAttribute("readerId");
		List<Message> messages = null;
		try {
			messages = userService.getMessagesByReaderId(readerId);
			userService.markReaded(readerId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("messages", messages);
		String url = "jsp/messages.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}
}
