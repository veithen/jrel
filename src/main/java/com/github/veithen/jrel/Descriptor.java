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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

final class Descriptor {
    private final Descriptor parent;
    private final Map<BinaryRelation<?,?,?,?>,ReferenceHolderAccessor> accessorMap = new HashMap<>();

    Descriptor(Descriptor parent, Map<BinaryRelation<?,?,?,?>,Field> fieldMap) {
        this.parent = parent;
        for (Map.Entry<BinaryRelation<?,?,?,?>,Field> entry : fieldMap.entrySet()) {
            accessorMap.put(entry.getKey(), new FieldAccessor(entry.getKey(), entry.getValue()));
        }
        outer: while (true) {
            for (Map.Entry<BinaryRelation<?,?,?,?>,ReferenceHolderAccessor> entry : accessorMap.entrySet()) {
                boolean modified = false;
                // TODO: this is not entirely correct because the dependencies may be relations between unrelated types
                for (BinaryRelation<?,?,?,?> dependency : entry.getKey().getDependencies()) {
                    if (getReferenceHolderAccessor(dependency) == null) {
                        accessorMap.put(dependency, new PiggybackAccessor(entry.getValue(), dependency));
                        modified = true;
                    }
                }
                if (modified) {
                    continue outer;
                }
            }
            break;
        }
    }

    ReferenceHolderAccessor getReferenceHolderAccessor(BinaryRelation<?,?,?,?> relation) {
        ReferenceHolderAccessor accessor = accessorMap.get(relation);
        if (accessor != null) {
            return accessor;
        }
        if (parent == null) {
            return null;
        }
        return parent.getReferenceHolderAccessor(relation);
    }
}
