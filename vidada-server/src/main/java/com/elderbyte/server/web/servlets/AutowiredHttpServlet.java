package com.elderbyte.server.web.servlets;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * A http servlet which supports spring dependency injection.
 */
public abstract class AutowiredHttpServlet extends HttpServlet {

    private AutowireCapableBeanFactory ctx;

    @Override
    public void init() throws ServletException {
        super.init();

        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        ctx = applicationContext.getAutowireCapableBeanFactory();
        //The following line does the magic autowiring
        ctx.autowireBean(this);
    }

}
