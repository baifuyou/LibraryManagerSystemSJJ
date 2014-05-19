package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/home")
public class Home extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private UserService userService = UserService.newInstance();

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			List<String> categories = userService.getAllBookCategories();
			List<Book> books = userService.getTopBooks();
			request.setAttribute("categories", categories);
			request.setAttribute("books", books);
			request.setAttribute("nowPageIndex", 1);
			request.setAttribute("pageCount", 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		RequestDispatcher rd = request.getRequestDispatcher("jsp/home_top.jsp");
		rd.forward(request, response);
	}

}
