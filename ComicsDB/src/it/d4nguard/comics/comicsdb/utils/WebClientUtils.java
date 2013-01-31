package it.d4nguard.comics.comicsdb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WebClientUtils
{
	public static final String TAG = "it.d4nguard.comics.comicsdb.utils.WebClientUtils";

	// connection timeout, in milliseconds (waiting to connect)
	private static final int CONN_TIMEOUT = 15000;
	// socket timeout, in milliseconds (waiting for data)
	private static final int SOCKET_TIMEOUT = 10000;

	public static final int POST_TASK = 1;
	public static final int GET_TASK = 2;

	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

	public void addNameValuePair(String name, String value)
	{
		params.add(new BasicNameValuePair(name, value));
	}

	// Establish connection and socket (data retrieval) timeouts
	private HttpParams getHttpParams()
	{
		HttpParams htpp = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
		return htpp;
	}

	private HttpResponse doResponse(String url, int taskType)
	{
		// Use our connection and data timeouts as parameters for our
		// DefaultHttpClient
		HttpClient httpclient = new DefaultHttpClient(getHttpParams());
		HttpResponse response = null;
		try
		{
			switch (taskType)
			{
				case POST_TASK:
					HttpPost httppost = new HttpPost(url);
					// Add parameters
					httppost.setEntity(new UrlEncodedFormEntity(params));
					response = httpclient.execute(httppost);
					break;
				case GET_TASK:
					HttpGet httpget = new HttpGet(url);
					response = httpclient.execute(httpget);
					break;
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		return response;
	}

	public static boolean isDeviceConnected(Context ctx)
	{
		ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null) && networkInfo.isConnected();
	}

	public static InputStream retrieveStream(URI url)
	{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getRequest;
		try
		{
			getRequest = new HttpGet(url);
			HttpResponse getResponse = client.execute(getRequest);
			StatusLine status = getResponse.getStatusLine();
			Log.i(TAG, String.valueOf(status.getStatusCode()));
			if (status.getStatusCode() == HttpStatus.SC_OK)
			{
				HttpEntity getResponseEntity = getResponse.getEntity();
				return getResponseEntity.getContent();
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	public static InputStream executeRequestWithHttpClient(URI uri, int taskType)
	{
		InputStream ws_response = null;
		HttpResponse response = new WebClientUtils().doResponse(uri.toASCIIString(), taskType);
		if (response != null)
		{
			try
			{
				ws_response = response.getEntity().getContent();
			}
			catch (IllegalStateException e)
			{
				Log.e(TAG, e.getLocalizedMessage(), e);
			}
			catch (IOException e)
			{
				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}
		else
		{
			Log.w(TAG, "Response from service was empty");
		}
		return ws_response;
	}

	public static InputStream executeRequestWithURLConnection(URI uri)
	{
		InputStream ws_response = null;
		try
		{
			HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
			conn.setReadTimeout(SOCKET_TIMEOUT);
			conn.setConnectTimeout(CONN_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Log.d(TAG, "The response is: " + response);
			ws_response = conn.getInputStream();
		}
		catch (MalformedURLException e)
		{
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		return ws_response;
	}

	public static String inputStreamToString(InputStream is)
	{
		String line = "";
		StringBuilder total = new StringBuilder();
		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try
		{
			// Read response until the end
			while ((line = rd.readLine()) != null)
			{
				total.append(line).append(System.getProperty("line.separator"));
			}
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		// Return full string
		return total.toString();
	}
}
