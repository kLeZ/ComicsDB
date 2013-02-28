package it.d4nguard.comics;

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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ComicsUtils
{
	public static final Type ComicListType = new TypeToken<ArrayList<Comic>>()
	{
	}.getType();
	public static final Type ComicType = new TypeToken<Comic>()
	{
	}.getType();

	public static Comics getComicsFromJson(final String json, final boolean isArray)
	{
		final Comics comics = new Comics();
		if (!StringUtils.isNullOrWhitespace(json)) comics.addAll(getComicsFromJson(new StringReader(json), isArray));
		return comics;
	}

	public static Comics getComicsFromJson(final Reader json, final boolean isArray)
	{
		final Comics comics = new Comics();
		try
		{
			if (json != null && json.ready())
			{
				final Gson gson = createJsonParser();
				final Collection<Comic> retrieved = fromJson(json, isArray, gson);
				comics.addAll(retrieved);
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		return comics;
	}

	public static Gson createJsonParser()
	{
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Money.class, new MoneyJsonSerializer());
		gsonBuilder.registerTypeAdapter(Money.class, new MoneyJsonDeserializer());
		final Gson gson = gsonBuilder.create();
		return gson;
	}

	public static Collection<Comic> fromJson(final String json, final boolean isArray, final Gson gson)
	{
		return fromJson(new StringReader(json), isArray, gson);
	}

	public static Collection<Comic> fromJson(final Reader json, final boolean isArray, final Gson gson)
	{
		final Collection<Comic> retrieved = new ArrayList<Comic>();
		if (isArray) retrieved.addAll(gson.<Collection<Comic>> fromJson(json, ComicListType));
		else retrieved.add(gson.<Comic> fromJson(json, ComicType));
		return retrieved;
	}

	public static DataTable comicsToDataTable(final Comics comics)
	{
		final DataTable table = new DataTable();
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

		for (final Comic comic : comics)
		{
			final DataRow row = table.add("Original Title", comic.getOriginalTitle());
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
