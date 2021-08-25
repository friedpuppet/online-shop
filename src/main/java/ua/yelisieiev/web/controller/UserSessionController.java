package ua.yelisieiev.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ua.yelisieiev.entity.AuthTokenWithTTL;
import ua.yelisieiev.service.SecurityService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class UserSessionController extends HttpServlet {
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityService securityService;

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password, HttpServletResponse resp) {
        final Optional<AuthTokenWithTTL> token = securityService.login(login, password);
        if (token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Cookie authCookie = new Cookie("auth-token", token.get().getToken());
        authCookie.setMaxAge(SecurityService.TOKEN_TTL_MINUTES * 60);
        resp.addCookie(authCookie);
        return "redirect:/products";
    }

    @PostMapping("/logout")
    public String logout(@CookieValue(value = "auth-token", required = false) String tokenString, HttpServletResponse resp) {
        if (tokenString != null) {
            securityService.logout(tokenString);
            Cookie authCookie = new Cookie("auth-token", tokenString);
            authCookie.setMaxAge(0);
            resp.addCookie(authCookie);
        }
        return "redirect:/products";
    }
}
