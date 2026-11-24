package de.nrw.it.ignrw.link;

public enum LinkName {

    RELOAD("reload"),
    DELETE("delete"),
    CREATE("create"),
    UPDATE("update"),
    PATCH("patch"),
    RESET("reset"),
    EXPORT("export"),
    FILTER("filter"),
    NEXT("next"),
    PREV("prev");

    private final String name;

    LinkName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
