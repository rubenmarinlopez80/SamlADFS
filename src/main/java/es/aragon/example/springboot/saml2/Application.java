package es.aragon.example.springboot.saml2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.web.authentication.logout.*;


@SpringBootApplication
@Controller
public class Application {
	
	Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@RequestMapping("/")
	public String index() {
		logger.info("Index");
		return "home";
	}

	@RequestMapping("/secured/hello")
	public String hello(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
		model.addAttribute("name", principal.getName());
		logger.info("prueba"+principal.getName());
		logger.info("prueba1"+principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname"));
		model.addAttribute("givenname", principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname"));
		logger.info("prueba2"+principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname"));
		model.addAttribute("surname", principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname"));
		logger.info("prueba3"+principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"));
		model.addAttribute("emailaddress", principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"));
		Map atributos =principal.getAttributes();
		atributos.forEach((k,v) -> logger.info("Key: " + k + ": Value: " + v));
		return "hello";
	}
	
	SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

	@RequestMapping("/mylogout")
	public String performLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
	    // .. perform logout
		logger.info("Entramos al logout");
	    this.logoutHandler.logout(request, response, authentication);
		return "home";
	}


}
