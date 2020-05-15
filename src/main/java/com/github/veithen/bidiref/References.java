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
import java.util.NoSuchElementException;
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
            if (nextIndex == -1) {
                throw new NoSuchElementException();
            }
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
            Object element = elements[currentIndex];
            removeElementAt(currentIndex);
            currentIndex = -1;
            listener.removed((T)element);
        }
    }

    private final static Object TOMBSTONE = new Object();

    private final float loadFactor;
    private int size;
    private int tombstones;
    private Object[] elements;
    private int[] prevIndexes;
    private int[] nextIndexes;
    private int firstIndex = -1;
    private int lastIndex = -1;

    References(ReferenceListener<?,T> listener, int initialCapacity, float loadFactor) {
        super(listener);
        this.loadFactor = loadFactor;
        elements = new Object[initialCapacity];
        prevIndexes = new int[initialCapacity];
        Arrays.fill(prevIndexes, -1);
        nextIndexes = new int[initialCapacity];
        Arrays.fill(nextIndexes, -1);
    }

    @Override
    boolean internalAdd(T object) {
        int capacity = elements.length;
        if (size + tombstones >= capacity*loadFactor) {
            // We only take into account size here because we will remove the tombstones. Note that
            // this means that we don't necessarily increase the capacity (and just remove the
            // tombstones).
            while (size >= capacity*loadFactor) {
                capacity *= 2;
            }
            Object[] newElements = new Object[capacity];
            int[] indexMap = new int[elements.length];
            for (int oldIndex = 0; oldIndex<elements.length; oldIndex++) {
                Object element = elements[oldIndex];
                if (element == null || element == TOMBSTONE) {
                    continue;
                }
                int hash = System.identityHashCode(element);
                int newIndex = hash % capacity;
                while (newElements[newIndex] != null) {
                    newIndex = (newIndex + 1) % capacity;
                }
                newElements[newIndex] = element;
                indexMap[oldIndex] = newIndex;
            }
            int[] newPrevIndexes = new int[capacity];
            Arrays.fill(newPrevIndexes, -1);
            int[] newNextIndexes = new int[capacity];
            Arrays.fill(newNextIndexes, -1);
            for (int oldIndex = 0; oldIndex<elements.length; oldIndex++) {
                Object element = elements[oldIndex];
                if (element == null || element == TOMBSTONE) {
                    continue;
                }
                if (prevIndexes[oldIndex] != -1) {
                    newPrevIndexes[indexMap[oldIndex]] = indexMap[prevIndexes[oldIndex]];
                }
                if (nextIndexes[oldIndex] != -1) {
                    newNextIndexes[indexMap[oldIndex]] = indexMap[nextIndexes[oldIndex]];
                }
            }
            tombstones = 0;
            elements = newElements;
            prevIndexes = newPrevIndexes;
            nextIndexes = newNextIndexes;
            if (firstIndex != -1) {
                firstIndex = indexMap[firstIndex];
            }
            if (lastIndex != -1) {
                lastIndex = indexMap[lastIndex];
            }
        }
        int hash = System.identityHashCode(object);
        int index = hash % capacity;
        while (true) {
            Object element = elements[index];
            if (element == null) {
                break;
            }
            if (element == TOMBSTONE) {
                tombstones--;
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
        prevIndexes[index] = -1;
        nextIndexes[index] = -1;
        size--;
        tombstones++;
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
        boolean removed = internalRemove((T)object);
        if (removed) {
            listener.removed((T)object);
        }
        return removed;
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

    public Iterable<T> snapshot() {
        final Object[] elements = new Object[this.elements.length];
        System.arraycopy(this.elements, 0, elements, 0, elements.length);
        final int[] nextIndexes = new int[this.nextIndexes.length];
        System.arraycopy(this.nextIndexes, 0, nextIndexes, 0, nextIndexes.length);
        final int firstIndex = this.firstIndex;
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new SnapshotIterator<>(elements, nextIndexes, firstIndex);
            }
        };
    }

    @Override
    public String toString() {
        Iterator<T> it = iterator();
        if (!it.hasNext()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder("[");
        while (true) {
            builder.append(it.next());
            if (!it.hasNext()) {
                builder.append("]");
                return builder.toString();
            }
            builder.append(", ");
        }
    }
}
