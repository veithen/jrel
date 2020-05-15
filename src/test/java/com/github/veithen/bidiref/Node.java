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

public class Node {
    private static final TransitiveRelation<Node> PARENT = new TransitiveRelation<>();

    static {
        PARENT.bind(o -> o.parent);
        PARENT.bindTransitive(o -> o.ancestors);
        PARENT.getReverse().bind(o -> o.children);
        PARENT.getReverse().bindTransitive(o -> o.descendants);
    }

    private final String name;
    public final Reference<Node> parent = PARENT.newReference(this);
    public final TransitiveReferences<Node> ancestors = PARENT.newTransitiveReferences(this);
    public final References<Node> children = PARENT.getReverse().newReferences(this);
    public final TransitiveReferences<Node> descendants = PARENT.getReverse().newTransitiveReferences(this);

    public Node(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
