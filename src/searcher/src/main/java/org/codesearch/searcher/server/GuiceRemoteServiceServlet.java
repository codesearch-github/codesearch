package org.codesearch.searcher.server;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 *
 * @author Samuel Kogler
 */
public class GuiceRemoteServiceServlet extends RemoteServiceServlet {

    @Inject
    private Injector injector;

    @SuppressWarnings({"unchecked"})
    private RemoteService getServiceInstance(Class serviceClass) {
        return (RemoteService) injector.getInstance(serviceClass);
    }
}
