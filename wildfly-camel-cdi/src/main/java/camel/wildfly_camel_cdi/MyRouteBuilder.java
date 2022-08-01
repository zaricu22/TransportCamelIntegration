/*
 * #%L
 * Wildfly Camel
 * %%
 * Copyright (C) 2013 - 2015 RedHat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package camel.wildfly_camel_cdi;

import java.util.Properties;
import java.util.logging.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import entiteti.Narudzba;


@ApplicationScoped
public class MyRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		//==================================	EJB  	==============================================
		// Simple consuming remote bean, camel's EJB component is deprecated and it isn't supported by new 'camel-core'
        // JndiBeanRepository repository = new JndiBeanRepository(createEjbContext());
        // Object sb = repository.lookupByName("ejb:/camel-test-ejb-0.0.1-SNAPSHOT/TestSLSB!test.TestSLSBRemote");
        // getContext().getRegistry().bind("TestSLSB", sb);
		
		//==================================	REGISTRY  	==============================================
		// Da ne bismo iznova kreirali objekat dodamo ga samo jednom u registar
//		getContext().getRegistry().bind("NarudzbaNormalizer", new NarudzbaNormalizer());
//		getContext().getRegistry().bind("NarudzbaSplitter", new NarudzbaSplitter());
		
		//==================================	CXF-WS  	==============================================
		// Finest SOAP logger to display all errors
//		java.util.logging.Logger.getLogger("com.sun.xml.internal.bind").setLevel(Level.FINEST);

		// 'camel-cxf' dependency is unnecessary if we don't create/add CXF component manually
		// CXF component use only JAXB provider, we can't specify custom providers
//        from("cxf://http://localhost:8083/wildfly-camel-cdi/webservices/narucivanje?"
//        		+ "dataFormat=PAYLOAD"	// type of message's body injected into the route(payload = soap:body)
//        		+ "&serviceClass=" + NarudzbaServiceWS.class.getName())
//	        .choice()
//	        .when(header("operationName").contains("slanje"))
//	        	.log("USAO 4 "+"\n ${body}");
        
		//==================================	CXF-RS  	==============================================
        // 'jackson-jaxrs-json-provider' dependency - provided by WildFly Camel patch
        // Jackson is popular library to work with JSON, it is much easier and powerful to use than default JSON-B
// 		getContext().getRegistry().bind("JacksonJsonProvider", new JacksonJsonProvider());
// 		
// 		from("cxfrs://http://localhost:8083/wildfly-camel-cdi/rest?"
// 				+ "bindingStyle=SimpleConsumer"		// auto mapping request to message
// 				+ "&resourceClasses=" + NarudzbaServiceRS.class.getName())
// 	        .choice()
// 	        .when(header("operationName").contains("slanjeNarudzbe"))
// 	        	.log("USAO 4 "+"\n ${body}");
        
		//==================================	JMS 	==============================================
 		// 'wildfly-jms-client-bom' dependency - provided by WildFly Camel patch
 		// 'camel-jms' dependency - provided by WildFly Camel patch
 		// !!! We must specify user on remote WildFly server in 'guest' group and use date 
 		// !!! We must specify 'Entries' of queues and topics on WildFly instance in 'java:jboss/exported/jms/..' not only 'java:/jms/..'
 		// !!! We must use standalone-full-camel.xml config on client server
 		// Lookup jms/RemoteConnectionFactory and create/add JMSComponent with it
//		Context initialContext = configureInitialContext();
//		ConnectionFactory connectionFactory = (ActiveMQJMSConnectionFactory) initialContext.lookup("java:jms/RemoteConnectionFactory");
//		ActiveMQComponent component = new ActiveMQComponent();
//		component.setConnectionFactory(connectionFactory);
//		component.setUsername("jmsClient1"); 	// specify on component for all endpoint
//		component.setPassword("secret");		//  or in particular endpoint URL (..?username=X&password=Y)
//		getContext().addComponent("jms", component);
        
        // Users of ActiveMQ 5.x(older) should use ActiveMQ component
        // Users of WildFly ActiveMQ Artemis(HornetQ) 2.x(successor ) should use the JMS component     
		// JMS component look for sender's message original class on client app to bind message 
		// Message type: Text, Object, Bytes, Map 
//        from("jms:queue:prijemNarudzbi")
//        	.log("USAO 4 "+"\n ${body}");
        
		//==================================	FTP 	==============================================
        // 'camel-ftp' dependency - provided by WildFly Camel patch
        // default ports (ftp = 21, sftp = 22, ftps = 2222)
        // default ftpClient properties related to SSL with the FTPS component, trust store accept all certificates
        // FTP consumer will by default leave the consumed files untouched on the remote FTP server
        // The option readLock can be used to force Camel not to consume files that is currently in the progress of being written
//        from("ftp://username@someftpserver.com/somedirectory/somesubdir?"
//        		+ "password=secret&"
//        		+ "binary=true") 
        
		//==================================	MAIL	==============================================
        // 'camel-mail' dependency - provided by WildFly Camel patch
        // SMTP, POP3, IMAP
        // default ports (SMTP 25, SMTPS 465, POP3 110, POP3S 995, IMAP 143, IMAPS 993)
        // POP3 has some limitations and end users are encouraged to use IMAP if possible.
        // To enable SSL login into your Google mail account and changing your settings to allow IMAP access
//        from("imaps://imap.gmail.com?"
//        		+ "username=YOUR_USERNAME@gmail.com&"
//        		+ "password=YOUR_PASSWORD&"
//        	    + "delete=false&"
//        	    + "unseen=true&"
//        	    + "searchTerm.subject=Camel&"
//       	    + "searchTerm.fromSentDate=now-24h")       
        
        //==================================	DATABASE	==============================================
        
        
	}
	
	/* JNDI applications need a way to communicate various preferences and properties 
     * that define the environment in which naming and directory services are accessed */
	// 'wildfly-ejb-client-bom' dependency
	// Java EJB-client properties (optimized for lookups beans)
    @SuppressWarnings("unused")
	private static Context createEjbContext() throws NamingException {
    	
    	final Properties properties = new Properties();
    	properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
    	properties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false"); 
    	properties.put("remote.connections", "moja1");
    	properties.put("remote.connection.moja1.host", "localhost");
    	properties.put("remote.connection.moja1.port", "8080"); 
    	properties.put("remote.connection.moja1.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

        return new InitialContext(properties);
        
    }
    
    // 'wildfly-jms-client-bom' dependency
    // Java Naming-client properties (lookup any JNDI )
    private Context configureInitialContext() throws NamingException {
    	
	  final Properties env = new Properties();
	  env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
	  env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, "http-remoting://localhost:8080"));
	  //env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", "jmsClient1"));
	  //env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", "secret"));
	  
	  return new InitialContext(env);
	  
	}
	
}

