package it.d4nguard.comics.web.servlet;

import it.d4nguard.comics.persistence.HibernateFactory;
import it.d4nguard.michelle.utils.io.DeepCopy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.cfg.Configuration;

/**
 * Servlet implementation class ConfManServlet
 */
public class ConfManServlet extends HttpServlet
{
	private static Properties DBConnectionInfo;

	public static Properties getDBConnectionInfo()
	{
		if (DBConnectionInfo == null)
		{
			DBConnectionInfo = new Properties();
			HibernateFactory.buildIfNeeded(null, null, null);
			HibernateFactory.closeFactory();
			Configuration configuration = HibernateFactory.getConfiguration();
			for (String prop : ConfiguredProperties)
			{
				DBConnectionInfo.setProperty(prop, configuration.getProperty(prop));
			}
		}
		return DeepCopy.copy(DBConnectionInfo);
	}

	public static String dbInfoToString()
	{
		StringWriter out = new StringWriter();
		PrintWriter pw = new PrintWriter(out);
		getDBConnectionInfo().list(pw);
		return out.toString().replaceAll("\\n", "<br />");
	}

	private static List<String> ConfiguredProperties = new ArrayList<String>();
	static
	{
		ConfiguredProperties.add("hibernate.dialect");
		ConfiguredProperties.add("hibernate.connection.driver_class");
		ConfiguredProperties.add("hibernate.connection.url");
		ConfiguredProperties.add("hibernate.connection.username");
		ConfiguredProperties.add("hibernate.connection.password");
	}

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ServletUtils.printRequest(request);
		String title = "", message = "";

		for (String prop : ConfiguredProperties)
		{
			DBConnectionInfo.setProperty(prop, request.getParameter(prop));
		}

		HibernateFactory.closeFactory();
		title = "Successful operation: Properties set";
		message = dbInfoToString();

		request.setAttribute("Title", title);
		request.setAttribute("Message", message);
		request.getRequestDispatcher("/message.jsp").forward(request, response);
	}
}
