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

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Generic fixed size array. */
final class Array<E> implements Iterable<E> {
    private class It implements Iterator<E> {
        private int index;

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            if (index == array.length) {
                throw new NoSuchElementException();
            }
            return (E) array[index++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final Object[] array;

    Array(int size) {
        array = new Object[size];
    }

    void set(int index, E element) {
        array[index] = element;
    }

    @SuppressWarnings("unchecked")
    E get(int index) {
        return (E) array[index];
    }

    int length() {
        return array.length;
    }

    @Override
    public Iterator<E> iterator() {
        return new It();
    }
}
