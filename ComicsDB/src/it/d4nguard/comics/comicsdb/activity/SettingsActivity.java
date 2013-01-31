package it.d4nguard.comics.comicsdb.activity;

import it.d4nguard.comics.comicsdb.R;
import it.d4nguard.comics.comicsdb.data.ComicsDBPreferences;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
{
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen()
	{
		if (!isSimplePreferences(this)) { return; }
		addPreferencesFromResource(R.xml.pref_general);
		bindSettings(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane()
	{
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context)
	{
		return ALWAYS_SIMPLE_PREFS || (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) || !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target)
	{
		if (!isSimplePreferences(this))
		{
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(Preference preference, Object value)
		{
			String stringValue = value.toString();

			if (preference instanceof ListPreference)
			{
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
			}
			else
			{
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			bindSettings(this);
		}

		public static void bindSettings(PreferenceFragment pref)
		{
			bindPref(pref.findPreference(ComicsDBPreferences.USE_WS), Boolean.class, false);
			//		Preference useWs = pref.findPreference(ComicsDBPreferences.USE_WS);
			//		useWs.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
			//		sBindPreferenceSummaryToValueListener.onPreferenceChange(useWs, PreferenceManager.getDefaultSharedPreferences(useWs.getContext()).getBoolean(useWs.getKey(), false));

			bindPref(pref.findPreference(ComicsDBPreferences.SYNC_FREQ), String.class, "360");
			//		Preference syncFreq = pref.findPreference(ComicsDBPreferences.SYNC_FREQ);
			//		syncFreq.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
			//		sBindPreferenceSummaryToValueListener.onPreferenceChange(syncFreq, PreferenceManager.getDefaultSharedPreferences(syncFreq.getContext()).getInt(syncFreq.getKey(), -1));

			bindPref(pref.findPreference(ComicsDBPreferences.HTTP_REQUEST_METHOD), String.class, "1");
			//		Preference reqMethod = pref.findPreference(ComicsDBPreferences.HTTP_REQUEST_METHOD);
			//		reqMethod.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
			//		sBindPreferenceSummaryToValueListener.onPreferenceChange(reqMethod, PreferenceManager.getDefaultSharedPreferences(reqMethod.getContext()).getInt(reqMethod.getKey(), 1));

			bindPref(pref.findPreference(ComicsDBPreferences.BASE_URL), String.class, "");
			//		bindPreferenceSummaryToValue(pref.findPreference(ComicsDBPreferences.BASE_URL));
		}
	}

	@SuppressWarnings("deprecation")
	public static void bindSettings(PreferenceActivity pref)
	{
		bindPref(pref.findPreference(ComicsDBPreferences.USE_WS), Boolean.class, false);
		//		Preference useWs = pref.findPreference(ComicsDBPreferences.USE_WS);
		//		useWs.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		//		sBindPreferenceSummaryToValueListener.onPreferenceChange(useWs, PreferenceManager.getDefaultSharedPreferences(useWs.getContext()).getBoolean(useWs.getKey(), false));

		bindPref(pref.findPreference(ComicsDBPreferences.SYNC_FREQ), String.class, "360");
		//		Preference syncFreq = pref.findPreference(ComicsDBPreferences.SYNC_FREQ);
		//		syncFreq.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		//		sBindPreferenceSummaryToValueListener.onPreferenceChange(syncFreq, PreferenceManager.getDefaultSharedPreferences(syncFreq.getContext()).getInt(syncFreq.getKey(), -1));

		bindPref(pref.findPreference(ComicsDBPreferences.HTTP_REQUEST_METHOD), String.class, "1");
		//		Preference reqMethod = pref.findPreference(ComicsDBPreferences.HTTP_REQUEST_METHOD);
		//		reqMethod.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		//		sBindPreferenceSummaryToValueListener.onPreferenceChange(reqMethod, PreferenceManager.getDefaultSharedPreferences(reqMethod.getContext()).getInt(reqMethod.getKey(), 1));

		bindPref(pref.findPreference(ComicsDBPreferences.BASE_URL), String.class, "");
		//		bindPreferenceSummaryToValue(pref.findPreference(ComicsDBPreferences.BASE_URL));
	}

	public static <T> void bindPref(Preference pref, Class<T> type, T defaultVal)
	{
		pref.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(pref.getContext());
		Object val = null;

		if (type.equals(String.class))
		{
			val = sharedPrefs.getString(pref.getKey(), (String) defaultVal);
		}
		/*
		 * This type can be evaluated only on Honeycomb API and greater.
		 * I cannot make a single method that does only this
		 * so when I pass the app to a better API level I will decomment it.
		 */
		/*
		else if (type.equals(Set.class) && type.getTypeParameters()[0].getClass().equals(String.class))
		{
			val = sharedPrefs.getStringSet(pref.getKey(), (Set<String>) defaultVal);
		}
		*/
		else if (type.equals(Boolean.class))
		{
			val = sharedPrefs.getBoolean(pref.getKey(), (Boolean) defaultVal);
		}
		else if (type.equals(Float.class))
		{
			val = sharedPrefs.getFloat(pref.getKey(), (Float) defaultVal);
		}
		else if (type.equals(Integer.class))
		{
			val = sharedPrefs.getInt(pref.getKey(), (Integer) defaultVal);
		}
		else if (type.equals(Long.class))
		{
			val = sharedPrefs.getLong(pref.getKey(), (Long) defaultVal);
		}

		sBindPreferenceSummaryToValueListener.onPreferenceChange(pref, val);
	}
}
