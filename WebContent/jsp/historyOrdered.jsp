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
<link type = "text/css" rel = "stylesheet" href = css/home.css>
<title>借阅历史</title>
</head>
<body>
<div class = "body">
	<div class = topbar>
		<a href = "home">我的首页</a>
	</div>
	<div class = "sheet">
		<p>
			<a href = "history?label=reading">在读</a>
			<a href = "history?label=noTake">未取</a>
			<a href = "history?label=ordered">预订</a>
			<a href = "history?label=returned">已还</a>
		</p>
	</div>
	<div class = "list">
		<table>
			<th>书名</th><th>作者</th><th>出版社</th><th>分类</th>
			<c:forEach var = "book" items = "${requestScope.books }" varStatus = "status">
				<c:if test="${status.index%2 eq 1 }">
					<tr id="oddTr">
				</c:if>
				<c:if test="${status.index%2 eq 0 }">
					<tr id = "evenTr">
				</c:if>
						<td>${book.bookName }</td>
						<td>${book.author }</td>
						<td>${book.press }</td>
						<td>${book.bookType }</td>
						<td>
							<form action="cancelOrdered">
								<input type = "hidden" name = "bookId" value = "${book.bookId }">
								<button>取消预订</button>
							</form>
						</td>
					</tr>
			</c:forEach>
		</table>
	</div>
</div>
</body>
</html>