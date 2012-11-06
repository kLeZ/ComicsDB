/*
 * @(#)StaticResourceBackedDynamicEnumTest.java     9 Feb 2009
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
package com.qrmedia.pattern.dynamicenum;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.qrmedia.pattern.dynamicenum.domain.AgentDescriptor;
import com.qrmedia.pattern.dynamicenum.persistence.DynamicEnumerableRepository;

/**
 * Unit tests for the {@link StaticResourceBackedDynamicEnum}.
 * 
 * @author anphilli
 * @since 5 Feb 2009
 */
@SuppressWarnings("unchecked")
public class StaticResourceBackedDynamicEnumTest
{
	private StaticResourceBackedDynamicEnum<Integer, AgentDescriptor> agents;

	private final DynamicEnumerableRepository<AgentDescriptor> agentDescriptorRepository = createMock(DynamicEnumerableRepository.class);

	private final AgentDescriptor agent2Descriptor = new AgentDescriptor(7, "James Bond", true);
	private final List<AgentDescriptor> agentDescriptors = Arrays.asList(new AgentDescriptor(2, "Bill Fairbanks", false), agent2Descriptor, new AgentDescriptor(9, "Peter Smith", false));

	@Test
	public void backingValueOf()
	{
		assertEquals(agent2Descriptor, agents.backingValueOf(Integer.valueOf(7)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void backingValueOf_nonexistent()
	{
		agents.backingValueOf(Integer.valueOf(1));
	}

	@Test(expected = NullPointerException.class)
	public void backingValueOf_null()
	{
		agents.backingValueOf(null);
	}

	@Test
	public void compare_equals()
	{
		assertTrue(agents.compare(Integer.valueOf(7), Integer.valueOf(7)) == 0);
	}

	@Test(expected = ClassCastException.class)
	public void compare_fromNonexistent()
	{
		agents.compare(Integer.valueOf(1), Integer.valueOf(7));
	}

	@Test(expected = NullPointerException.class)
	public void compare_fromNull()
	{
		agents.compare(null, Integer.valueOf(7));
	}

	@Test
	public void compare_greaterThan()
	{
		assertTrue(agents.compare(Integer.valueOf(9), Integer.valueOf(7)) > 0);
	}

	@Test
	public void compare_lessThan()
	{
		assertTrue(agents.compare(Integer.valueOf(2), Integer.valueOf(7)) < 0);
	}

	@Test(expected = ClassCastException.class)
	public void compare_toNonexistent()
	{
		agents.compare(Integer.valueOf(2), Integer.valueOf(8));
	}

	@Test(expected = NullPointerException.class)
	public void compare_toNull()
	{
		agents.compare(Integer.valueOf(2), null);
	}

	@Test(expected = AssertionError.class)
	public void construct_duplicateEnumValues()
	{

		// run initialization again, returning invalid values 
		reset(agentDescriptorRepository);
		agentDescriptorRepository.loadAll();
		expectLastCall().andReturn(Arrays.asList(new AgentDescriptor(7, "James Bond", true), new AgentDescriptor(7, "James Bond", true)));
		replay(agentDescriptorRepository);

		agents = new StaticResourceBackedDynamicEnum<Integer, AgentDescriptor>(agentDescriptorRepository);
	}

	@Test
	public void exists()
	{
		assertTrue(agents.exists(2));
	}

	@Test
	public void exists_nonexistent()
	{
		assertFalse(agents.exists(8));
	}

	@Test
	public void exists_null()
	{
		assertFalse(agents.exists(null));
	}

	@Test
	public void ordinal()
	{

		// enum starts at 0
		assertEquals(1, agents.ordinal(Integer.valueOf(7)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void ordinal_nonexistent()
	{
		agents.ordinal(Integer.valueOf(8));
	}

	@Test(expected = NullPointerException.class)
	public void ordinal_null()
	{
		agents.ordinal(null);
	}

	@Before
	public void prepareFixture()
	{

		// this call will be made while the object is being initialized
		agentDescriptorRepository.loadAll();
		expectLastCall().andReturn(agentDescriptors);
		replay(agentDescriptorRepository);

		agents = new StaticResourceBackedDynamicEnum<Integer, AgentDescriptor>(agentDescriptorRepository);
	}

	@Test
	public void range()
	{
		assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(Integer.valueOf(7), Integer.valueOf(9)), agents.range(Integer.valueOf(7), Integer.valueOf(9))));
	}

	@Test(expected = IllegalArgumentException.class)
	public void range_fromNonexistent()
	{
		agents.range(Integer.valueOf(1), Integer.valueOf(7));
	}

	@Test(expected = NullPointerException.class)
	public void range_fromNull()
	{
		agents.range(null, Integer.valueOf(7));
	}

	@Test(expected = IllegalArgumentException.class)
	public void range_invalidEndpoints()
	{
		agents.range(Integer.valueOf(7), Integer.valueOf(2));
	}

	@Test
	public void range_singleton()
	{
		assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(Integer.valueOf(2)), agents.range(Integer.valueOf(2), Integer.valueOf(2))));
	}

	@Test(expected = IllegalArgumentException.class)
	public void range_toNonexistent()
	{
		agents.range(Integer.valueOf(2), Integer.valueOf(8));
	}

	@Test(expected = NullPointerException.class)
	public void range_toNull()
	{
		agents.range(Integer.valueOf(2), null);
	}

	@Test
	public void valueOf()
	{
		assertEquals(Integer.valueOf(7), agents.valueOf("007"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void valueOf_nonexistent()
	{
		agents.valueOf("non-existent");
	}

	@Test(expected = NullPointerException.class)
	public void valueOf_null()
	{
		agents.valueOf(null);
	}

	@Test
	public void values()
	{
		assertEquals(Arrays.asList(Integer.valueOf(2), Integer.valueOf(7), Integer.valueOf(9)), agents.values());
	}

}
