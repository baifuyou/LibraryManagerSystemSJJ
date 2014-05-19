<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加图书</title>
</head>
<body>
<a href = "managerHome">返回主面板</a>
<h3>添加图书</h3>
<form action="addBook" method = "POST">
	书名：<input type = "text" name = "bookName"><br>
	isbn:<input type = "text" name = "isbn"><br>
	分类：<input type = "text" name = "category"><br>
	出版社:<input type = "text" name = "press"><br>
	作者：<input type = "text" name = "author"><br>
	数量：<input type = "text" name = "number"><br>
	<button>确认</button>
</form>
</body>
</html>