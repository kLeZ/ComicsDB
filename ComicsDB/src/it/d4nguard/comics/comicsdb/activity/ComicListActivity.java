package it.d4nguard.comics.comicsdb.activity;

import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ComicListActivity extends ListActivity
{
	private static final int LIST_ITEM_VIEW = android.R.layout.simple_list_item_1;
	public static final String COMICS_EXTRA_NAME = "it.d4nguard.comics.comicsdb.Comics";

	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Comics comics = (Comics) getIntent().getSerializableExtra(COMICS_EXTRA_NAME);
		if (comics == null)
		{
			comics = new Comics();
		}
		List<String> comicTitles = new ArrayList<String>();
		for (Comic comic : comics)
		{
			comicTitles.add(comic.getTitle());
		}
		adapter = new ArrayAdapter<String>(this, LIST_ITEM_VIEW, comicTitles);
		setListAdapter(adapter);
	}
}
