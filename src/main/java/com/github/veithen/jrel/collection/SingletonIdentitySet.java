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

import java.util.Collections;
import java.util.Iterator;

public final class SingletonIdentitySet<E> extends AbstractListenableSet<E> {
    private E element;

    public void set(E element) {
        if (this.element == element) {
            return;
        }
        clear();
        this.element = element;
        if (element != null) {
            fireAdded(element);
        }
    }

    public E get() {
        return element;
    }

    @Override
    public void clear() {
        if (element != null) {
            E orgElement = element;
            element = null;
            fireRemoved(orgElement);
        }
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("This set does not permit null elements");
        }
        if (this.element == element) {
            return false;
        }
        clear();
        this.element = element;
        fireAdded(element);
        return true;
    }

    @Override
    public boolean remove(Object element) {
        if (element == null || this.element != element) {
            return false;
        }
        E orgElement = this.element;
        this.element = null;
        fireRemoved(orgElement);
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return element == null
                ? Collections.emptyIterator()
                : Collections.singleton(element).iterator();
    }

    @Override
    public boolean contains(Object element) {
        return element != null && this.element == element;
    }

    @Override
    public int size() {
        return element == null ? 0 : 1;
    }
}
