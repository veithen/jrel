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

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Minimal fixed-size mutable array.
 *
 * <p>This class provides constant-time indexed access (via {@link #get(int)} / {@link #set(int,
 * Object)}) and an {@link java.lang.Iterable} view backed by a compact {@code Object[]}.
 *
 * <p>It is intentionally lighter-weight than a full {@link java.util.List} implementation: the size
 * is fixed (no resizing or capacity management), the API surface is intentionally small, and the
 * class avoids exposing callers to unchecked generic-array creation. Compared to {@link
 * java.util.Arrays#asList(Object[])}, this class avoids caller-side casts and keeps the
 * implementation minimal.
 *
 * <p>Keep this class when a tiny, allocation-efficient, fixed-size mutable container is required;
 * replace with a {@code List} only if the full {@code List} API is needed or when interoperability
 * with external APIs is a concern.
 */
@NullMarked
final class Array<E> implements Iterable<@Nullable E> {
    private class It implements Iterator<@Nullable E> {
        private int index;

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable E next() {
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

    private final @Nullable Object[] array;

    Array(int size) {
        array = new Object[size];
    }

    void set(int index, @Nullable E element) {
        array[index] = element;
    }

    @SuppressWarnings("unchecked")
    @Nullable E get(int index) {
        return (E) array[index];
    }

    int length() {
        return array.length;
    }

    @Override
    public Iterator<@Nullable E> iterator() {
        return new It();
    }
}
