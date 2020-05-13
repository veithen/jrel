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

final class ReferenceListener<T,U> {
    private final T owner;
    private final Relation<U,T> reverseRelation;

    ReferenceListener(T owner, Relation<U,T> reverseRelation) {
        this.owner = owner;
        this.reverseRelation = reverseRelation;
    }

    void added(U object) {
        reverseRelation.getter().apply(object).internalAdd(owner);
    }

    void removed(U object) {
        reverseRelation.getter().apply(object).internalRemove(owner);
    }
}
