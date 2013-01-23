package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;

import java.io.IOException;
import java.util.List;

public interface ComicsSourceParser
{
	public abstract List<Comic> parse(final Comics comics) throws IOException;

	public abstract String getUrl();

	public abstract void setUrl(String url);

	public abstract String getConfigFileName();

	public abstract void setConfigFileName(String configFileName);
}
