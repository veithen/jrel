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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ReferencesTest {
    @Test
    public void testGetSize() {
        References<Child> children = new Parent().getChildren();
        children.add(new Child());
        children.add(new Child());
        assertThat(children).hasSize(2);
    }

    @Test
    public void testIsEmpty() {
        References<Child> children = new Parent().getChildren();
        assertThat(children.isEmpty()).isTrue();
        children.add(new Child());
        assertThat(children.isEmpty()).isFalse();
    }

    @Test
    public void testAddExisting() {
        References<Child> children = new Parent().getChildren();
        Child child = new Child();
        assertThat(children.add(child)).isTrue();
        assertThat(children.add(child)).isFalse();
    }

    @Test
    public void testAddMany() {
        References<Child> children = new Parent().getChildren();
        Child[] expectedChildren = new Child[1000];
        for (int i=0; i<expectedChildren.length; i++) {
            Child child = new Child();
            children.add(child);
            expectedChildren[i] = child;
        }
        assertThat(children).containsExactlyElementsIn(expectedChildren);
    }

    @Test
    public void testContains() {
        References<Child> children = new Parent().getChildren();
        for (int i=0; i<10; i++) {
            children.add(new Child());
        }
        Child child = new Child();
        assertThat(children.contains(child)).isFalse();
        children.add(child);
        assertThat(children.contains(child)).isTrue();
    }

    @Test
    public void testRemove() {
        Child[] allChildren = new Child[10];
        for (int i=0; i<10; i++) {
            allChildren[i] = new Child();
        }
        References<Child> children = new Parent().getChildren();
        for (int i=0; i<10; i++) {
            children.add(allChildren[i]);
        }
        for (int i=0; i<10; i++) {
            assertThat(children.remove(allChildren[i])).isTrue();
            for (int j=0; j<=i; j++) {
                assertThat(children).doesNotContain(allChildren[j]);
            }
            for (int j=i+1; j<10; j++) {
                assertThat(children).contains(allChildren[j]);
            }
        }
    }

    @Test
    public void testAddRemoveRandom() {
        Child[] allChildren = new Child[1000];
        for (int i=0; i<allChildren.length; i++) {
            allChildren[i] = new Child();
        }
        References<Child> children = new Parent().getChildren();
        Random random = new Random(0);
        Set<Child> expectedChildren = new LinkedHashSet<>();
        for (int i=0; i<allChildren.length*10; i++) {
            Child child = allChildren[random.nextInt(allChildren.length)];
            if (expectedChildren.contains(child)) {
                expectedChildren.remove(child);
                children.remove(child);
            } else {
                expectedChildren.add(child);
                children.add(child);
            }
            assertThat(children).containsExactlyElementsIn(expectedChildren).inOrder();
        }
    }

    @Test
    public void testIteratorAfterRemove() {
        References<Child> children = new Parent().getChildren();
        Child child1 = new Child();
        Child child2 = new Child();
        Child child3 = new Child();
        children.add(child1);
        children.add(child2);
        children.add(child3);
        children.remove(child2);
        Iterator<Child> it = children.iterator();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(child1);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(child3);
        assertThat(it.hasNext()).isFalse();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    public void testIteratorRemove() {
        References<Child> children = new Parent().getChildren();
        Child child1 = new Child();
        Child child2 = new Child();
        Child child3 = new Child();
        children.add(child1);
        children.add(child2);
        children.add(child3);
        Iterator<Child> it = children.iterator();
        it.next();
        it.next();
        it.remove();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(child3);
        assertThat(children).containsExactly(child1, child3);
    }

    @Test
    public void testSnapshot() {
        References<Child> children = new Parent().getChildren();
        Child child1 = new Child();
        Child child2 = new Child();
        Child child3 = new Child();
        children.add(child1);
        children.add(child2);
        children.add(child3);
        Iterable<Child> snapshot = children.snapshot();
        children.remove(child1);
        children.add(new Child());
        Iterator<Child> it = snapshot.iterator();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(child1);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(child2);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(child3);
        assertThat(it.hasNext()).isFalse();
        assertThrows(NoSuchElementException.class, it::next);
    }
}
