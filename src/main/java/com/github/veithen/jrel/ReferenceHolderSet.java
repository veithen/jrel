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
package com.github.veithen.jrel;

final class ReferenceHolderSet {
    private RelationToReferenceHolderMapping unboundReferenceHolders;

    final <T1, T2> ReferenceHolder<T2> getUnboundReferenceHolder(
            BinaryRelation<T1, T2> relation, T1 owner) {
        if (unboundReferenceHolders == null) {
            unboundReferenceHolders = new RelationToReferenceHolderMapping();
        }
        ReferenceHolder<T2> referenceHolder = unboundReferenceHolders.get(relation);
        if (referenceHolder == null) {
            referenceHolder = relation.newReferenceHolder(owner);
            unboundReferenceHolders.put(relation, referenceHolder);
        }
        return referenceHolder;
    }
}
