package org.codesearch.searcher.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.web.bindery.event.shared.UmbrellaException;

/**
 * Handles uncaught exceptions by logging them to the client.
 * 
 * @author Samuel Kogler
 */
public class DefaultExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger LOG = Logger.getLogger(DefaultExceptionHandler.class.getName());

    /** {@inheritDoc} */
    @Override
    public void onUncaughtException(Throwable e) {
        LOG.log(Level.SEVERE, "Unexpected Exception:", e);
        LOG.log(Level.SEVERE, "Root cause:", unwrap(e));
    }

    private Throwable unwrap(Throwable e) {
        if (e instanceof UmbrellaException) {
            UmbrellaException ue = (UmbrellaException) e;
            if (ue.getCauses().size() == 1) {
                return unwrap(ue.getCauses().iterator().next());
            }
        }
        return e;
    }
}
