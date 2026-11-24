package de.nrw.it.ignrw.link;

public enum CollectionRel {

    CREATE("create"),
    NEXT("next"),
    PREV("prev"),
    RESET("reset"),
    SEARCH("search"),
    FILTER("filter"),
    EXPORT("export");

    private final String rel;

    CollectionRel(String rel) {
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
