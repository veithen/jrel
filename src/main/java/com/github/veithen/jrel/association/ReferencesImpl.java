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

import com.github.veithen.jrel.collection.CollectionListener;
import com.github.veithen.jrel.collection.LinkedIdentityHashSet;

final class ReferencesImpl<T,U> extends AbstractMutableReferenceHolder<T,U> implements References<U> {
    private final LinkedIdentityHashSet<U> set = new LinkedIdentityHashSet<U>();

    ReferencesImpl(ToManyAssociation<T,U,?> association, T owner) {
        super(association, owner);
    }

    public void addListener(CollectionListener<? super U> listener) {
        validate();
        set.addListener(listener);
    }

    public void removeListener(CollectionListener<? super U> listener) {
        validate();
        set.removeListener(listener);
    }

    public boolean isEmpty() {
        validate();
        return set.isEmpty();
    }

    public boolean add(U object) {
        validate();
        return set.add(object);
    }

    public Object[] toArray() {
        validate();
        return set.toArray();
    }

    public boolean removeAll(Collection<?> c) {
        validate();
        return set.removeAll(c);
    }

    public <V> V[] toArray(V[] a) {
        validate();
        return set.toArray(a);
    }

    public boolean remove(Object object) {
        validate();
        return set.remove(object);
    }

    public int size() {
        validate();
        return set.size();
    }

    public boolean contains(Object object) {
        validate();
        return set.contains(object);
    }

    public Iterator<U> iterator() {
        validate();
        return set.iterator();
    }

    public void clear() {
        validate();
        set.clear();
    }

    public boolean containsAll(Collection<?> c) {
        validate();
        return set.containsAll(c);
    }

    public boolean addAll(Collection<? extends U> c) {
        validate();
        return set.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        validate();
        return set.retainAll(c);
    }

    public String toString() {
        validate();
        return set.toString();
    }
}
