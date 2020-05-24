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
package com.github.veithen.jrel.collection;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class LinkedIdentityHashSetTest {
    private static LinkedIdentityHashSet<Object> createTestSet() {
        LinkedIdentityHashSet<Object> set = new LinkedIdentityHashSet<>();
        ValidatingListener.addTo(set);
        return set;
    }

    @Test
    public void testGetSize() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        set.add(new Object());
        set.add(new Object());
        assertThat(set).hasSize(2);
    }

    @Test
    public void testIsEmpty() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        assertThat(set.isEmpty()).isTrue();
        set.add(new Object());
        assertThat(set.isEmpty()).isFalse();
    }

    @Test
    public void testAddExisting() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object = new Object();
        assertThat(set.add(object)).isTrue();
        assertThat(set.add(object)).isFalse();
    }

    @Test
    public void testAddMany() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object[] expectedObjects = new Object[1000];
        for (int i=0; i<expectedObjects.length; i++) {
            Object object = new Object();
            set.add(object);
            expectedObjects[i] = object;
        }
        assertThat(set).containsExactlyElementsIn(expectedObjects);
    }

    @Test
    public void testContains() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        for (int i=0; i<10; i++) {
            set.add(new Object());
        }
        Object object = new Object();
        assertThat(set.contains(object)).isFalse();
        set.add(object);
        assertThat(set.contains(object)).isTrue();
    }

    @Test
    public void testRemove() {
        Object[] allObjects = new Object[10];
        for (int i=0; i<10; i++) {
            allObjects[i] = new Object();
        }
        LinkedIdentityHashSet<Object> set = createTestSet();
        for (int i=0; i<10; i++) {
            set.add(allObjects[i]);
        }
        for (int i=0; i<10; i++) {
            assertThat(set.remove(allObjects[i])).isTrue();
            for (int j=0; j<=i; j++) {
                assertThat(set).doesNotContain(allObjects[j]);
            }
            for (int j=i+1; j<10; j++) {
                assertThat(set).contains(allObjects[j]);
            }
        }
    }

    @Test
    public void testRemoveNotContained() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        set.add(new Object());
        assertThat(set.remove(new Object())).isFalse();
    }

    @Test
    public void testAddRemoveRandom() {
        Object[] allObjects = new Object[1000];
        for (int i=0; i<allObjects.length; i++) {
            allObjects[i] = new Object();
        }
        LinkedIdentityHashSet<Object> set = createTestSet();
        Random random = new Random(0);
        Set<Object> expectedObjects = new LinkedHashSet<>();
        for (int i=0; i<allObjects.length*10; i++) {
            Object object = allObjects[random.nextInt(allObjects.length)];
            if (random.nextBoolean()) {
                assertThat(set.remove(object)).isEqualTo(expectedObjects.remove(object));
            } else {
                assertThat(set.add(object)).isEqualTo(expectedObjects.add(object));
            }
            assertThat(set).containsExactlyElementsIn(expectedObjects).inOrder();
        }
    }

    @Test
    public void testIteratorAfterRemove() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();
        set.add(object1);
        set.add(object2);
        set.add(object3);
        set.remove(object2);
        Iterator<Object> it = set.iterator();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(object1);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(object3);
        assertThat(it.hasNext()).isFalse();
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    public void testIteratorRemove() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();
        set.add(object1);
        set.add(object2);
        set.add(object3);
        Iterator<Object> it = set.iterator();
        it.next();
        it.next();
        it.remove();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(object3);
        assertThat(set).containsExactly(object1, object3);
    }

    @Test
    public void testIteratorRemoveWithoutNext() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        set.add(new Object());
        Iterator<Object> it = set.iterator();
        assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    public void testIteratorRemoveTwice() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        set.add(new Object());
        set.add(new Object());
        Iterator<Object> it = set.iterator();
        it.next();
        it.remove();
        it.remove();
        assertThat(set).hasSize(1);
    }

    @Test
    public void testToString() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        set.add(object1);
        set.add(object2);
        assertThat(set.toString()).isEqualTo(new LinkedHashSet<Object>(Arrays.asList(object1, object2)).toString());
    }

    @Test
    public void testToStringEmpty() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        assertThat(set.toString()).isEqualTo("[]");
    }

    @Test
    public void testClear() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        set.add(new Object());
        set.clear();
        assertThat(set.size()).isEqualTo(0);
        assertThat(set.iterator().hasNext()).isFalse();
    }

    @Test
    public void testClearAfterRemove() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        for (int i=0; i<10; i++) {
            set.add(new Object());
        }
        Iterator<Object> it = set.iterator();
        it.next();
        it.remove();
        set.clear();
        assertThat(set).containsExactly();
    }

    @Test
    public void testClearEmpty() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        set.clear();
        assertThat(set).isEmpty();
    }

    @Test
    public void testAddWhileIterating() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();
        set.add(object1);
        List<Object> seen = new ArrayList<>();
        for (Object o : set) {
            if (o == object1) {
                set.add(object2);
                set.add(object3);
            }
            seen.add(o);
        };
        assertThat(set).containsExactly(object1, object2, object3);
        assertThat(seen).containsExactly(object1, object2, object3).inOrder();
    }

    @Test
    public void testRemoveCurrentWhileIterating() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();
        set.add(object1);
        set.add(object2);
        set.add(object3);
        List<Object> seen = new ArrayList<>();
        for (Object o : set) {
            if (o == object2) {
                set.remove(o);
            }
            seen.add(o);
        };
        assertThat(set).containsExactly(object1, object3);
        assertThat(seen).containsExactly(object1, object2, object3).inOrder();
    }

    @Test
    public void testRemoveNextWhileIterating() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();
        set.add(object1);
        set.add(object2);
        set.add(object3);
        List<Object> seen = new ArrayList<>();
        for (Object o : set) {
            if (o == object1) {
                set.remove(object2);
            }
            seen.add(o);
        };
        assertThat(set).containsExactly(object1, object3);
        assertThat(seen).containsExactly(object1, object3).inOrder();
    }

    @Test
    public void testClearWhileIterating() {
        LinkedIdentityHashSet<Object> set = createTestSet();
        Object object1 = new Object();
        Object object2 = new Object();
        Object object3 = new Object();
        set.add(object1);
        set.add(object2);
        set.add(object3);
        List<Object> seen = new ArrayList<>();
        for (Object o : set) {
            if (o == object2) {
                set.clear();
            }
            seen.add(o);
        };
        assertThat(set).isEmpty();
        assertThat(seen).containsExactly(object1, object2).inOrder();
    }
}
