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
package com.github.veithen.jrel.association;

import com.github.veithen.jrel.BinaryRelation;

public abstract class Association<T,U,ReferenceHolder1 extends MutableReferenceHolder<U>,ReferenceHolder2 extends MutableReferenceHolder<T>> extends BinaryRelation<T,U,ReferenceHolder1,ReferenceHolder2> {
    public abstract Association<U,T,ReferenceHolder2,ReferenceHolder1> getConverse();

    final void addListener(MutableReferenceHolder<U> referenceHolder, T owner) {
        AbstractMutableReferenceHolder.validationDisabled.set(true);
        try {
            referenceHolder.addListener(new ConverseAssociationUpdater<T,U>(owner, getConverse(), referenceHolder));
        } finally {
            AbstractMutableReferenceHolder.validationDisabled.set(false);
        }
    }
}
