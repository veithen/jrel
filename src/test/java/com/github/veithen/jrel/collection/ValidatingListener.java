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

import static com.google.common.truth.Truth.assertThat;

public class ValidatingListener implements SetListener<Object> {
    private final ListenableSet<?> set;

    private ValidatingListener(ListenableSet<?> set) {
        this.set = set;
    }

    public static void addTo(ListenableSet<?> set) {
        set.addListener(new ValidatingListener(set));
    }

    @Override
    public void added(Object object) {
        assertThat(set).contains(object);
    }

    @Override
    public void removed(Object object) {
        assertThat(set).doesNotContain(object);
    }
}
