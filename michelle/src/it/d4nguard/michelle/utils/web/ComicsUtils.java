package it.d4nguard.michelle.utils.web;

import static it.d4nguard.michelle.utils.GenericsUtils.safeGetter;
import it.d4nguard.comics.beans.Author;
import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.Editor;
import it.d4nguard.comics.beans.Typology;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.michelle.utils.Money;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.data.DataRow;
import it.d4nguard.michelle.utils.data.DataTable;
import it.d4nguard.michelle.utils.data.MoneyJsonDeserializer;
import it.d4nguard.michelle.utils.data.MoneyJsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ComicsUtils
{
	@SuppressWarnings("unchecked")
	public static Comics getComicsFromJson(String json, boolean isArray)
	{
		Comics comics = new Comics();
		if (!StringUtils.isNullOrWhitespace(json))
		{
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Money.class, new MoneyJsonSerializer());
			gsonBuilder.registerTypeAdapter(Money.class, new MoneyJsonDeserializer());
			Gson gson = gsonBuilder.create();
			Type t = null;
			if (isArray)
			{
				t = new TypeToken<ArrayList<Comic>>()
				{
				}.getType();
				comics.addAll((Collection<Comic>) gson.fromJson(json, t));
			}
			else
			{
				t = new TypeToken<Comic>()
				{
				}.getType();
				comics.add((Comic) gson.fromJson(json, t));
			}
		}
		return comics;
	}

	public static DataTable comicsToDataTable(Comics comics)
	{
		DataTable table = new DataTable();
		table.insertColumn("Original Title", String.class);
		table.insertColumn("English Title", String.class);
		table.insertColumn("Artworker", String.class);
		table.insertColumn("Storywriter", String.class);
		table.insertColumn("Original Editor", String.class);
		table.insertColumn("Italian Editor", String.class);
		table.insertColumn("Typology", String.class);
		table.insertColumn("Genres", String.class);
		table.insertColumn("Year", Short.class);
		table.insertColumn("Complete", Boolean.class);
		table.insertColumn("Complete In Country", Boolean.class);
		table.insertColumn("Volumes", Integer.class);

		for (Comic comic : comics)
		{
			DataRow row = table.add("Original Title", comic.getOriginalTitle());
			row.set("English Title", comic.getEnglishTitle());
			row.set("Artworker", safeGetter(comic.getArtworker(), Author.class).toString());
			row.set("Storywriter", safeGetter(comic.getStorywriter(), Author.class).toString());
			row.set("Original Editor", safeGetter(comic.getOriginalEditor(), Editor.class).toString());
			row.set("Italian Editor", safeGetter(comic.getItalianEditor(), Editor.class).toString());
			row.set("Typology", safeGetter(comic.getTypology(), Typology.class).toString());
			row.set("Genres", comic.getGenres().toString());
			row.set("Year", comic.getYear());
			row.set("Complete", comic.isComplete());
			row.set("Complete In Country", comic.isCompleteInCountry());
			row.set("Volumes", comic.getSerie().size());
		}
		return table;
	}
}
