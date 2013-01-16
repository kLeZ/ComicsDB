package it.d4nguard.comics.web.servlet;

import it.d4nguard.comics.web.utils.ComicsUtils;
import it.d4nguard.comics.web.utils.HtmlTable;
import it.d4nguard.comics.web.utils.ServletUtils;
import it.d4nguard.comics.web.utils.WebUtils;
import it.d4nguard.comicsimporter.bo.Comics;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JsonPresenterServlet
 */
@WebServlet("/JsonPresenterServlet")
public class JsonPresenterServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JsonPresenterServlet()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Map<String, String> query = WebUtils.getQueryMap(request.getQueryString());
		if (query.containsKey("q"))
		{
			String ws_response = WebUtils.excuteGet(ServletUtils.getBaseUrl(request).concat(query.get("q")));
			Comics comics = ComicsUtils.getComicsFromJson(ws_response);
			request.setAttribute("Comics", new HtmlTable(ComicsUtils.comicsToDataTable(comics)).render());
			request.getRequestDispatcher("/comics.jsp").forward(request, response);
		}
	}
}
