package es.aragon.example.springboot.saml2;

import java.io.File;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.opensaml.security.x509.X509Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.core.io.ClassPathResource;

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
	        ).saml2Login();

		// add auto-generation of ServiceProvider Metadata
		Converter<HttpServletRequest, RelyingPartyRegistration> relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);
		Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());
		http.addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class);
	}
	
	/*@Bean
	protected RelyingPartyRegistrationRepository relyingPartyRegistrationsOKTA() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File verificationKey = new File(classLoader.getResource("saml-certificate/okta.crt").getFile());
	    X509Certificate certificate = X509Support.decodeCertificate(verificationKey);
	    Saml2X509Credential credential = Saml2X509Credential.verification(certificate);
	    RelyingPartyRegistration registration = RelyingPartyRegistration
	            .withRegistrationId("okta-saml")
	            .assertingPartyDetails(party -> party
	                .entityId("http://www.okta.com/exk6sni93NCyDl9VP5d6")
	                .singleSignOnServiceLocation("https://dev-11017565.okta.com/app/dev-11017565_appsaml_1/exk6sni93NCyDl9VP5d6/sso/saml")
	                .wantAuthnRequestsSigned(false)
	                .verificationX509Credentials(c -> c.add(credential))
	            ).build();
	    return new InMemoryRelyingPartyRegistrationRepository(registration);
	}*/
	
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

		ClassLoader classLoader = getClass().getClassLoader();
		File verificationKey = new File(classLoader.getResource("saml-certificate/adfs.crt").getFile());
	    X509Certificate certificate = X509Support.decodeCertificate(somethingFile);
	    Saml2X509Credential credential = Saml2X509Credential.verification(certificate);

	 	String relyingPartyEntityId = "https://samladfs-tpvams.apps.pre.aragon.es/saml2/service-provider-metadata/testSAML";
		String assertingConsumerServiceLocation = "https://samladfs-tpvams.apps.pre.aragon.es/login/saml2/sso/testSAML";
		
	    RelyingPartyRegistration registration = RelyingPartyRegistration
	            .withRegistrationId("testSAML")
	            .entityId(relyingPartyEntityId)
	 			.assertionConsumerServiceLocation(assertingConsumerServiceLocation)
	            .assertingPartyDetails(party -> party
	                //.entityId("testSAML")
	                .singleSignOnServiceLocation("http://ssoa.aragon.es/adfs/services/trust")
	                .wantAuthnRequestsSigned(false)
	                .verificationX509Credentials(c -> c.add(credential))
	            ).build();
	    return new InMemoryRelyingPartyRegistrationRepository(registration);
	}
	
	

	
}
