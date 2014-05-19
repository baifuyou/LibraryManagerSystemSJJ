package com.bfyycdi.lms.manager;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.bfyycdi.lms.reader.NoAvailableBookException;

@WebServlet("/returnBook")
public class ReturnBook extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final ManagerService managerService = ManagerService.newInstance();
	
    public ReturnBook() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long borrowedId = Long.parseLong(request.getParameter("borrowedId"));
		long bookItemId = Long.parseLong(request.getParameter("bookItemId"));
		String result = null;
		try {
			managerService.returnBook(borrowedId, bookItemId);
			result = "成功";
		} catch (SQLException e) {
			result = "失败";
			e.printStackTrace();
		}
		if (result.equals("成功")) {
			try {
				managerService.notifyOrderIfExist(bookItemId);
			} catch (SQLException | NoAvailableBookException e) {
				e.printStackTrace();
			}
		}
		String url = "jsp/returnBookResult.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(url);
		request.setAttribute("result", result);
		rd.forward(request, response);
	}

}
