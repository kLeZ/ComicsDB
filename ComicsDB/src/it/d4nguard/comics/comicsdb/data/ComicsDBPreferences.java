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
		return preferences.getString(BASE_URL, "");
	}

	public boolean useWs()
	{
		return preferences.getBoolean(USE_WS, false);
	}

	public String getSyncFrequency()
	{
		return preferences.getString(SYNC_FREQ, "360");
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
		builder.append("]");
		return builder.toString();
	}
}
