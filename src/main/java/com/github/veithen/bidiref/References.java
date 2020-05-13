/*-
 * #%L
 * bidiref
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
package com.github.veithen.bidiref;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class References<T> extends ReferenceHolder<T> implements Set<T> {
    private class It implements Iterator<T> {
        private int currentIndex = -1;
        private int nextIndex = firstIndex;

        @Override
        public boolean hasNext() {
            return nextIndex != -1;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            // TODO: throw exception if hasNext==false
            Object element = elements[nextIndex];
            // TODO: check element != null and throw ConcurrentModificationException
            nextIndex = nextIndexes[currentIndex = nextIndex];
            return (T)element;
        }

        @Override
        public void remove() {
            if (currentIndex == -1) {
                throw new IllegalStateException();
            }
            removeElementAt(currentIndex);
            currentIndex = -1;
            // TODO: call listener
        }
    }

    private final static Object TOMBSTONE = new Object();

    private int size;
    private Object[] elements;
    private int[] prevIndexes;
    private int[] nextIndexes;
    private int firstIndex = -1;
    private int lastIndex = -1;

    References(ReferenceListener<?,T> listener, int initialCapacity) {
        super(listener);
        elements = new Object[initialCapacity];
        prevIndexes = new int[initialCapacity];
        Arrays.fill(prevIndexes, -1);
        nextIndexes = new int[initialCapacity];
        Arrays.fill(nextIndexes, -1);
    }

    @Override
    boolean internalAdd(T object) {
        int hash = System.identityHashCode(object);
        int capacity = elements.length;
        int index = hash % capacity;
        while (true) {
            Object element = elements[index];
            if (element == null) {
                break;
            }
            if (element == object) {
                return false;
            }
            index = (index + 1) % capacity;
        }
        elements[index] = object;
        if (size == 0) {
            firstIndex = index;
        } else {
            nextIndexes[lastIndex] = index;
            prevIndexes[index] = lastIndex;
        }
        lastIndex = index;
        size++;
        return true;
    }

    private void removeElementAt(int index) {
        elements[index] = TOMBSTONE;
        if (nextIndexes[index] == -1) {
            lastIndex = prevIndexes[index];
        } else {
            prevIndexes[nextIndexes[index]] = prevIndexes[index];
        }
        if (prevIndexes[index] == -1) {
            firstIndex = nextIndexes[index];
        } else {
            nextIndexes[prevIndexes[index]] = nextIndexes[index];
        }
        size--;
    }

    @Override
    boolean internalRemove(T object) {
        int hash = System.identityHashCode(object);
        int capacity = elements.length;
        int index = hash % capacity;
        while (true) {
            Object element = elements[index];
            if (element == null) {
                return false;
            }
            if (element == object) {
                break;
            }
            index = (index + 1) % capacity;
        }
        removeElementAt(index);
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object object) {
        int hash = System.identityHashCode(object);
        int capacity = elements.length;
        int index = hash % capacity;
        while (true) {
            Object element = elements[index];
            if (element == null) {
                return false;
            }
            if (element == object) {
                return true;
            }
            index = (index + 1) % capacity;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new It();
    }

    @Override
    public Object[] toArray() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public <U> U[] toArray(U[] a) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T object) {
        boolean added = internalAdd(object);
        if (added) {
            listener.added(object);
        }
        return added;
    }

    @Override
    public boolean remove(Object object) {
        // TODO: avoid cast here
        // TODO: invoke listener
        return internalRemove((T)object);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
