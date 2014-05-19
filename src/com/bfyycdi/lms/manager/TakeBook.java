package com.bfyycdi.lms.manager;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/takeBook")
public class TakeBook extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final ManagerService managerService = ManagerService
			.newInstance();

	public TakeBook() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long borrowedId = Long.parseLong(request.getParameter("borrowedId"));
		String result = null;
		try {
			managerService.takeBook(borrowedId);
			result = "成功";
		} catch (SQLException e) {
			result = "失败";
			e.printStackTrace();
		}
		String url = "jsp/takeBookResult.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(url);
		request.setAttribute("result", result);
		rd.forward(request, response);
	}

}
