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
package com.github.veithen.jrel;

final class UnboundReferenceHolderAccessor extends ReferenceHolderAccessor {
    private final ReferenceHolderSetAccessor referenceHolderSetAccessor;
    private final BinaryRelation<?, ?> relation;

    UnboundReferenceHolderAccessor(
            ReferenceHolderSetAccessor referenceHolderSetAccessor, BinaryRelation<?, ?> relation) {
        this.referenceHolderSetAccessor = referenceHolderSetAccessor;
        this.relation = relation;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    ReferenceHolder<?> get(Object owner) {
        return referenceHolderSetAccessor
                .get(owner)
                .getUnboundReferenceHolder((BinaryRelation) relation, owner);
    }
}
