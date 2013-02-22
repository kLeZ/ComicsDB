<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sync Manager</title>
<link rel="stylesheet" href="../themes/default/theme.css" />
<script language="javascript" type="text/javascript"
	src="../js/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="../js/general.js"></script>
<script language="javascript" type="text/javascript" src="../js/rest-util.js"></script>
</head>
<body>
	<form id="the_form" action="../ComicsDB/admin/sync" method="post"
		enctype="multipart/form-data" accept-charset="utf-8">
		<p style="padding: 2px; margin: 2px; border: 1px solid black;">
			<span
				style="padding: 2px; margin: 2px; border-bottom: 1px solid black; border-right: 1px solid black;">Choose
				a cache file to use in loading comics...</span><br />
			<input style="padding: 2px; margin: 2px;" type="file" id="cache-file"
				name="cache-file" />
		</p>
		<input type="checkbox" id="openThread" name="openThread" value="true"
			checked="checked" />
		Open a thread or do not call it at all.<br />
		<input type="checkbox" id="syncFeed" name="syncFeed" value="true"
			checked="checked" />
		Synchronize the database with the rss feeds and the news pages provided by the
		configured publishers.<br />
		<input type="checkbox" id="wipedb" name="wipedb" value="true" />
		Choose to wipe database during sync<br />
		<input type="checkbox" id="dryRun" name="dryRun" value="true" />
		Dry run (does not execute the main capabilities of syncer, import from main
		source and sync with feeders)<br />
		<input type="button" id="send" name="send" value="Invia"
			onclick="postRequest('the_form');" />
		<input type="button" id="stop" name="stop" value="Stop"
			onclick="stopPolling('messageStack');" />
	</form>
	<div id="messageStack"></div>
</body>
</html>
