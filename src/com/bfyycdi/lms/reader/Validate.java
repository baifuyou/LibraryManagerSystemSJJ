package com.bfyycdi.lms.reader;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/validate")
public class Validate extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private boolean isRememberMe;
	private UserService userService = UserService.newInstance();
	
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setParameter(request, response);
		String validateResult = userService.validate(username, password);
		String tip = "";
		String url = "";
		if (validateResult.equals("success")) {
			if (isRememberMe) {
				userService.rememberUser(username, password, response);
			} else {
				userService.forgiveUser(username, password, response);
			}
			url = "home";
			try {
				Long readerId = userService.getReaderIdByUsername(username);
				request.getSession().setAttribute("readerId", readerId);
				request.getSession().setAttribute("username", username);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			if (validateResult.equals("userNoExist")) {
				tip = "用户名不存在！";
			} else if (validateResult.equals("passwordError")) {
				tip = "密码错误！";
			}
			url = "login?tip=" + URLEncoder.encode(tip, "UTF-8");
			userService.forgiveUser(username, password, response);
		}
		response.sendRedirect(url);
	}
	
	private void setParameter(HttpServletRequest request, 
			HttpServletResponse reponse) {
		username = request.getParameter("username");
		password = request.getParameter("password");
		String[] loginOption = request.getParameterValues("loginOption");
		if ( loginOption != null) {
			isRememberMe = true;
		} else {
			isRememberMe = false;
		}
	}
}