/* JMS QUERY PARAMETERS:
    clientId (common) - Sets the JMS client ID to use
	connectionFactory (common) - The connection factory to be use. A connection factory must be configured either on the component or endpoint.
	disableReplyTo (common) - Specifies whether Camel ignores the JMSReplyTo header in messages
	durableSubscriptionName (common) - The durable subscriber name for specifying durable topic subscriptions
 	jmsMessageType (common) - Allows you to force the use of a specific javax.jms.Message implementation for sending JMS messages. Possible values are: Bytes, Map, Object, Stream, Text
 	testConnectionOnStartup (common) - Specifies whether to test the connection on startup. This ensures that when Camel starts that all the JMS consumers have a valid connection to the JMS broker
 	password (security) - Password to use with the ConnectionFactory. You can also configure username/password directly on the ConnectionFactory.
	username (security) - Username to use with the ConnectionFactory. You can also configure username/password directly on the ConnectionFactory.
 
 */

/* JMS CONSUMER QUERY PARAMETERS:
	acknowledgementModeName (consumer) - The JMS acknowledgement name, which is one of: SESSION_TRANSACTED, CLIENT_ACKNOWLEDGE, AUTO_ACKNOWLEDGE, DUPS_OK_ACKNOWLEDGE
 	asyncConsumer (consumer) - Whether the JmsConsumer processes the Exchange asynchronously
 	maxConcurrentConsumers (consumer) - Specifies the maximum number of concurrent consumers when consuming from JMS (not for request/reply over JMS)
 	replyTo (consumer) - Provides an explicit ReplyTo destination, which overrides any incoming value of Message.getJMSReplyTo()
 	replyToDeliveryPersistent (consumer) - Specifies whether to use persistent delivery by default for replies
 	selector (consumer) - Sets the JMS selector to use
 	subscriptionDurable (consumer) - Set whether to make the subscription durable
 	subscriptionName (consumer) - Set the name of a subscription to create
 	acceptMessagesWhileStopping (consumer) - Specifies whether the consumer accept messages while it is stopping
 	exchangePattern (consumer) - Sets the exchange pattern when the consumer creates an exchange. The value can be one of: InOnly, InOut, InOptionalOut
 	replyToSameDestinationAllowed (consumer) - Whether a JMS consumer is allowed to send a reply message to the same destination that the consumer is using to consume from
 	receiveTimeout (advanced) - The timeout for receiving messages (in milliseconds).
 	
 */

