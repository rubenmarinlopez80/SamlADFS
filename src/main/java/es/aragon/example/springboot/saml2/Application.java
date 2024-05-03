package es.aragon.example.springboot.saml2;

import java.io.*;
import java.util.Map;

import javax.servlet.http.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
	
	/*SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

	@RequestMapping("/mylogout")
	public String performLogout(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, HttpServletRequest request, HttpServletResponse response) {
	    // .. perform logout
		logger.info("Entramos al logout");
		
		String url = "https://ssoa.aragon.es/adfs/ls/?wa=wsignout1.0";
		try {
			URL obj= new URL(url);
			
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			// optional default is GET
			con.setRequestMethod("GET");
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestProperty("cache-control", "no-cache");
			con.setDoInput(true);
			con.setDoOutput(true);
			
			
			// write out form parameters
			String getParamaters = "wa=wsignout1.0";
			con.setFixedLengthStreamingMode(getParamaters.getBytes().length);
			PrintWriter out = new PrintWriter(con.getOutputStream());
			out.print(getParamaters);
			out.close();

			con.connect();
			
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
			           new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer responseBuffer = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);
			}
			in.close();
	
			//print result
			System.out.println(responseBuffer.toString());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return "home";
	    
	    //this.logoutHandler.logout(request, response, authentication);
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	      int i = 0;
	      logger.info("Longitud cookies "+cookies.length);
	      while ( i < cookies.length) {
	    	  logger.info("Entramos antes de borrar cookie "+i+" "+cookies[i].getName()+cookies[i].getDomain());
	    	  cookies[i].setMaxAge(0);
	          i++;
	      }
	    }
	    deleteCookiesFromDomain(request,response,"samladfs-tpvams.apps.pre.aragon.es");
	    deleteCookiesFromDomain(request,response,"ssoa.aragon.es");

		
	}
	
	public void deleteCookiesFromDomain(HttpServletRequest request, HttpServletResponse response, String domain) {
	    	try {
				Arrays.stream(request.getCookies()).filter(cookie -> cookie.getDomain().equals(domain)).forEach(cookie -> {
		    	if (cookie!= null)
					logger.info("Dominio "+domain+" Cookie"+cookie.getName());
			    	cookie.setMaxAge(0);
			        cookie.setPath("/");
			        response.addCookie(cookie);
				});
	    	} catch (Exception e) {
				e.getMessage();

			}
	}*/

}
