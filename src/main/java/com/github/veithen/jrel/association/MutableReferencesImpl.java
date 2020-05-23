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
package com.github.veithen.jrel.association;

import java.util.Collection;
import java.util.Iterator;

import com.github.veithen.jrel.collection.SetListener;
import com.github.veithen.jrel.DomainObject;
import com.github.veithen.jrel.collection.LinkedIdentityHashSet;
import com.github.veithen.jrel.collection.ListenableSet;

final class MutableReferencesImpl<T extends DomainObject,U extends DomainObject> implements MutableReferences<U> {
    private final LinkedIdentityHashSet<U> set = new LinkedIdentityHashSet<U>();

    @Override
    public ListenableSet<U> asSet() {
        return this;
    }

    public void addListener(SetListener<? super U> listener) {
        set.addListener(listener);
    }

    public void removeListener(SetListener<? super U> listener) {
        set.removeListener(listener);
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean add(U object) {
        return set.add(object);
    }

    public Object[] toArray() {
        return set.toArray();
    }

    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c);
    }

    public <V> V[] toArray(V[] a) {
        return set.toArray(a);
    }

    public boolean remove(Object object) {
        return set.remove(object);
    }

    public int size() {
        return set.size();
    }

    public boolean contains(Object object) {
        return set.contains(object);
    }

    public Iterator<U> iterator() {
        return set.iterator();
    }

    public void clear() {
        set.clear();
    }

    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    public boolean addAll(Collection<? extends U> c) {
        return set.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    public String toString() {
        return set.toString();
    }
}
