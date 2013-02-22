<%
	TimeElapsed elapsed = new TimeElapsed();
	elapsed.start();
%>
<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="java.util.*"%>
<%@ page import="it.d4nguard.michelle.utils.*"%>
<%@ page import="it.d4nguard.michelle.utils.web.Tablizer"%>
<%@ page import="it.d4nguard.comics.beans.*"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Comics Database</title>
<link rel="stylesheet" href="themes/default/theme.css" />
<script language="javascript" type="text/javascript" src="js/general.js"></script>
<script language="javascript" type="text/javascript" src="js/rest-util.js"></script>
</head>
<body>
	<div id="header">
		<h1>Welcome to the Comics Database web site!</h1>
	</div>
	<div id="col1">
		<p>
			<a href="admin/config-man.jsp">DB Configuration page</a><br />
			<a href="admin/sync-man.jsp">Syncronization page</a>
		</p>
		<ul>
			<li><a href="./JsonPresenter?type=array&q=/ComicsDB/comics">GetAll</a> | Prints an
				xml with all the Comics loaded from database (now 3012)</li>
			<li><a href="javascript:getBy();">GetBy</a><br /> {<input type="radio"
					name="getBy" id="getById" value="id" checked="checked" /> Id: <input
					type="text" id="comic_id" onfocus="selectOnFocus('getById');" />}<br /> {<input
					type="radio" name="getBy" id="getByField" value="field" /> Field:[
				<ul>
					<li>param: <input type="text" id="comic_param"
							onfocus="selectOnFocus('getByField');" /></li>
					<li>method: <select id="method">
							<option value="eq">Equals</option>
							<option value="ne">Not Equals</option>
							<option value="like">Like</option>
							<option value="gt">Greater than</option>
							<option value="ge">Greater than or Equal to</option>
							<option value="lt">Lesser than</option>
							<option value="le">Lesser than or Equal to</option>
							<option value="isNull">Null (don't accept value field)</option>
							<option value="isNotNull">Not Null (don't accept value field)</option>
							<option value="isEmpty">Empty (don't accept value field, only
								for collection fields)</option>
							<option value="isNotEmpty">Not Empty (don't accept value field,
								only for collection fields)</option>
					</select></li>
					<li>value: <input type="text" id="comic_value" />]}
					</li>
				</ul></li>
		</ul>
	</div>
	<div id="col2">
		<p>The "Comics" resource is queryable by several parameters that are part
			of the "Comic" structure represented in the table below:</p>
		<%=new Tablizer<Comic>(Comic.class).render()%>
		<%=new Tablizer<Author>(Author.class).render()%>
		<%=new Tablizer<Editor>(Editor.class).render()%>
		<%=new Tablizer<Genre>(Genre.class).render()%>
		<%=new Tablizer<Typology>(Typology.class).render()%>
		<%=new Tablizer<Volume>(Volume.class).render()%>
	</div>
	<div id="footer">
		<p><%=DateUtils.formatDate(new Date(), "EEEE dd MMMM yyyy HH:mm:ss.SSS zzz")%></p>
		<%
			elapsed.stop();
		%>
		<%=elapsed.getFormatted("<p>Loading time ").concat("</p>")%>
	</div>
</body>
</html>