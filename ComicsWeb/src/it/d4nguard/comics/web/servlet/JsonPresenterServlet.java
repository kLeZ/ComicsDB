package it.d4nguard.comics.web.servlet;

import it.d4nguard.comics.ComicsUtils;
import it.d4nguard.comics.WebUtils;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.michelle.utils.TimeElapsed;
import it.d4nguard.michelle.utils.web.HtmlTable;
import it.d4nguard.michelle.utils.web.NetUtils;

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
@WebServlet("/JsonPresenter")
public class JsonPresenterServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		final Map<String, String> query = NetUtils.getQueryMap(request.getQueryString());
		if (query.containsKey("q"))
		{
			final TimeElapsed elapsed = new TimeElapsed();

			elapsed.start();
			final String ws_response = NetUtils.excuteGet(WebUtils.getBaseUrl(request).concat(query.get("q")));
			final boolean isArray = query.get("type").equalsIgnoreCase("array");
			final Comics comics = ComicsUtils.getComicsFromJson(ws_response, isArray);
			elapsed.stop();
			final long elapsedTime = elapsed.get();

			request.setAttribute("NanoTiming", elapsedTime);
			request.setAttribute("NanoTimingFormatted", TimeElapsed.formatted("", elapsedTime, true));

			request.setAttribute("Comics", new HtmlTable(ComicsUtils.comicsToDataTable(comics)).render());
			request.setAttribute("TotalComics", comics.size());
			request.getRequestDispatcher("/comics.jsp").forward(request, response);
		}
	}
}
