package es.aragon.example.springboot.saml2;

import java.io.File;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.security.x509.X509Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.logout.OpenSaml4LogoutRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;


	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
	    	.authorizeRequests(authorize -> 
	    		authorize.antMatchers("/").permitAll().anyRequest().authenticated()
	        )
	    	.saml2Login(Customizer.withDefaults())
	    	.saml2Logout(Customizer.withDefaults())
	    	.logout((logout) ->
				logout.deleteCookies("remove")
					.invalidateHttpSession(true)
					.clearAuthentication(true)
					.logoutUrl("/logout")
					.logoutSuccessUrl("/"));

		// add auto-generation of ServiceProvider Metadata
		Converter<HttpServletRequest, RelyingPartyRegistration> relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);
		Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());
		http.addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class);
		
			
	}

	@Bean
	protected RelyingPartyRegistrationRepository relyingPartyRegistrations() throws Exception {
		
		ClassPathResource classPathResource = new ClassPathResource("saml-certificate/adfs.crt");
		InputStream inputStream = classPathResource.getInputStream();
		File somethingFile = File.createTempFile("adfs", ".crt");
		try {
		    FileUtils.copyInputStreamToFile(inputStream, somethingFile);
		} finally {
		    IOUtils.closeQuietly(inputStream);
		}

		//ClassLoader classLoader = getClass().getClassLoader();
		//File verificationKey = new File(classLoader.getResource("saml-certificate/adfs.crt").getFile());
	    X509Certificate certificate = X509Support.decodeCertificate(somethingFile);
	    Saml2X509Credential credential = Saml2X509Credential.verification(certificate);

	 	String relyingPartyEntityId = "https://samladfs-tpvams.apps.pre.aragon.es/saml2/service-provider-metadata/testSAML";
		String assertingConsumerServiceLocation = "https://samladfs-tpvams.apps.pre.aragon.es/login/saml2/sso/testSAML";
		 
	    RelyingPartyRegistration registration = RelyingPartyRegistration
	            .withRegistrationId("testSAML")
	            .entityId(relyingPartyEntityId)
	 			.assertionConsumerServiceLocation(assertingConsumerServiceLocation)
	            .assertingPartyDetails(party -> party
	                .entityId("http://ssoa.aragon.es/adfs/services/trust")
	                .singleSignOnServiceLocation("https://ssoa.aragon.es/adfs/ls/IdpInitiatedSignon")
	                .singleLogoutServiceLocation("https://samladfs-tpvams.apps.pre.aragon.es/logout/saml2/slo")
	                //.singleLogoutServiceLocation("https://ssoa.aragon.es/adfs/ls/?wa=wsignout1.0")
	                .wantAuthnRequestsSigned(false)
	                .verificationX509Credentials(c -> c.add(credential))
	            ).build();
	    return new InMemoryRelyingPartyRegistrationRepository(registration);
	}


	
	

	
}
