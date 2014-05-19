<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<base href="<%=basePath%>" />
<title>我的消息</title>
</head>
<body>
	<div class = "body">
		<h4>消息列表</h4>
		<div>
			<ul>
				<c:forEach var = "message" items = "${requestScope.messages }">
					<li>
						<c:if test="${message.readed eq false }">
							${message.addTime }(新):${message.content }
						</c:if>
						<c:if test="${message.readed eq true }">
							${message.addTime }:${message.content }
						</c:if>
						<form action="deleteMessage" method = "POST">
							<input type = "hidden" name = "messageId" value = "${message.messageId }">
							<button>删除</button>
						</form>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>

</body>
</html>