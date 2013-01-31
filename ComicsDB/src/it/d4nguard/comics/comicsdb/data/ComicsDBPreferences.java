package it.d4nguard.comics.comicsdb.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ComicsDBPreferences
{
	public static final String BASE_URL = "BaseURL";
	public static final String USE_WS = "UseWS";
	public static final String SYNC_FREQ = "SyncFrequency";
	public static final String HTTP_REQUEST_METHOD = "HttpRequestMethod";

	private final SharedPreferences preferences;

	public ComicsDBPreferences(Context context)
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public SharedPreferences getPreferences()
	{
		return preferences;
	}

	public String getBaseUrl()
	{
		return preferences.getString(BASE_URL, "http://10.0.2.2:8080/ComicsWeb");
	}

	public boolean useWs()
	{
		return preferences.getBoolean(USE_WS, true);
	}

	public String getSyncFrequency()
	{
		return preferences.getString(SYNC_FREQ, "360");
	}

	public String getHttpRequestMethod()
	{
		return preferences.getString(HTTP_REQUEST_METHOD, "1");
	}

	public Toast getDebugToast(Context context, int duration)
	{
		return Toast.makeText(context, toString(), duration);

	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ComicsDBPreferences [getBaseUrl()=");
		builder.append(getBaseUrl());
		builder.append(", useWs()=");
		builder.append(useWs());
		builder.append(", getSyncFrequency()=");
		builder.append(getSyncFrequency());
		builder.append(", getHttpRequestMethod()=");
		builder.append(getHttpRequestMethod());
		builder.append("]");
		return builder.toString();
	}
}
