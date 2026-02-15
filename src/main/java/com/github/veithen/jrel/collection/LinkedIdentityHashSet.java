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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Iterators returned by this implementation make additional guarantees:
 *
 * <ul>
 *   <li>They will not throw {@link ConcurrentModificationException}.
 *   <li>{@link Iterator#next()} will never return an element that is not contained in the set at
 *       the time the method is called. That means that elements removed from the set while
 *       iterating will not be visited (unless of course they had already been visited before being
 *       removed).
 *   <li>If {@link Iterator#hasNext()} returns {@code false}, then all elements contained in the set
 *       at the time that method is called have been visited. That means that elements added to the
 *       set while iterating will be visited.
 * </ul>
 *
 * @param <E> the type of elements in this set
 */
@NullMarked
public final class LinkedIdentityHashSet<E> extends AbstractListenableSet<E> {
    private static class Node<E> {
        private @Nullable E element;
        @Nullable Node<E> previous;
        @Nullable Node<E> next;

        Node(E element) {
            this.element = element;
        }

        void removed() {
            element = null;
        }

        boolean isRemoved() {
            return element == null;
        }

        E getElement() {
            if (element == null) {
                throw new IllegalStateException("Element has been removed");
            }
            return element;
        }
    }

    private class It implements Iterator<E> {
        private @Nullable Node<E> currentNode;

        private @Nullable Node<E> getNextNode() {
            Node<E> node = currentNode;
            while (node != null && node.isRemoved()) {
                node = node.previous;
            }
            return node == null ? firstNode : node.next;
        }

        @Override
        public boolean hasNext() {
            return getNextNode() != null;
        }

        @Override
        public E next() {
            Node<E> nextNode = getNextNode();
            if (nextNode == null) {
                throw new NoSuchElementException();
            }
            currentNode = nextNode;
            return nextNode.getElement();
        }

        @Override
        public void remove() {
            if (currentNode == null) {
                throw new IllegalStateException();
            }
            if (!currentNode.isRemoved()) {
                removeElement(currentNode);
            }
        }
    }

    private final float loadFactor;
    private int size;
    private int tombstones;
    private Array<Node<E>> nodes;
    private @Nullable Node<E> firstNode;
    private @Nullable Node<E> lastNode;

    public LinkedIdentityHashSet(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        nodes = new Array<>(initialCapacity);
    }

    public LinkedIdentityHashSet() {
        this(16, 0.5f);
    }

    @Override
    public boolean add(E object) {
        int capacity = nodes.length();
        if (size + tombstones >= capacity * loadFactor) {
            // We only take into account size here because we will remove the tombstones. Note that
            // this means that we don't necessarily increase the capacity (and just remove the
            // tombstones).
            while (size >= capacity * loadFactor) {
                capacity *= 2;
            }
            Array<Node<E>> newNodes = new Array<>(capacity);
            for (Node<E> node : nodes) {
                if (node.isRemoved()) {
                    continue;
                }
                int newIndex = System.identityHashCode(node.getElement()) % capacity;
                while (newNodes.get(newIndex) != null) {
                    newIndex = (newIndex + 1) % capacity;
                }
                newNodes.set(newIndex, node);
            }
            tombstones = 0;
            nodes = newNodes;
        }
        int index = System.identityHashCode(object) % capacity;
        int tombstoneIndex = -1;
        while (true) {
            Node<E> node = nodes.get(index);
            if (node == null) {
                break;
            }
            if (node.isRemoved()) {
                // If we encounter a tombstone, remember its index so that we can replace it. Note
                // that we still need to continue because we may find the element later.
                if (tombstoneIndex == -1) {
                    tombstoneIndex = index;
                }
            } else if (node.getElement() == object) {
                return false;
            }
            index = (index + 1) % capacity;
        }
        if (tombstoneIndex != -1) {
            index = tombstoneIndex;
            tombstones--;
        }
        Node<E> node = new Node<>(object);
        nodes.set(index, node);
        if (firstNode == null) {
            firstNode = node;
        }
        if (lastNode != null) {
            node.previous = lastNode;
            lastNode.next = node;
        }
        lastNode = node;
        size++;
        fireAdded(object);
        return true;
    }

    private void removeElement(Node<E> node) {
        E object = node.getElement();
        node.removed();
        if (node.previous != null) {
            node.previous.next = node.next;
        }
        if (node.next != null) {
            node.next.previous = node.previous;
        }
        if (node == firstNode) {
            firstNode = node.next;
        }
        if (node == lastNode) {
            lastNode = node.previous;
        }
        size--;
        tombstones++;
        fireRemoved(object);
    }

    @Override
    public boolean remove(Object object) {
        int capacity = nodes.length();
        int index = System.identityHashCode(object) % capacity;
        while (true) {
            Node<E> node = nodes.get(index);
            if (node == null) {
                return false;
            }
            if (!node.isRemoved() && node.getElement() == object) {
                removeElement(node);
                return true;
            }
            index = (index + 1) % capacity;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object object) {
        int capacity = nodes.length();
        int index = System.identityHashCode(object) % capacity;
        while (true) {
            Node<E> node = nodes.get(index);
            if (node == null) {
                return false;
            }
            if (!node.isRemoved() && node.getElement() == object) {
                return true;
            }
            index = (index + 1) % capacity;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new It();
    }

    @Override
    public void clear() {
        if (size == 0) {
            return;
        }
        Array<Node<E>> oldNodes = nodes;
        nodes = new Array<Node<E>>(nodes.length());
        size = 0;
        tombstones = 0;
        firstNode = null;
        lastNode = null;
        for (int i = 0; i < oldNodes.length(); i++) {
            Node<E> node = oldNodes.get(i);
            if (node != null && !node.isRemoved()) {
                fireRemoved(node.getElement());
                node.removed();
            }
        }
    }
}
