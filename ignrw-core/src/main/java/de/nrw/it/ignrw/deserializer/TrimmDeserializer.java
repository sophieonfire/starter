package de.nrw.it.ignrw.deserializer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class TrimmDeserializer extends ValueDeserializer<String> {

    private final boolean isTrimmed;

    // Standard-Konstruktor für Jackson (inaktiv)
    public TrimmDeserializer() {
        this.isTrimmed = false;
    }

    // Privater Konstruktor für die maßgeschneiderte Feld-Konfiguration
    private TrimmDeserializer(boolean isTrimmed) {
        this.isTrimmed = isTrimmed;
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String value = p.getValueAsString();
        if (value == null) {
            return null;
        }

        // 1. Trimmen, falls aktiviert
        if (isTrimmed) {
            value = value.trim();
        }

        return value;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) {
            Trimm annotation = property.getAnnotation(Trimm.class);
            if (annotation != null) {
                // Hier lesen wir die echten Werte aus der Annotation des Feldes aus
                return new TrimmDeserializer(
                        true
                );
            }
        }
        return this;
    }
}
