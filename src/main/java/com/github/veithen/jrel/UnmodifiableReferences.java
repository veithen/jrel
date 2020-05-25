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
package com.github.veithen.jrel;

import java.util.Iterator;

import com.github.veithen.jrel.collection.LinkedIdentityHashSet;
import com.github.veithen.jrel.collection.ListenableSet;
import com.github.veithen.jrel.collection.UnmodifiableListenableSet;

public abstract class UnmodifiableReferences<T> extends References<T> {
    protected final ListenableSet<T> set = new LinkedIdentityHashSet<>();
    private final ListenableSet<T> unmodifiableSet = new UnmodifiableListenableSet<>(set);

    protected UnmodifiableReferences(Object owner) {
        super(owner);
    }

    @Override
    public final ListenableSet<T> asSet() {
        return unmodifiableSet;
    }

    @Override
    public final Iterator<T> iterator() {
        return unmodifiableSet.iterator();
    }

    @Override
    public final boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public final boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public final int size() {
        return set.size();
    }
}
