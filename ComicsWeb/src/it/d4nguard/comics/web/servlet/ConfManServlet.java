package it.d4nguard.comics.web.servlet;

import it.d4nguard.comicsimporter.ComicsConfiguration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ConfManServlet
 */
public class ConfManServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static ComicsConfiguration conf = ComicsConfiguration.getInstance();

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		String title = "", message = "";
		conf.setDBConnectionInfo(request.getParameterMap());
		conf.setBaseDir(request.getParameter("comicsdb.basedir"));
		String cmd = request.getParameter("comicsdb.cmd");
		if (cmd == null)
		{
			cmd = "";
		}
		conf.load(cmd.split("\\|"));

		title = "Successful operation: Properties set";
		message = conf.toString();

		request.setAttribute("Title", title);
		request.setAttribute("Message", message);
		request.getRequestDispatcher("/message.jsp").forward(request, response);
	}
}
