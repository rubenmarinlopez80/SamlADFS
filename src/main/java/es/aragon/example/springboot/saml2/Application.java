package es.aragon.example.springboot.saml2;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


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

}
