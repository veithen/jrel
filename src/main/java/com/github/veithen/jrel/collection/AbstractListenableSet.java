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

import java.util.AbstractSet;

public abstract class AbstractListenableSet<E> extends AbstractSet<E> implements ListenableSet<E> {
    private final SetListenerList<E> listeners = new SetListenerList<>();

    @Override
    public final void addListener(SetListener<? super E> listener) {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(SetListener<? super E> listener) {
        listeners.remove(listener);
    }

    protected final void fireAdded(E object) {
        listeners.fireAdded(object);
    }

    protected final void fireRemoved(E object) {
        listeners.fireRemoved(object);
    }
}
