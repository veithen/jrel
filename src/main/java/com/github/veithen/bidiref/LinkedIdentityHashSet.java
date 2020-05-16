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
package com.github.veithen.bidiref;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class LinkedIdentityHashSet<T> extends AbstractSet<T> implements ListenableCollection<T>, SnapshotableCollection<T> {
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
        }
    }

    private final static Object TOMBSTONE = new Object();

    private final ListenableCollectionSupport<T> listeners = new ListenableCollectionSupport<>();
    private final float loadFactor;
    private int size;
    private int tombstones;
    private Object[] elements;
    private int[] prevIndexes;
    private int[] nextIndexes;
    private int firstIndex = -1;
    private int lastIndex = -1;

    public LinkedIdentityHashSet(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        elements = new Object[initialCapacity];
        prevIndexes = new int[initialCapacity];
        Arrays.fill(prevIndexes, -1);
        nextIndexes = new int[initialCapacity];
        Arrays.fill(nextIndexes, -1);
    }

    public LinkedIdentityHashSet() {
        this(16, 0.5f);
    }

    public void addListener(CollectionListener<? super T> listener) {
        listeners.addListener(listener);
    }

    public void removeListener(CollectionListener<? super T> listener) {
        listeners.removeListener(listener);
    }

    @Override
    public boolean add(T object) {
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
        listeners.fireAdded(object);
        return true;
    }

    private void removeElementAt(int index) {
        T object = (T)elements[index];
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
        listeners.fireRemoved(object);
    }

    @Override
    public boolean remove(Object object) {
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
    public void clear() {
        if (size == 0) {
            return;
        }
        Object[] oldElements = elements;
        elements = new Object[elements.length];
        size = 0;
        tombstones = 0;
        Arrays.fill(elements, null);
        Arrays.fill(prevIndexes, -1);
        Arrays.fill(nextIndexes, -1);
        firstIndex = -1;
        lastIndex = -1;
        for (int i=0; i<oldElements.length; i++) {
            Object element = oldElements[i];
            if (element != null && element != TOMBSTONE) {
                listeners.fireRemoved((T)element);
            }
        }
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
}
