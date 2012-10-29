/*
 * @(#)DynamicEnumerableRepository.java     9 Feb 2009
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
package com.qrmedia.pattern.dynamicenum.persistence;

import java.util.List;

import com.qrmedia.pattern.dynamicenum.DynamicEnumerable;

/**
 * A repository for {@link DynamicEnumerable} objects underlying a set of dynamic 
 * enum constants.
 * 
 * @param <D>	the type of the objects giving rise to the enum values
 * @author anphilli
 * @since 5 Feb 2009
 *
 */
public interface DynamicEnumerableRepository<D extends DynamicEnumerable<?>> {

    /**
     * Loads all the objects underlying the dynamic enumeration.
     * 
     * @return	all the objects underlying the dynamic enumeration
     */
    List<D> loadAll();
}
