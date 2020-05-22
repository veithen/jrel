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

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * <p>
 * Iterators returned by this implementation make additional guarantees:
 * <ul>
 * <li>They will not throw {@link ConcurrentModificationException}.
 * <li>{@link Iterator#next()} will never return an element that is not contained in the set at the
 * time the method is called. That means that elements removed from the set while iterating will not
 * be visited (unless of course they had already been visited before being removed).
 * <li>If {@link Iterator#hasNext()} returns {@code false}, then all elements contained in the set
 * at the time that method is called have been visited. That means that elements added to the set
 * while iterating will be visited.
 * </ul>
 * 
 * @param <T> the type of elements in this set
 */
public final class LinkedIdentityHashSet<T> extends AbstractListenableSet<T> {
    private static class Node {
        private Object element;
        Node previous;
        Node next;

        Node(Object element) {
            this.element = element;
        }

        void removed() {
            element = null;
        }

        boolean isRemoved() {
            return element == null;
        }

        @SuppressWarnings("unchecked")
        <T> T getElement() {
            return (T)element;
        }
    }

    private class It implements Iterator<T> {
        private Node currentNode;

        private Node getNextNode() {
            Node node = currentNode;
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
        public T next() {
            Node nextNode = getNextNode();
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
    private Node[] nodes;
    private Node firstNode;
    private Node lastNode;

    public LinkedIdentityHashSet(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        nodes = new Node[initialCapacity];
    }

    public LinkedIdentityHashSet() {
        this(16, 0.5f);
    }

    @Override
    public boolean add(T object) {
        int capacity = nodes.length;
        if (size + tombstones >= capacity*loadFactor) {
            // We only take into account size here because we will remove the tombstones. Note that
            // this means that we don't necessarily increase the capacity (and just remove the
            // tombstones).
            while (size >= capacity*loadFactor) {
                capacity *= 2;
            }
            Node[] newNodes = new Node[capacity];
            for (Node node : nodes) {
                if (node == null || node.isRemoved()) {
                    continue;
                }
                int hash = System.identityHashCode(node.getElement());
                int newIndex = hash % capacity;
                while (newNodes[newIndex] != null) {
                    newIndex = (newIndex + 1) % capacity;
                }
                newNodes[newIndex] = node;
            }
            tombstones = 0;
            nodes = newNodes;
        }
        int hash = System.identityHashCode(object);
        int index = hash % capacity;
        while (true) {
            Node node = nodes[index];
            if (node == null) {
                break;
            }
            if (node.isRemoved()) {
                tombstones--;
                break;
            }
            if (node.getElement() == object) {
                return false;
            }
            index = (index + 1) % capacity;
        }
        Node node = new Node(object);
        nodes[index] = node;
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

    private void removeElement(Node node) {
        T object = node.getElement();
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
        int hash = System.identityHashCode(object);
        int capacity = nodes.length;
        int index = hash % capacity;
        while (true) {
            Node node = nodes[index];
            if (node == null) {
                return false;
            }
            if (node.getElement() == object) {
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
        int hash = System.identityHashCode(object);
        int capacity = nodes.length;
        int index = hash % capacity;
        while (true) {
            Node node = nodes[index];
            if (node == null) {
                return false;
            }
            if (node.getElement() == object) {
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
        Node[] oldNodes = nodes;
        nodes = new Node[nodes.length];
        size = 0;
        tombstones = 0;
        firstNode = null;
        lastNode = null;
        for (int i=0; i<oldNodes.length; i++) {
            Node node = oldNodes[i];
            if (node != null && !node.isRemoved()) {
                fireRemoved(node.getElement());
                node.removed();
            }
        }
    }
}