/* JMS CONSUMER QUERY PARAMETERS:
 	deliveryMode (producer) - Specifies the delivery mode to be used. Possibles values are those defined by javax.jms.DeliveryMode. NON_PERSISTENT = 1 and PERSISTENT = 2
 	lazyStartProducer (producer) - Whether the producer should be started lazy (on the first message)
 	priority (producer) - Values greater than 1 specify the message priority when sending (where 0 is the lowest priority and 9 is the highest)
 	timeToLive (producer) - When sending messages, specifies the time-to-live of the message (in milliseconds)
 	disableTimeToLive (producer) - Use this option to force disabling time to live
 	explicitQosEnabled (producer) - Set if the deliveryMode, priority or timeToLive qualities of service should be used when sending messages
 	replyToMaxConcurrentConsumers (producer) - Specifies the maximum number of concurrent consumers when using request/reply over JMS
 	replyToOverride (producer) - Provides an explicit ReplyTo destination in the JMS message, which overrides the setting of replyTo
 	replyToType (producer) - Allows for explicitly specifying which kind of strategy to use for replyTo queues when doing request/reply over JMS. Possible values are: Temporary, Shared, or Exclusive
 	requestTimeout (producer) - The timeout for waiting for a reply when using the InOut Exchange Pattern (in milliseconds). The default is 20 seconds
 	correlationProperty (producer) - When using InOut exchange pattern use this JMS property instead of JMSCorrelationID JMS property to correlate messages
 	forceSendOriginalMessage (producer) - When using mapJmsMessage=false Camel will create a new JMS message to send to a new JMS destination if you touch the headers (get or set) during the route
 	replyToDestinationSelectorName (producer) - Sets the JMS Selector using the fixed name to be used so you can filter out your own replies from the others when using a shared queue
 	useMessageIDAsCorrelationID (advanced) - Specifies whether JMSMessageID should always be used as JMSCorrelationID for InOut messages

 */

