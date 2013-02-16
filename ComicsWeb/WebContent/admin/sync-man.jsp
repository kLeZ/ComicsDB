<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sync Manager</title>
<link rel="stylesheet" href="../themes/default/theme.css" />
<script language="javascript" type="text/javascript" src="../js/general.js"></script>
<script language="javascript" type="text/javascript" src="../js/rest-util.js"></script>
<script type="text/javascript">
	function submit() {
		alert('Form submitted!');
		document.the_form.submit();
		startPolling(document.the_form.attr('action'), 'messageStack', 5000);
		alert('Polling started!');
		return true;
	}
</script>
</head>
<body>
	<form id="the_form" action="../ComicsDB/admin/sync" method="post"
		enctype="application/x-www-form-urlencoded" onsubmit="return false;">
		<input type="checkbox" id="wipedb" name="wipedb" value="true" />
		Choose to wipe database during sync<br />
		<input type="checkbox" id="dryRun" name="dryRun" value="true" />
		Dry run (does not execute the main capabilities of syncer, import from main
		source and sync with feeders)<br />
		<input type="checkbox" id="openThread" name="openThread" value="true" />
		Open a thread or do not call it at all.<br />
		<input type="button" id="submit" name="submit" value="Invia" onclick="return submit();" />
	</form>
	<div id="messageStack"></div>
</body>
</html>
