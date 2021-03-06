<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="it.d4nguard.comicsimporter.ComicsConfiguration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Configuration Manager</title>
<style type="text/css">
input[type="text"] {
	width: 400px;
}
</style>
</head>
<body>
	<a href="/">HOME</a>
	<form action="ConfMan" method="post">
		<h1>Current Configuration:</h1>
		<p>
			<%=ComicsConfiguration.getInstance().load(new String[] {}).dbInfoToString()%>
		</p>
		<p>
			Type sql database dialect here (in a hibernate format):
			<input type="text" id="hibernate.dialect" name="hibernate.dialect"
				value="org.hibernate.dialect.MySQLDialect" />
			<br />
			Type the class FQ name for the driver class:
			<input type="text" id="hibernate.connection.driver_class"
				name="hibernate.connection.driver_class" value="com.mysql.jdbc.Driver" />
			<br />
			Type the connection url in jdbc protocol form:
			<input type="text" id="hibernate.connection.url"
				name="hibernate.connection.url" value="jdbc:mysql://localhost/comics" />
			<br />
			Type the db username:
			<input type="text" id="hibernate.connection.username"
				name="hibernate.connection.username" />
			<br />
			Type the db password:
			<input type="password" id="hibernate.connection.password"
				name="hibernate.connection.password" />
			<br />
			Type the config base directory (default is ~/app-root/data):
			<input type="text" id="comicsdb.basedir" name="comicsdb.basedir" />
			<br />
			Type the command line options (pipe '|'-separated):
			<input type="text" id="comicsdb.cmd" name="comicsdb.cmd" />
		</p>
		<input type="submit" />
	</form>
</body>
</html>