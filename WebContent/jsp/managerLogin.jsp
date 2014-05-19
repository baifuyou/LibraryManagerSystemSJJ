<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理员登陆</title>
</head>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<base href="<%=basePath%>" />
<body>
<div id = "managerLogin">
	<form action = "managerValidate" method = "POST">
		<%=request.getAttribute("tip") %><br>
		<label>用户名：</label> <input type="text" name="username" value = "${requestScope.username }" /><br>
		<label>密　码：</label> <input type="password" name="password" value = "${requestScope.password }" /><br>
		<c:if test="${requestScope.username == '' || requestScope.password == '' }">
			<input type = "checkbox" name = "loginOption"/><label>7天内记住我</label><br>
		</c:if>
		<c:if test="${requestScope.username != '' && requestScope.password != '' }">
			<input type = "checkbox" name = "loginOption" checked/><label>7天内记住我</label><br>
		</c:if>
		<input type="submit" value="登陆" />
	</form>
</div>
</body>
</html>