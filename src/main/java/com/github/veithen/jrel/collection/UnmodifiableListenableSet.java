/*-
 * #%L
 * jrel
 * %%
 * Copyright (C) 2020 Andreas Veithen
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

import java.util.Collection;
import java.util.Iterator;

public final class UnmodifiableListenableSet<E> implements ListenableSet<E> {
    private ListenableSet<E> parent;

    public UnmodifiableListenableSet(ListenableSet<E> parent) {
        this.parent = parent;
    }

    @Override
    public void addListener(SetListener<? super E> listener) {
        parent.addListener(listener);
    }

    @Override
    public void removeListener(SetListener<? super E> listener) {
        parent.removeListener(listener);
    }

    @Override
    public boolean contains(Object object) {
        return parent.contains(object);
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public Object[] toArray() {
        return parent.toArray();
    }

    @Override
    public <V> V[] toArray(V[] a) {
        return parent.toArray(a);
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> it = parent.iterator();
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                return it.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return parent.containsAll(c);
    }

    @Override
    public String toString() {
        return parent.toString();
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
