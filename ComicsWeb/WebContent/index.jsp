<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
	<jsp:directive.page contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8" session="false" />
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
	<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Comics Database</title>
</head>
<body>
	<script type="text/javascript" language="javascript">
	<![CDATA[
		function getBy()
		{
			var radios = document.getElementsByName('getBy');
			var value = 'id';
			for (var i=0; i < radios.length; i++)
			{
				if (radios[i].type === 'radio' && radios[i].checked)
				{
					value = radios[i].value;
					break;
				}
			}
			var url = '';
			switch (value)
			{
			case 'id':
				url = './ComicsDB/comic/' + value + '/' + document.getElementById('comic_' + value).value;
				break;
			case 'field':
				url = './ComicsDB/comic/' + document.getElementById('comic_param').value + '/' + document.getElementById('comic_value').value;
				break;
			}
			if (!confirm(url))
				return false;
			location.href = url;
		}
	]]>
	</script>
	<h1>Welcome to the Comics Database web site!</h1>
	<p>In order to go to the WebService default url follow the link
		below:</p>
	<ul>
		<li><a href="./ComicsDB/">Root</a> | Prints --> "Hello World!"</li>
		<li><a href="./ComicsDB/comics">GetAll</a> | Prints an xml with
			all the Comics loaded from database (now 3012)</li>
		<li><a href="javascript:getBy();">GetBy</a>
		{<input type="radio" name="getBy" value="id" checked="true" />Id: <input type="text" id="comic_id" />} or
		{<input type="radio" name="getBy" value="field" />Field: [param: <input type="text" id="comic_param" />; value: <input type="text" id="comic_value" />]}</li>
	</ul>
</body>
	</html>
</jsp:root>