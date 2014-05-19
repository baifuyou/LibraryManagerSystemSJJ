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
<title>欢迎使用小白的图书馆管理系统</title>
</head>
<body>
	<div class="body">
		<div class="topbar">
			<p>欢迎您，${sessionScope.username }! <a href = "showMessage">我的消息</a>	<a href = "history?label=reading">我的订阅</a></p>
		</div>
		<div class="search">
		<form action="searchBooks" method = "GET">
			<p>
				<input type="text" name="keyword" />
				<input type="hidden" name = "pageIndex" value = "1">
				<button name = "searchMethod" value = "bookName">搜书名</button>
				<button name = "searchMethod" value = "author">搜作者</button>
			</p>
		</form>
		</div>
		<div class="sidebar">
			<h3>分类浏览</h3>
			<ul>
				<li><a href = "searchBooks?searchMethod=tops&keyword=noword&pageIndex=1">TOP榜单</a>
				<c:forEach var = "category" items = "${requestScope.categories }">
					<li><a href = "searchBooks?keyword=${category }&searchMethod=category&pageIndex=1">${category }</a></li>
				</c:forEach>
			</ul>
		</div>
		<div class="list">
			<table>
				<th>书名</th><th>作者</th><th>出版社</th><th>分类</th><th>累计借阅</th><th>在库</th><th>操作</th>
				<c:forEach var = "book" items = "${requestScope.books }" varStatus = "status">
					<c:if test="${status.index%2 eq 0 }">
						<tr id = oddTr>
					</c:if>
					<c:if test="${status.index%2 eq 1 }">
						<tr id = evenTr>
					</c:if>
						<td>${book.bookName }</td>
						<td>${book.author }</td>
						<td>${book.press }</td>
						<td>${book.bookType }
						<td>${book.borrowed_number }
						<td>${book.margin }
						<c:if test = "${book.margin < 1 }">
							<td>
								<form action = "orderBook" method = "POST">
									<input type = "hidden" name = "bookId" value = "${book.bookId }">
									<button>预订</button>
								</form>
							</td>
						</c:if>
						<c:if test = "${book.margin > 0 }">
							<td>
								<form action = "borrowBook" method = "POST">
									<input type = "hidden" name = "bookId" value = "${book.bookId }">
									<button>借阅</button>
								</form>
							</td>
						</c:if>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div class = "pageIndex">
		<p>
			<c:forEach var = "index" begin = "${requestScope.nowPageIndex - 3 < 1?1:requestScope.nowPageIndex - 3}" end = "${requestScope.nowPageIndex + 3 > requestScope.pageCount?requestScope.pageCount:requestScope.nowPageIndex + 3 }" step = "1">
				<c:if test = "${index eq requestScope.nowPageIndex }">
					<form action="searchBooks">
						<input type="hidden" name = "pageIndex" value = "${index }">
						<input type="hidden" name = "searchMethod" value = "unknown">
						<button id = "selected">${index }</button>
					</form>
				</c:if>
				<c:if test="${index ne requestScope.nowPageIndex }">
					<form action="searchBooks">
						<input type="hidden" name = "pageIndex" value = "${index }">
						<input type="hidden" name = "searchMethod" value = "unknown">
						<button id = "notSelected">${index }</button>
					</form>
				</c:if>
			</c:forEach>
		</p>
		</div>
	</div>
</body>
</html>