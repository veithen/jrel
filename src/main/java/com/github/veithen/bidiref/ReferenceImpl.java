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

import java.util.Collections;
import java.util.Iterator;

final class ReferenceImpl<T,U> extends AbstractMutableReferenceHolder<T,U> implements Reference<U> {
    private final ListenableCollectionSupport<U> listeners = new ListenableCollectionSupport<>();
    private U target;

    ReferenceImpl(Relation<T,U> relation, T owner) {
        super(relation, owner);
    }

    public void addListener(CollectionListener<? super U> listener) {
        validate();
        listeners.addListener(listener);
    }

    public void removeListener(CollectionListener<? super U> listener) {
        validate();
        listeners.addListener(listener);
    }

    public U get() {
        validate();
        return target;
    }

    public void set(U target) {
        validate();
        if (this.target == target) {
            return;
        }
        clear();
        this.target = target;
        if (target != null) {
            listeners.fireAdded(target);
        }
    }

    public void clear() {
        validate();
        if (target != null) {
            listeners.fireRemoved(target);
            target = null;
        }
    }

    @Override
    public boolean add(U object) {
        validate();
        if (object == target) {
            return false;
        }
        clear();
        target = object;
        listeners.fireAdded(object);
        return true;
    }

    @Override
    public boolean remove(Object object) {
        validate();
        // TODO: missing listere invocation here
        if (object != target) {
            return false;
        }
        target = null;
        return true;
    }

    @Override
    public Iterator<U> iterator() {
        return target == null ? Collections.emptyIterator() : Collections.singleton(target).iterator();
    }

    @Override
    public boolean contains(Object object) {
        return object != null && target == object;
    }
}