/*
 * CXF-RS QUERY PARAMETERS:
 	features (common) - Set the feature list to the CxfRs endpoint.
 	modelRef (common) - This option is used to specify the model file which is useful for the resource class without annotation.
 	providers (common) - Set custom JAX-RS provider(s) list to the CxfRs endpoint. You can specify a string with a list of providers to lookup in the registy separated by comma.
 	resourceClasses (common) - The resource classes which you want to export as REST service. Multiple classes can be separated by comma.
 	schemaLocations (common) - Sets the locations of the schema(s) which can be used to validate the incoming XML or JAXB-driven JSON.
 	bindingStyle (consumer) -  Sets how requests and responses will be mapped to/from Camel. Two values are possible: SimpleConsumer and Default
 	publishedEndpointUrl (consumer) - This option can override the endpointUrl that published from the WADL which can be accessed with resource address url plus _wadl
 	exchangePattern (consumer) - Sets the exchange pattern when the consumer creates an exchange. The value can be one of: InOnly, InOut, InOptionalOut
	lazyStartProducer (producer) - Whether the producer should be started lazy (on the first message).
	httpClientAPI (producer) - If it is true, the CxfRsProducer will use the HttpClientAPI to invoke the service. If it is false, the CxfRsProducer will use the ProxyClientAPI to invoke the service
 */

/*
 * CXF QUERY PARAMETERS:
 	dataFormat (common) - The data type messages supported by the CXF endpoint. The value can be one of: PAYLOAD, RAW, MESSAGE, CXF_MESSAGE, POJO
 	wrappedStyle (common) - The WSDL style that describes how parameters are represented in the SOAP body. If the value is false, CXF will chose the document-literal unwrapped style, If the value is true, CXF will chose the document-literal wrapped style
 	exchangePattern (consumer) - Sets the exchange pattern when the consumer creates an exchange. The value can be one of: InOnly, InOut, InOptionalOut
 	lazyStartProducer (producer) - Whether the producer should be started lazy (on the first message).
 	username (security) - This option is used to set the basic authentication information of username for the CXF client.
 	password (security) - This option is used to set the basic authentication information of password for the CXF client.
 	portName (service) - The endpoint name this service is implementing, it maps to the wsdl:portname. In the format of ns:PORT_NAME where ns is a namespace prefix valid at this scope.
 	publishedEndpointUrl (service) - This option can override the endpointUrl that published from the WSDL which can be accessed with service address url plus wsd
 	serviceClass (service) - The class name of the SEI (Service Endpoint Interface) class which could have JSR181 annotation or not.
 	serviceName (service) - The service name this service is implementing, it maps to the wsdl:servicename.
 	wsdlURL (service) - The location of the WSDL. Can be on the classpath, file system, or be hosted remotely.
 */

/*
   
 	List<Artikal> listaArtikala = new ArrayList<Artikal>();
	listaArtikala.add(new Artikal("Proizvod2", "povrce", "BCD", 50, 20.0));
	listaArtikala.add(new Artikal("Proizvod3", "povrce", "AFGD", 10, 20.0));
	Narudzba nrdzb = new Narudzba(
		new Narucilac("Narucilac2", "adresaNaruioca2", "kontaktNarucioca2", "Sombor"), 
		listaArtikala, 
		"obicna",
		new Date()
	);
	String id = bean1.slanjeNarudzbe(nrdzb);
	
	System.out.println("PRIJEM OBAVESTENJA");
	System.out.println(bean1.prijemObavestenja("Narucilac2",id));
	
	---------------------
	
	List<String> lista = bean2.ponudaOtprema();
	for (String string : lista) {
		System.out.println(string);
	}
	
	System.out.println(bean1.potvrdiOtpremu("5f67904b5335cd1e9c53042c", 
				new Dostavljac("Dostavljac1", "AdresaDostavljaca 1", "KontaktDostavljaca 1", "Mesto 1")));
				
*/

/*
	// prema WildFly Camel dokumentaciji
	restConfiguration().component("undertow")
		.contextPath("/rest").port(8080).bindingMode(RestBindingMode.json);

	rest("/say").consumes("application/json").produces("application/json")
		.get("/hello").to("direct:hello");

	from("direct:hello").log(simple("${body}").getText());
*/