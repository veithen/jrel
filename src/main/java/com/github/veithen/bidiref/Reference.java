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

import java.util.function.Supplier;

public final class Reference<T> implements ReferenceHolder<T>, Supplier<T> {
    private final ReferenceListenerList<T> listeners = new ReferenceListenerList<>();
    private T target;

    public void addReferenceListener(ReferenceListener<? super T> listener) {
        listeners.add(listener);
    }

    public T get() {
        return target;
    }

    public void set(T target) {
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
        if (target != null) {
            listeners.fireRemoved(target);
            target = null;
        }
    }

    @Override
    public boolean add(T object) {
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
        // TODO: missing listere invocation here
        if (object != target) {
            return false;
        }
        target = null;
        return true;
    }
}
