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

@WebServlet("/searchBooks")
public class SearchBooks extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private UserService userService = UserService.newInstance();

	private final int PAGE_SIZE = 10;

	public SearchBooks() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String keyword = request.getParameter("keyword");
		String searchMethod = request.getParameter("searchMethod");
		int nowPageIndex = Integer.parseInt(request.getParameter("pageIndex"));
		int pageCount = 1;
		if (keyword == null) {
			keyword = (String) request.getSession().getAttribute(
					"keyword");
		} else {
			keyword = new String( keyword.getBytes("8859_1"));
			request.getSession().setAttribute("keyword", keyword);
		}
		
		if (searchMethod.equals("unknown")) {
			searchMethod = (String) request.getSession().getAttribute(
					"searchMethod");
		} else {
			request.getSession().setAttribute("searchMethod", searchMethod);
		}
		try {
			List<Book> books = null;
			if (searchMethod.equals("bookName")) {
				books = userService.searchBooksByName(keyword, nowPageIndex,
						PAGE_SIZE);
				pageCount = userService.getSearchBooksByNamePageCount(keyword,
						PAGE_SIZE);
			} else if (searchMethod.equals("author")){
				books = userService.searchBooksByAuthor(keyword, nowPageIndex,
						PAGE_SIZE);
				pageCount = userService.getSearchBooksByAuthorPageCount(keyword,
						PAGE_SIZE);
			} else if (searchMethod.equals("category")){
				books = userService.getBooksByCategory(keyword, nowPageIndex, PAGE_SIZE);
				pageCount = userService.getBooksByCategoryPageCount(keyword,
						PAGE_SIZE);
			} else if (searchMethod.equals("tops")) {
				books = userService.getTopBooks();
				pageCount = 1;
			}
			List<String> categories = userService.getAllBookCategories();
			request.setAttribute("categories", categories);
			request.setAttribute("pageCount", pageCount);
			request.setAttribute("books", books);
			request.setAttribute("nowPageIndex", nowPageIndex);
			request.setAttribute("pageCount", pageCount);
			RequestDispatcher rd = request
					.getRequestDispatcher("jsp/home_top.jsp");
			rd.forward(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
