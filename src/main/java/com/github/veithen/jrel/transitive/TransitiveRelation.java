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
package com.github.veithen.jrel.transitive;

import com.github.veithen.jrel.BinaryRelation;
import com.github.veithen.jrel.association.Association;

public final class TransitiveRelation<T> extends BinaryRelation<T,T,TransitiveReferences<T>,TransitiveReferences<T>> {
    private final Association<T,T,?,?> association;
    private final TransitiveRelation<T> converse;

    TransitiveRelation(Association<T,T,?,?> association, TransitiveRelation<T> converse) {
        this.association = association;
        this.converse = converse;
    }

    public TransitiveRelation(Association<T,T,?,?> association) {
        this.association = association;
        converse = new TransitiveRelation<T>(association.getConverse(), this);
    }

    public Association<T,T,?,?> getAssociation() {
        return association;
    }

    @Override
    public TransitiveRelation<T> getConverse() {
        return converse;
    }

    public TransitiveReferences<T> newReferenceHolder(T owner) {
        return new TransitiveReferences<>(this, owner);
    }
}
