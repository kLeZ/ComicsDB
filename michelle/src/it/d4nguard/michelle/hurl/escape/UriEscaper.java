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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Escaper for escaping using standard (e.g. {@code "%A3"}) percentage URI
 * sequences.
 * 
 * @author McDowell
 */
public class UriEscaper implements Escaper
{

	private final Charset encoding;
	private final Pattern unsafeChars;

	/**
	 * Any unsafe characters will be escaped, first by using the given encoding
	 * to
	 * create an encoded byte array and then by escaping the byte values using a
	 * percentage symbol (%) and a two-digit hexadecimal number. The set of
	 * unsafe
	 * characters must include the percentage symbol. It is recommended that the
	 * pattern white-list safe characters and match anything else using the not
	 * (^) pattern.
	 * 
	 * @param encoding
	 *            the character encoding
	 * @param unsafeChars
	 *            matches the set of unsafe characters
	 */
	public UriEscaper(final Charset encoding, final Pattern unsafeChars)
	{
		if (encoding == null || unsafeChars == null) throw new IllegalArgumentException("null");
		this.encoding = encoding;
		this.unsafeChars = unsafeChars;
	}

	@Override
	public String escape(final String unescaped)
	{
		return process(unsafeChars, Escape.INSTANCE, unescaped);
	}

	@Override
	public String unescape(final String escaped)
	{
		return process(Unescape.ESCAPED, Unescape.INSTANCE, escaped);
	}

	private String process(final Pattern pattern, final Processor proc, final String data)
	{
		final Matcher matcher = pattern.matcher(data);
		StringBuilder sb = null;
		int offset = 0;
		while (matcher.find())
		{
			if (sb == null) sb = new StringBuilder();
			sb.append(data.substring(offset, matcher.start()));
			proc.append(sb, encoding, data.substring(matcher.start(), matcher.end()));
			offset = matcher.end();
		}
		if (offset == 0) return data;
		sb.append(data.substring(offset, data.length()));
		return sb.toString();
	}

	private static interface Processor
	{
		public void append(StringBuilder sb, Charset encoding, String data);
	}

	/**
	 * Performs the escaping of a given sequence.
	 */
	private static class Escape implements Processor
	{
		public static Processor INSTANCE = new Escape();

		@Override
		public void append(final StringBuilder sb, final Charset encoding, final String sub)
		{
			try
			{
				final byte[] arr = sub.getBytes(encoding.name());
				for (final byte element : arr)
					sb.append('%').append(high(element)).append(low(element));
			}
			catch (final IOException e)
			{
				throw new IllegalStateException(e);
			}
		}

		private char low(final byte b)
		{
			final int n = b & 0xF;
			if (n < 10) return (char) (0xFFFF & '0' + n);
			else // upper case ALPHA as per RFC 3986 6.2.2.1. Case Normalization
			return (char) (0xFFFF & 'A' + n - 10);
		}

		private char high(byte b)
		{
			b = (byte) (b >> 4);
			return low(b);
		}
	}

	/**
	 * Unescapes the given sequence.
	 */
	private static class Unescape implements Processor
	{
		/** Matches escaped percentage-hex patterns like "%A3" */
		public static final Pattern ESCAPED = Pattern.compile("(%\\p{XDigit}{2})++");

		public static Processor INSTANCE = new Unescape();

		@Override
		public void append(final StringBuilder sb, final Charset encoding, final String data)
		{
			if (data.length() % 3 != 0) throw new IllegalArgumentException(data);
			final int count = data.length() / 3;
			final byte[] encoded = new byte[count];
			for (int i = 0; i < count; i++)
				encoded[i] = escapeToByte(data, i * 3);
			try
			{
				final String unencoded = new String(encoded, encoding.name());
				sb.append(unencoded);
			}
			catch (final IOException e)
			{
				throw new IllegalStateException(e);
			}
		}

		private byte escapeToByte(final String data, int offset)
		{
			final int high = hexToByte(data.charAt(++offset));
			final int low = hexToByte(data.charAt(++offset));
			return (byte) (high << 4 | low);
		}

		private byte hexToByte(final char ch)
		{
			if (ch >= '0' && ch <= '9') return (byte) (ch - '0');
			if (ch >= 'a' && ch <= 'z') return (byte) (ch - 'a' + 10);
			if (ch >= 'A' && ch <= 'Z') return (byte) (ch - 'A' + 10);
			throw new IllegalArgumentException(Character.toString(ch));
		}
	}

	private static class AlphaHolder
	{
		private static final Pattern NOT_ALPHANUM = Pattern.compile("[^\\p{Alnum}]++");
	}

	/**
	 * Pattern for matching anything that isn't an in the range A-Z, a-z or 0-9.
	 */
	public static Pattern getNonAlphanumPattern()
	{
		return AlphaHolder.NOT_ALPHANUM;
	}
}
