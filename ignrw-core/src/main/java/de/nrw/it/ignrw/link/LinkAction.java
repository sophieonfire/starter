package de.nrw.it.ignrw.link;

public enum LinkAction {

    READ("read"),
    EDIT("edit"),
    UPDATE("update"),
    CREATE("create"),
    DELETE("delete");

    public String getLabel() {
        return label;
    }

    public final String label;

    private LinkAction(String label) {
        this.label = label;
    }
}
