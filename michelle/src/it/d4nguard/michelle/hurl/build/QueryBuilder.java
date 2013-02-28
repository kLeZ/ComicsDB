/*
Copyright (c) 2009 McDowell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package it.d4nguard.michelle.hurl.build;

import it.d4nguard.michelle.hurl.escape.EscapeHolder;
import it.d4nguard.michelle.hurl.escape.Escaper;
import it.d4nguard.michelle.hurl.incidental.Arguments;
import it.d4nguard.michelle.hurl.parts.Delimiter;
import it.d4nguard.michelle.hurl.parts.DelimiterHolder;
import it.d4nguard.michelle.hurl.parts.Param;
import it.d4nguard.michelle.hurl.parts.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Use this type to edit or parse the query part of a URI according to typical
 * web application conventions.
 * Mutable. Not thread safe.
 * 
 * @author McDowell
 */
public class QueryBuilder implements EscapeHolder, DelimiterHolder, Builder<Query>, Parser<QueryBuilder>
{

	private final Escaper escaper;
	private final ArrayList<Param> params = new ArrayList<Param>();
	private final Delimiter delimiter;

	private QueryBuilder(final Escaper escaper, final Delimiter delimiter)
	{
		Arguments.assertNotNull(escaper, delimiter);
		this.escaper = escaper;
		this.delimiter = delimiter;
	}

	@Override
	public Escaper getEscaper()
	{
		return escaper;
	}

	@Override
	public Delimiter getDelimiter()
	{
		return delimiter;
	}

	/**
	 * Constructs a query from the current state.
	 * 
	 * @return a new {@link Query}
	 */
	@Override
	public Query build()
	{
		// Query constructor will fix any escaper mismatches
		return new Query(escaper, delimiter, params);
	}

	/**
	 * Adds a parameter. The escaper of the provided parameter does not need to
	 * match that of the builder.
	 * 
	 * @param param
	 *            a parameter
	 * @return this
	 */
	public QueryBuilder addParam(final Param param)
	{
		params.add(param);
		return this;
	}

	/**
	 * Adds a parameter.
	 * 
	 * @param unescapedKey
	 *            unescaped parameter key
	 * @param unescapedValue
	 *            unescaped parameter value
	 * @return this
	 */
	public QueryBuilder addParam(final String unescapedKey, final String unescapedValue)
	{
		return addParam(new Param(escaper, unescapedKey, unescapedValue));
	}

	/**
	 * Adds a parameter that is just a single value and not a key-value pair.
	 * 
	 * @param unescapedKey
	 *            the unescaped key
	 * @return this
	 */
	public QueryBuilder addParam(final String unescapedKey)
	{
		return addParam(unescapedKey, null);
	}

	/**
	 * Adds all the given parameters. The
	 * {@link it.d4nguard.michelle.hurl.escape.Escaper} used by
	 * the
	 * arguments does NOT have to match that of this builder.
	 * 
	 * @param params
	 *            a list of parameters
	 * @return this
	 */
	public QueryBuilder addParams(final Collection<? extends Param> params)
	{
		final ArrayList<Param> list = this.params;
		list.ensureCapacity(list.size() + params.size());
		list.addAll(params);
		return this;
	}

	/**
	 * @see #addParams(Collection)
	 */
	public QueryBuilder addParams(final Query query)
	{
		return addParams(query.getParams());
	}

	/**
	 * Removes any parameters matching the given key.
	 * 
	 * @param unescapedKey
	 *            the unescaped form of the key
	 * @return this
	 */
	public QueryBuilder removeParams(final String unescapedKey)
	{
		final Iterator<Param> iterator = params.iterator();
		while (iterator.hasNext())
		{
			final Param param = iterator.next();
			if (param.getKey().equals(unescapedKey)) iterator.remove();
		}
		return this;
	}

	/**
	 * Sets a single parameter to the given value. If a parameter with this key
	 * is
	 * not present, this value is appended. If more than one parameter has a
	 * matching key, the other matches are removed.
	 * 
	 * @param paramToSet
	 *            the parameter to set
	 * @return this
	 */
	public QueryBuilder setParam(final Param paramToSet)
	{
		boolean set = false;
		final ListIterator<Param> iterator = params.listIterator();
		while (iterator.hasNext())
		{
			final Param param = iterator.next();
			if (paramToSet.getKey().equals(param.getKey())) if (set) iterator.remove();
			else
			{
				iterator.set(paramToSet);
				set = true;
			}
		}
		if (!set) iterator.add(paramToSet);
		return this;
	}

	/**
	 * @see #setParam(Param)
	 */
	public QueryBuilder setParam(final String unescapedKey, final String unescapedValue)
	{
		return setParam(new Param(escaper, unescapedKey, unescapedValue));
	}

	/**
	 * Sets the state to the given argument. Existing parameters are discarded.
	 * The string must be escaped in a scheme matching that used by the
	 * {@link it.d4nguard.michelle.hurl.escape.Escaper}.
	 * 
	 * @param escapedQuery
	 *            the query part to be parsed
	 * @return this
	 */
	@Override
	public QueryBuilder parse(final String escapedQuery)
	{
		return setQuery(new Query(escaper, delimiter, escapedQuery));
	}

	/**
	 * @see #setQuery(Collection)
	 */
	public QueryBuilder setQuery(final Query query)
	{
		return setQuery(query.getParams());
	}

	/**
	 * Sets the state to the parameters in the given query. Existing parameters
	 * are discarded. The {@link it.d4nguard.michelle.hurl.escape.Escaper} of
	 * the given query does
	 * not
	 * have to match the one used by this builder.
	 * 
	 * @param paramsToSet
	 *            the parameters
	 * @return this
	 */
	public QueryBuilder setQuery(final Collection<? extends Param> paramsToSet)
	{
		params.clear();
		params.ensureCapacity(paramsToSet.size());
		params.addAll(paramsToSet);
		return this;
	}

	/**
	 * @see Query#toString()
	 */
	@Override
	public String toString()
	{
		return build().toString();
	}

	/**
	 * Creates a new builder suitable for working with queries conformant to RFC
	 * 3986 using {@code application/x-www-form-urlencoded} conventions.
	 * 
	 * @return a new builder
	 * @see UrlDefaults#queryEscaper()
	 * @see UrlDefaults#queryDelimiter()
	 */
	public static QueryBuilder create()
	{
		return create(UrlDefaults.queryEscaper(), UrlDefaults.queryDelimiter());
	}

	/**
	 * Creates a new builder that will use the given escaper and delimiter.
	 * This method could be used for working with path parameters, for example.
	 * 
	 * @param escaper
	 *            the escaper
	 * @param delimiter
	 *            an alternative delmiter
	 * @return a new builder
	 */
	public static QueryBuilder create(final Escaper escaper, final Delimiter delimiter)
	{
		return new QueryBuilder(escaper, delimiter);
	}

}
