package de.nrw.it.ignrw.context;

import de.nrw.it.ignrw.request.header.IGNRWContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.UUID;

public class IGNRWContextHolder {
    private static final String CONTEXT_KEY = "IGNRW_REQUEST_CONTEXT";

    public static void setContext(IGNRWContext context) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // Bindet das Objekt an den aktuellen Spring-Web-Request
            attributes.setAttribute(CONTEXT_KEY, context, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public static IGNRWContext getContext() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return (IGNRWContext) attributes.getAttribute(CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST);
        }
        return null;
    }

    public static UUID getOrgUuid() {
        IGNRWContext ctx = getContext();
        return (ctx != null) ? ctx.getOrgUuid() : null;
    }

//    /**
//     * @deprecated Wird nicht mehr benötigt! Spring bereinigt den Speicher
//     * am Ende des HTTP-Requests vollautomatisch.
//     */
//    @Deprecated
//    public static void clear() {
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//        if (attributes != null) {
//            attributes.removeAttribute(CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST);
//        }
//    }
}
