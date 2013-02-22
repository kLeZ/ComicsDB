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
	public static void printRequest(HttpServletRequest request) throws IOException, ServletException
	{
		System.out.println("--- Start Attributes");
		while (request.getAttributeNames().hasMoreElements())
		{
			System.out.println(request.getAttributeNames().nextElement());
		}
		System.out.println("--- End Attributes");

		System.out.println("--- Start Parameter Map");
		for (Entry<String, String[]> entry : request.getParameterMap().entrySet())
		{
			System.out.println(entry.getKey());
			for (int i = 0; i < entry.getValue().length; i++)
			{
				System.out.println(entry.getValue()[i]);
			}
			System.out.println("\t---");
		}
		System.out.println("--- End Parameter Map");

		System.out.println("--- Start Parts");
		for (Part part : request.getParts())
		{
			System.out.println(String.format("%s (%d) : %s", part.getName(), part.getSize(), part.getContentType()));
			for (String header : part.getHeaderNames())
			{
				System.out.println(header);
			}
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
	public static String getRelativeUrl(HttpServletRequest request)
	{
		String baseUrl = getBaseUrl(request);
		StringBuffer buf = request.getRequestURL();

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
	public static String getBaseUrl(HttpServletRequest request)
	{
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
		{
			return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		}
		else
		{
			return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		}
	}

	/**
	 * Returns the file specified by <tt>path</tt> as returned by
	 * <tt>ServletContext.getRealPath()</tt>.
	 */
	public static File getRealFile(HttpServletRequest request, String path)
	{
		return new File(request.getSession().getServletContext().getRealPath(path));
	}

	public static String getFilename(Part part)
	{
		for (String cd : part.getHeader("content-disposition").split(";"))
		{
			if (cd.trim().startsWith("filename"))
			{
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
			}
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
	public static String getFileName(Map<String, List<InputPart>> form, String paramName)
	{
		String ret = "";
		if (form.containsKey(paramName))
		{
			List<InputPart> parts = form.get(paramName);
			for (InputPart inputPart : parts)
			{
				ret = getFileName(inputPart.getHeaders());
			}
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
	public static String getFileName(MultivaluedMap<String, String> header)
	{
		String ret = "";
		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
		for (String filename : contentDisposition)
		{
			if ((filename.trim().startsWith("filename")))
			{
				String[] name = filename.split("=");
				String finalFileName = name[1].trim().replaceAll("\"", "");
				ret = finalFileName;
			}
		}
		return ret;
	}

	public static <T> T getValue(Map<String, List<InputPart>> form, String paramName, Class<T> returnType, T defaultVal) throws IOException
	{
		T ret = defaultVal;
		if (form.containsKey(paramName))
		{
			List<InputPart> parts = form.get(paramName);
			for (InputPart inputPart : parts)
			{
				ret = inputPart.getBody(returnType, null);
			}
		}
		return ret;
	}
}
