package it.d4nguard.comics.comicsdb.activity;

import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.comicsdb.R;
import it.d4nguard.comics.comicsdb.data.ComicsDBPreferences;
import it.d4nguard.comics.comicsdb.logic.FetchComicsTask;
import it.d4nguard.comics.comicsdb.utils.WebClientUtils;
import it.d4nguard.michelle.hurl.build.PathBuilder;
import it.d4nguard.michelle.hurl.build.UriBuilder;
import it.d4nguard.michelle.utils.BlankRemover;
import it.d4nguard.michelle.utils.data.BooleanOperatorType;
import it.d4nguard.michelle.utils.web.NetUtils;

import java.net.URI;
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

public class SearchActivity extends Activity
{
	private static final String TAG = SearchActivity.class.getCanonicalName();

	private final LinkedHashMap<String, String> ops = new LinkedHashMap<String, String>();

	private String baseUrl = "";
	private boolean useWs = false;
	private int requestType = 1;

	ComicsDBPreferences prefs = null;

	// Widgets
	private Spinner searchOperator;
	private TextView etSearchField;
	private TextView etSearchValue;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		prefs = new ComicsDBPreferences(getApplicationContext());

		etSearchField = (TextView) findViewById(R.id.etSearchField);
		etSearchValue = (TextView) findViewById(R.id.etSearchValue);
		searchOperator = (Spinner) findViewById(R.id.sSearchOperator);

		baseUrl = prefs.getBaseUrl();
		useWs = prefs.useWs();
		requestType = Integer.valueOf(prefs.getHttpRequestMethod());

		prefs.getDebugToast(this, Toast.LENGTH_LONG).show();

		searchOperator.setAdapter(fillAdapter(android.R.layout.simple_list_item_1));
	}

	private ArrayAdapter<String> fillAdapter(int textViewResourceId)
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, textViewResourceId);
		String name = "", text = "";
		final String hibRestrCause = getString(R.string.hibernate_restriction_cause);
		String errormsg = getString(R.string.get_id_reflection_denied, hibRestrCause);
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
		return adapter;
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
		executeSearch(true);
	}

	public void getAll(View view)
	{
		executeSearch(false);
	}

	public void executeSearch(boolean addParams)
	{
		UriBuilder builder = UriBuilder.create(baseUrl);
		PathBuilder path = PathBuilder.create().parse(builder.getPath(), "/ComicsDB/comics");
		if (addParams)
		{
			path.addElement(getField()).addElement(getMethodName()).addElement(getValue());
		}

		URI uri = builder.setPath(path).build().normalize();

		Log.d(TAG, NetUtils.uriToString(uri));
		Toast.makeText(this, uri.toASCIIString(), Toast.LENGTH_LONG).show();
		if (useWs && (uri.getHost() != null))
		{
			if (WebClientUtils.isDeviceConnected(this))
			{
				FetchComicsTask fetcher = createComicsFetcher();
				fetcher.execute(uri);
			}
			else
			{
				Log.e(TAG, "No network connected!");
				Toast.makeText(getApplicationContext(), "No network connected!", Toast.LENGTH_LONG).show();
			}
		}
	}

	public FetchComicsTask createComicsFetcher()
	{
		final FetchComicsTask fetcher = new FetchComicsTask(WebClientUtils.GET_TASK, requestType, this, this, getString(R.string.processing_message));
		fetcher.setOnProgressUpdateListener(new FetchComicsTask.OnProgressUpdateListener()
		{
			@Override
			public void doProgressUpdate(Integer... values)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("Current set of comics came with %d elements", values[0]));
				sb.append(System.getProperty("line.separator"));
				sb.append(String.format("Total set of comics has %d elements", values[1]));
				sb.append(System.getProperty("line.separator"));
				sb.append(String.format("Current url iteration index is %d", values[2]));
				sb.append(System.getProperty("line.separator"));
				sb.append(String.format("Total url iterations are %d", values[3]));
				sb.append(System.getProperty("line.separator"));

				Toast.makeText(getParent().getCaller(), sb.toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public FetchComicsTask getParent()
			{
				return fetcher;
			}
		});
		fetcher.setOnPostExecuteListener(new FetchComicsTask.OnSomeActionWithResultListener()
		{
			@Override
			public void doSomeActionWithResult(Comics result)
			{
				Toast.makeText(getParent().getCaller(), "Finished!", Toast.LENGTH_LONG).show();
				Intent comicsList = new Intent(getParent().getCaller(), ComicListActivity.class);
				comicsList = comicsList.putExtra(ComicListActivity.COMICS_EXTRA_NAME, result);
				getParent().getCaller().startActivity(comicsList);
			}

			@Override
			public FetchComicsTask getParent()
			{
				return fetcher;
			}
		});
		return fetcher;
	}

	public String getMethodName()
	{
		String selectedOperator = searchOperator.getSelectedItem().toString();
		String methodName = BooleanOperatorType.valueOf(ops.get(selectedOperator)).name();
		return methodName;
	}

	public String getValue()
	{
		String value = BlankRemover.trim(etSearchValue.getText()).toString();
		return value;
	}

	public String getField()
	{
		String field = BlankRemover.trim(etSearchField.getText()).toString();
		return field;
	}
}
