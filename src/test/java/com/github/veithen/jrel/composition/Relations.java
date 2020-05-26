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
package com.github.veithen.jrel.composition;

import com.github.veithen.jrel.association.ManyToManyAssociation;

public class Relations {
    public static final ManyToManyAssociation<A,B> AB = new ManyToManyAssociation<>(A.class, B.class, false);
    public static final ManyToManyAssociation<B,C> BC = new ManyToManyAssociation<>(B.class, C.class, false);
    public static final CompositionRelation<A,B,C> AC = new CompositionRelation<>(AB, BC);
}
