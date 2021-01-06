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

import java.util.List;

final class ReferenceHolderSetAccessor {
    private final List<BoundReferenceHolderAccessor> boundReferenceHolderAccessors;

    ReferenceHolderSetAccessor(List<BoundReferenceHolderAccessor> boundReferenceHolderAccessors) {
        this.boundReferenceHolderAccessors = boundReferenceHolderAccessors;
    }

    ReferenceHolderSet get(Object owner) {
        for (BoundReferenceHolderAccessor accessor : boundReferenceHolderAccessors) {
            ReferenceHolder<?> referenceHolder = accessor.get(owner);
            if (referenceHolder == null) {
                return null;
            }
            ReferenceHolderSet referenceHolderSet = referenceHolder.getReferenceHolderSet();
            if (referenceHolderSet != null) {
                return referenceHolderSet;
            }
        }
        return null;
    }
}
