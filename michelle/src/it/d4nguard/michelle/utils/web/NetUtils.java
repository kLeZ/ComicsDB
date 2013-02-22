/**
 * 
 */
package it.d4nguard.michelle.utils.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author kLeZ-hAcK
 */
public class NetUtils
{
	public static String uriToString(URI uri)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Uri params [");
		sb.append("Scheme: ").append(uri.getScheme()).append(", ");
		sb.append("SchemeSpecificPart: ").append(uri.getSchemeSpecificPart()).append(", ");
		sb.append("Authority: ").append(uri.getAuthority()).append(", ");
		sb.append("UserInfo: ").append(uri.getUserInfo()).append(", ");
		sb.append("Host: ").append(uri.getHost()).append(", ");
		sb.append("Port: ").append(uri.getPort()).append(", ");
		sb.append("Path: ").append(uri.getPath()).append(", ");
		sb.append("Query: ").append(uri.getQuery()).append("]");
		return sb.toString();
	}

	public static Map<String, String> getQueryMap(String query)
	{
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params)
		{
			String[] split = param.split("=");
			String name = split[0];
			String value = split[1];
			map.put(name, value);
		}
		return map;
	}

	public static String excuteGet(String targetURL)
	{
		URL url;
		HttpURLConnection connection = null;
		try
		{
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			//Send request
			connection.connect();

			//Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = rd.readLine()) != null)
			{
				response.append(line).append(System.getProperty("line.separator"));
			}
			rd.close();
			return response.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	public static String excutePost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;
		try
		{
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", Locale.getDefault().toString());

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = rd.readLine()) != null)
			{
				response.append(line).append(System.getProperty("line.separator"));
			}
			rd.close();
			return response.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}
}
