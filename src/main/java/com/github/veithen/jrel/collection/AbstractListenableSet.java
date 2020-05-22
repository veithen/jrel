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

import java.util.AbstractSet;

public abstract class AbstractListenableSet<T> extends AbstractSet<T> implements ListenableSet<T> {
    private final SetListenerList<T> listeners = new SetListenerList<>();

    public final void addListener(SetListener<? super T> listener) {
        listeners.add(listener);
    }

    public final void removeListener(SetListener<? super T> listener) {
        listeners.remove(listener);
    }

    protected final void fireAdded(T object) {
        listeners.fireAdded(object);
    }

    protected final void fireRemoved(T object) {
        listeners.fireRemoved(object);
    }
}
