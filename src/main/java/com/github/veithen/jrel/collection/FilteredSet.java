/*-
 * #%L
 * jrel
 * %%
 * Copyright (C) 2020 - 2022 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.jrel.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public final class FilteredSet<E> extends AbstractSet<E> {
    private final Set<E> parent;
    private final Predicate<? super E> predicate;

    public FilteredSet(Set<E> parent, Predicate<? super E> predicate) {
        this.parent = parent;
        this.predicate = predicate;
    }

    @Override
    public Iterator<E> iterator() {
        return new FilteredIterator<E>(parent.iterator(), predicate);
    }

    @Override
    public int size() {
        if (parent.isEmpty()) {
            return 0;
        }
        int size = 0;
        for (E element : parent) {
            if (predicate.test(element)) {
                size++;
            }
        }
        return size;
    }
}
