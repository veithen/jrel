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

import java.util.Iterator;
import java.util.NoSuchElementException;

final class SnapshotIterator<T> implements Iterator<T> {
    private final Object[] elements;
    private final int[] nextIndexes;
    private int nextIndex;

    SnapshotIterator(Object[] elements, int[] nextIndexes, int firstIndex) {
        this.elements = elements;
        this.nextIndexes = nextIndexes;
        nextIndex = firstIndex;
    }

    @Override
    public boolean hasNext() {
        return nextIndex != -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T next() {
        if (nextIndex == -1) {
            throw new NoSuchElementException();
        }
        Object element = elements[nextIndex];
        nextIndex = nextIndexes[nextIndex];
        return (T)element;
    }
}
