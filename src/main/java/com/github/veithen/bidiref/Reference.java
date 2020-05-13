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

public final class Reference<T> extends ReferenceHolder<T> {
    private T target;

    Reference(ReferenceListener<?,T> listener) {
        super(listener);
    }

    public void set(T target) {
        if (this.target == target) {
            return;
        }
        if (this.target != null) {
            listener.removed(this.target);
        }
        this.target = target;
        if (target != null) {
            listener.added(target);
        }
    }

    @Override
    boolean internalAdd(T object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    boolean internalRemove(T object) {
        // TODO Auto-generated method stub
        return false;
    }
}
