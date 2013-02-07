<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sync Manager</title>
</head>
<body>
	<form action="../ComicsDB/admin/sync" method="post">
		<input type="checkbox" id="wipedb" name="wipedb" /> Choose to wipe database during sync.<br />
		<input type="submit" id="submit" name="submit" />
	</form>
</body>
</html>