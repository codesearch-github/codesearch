package org.codesearch.searcher.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Convenience class that enables spring autowiring for a gwt service servlet.
 * @author Samuel Kogler
 */
public abstract class AutowiringRemoteServiceServlet extends RemoteServiceServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        AutowireCapableBeanFactory beanFactory = ctx.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(this);
        postConstruct();
    }

    protected abstract void postConstruct();
}
