package com.bfyycdi.lms.manager;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/managerValidate")
public class ManagerValidate extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private boolean isRememberMe;
	private ManagerService managerService = ManagerService.newInstance();
	
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setParameter(request, response);
		String validateResult = managerService.validateManager(username, password);
		String tip = "";
		String url = "";
		if (validateResult.equals("success")) {
			if (isRememberMe) {
				managerService.rememberUser(username, password, response);
			} else {
				managerService.forgiveUser(username, password, response);
			}
			url = "managerHome";
			try {
				Long managerId = managerService.getManagerIdByUsername(username);
				request.getSession().setAttribute("readerId", managerId);
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
			managerService.forgiveUser(username, password, response);
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
