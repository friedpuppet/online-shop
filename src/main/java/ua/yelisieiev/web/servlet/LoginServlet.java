package ua.yelisieiev.web.servlet;

import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.yelisieiev.service.ProductsService;
import ua.yelisieiev.service.SecurityService;
import ua.yelisieiev.service.ServiceLocator;
import ua.yelisieiev.web.PageWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import static javax.servlet.http.HttpServletResponse.*;

public class LoginServlet extends HttpServlet {
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SecurityService securityService;
    private final PageWriter pageWriter = new PageWriter();

    public LoginServlet() {
        securityService = ServiceLocator.getService(SecurityService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer writer = resp.getWriter();
        try {
            pageWriter.writePage("/login.html", null, writer);
        } catch (TemplateNotFoundException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, "Template not found: " + e.getMessage());
        } catch (TemplateException e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, "Can't apply a template: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        try {
            if (String.valueOf(login).isEmpty() || String.valueOf(password).isEmpty()) {
                throw new ServletException("Both login and password must be submitted");
            }
            if (!securityService.isLoginPassValid(login, password)) {
                resp.sendError(SC_FORBIDDEN);
                return;
            }
            String token = securityService.createToken(login, req.getSession().getId());
            resp.addCookie(new Cookie("auth-token", token));
            resp.sendRedirect("/products");
        } catch (ServletException e) {
            resp.sendError(SC_BAD_REQUEST, e.getMessage());
        }
    }
}
