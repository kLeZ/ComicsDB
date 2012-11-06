/*
 * @(#)AgentDescriptor.java     9 Feb 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.qrmedia.pattern.dynamicenum.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.qrmedia.pattern.dynamicenum.DynamicEnumerable;

/**
 * A secret agent.
 * 
 * @author anphilli
 * @since 5 Feb 2009
 */
public class AgentDescriptor implements DynamicEnumerable<Integer>
{
	private final int codenumber;

	private final String name;

	private final boolean alive;

	/**
	 * Creates an agent descriptor.
	 * 
	 * @param codenumber
	 *            the agent's codenumber (00 number)
	 * @param name
	 *            the agent's name
	 * @param alive
	 *            <code>true</code> iff the agent is still alive
	 */
	public AgentDescriptor(final int codenumber, final String name, final boolean alive)
	{

		if (name == null) throw new IllegalArgumentException("Name may not be null");

		this.codenumber = codenumber;
		this.name = name;
		this.alive = alive;
	}

	public Integer enumValue()
	{
		return Integer.valueOf(codenumber);
	}

	@Override
	public boolean equals(final Object obj)
	{

		if (obj == this) return true;
		else if (!(obj instanceof AgentDescriptor)) return false;

		final AgentDescriptor other = (AgentDescriptor) obj;
		return new EqualsBuilder().append(codenumber, other.codenumber).append(name, other.name).append(alive, other.alive).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(codenumber).append(name).append(alive).toHashCode();
	}

	public String name()
	{
		return "00" + codenumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}

}
