<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理员界面</title>
</head>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<base href="<%=basePath%>" />
<body>
	<div class = "body">
		<a href = "requestAddBook">添加图书</a>
		<div id = "searchBar">
			<form action="searchBookItem">
				<input type = "text" name = "keyword">
				<button name = "searchMethod" value = "reader">搜读者</button>
				<button name = "searchMethod" value = "bookItem">搜图书编号</button>
			</form>
		</div>
		<div class = "list">
			<table>
				<th>书名</th><th>条目编号</th><th>读者名</th><th>被借日期</th><th>罚款</th><th>操作</th>
				<c:forEach var = "bookItem" items = "${requestScope.bookItems }" varStatus = "status">
					<c:if test = "${status.index%2 eq 1 }">
						<tr id = "oddTr">
					</c:if>
					<c:if test="${status.index%2 eq 0 }">
						<tr id = "evenTr">
					</c:if>
					<td>${bookItem.bookName }</td>
					<td>${bookItem.bookItemId }</td>
					<td>${bookItem.readerName }</td>
					<td>${bookItem.borrowedDate }</td>
					<td>${bookItem.penalty/10.0 }</td>
					<c:if test="${bookItem.state eq '未取' }">
						<td>
							<form action = "takeBook" method = "POST">
								<input type = "hidden" name = "borrowedId" value = "${bookItem.borrowedId }">
								<button>取书</button>
							</form>
						</td>
					</c:if>
					<c:if test="${bookItem.state eq '未还' }">
						<td>
							<form action = "returnBook" method = "POST">
								<input type = "hidden" name = "borrowedId" value = "${bookItem.borrowedId }">
								<input type = "hidden" name = "bookItemId" value = "${bookItem.bookItemId }">
								<button>还书</button>
							</form>
						</td>
					</c:if>
					</tr>
				</c:forEach>	
			</table>
		</div>
	</div>
</body>
</html>