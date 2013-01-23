package it.d4nguard.comics.comicsdb;

import static it.d4nguard.comics.utils.BlankRemover.itrim;
import static it.d4nguard.comics.utils.BlankRemover.lrtrim;
import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.utils.data.BooleanOperatorType;
import it.d4nguard.comics.utils.web.ComicsUtils;
import it.d4nguard.comics.utils.web.WebUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private final LinkedHashMap<String, String> ops = new LinkedHashMap<String, String>();
	private static final String TAG = MainActivity.class.getPackage().getName().concat(".").concat(MainActivity.class.getName());
	private String baseUrl = "";
	private boolean useWs = false;
	private String syncFreq = "360";
	ComicsDBPreferences prefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = new ComicsDBPreferences(getApplicationContext());

		baseUrl = prefs.getBaseUrl();
		useWs = prefs.useWs();
		syncFreq = prefs.getSyncFrequency();

		Toast.makeText(this, String.format("Preferences are: BaseURL: '%s';\n UseWS: %s; \n SyncFrequency: %s", baseUrl, useWs, syncFreq), Toast.LENGTH_LONG).show();

		Spinner operators = (Spinner) findViewById(R.id.sSearchOperator);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		operators.setAdapter(adapter);

		String name = "", text = "";
		String errormsg = getString(R.string.get_id_reflection_denied, getString(R.string.hibernate_restriction_cause));
		try
		{
			for (BooleanOperatorType bot : BooleanOperatorType.values())
			{
				name = bot.name();
				text = getString(R.string.class.getDeclaredField(name).getInt(null));
				adapter.add(text);
				ops.put(text, name);
			}
		}
		catch (IllegalArgumentException e)
		{
			Log.e(TAG, errormsg.concat("'").concat(name).concat("'"), e);
		}
		catch (IllegalAccessException e)
		{
			Log.e(TAG, errormsg.concat("'").concat(name).concat("'"), e);
		}
		catch (NoSuchFieldException e)
		{
			Log.e(TAG, errormsg.concat("'").concat(name).concat("'"), e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			default:
				return super.onMenuItemSelected(featureId, item);
		}
	}

	public void search(View view)
	{
		String field = itrim(lrtrim(((TextView) findViewById(R.id.etSearchField)).getText())).toString();
		String value = itrim(lrtrim(((TextView) findViewById(R.id.etSearchValue)).getText())).toString();
		BooleanOperatorType method = BooleanOperatorType.valueOf(ops.get(((Spinner) findViewById(R.id.sSearchOperator)).getSelectedItem().toString()));

		URL url = null;
		try
		{
			url = new URL(String.format("%s/ComicsDB/comics/%s/%s/%s/", baseUrl, field, method.name(), value));
		}
		catch (MalformedURLException e)
		{
			Log.e(TAG, getString(R.string.malformed_url), e);
		}

		if (!useWs)
		{
			Toast.makeText(this, url.toString(), Toast.LENGTH_LONG).show();
		}
		else if (url != null)
		{
			String ws_response = WebUtils.excuteGet(url.toString());
			Comics comics = ComicsUtils.getComicsFromJson(ws_response);
			for (Comic comic : comics)
			{
				Toast.makeText(this, comic.toString(), Toast.LENGTH_LONG).show();
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{
				}
			}
		}

		Toast.makeText(this, "Search tapped!", Toast.LENGTH_LONG).show();
	}

	public void getAll(View view)
	{
		Toast.makeText(this, "GetAll tapped!", Toast.LENGTH_LONG).show();
	}
}
