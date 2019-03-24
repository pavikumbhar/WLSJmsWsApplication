package com.pavikumbhar.javaheart.config;

import java.io.StringReader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Class to configure and make a webservice call
 */

/**
 * Class to configure and make a webservice call
 * @author pavikumbhar
 *
 */
public abstract class WebServiceClient {

	@Autowired
	private Environment environment;

    @Autowired
    private WebServiceTemplate webServicetemplate;
    
    /**
     *  Makes a webservice call using the provided URI with xml as the body
     * @param xml
     * @return
     */
    public StreamResult send(String xml) {
        webServicetemplate.setDefaultUri(getProperty(getUriProp()));
        String userName = getProperty(getUserName());
        String password = getProperty(getPassword());
        if (isNotEmpty(userName) && isNotEmpty(password)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            HttpClient httpClient = httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider).build();
            HttpComponentsMessageSender header = new HttpComponentsMessageSender(httpClient);
            webServicetemplate.setMessageSender(header);
        }

        StringBuilder buf = new StringBuilder();
        buf.append(getProperty(getSoapReqPrefixProp()));
        buf.append(xml);
        buf.append(getProperty(getSoapReqSuffixProp()));
        StreamSource source = new StreamSource(new StringReader(buf.toString()));
        StreamResult result = new StreamResult(System.out);
        webServicetemplate.sendSourceAndReceiveToResult(source, result);
        return result;
    }

    private String getProperty(String key) {
         return environment.getProperty(key);
    }

    /** This method fetches the SOAP Prefix for Response */
    protected abstract String getSoapReqPrefixProp();

    /** This method fetches the SOAP Suffix for Response */
    protected abstract String getSoapReqSuffixProp();

    /** This method fetches the SOAP URI for Response */
    protected abstract String getUriProp();

    /** This method fetches the User Name for WSDL */
    protected abstract String getUserName();

    /** This method fetches the Password for WSDL */
    protected abstract String getPassword();

}
