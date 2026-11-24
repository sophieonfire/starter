package de.nrw.it.ignrw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE}) // Funktioniert auf Methoden- und Controller-Ebene
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresOrganisationContext {

}
