package com.github.veithen.jrel;

import java.util.HashMap;
import java.util.Map;

public final class Domain {
    private final Map<BinaryRelation<?,?,?,?>,BinaryRelationInstance<?,?>> binaryRelationInstances = new HashMap<>();

    @SuppressWarnings("unchecked")
    <T extends DomainObject,R extends ReferenceHolder<?>> BinaryRelationInstance<T,R> getBinaryRelationInstance(BinaryRelation<T,?,R,?> relation) {
        return (BinaryRelationInstance<T,R>)binaryRelationInstances.computeIfAbsent(relation, BinaryRelationInstance::new);
    }
}
