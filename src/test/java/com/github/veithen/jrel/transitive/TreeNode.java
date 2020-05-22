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

import com.github.veithen.jrel.References;
import com.github.veithen.jrel.association.ManyToOneAssociation;
import com.github.veithen.jrel.association.MutableReference;
import com.github.veithen.jrel.association.MutableReferences;

public class TreeNode {
    private static final ManyToOneAssociation<TreeNode,TreeNode> PARENT = new ManyToOneAssociation<>();
    private static final TransitiveClosure<TreeNode> ANCESTOR = new TransitiveClosure<>(PARENT);

    static {
        PARENT.bind(o -> o.parent, o -> o.children);
        ANCESTOR.bind(o -> o.ancestors, o -> o.descendants);
    }

    private final String name;
    public final MutableReference<TreeNode> parent = PARENT.newReferenceHolder(this);
    public final References<TreeNode> ancestors = ANCESTOR.newReferenceHolder(this);
    public final MutableReferences<TreeNode> children = PARENT.getConverse().newReferenceHolder(this);
    public final References<TreeNode> descendants = ANCESTOR.getConverse().newReferenceHolder(this);

    public TreeNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
