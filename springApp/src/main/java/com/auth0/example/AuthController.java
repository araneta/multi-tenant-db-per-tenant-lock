package com.auth0.example;

import com.auth0.AuthenticationController;
import com.auth0.AuthorizeUrl;
import com.auth0.IdentityVerificationException;
import com.auth0.SessionUtils;
import com.auth0.Tokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthController {

    private final AuthenticationController controller;
    private final String userInfoAudience;
    private static final String RETURN_TO = "auth0_return_to";

    @Autowired
    public AuthController(AppConfig config) {
        controller = AuthenticationController.newBuilder(config.getDomain(), config.getClientId(), config.getClientSecret())
                .build();
        userInfoAudience = String.format("https://%s/userinfo", config.getDomain());
    }

    public Tokens handle(HttpServletRequest request) throws IdentityVerificationException {
        return controller.handle(request);
    }
    
    public String getReturnTo(HttpServletRequest req) {
        return (String)SessionUtils.get(req, RETURN_TO);
    }

    public String buildAuthorizeUrl(HttpServletRequest request, boolean sso) {
        String redirectUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/callback";
        return this.buildAuthorizeUrl(request, redirectUri, sso);
    }

    public String buildAuthorizeUrl(HttpServletRequest request, String redirectUri, boolean sso) {
        AuthorizeUrl authUrl = controller
            .buildAuthorizeUrl(request, redirectUri)
            .withAudience(userInfoAudience);

        String returnTo = (String)SessionUtils.get(request, RETURN_TO);
        if (returnTo == null) SessionUtils.set(request, RETURN_TO, request.getPathInfo());          

        /* Add prompt=none if SSO is desired */
        if (sso) authUrl.withParameter("prompt", "none");
        else authUrl.withParameter("prompt", "login");

        return authUrl.build();
    }

}
