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

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class ManyToOneTest {
    @Test
    public void testSetParent() {
        Parent parent = new Parent();
        Child child1 = new Child();
        Child child2 = new Child();
        child1.setParent(parent);
        child2.setParent(parent);
        assertThat(parent.getChildren()).containsExactly(child1, child2).inOrder();
    }

    @Test
    public void testAddChild() {
        Parent parent = new Parent();
        Child child = new Child();
        parent.getChildren().add(child);
        assertThat(child.getParent()).isSameInstanceAs(parent);
    }

    @Test
    public void testAddChildToOtherParent() {
        Parent parent1 = new Parent();
        Parent parent2 = new Parent();
        Child child = new Child();
        parent1.getChildren().add(child);
        assertThat(child.getParent()).isSameInstanceAs(parent1);
        parent2.getChildren().add(child);
        assertThat(child.getParent()).isSameInstanceAs(parent2);
        assertThat(parent1.getChildren()).isEmpty();
    }

    @Test
    public void testRemoveChild() {
        Parent parent = new Parent();
        Child child = new Child();
        child.setParent(parent);
        assertThat(parent.getChildren().remove(child)).isTrue();
        assertThat(child.getParent()).isNull();
    }
}
