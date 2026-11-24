package de.nrw.it.ignrw.link;

public enum ResourceRel {

    UPDATE("update"),
    DELETE("delete");

    private final String rel;

    ResourceRel(String rel) {
        this.rel = rel;
    }

    public String getRel() {
        return rel;
    }

    @Override
    public String toString() {
        return rel;
    }
}
