package it.d4nguard.comics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

public class WebUtils
{
	/**
	 * @param request
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void printRequest(final HttpServletRequest request) throws IOException, ServletException
	{
		System.out.println("--- Start Attributes");
		while (request.getAttributeNames().hasMoreElements())
			System.out.println(request.getAttributeNames().nextElement());
		System.out.println("--- End Attributes");

		System.out.println("--- Start Parameter Map");
		for (final Entry<String, String[]> entry : request.getParameterMap().entrySet())
		{
			System.out.println(entry.getKey());
			for (int i = 0; i < entry.getValue().length; i++)
				System.out.println(entry.getValue()[i]);
			System.out.println("\t---");
		}
		System.out.println("--- End Parameter Map");

		System.out.println("--- Start Parts");
		for (final Part part : request.getParts())
		{
			System.out.println(String.format("%s (%d) : %s", part.getName(), part.getSize(), part.getContentType()));
			for (final String header : part.getHeaderNames())
				System.out.println(header);
			System.out.println("\t---");
		}
		System.out.println("--- End Parts");
	}

	/**
	 * NOT UNIT TESTED Returns the URL (including query parameters) minus the
	 * scheme, host, and
	 * context path. This method probably be moved to a more general purpose
	 * class.
	 */
	public static String getRelativeUrl(final HttpServletRequest request)
	{
		final String baseUrl = getBaseUrl(request);
		final StringBuffer buf = request.getRequestURL();

		if (request.getQueryString() != null)
		{
			buf.append("?");
			buf.append(request.getQueryString());
		}

		return buf.substring(baseUrl.length());
	}

	/**
	 * NOT UNIT TESTED Returns the base url (e.g,
	 * <tt>http://myhost:8080/myapp</tt>) suitable for
	 * using in a base tag or building reliable urls.
	 */
	public static String getBaseUrl(final HttpServletRequest request)
	{
		if (request.getServerPort() == 80 || request.getServerPort() == 443) return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		else return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}

	/**
	 * Returns the file specified by <tt>path</tt> as returned by
	 * <tt>ServletContext.getRealPath()</tt>.
	 */
	public static File getRealFile(final HttpServletRequest request, final String path)
	{
		return new File(request.getSession().getServletContext().getRealPath(path));
	}

	public static String getFilename(final Part part)
	{
		for (final String cd : part.getHeader("content-disposition").split(";"))
			if (cd.trim().startsWith("filename"))
			{
				final String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
			}
		return null;
	}

	/**
	 * header sample
	 * {
	 * Content-Type=[image/png],
	 * Content-Disposition=[form-data; name="file";
	 * filename="filename.extension"]
	 * }
	 **/
	public static String getFileName(final Map<String, List<InputPart>> form, final String paramName)
	{
		String ret = "";
		if (form.containsKey(paramName))
		{
			final List<InputPart> parts = form.get(paramName);
			for (final InputPart inputPart : parts)
				ret = getFileName(inputPart.getHeaders());
		}
		return ret;
	}

	/**
	 * header sample
	 * {
	 * Content-Type=[image/png],
	 * Content-Disposition=[form-data; name="file";
	 * filename="filename.extension"]
	 * }
	 **/
	public static String getFileName(final MultivaluedMap<String, String> header)
	{
		String ret = "";
		final String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
		for (final String filename : contentDisposition)
			if (filename.trim().startsWith("filename"))
			{
				final String[] name = filename.split("=");
				final String finalFileName = name[1].trim().replaceAll("\"", "");
				ret = finalFileName;
			}
		return ret;
	}

	public static <T> T getValue(final Map<String, List<InputPart>> form, final String paramName, final Class<T> returnType, final T defaultVal) throws IOException
	{
		T ret = defaultVal;
		if (form.containsKey(paramName))
		{
			final List<InputPart> parts = form.get(paramName);
			for (final InputPart inputPart : parts)
				ret = inputPart.getBody(returnType, null);
		}
		return ret;
	}
}
