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

package it.d4nguard.michelle.hurl.escape;

import it.d4nguard.michelle.hurl.incidental.Arguments;

/**
 * Class for running a sequence of escapers.
 * 
 * @author McDowell
 */
public final class CompositeEscaper implements Escaper
{
	private final Escaper[] escapers;

	public CompositeEscaper(final Escaper... escapers)
	{
		this.escapers = escapers.clone();
		for (final Escaper escaper : escapers)
			Arguments.assertNotNull(escaper);
	}

	/**
	 * Runs the argument through all the escapers in the order they are provided
	 * to the constructor.
	 */
	@Override
	public String escape(final String unescaped)
	{
		String ret = unescaped;
		for (final Escaper escaper : escapers)
			ret = escaper.escape(ret);
		return ret;
	}

	/**
	 * Runs the argument through all the escapers in reverse of the order they
	 * are
	 * provided to the constructor.
	 */
	@Override
	public String unescape(final String escaped)
	{
		String ret = escaped;
		for (int i = escapers.length - 1; i >= 0; i--)
			ret = escapers[i].unescape(ret);
		return ret;
	}
}
