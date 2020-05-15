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

public final class Reference<T> extends ReferenceHolder<T> implements Supplier<T> {
    private T target;

    Reference(ReverseRelationUpdater<?,T> listener) {
        super(listener);
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
            listener.added(target);
        }
    }

    public void clear() {
        if (target != null) {
            listener.removed(target);
            target = null;
        }
    }

    @Override
    boolean internalAdd(T object) {
        if (object == target) {
            return false;
        }
        clear();
        target = object;
        return true;
    }

    @Override
    boolean internalRemove(T object) {
        if (object != target) {
            return false;
        }
        target = null;
        return true;
    }
}
