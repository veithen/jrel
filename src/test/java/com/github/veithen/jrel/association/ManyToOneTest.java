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
package com.github.veithen.jrel.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.veithen.jrel.collection.SetListener;

public class ManyToOneTest {
    @Test
    public void testSetParent() {
        Parent parent = new Parent();
        Child child1 = new Child();
        Child child2 = new Child();
        child1.setParent(parent);
        child2.setParent(parent);
        assertThat(parent.getChildren()).containsExactly(child1, child2);
    }

    @Test
    public void testAddChild() {
        Parent parent = new Parent();
        Child child = new Child();
        parent.getChildren().add(child);
        assertThat(child.getParent()).isSameAs(parent);
    }

    @Test
    public void testAddChildToOtherParent() {
        Parent parent1 = new Parent();
        Parent parent2 = new Parent();
        Child child = new Child();
        parent1.getChildren().add(child);
        assertThat(child.getParent()).isSameAs(parent1);
        parent2.getChildren().add(child);
        assertThat(child.getParent()).isSameAs(parent2);
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

    @Test
    public void testRemoveChildUsingIterator() {
        Parent parent = new Parent();
        Child child = new Child();
        child.setParent(parent);
        Iterator<Child> it = parent.getChildren().iterator();
        it.next();
        it.remove();
        assertThat(child.getParent()).isNull();
    }

    @Test
    public void testClearChildren() {
        Parent parent = new Parent();
        Child child1 = new Child();
        Child child2 = new Child();
        child1.setParent(parent);
        child2.setParent(parent);
        parent.getChildren().clear();
        assertThat(child1.getParent()).isNull();
        assertThat(child2.getParent()).isNull();
    }

    @Test
    public void testListeners() {
        boolean[] addedFired = new boolean[2];
        boolean[] removedFired = new boolean[2];
        Parent parent = new Parent();
        Child child = new Child();
        parent.getChildren()
                .addListener(
                        new SetListener<Child>() {
                            @Override
                            public void added(Child object) {
                                assertThat(object).isSameAs(child);
                                addedFired[0] = true;
                            }

                            @Override
                            public void removed(Child object) {
                                assertThat(object).isSameAs(child);
                                removedFired[0] = true;
                            }
                        });
        child.getParentReference()
                .asSet()
                .addListener(
                        new SetListener<Parent>() {
                            @Override
                            public void added(Parent object) {
                                assertThat(object).isSameAs(parent);
                                addedFired[1] = true;
                            }

                            @Override
                            public void removed(Parent object) {
                                assertThat(object).isSameAs(parent);
                                removedFired[1] = true;
                            }
                        });
        child.setParent(parent);
        assertThat(addedFired).containsExactly(true, true);
        assertThat(removedFired).containsExactly(false, false);
        Arrays.fill(addedFired, false);
        child.setParent(null);
        assertThat(addedFired).containsExactly(false, false);
        assertThat(removedFired).containsExactly(true, true);
    }

    @Test
    public void testBiPredicate() {
        Parent parent = new Parent();
        Child child = new Child();
        assertThat(Relations.PARENT.test(child, parent)).isFalse();
        child.setParent(parent);
        assertThat(Relations.PARENT.test(child, parent)).isTrue();
    }

    @Test
    public void testFunction() {
        Parent parent = new Parent();
        Child child = new Child();
        assertThat(Relations.PARENT.apply(child)).isNull();
        child.setParent(parent);
        assertThat(Relations.PARENT.apply(child)).isSameAs(parent);
    }

    @Test
    public void testListener() {
        Parent parent1 = new Parent();
        Parent parent2 = new Parent();
        Child child = new Child();
        child.setParent(parent1);
        List<String> events = new ArrayList<>();
        parent1.getChildren()
                .addListener(
                        new SetListener<Child>() {
                            @Override
                            public void added(Child object) {
                                fail();
                            }

                            @Override
                            public void removed(Child object) {
                                events.add("removed from parent 1");
                            }
                        });
        parent2.getChildren()
                .addListener(
                        new SetListener<Child>() {
                            @Override
                            public void added(Child object) {
                                events.add("added to parent 1");
                            }

                            @Override
                            public void removed(Child object) {
                                fail();
                            }
                        });
        child.setParent(parent2);
        assertThat(events).containsExactly("removed from parent 1", "added to parent 1");
    }
}
