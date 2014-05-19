package com.bfyycdi.lms.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/searchBookItem")
public class SearchBookItem extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final ManagerService managerService = ManagerService.newInstance();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String keyword = request.getParameter("keyword");
		String searchMethod = request.getParameter("searchMethod");
		List<ExtendedBookItem> bookItems = null;
		try {
			if (searchMethod.equals("bookItem")) {
				bookItems = managerService.searchBookItemsByBookItemId(Long
						.parseLong(keyword));
			} else if (searchMethod.equals("reader")) {
				bookItems = managerService.searchBookItemsByUsername(keyword);
			}
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("bookItems", bookItems);
		String url = "jsp/managerHome.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}
}
