package com.github.veithen.jrel;

import java.util.IdentityHashMap;
import java.util.Map;

final class BinaryRelationInstance<T extends DomainObject,R extends ReferenceHolder<?>> {
    private final BinaryRelation<T,?,R,?> relation;
    private final Map<T,R> referenceHolders = new IdentityHashMap<>();

    BinaryRelationInstance(BinaryRelation<T,?,R,?> relation) {
        this.relation = relation;
    }

    R getReferenceHolder(T owner) {
        return referenceHolders.computeIfAbsent(owner, relation::newReferenceHolder);
    }
}
