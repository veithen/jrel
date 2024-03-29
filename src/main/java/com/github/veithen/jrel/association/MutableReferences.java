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
package com.github.veithen.jrel.association;

import java.util.Collection;
import java.util.Iterator;

import com.github.veithen.jrel.References;
import com.github.veithen.jrel.collection.LinkedIdentityHashSet;
import com.github.veithen.jrel.collection.ListenableSet;
import com.github.veithen.jrel.collection.SetListener;

public final class MutableReferences<T> extends References<T> implements ListenableSet<T> {
    private final LinkedIdentityHashSet<T> set = new LinkedIdentityHashSet<T>();

    MutableReferences() {}

    @Override
    public ListenableSet<T> asSet() {
        return this;
    }

    @Override
    public void addListener(SetListener<? super T> listener) {
        set.addListener(listener);
    }

    @Override
    public void removeListener(SetListener<? super T> listener) {
        set.removeListener(listener);
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean add(T object) {
        return set.add(object);
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }

    @Override
    public <V> V[] toArray(V[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean remove(Object object) {
        return set.remove(object);
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean contains(Object object) {
        return set.contains(object);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return set.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public String toString() {
        return set.toString();
    }
}
