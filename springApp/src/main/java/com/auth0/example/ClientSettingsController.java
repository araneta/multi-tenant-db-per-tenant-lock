package com.auth0.example;

import com.auth0.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings("unused")
@Controller
public class ClientSettingsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @CrossOrigin(origins = "https://mostekcm-oc.auth0.com")    
    @RequestMapping(value = "/settings/{tenant}.json", 
    method = RequestMethod.GET,
    produces = { "application/json" })
    protected ResponseEntity<Settings> home(final HttpServletRequest req,
        @PathVariable("tenant") final String tenant) {

        logger.info("Settings page");

        return new ResponseEntity<Settings>(new Settings(tenant), HttpStatus.OK);
    }

}
