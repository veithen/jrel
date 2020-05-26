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

import com.github.veithen.jrel.association.ManyToOneAssociation;
import com.github.veithen.jrel.association.MutableReference;
import com.github.veithen.jrel.transitive.TransitiveClosure;

public class Node {
    private static final ManyToOneAssociation<Node,Node> PARENT = new ManyToOneAssociation<>(Node.class, Node.class, true);
    private static final TransitiveClosure<Node> DESCENDANTS = new TransitiveClosure<>(PARENT.getConverse(), false);

    public final MutableReference<Node> parent = PARENT.newReferenceHolder(this);
    public final References<Node> descendants = DESCENDANTS.newReferenceHolder(this);
}
