package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/login")
public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private String tip;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		setParameter(request, response);
		Cookie[] cookies = request.getCookies();
		String username = "";
		String password = "";
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username")) {
					username = cookie.getValue();
				}
				if (cookie.getName().equals("password")) {
					password = cookie.getValue();
				}
			}
		}
		request.setAttribute("username", username);
		request.setAttribute("password", password);
		request.setAttribute("tip", tip);
		RequestDispatcher rd = request.getRequestDispatcher("jsp/login.jsp");
		rd.forward(request, response);
	}

	private void setParameter(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		tip = request.getParameter("tip");
		if (tip != null) {
			tip = new String(tip.getBytes("ISO-8859-1"));
		} else {
			tip = " ";
		}
	}
}
