package de.nrw.it.ignrw.exception;

import org.springframework.security.access.AccessDeniedException;

public class DsgvoException extends AccessDeniedException {

    public DsgvoException() {
        super("Dsgvo Fehler");
    }

    public DsgvoException(String msg) {
        super(msg);
    }
}
