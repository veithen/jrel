package com.github.veithen.jrel;

public abstract class AbstractDomainObject implements DomainObject {
    private final Domain domain;

    public AbstractDomainObject(Domain domain) {
        this.domain = domain;
    }

    @Override
    public Domain getDomain() {
        return domain;
    }
}
