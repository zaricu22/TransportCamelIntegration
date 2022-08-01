package camel.wildfly_camel_cdi;


import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.jndi.JndiBeanRepository;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;

import entiteti.Artikal;
import entiteti.Narudzba;
import entiteti.PotvrdaNarudzbe;
import opcije.OtpremaAggregatorStrategy;
import opcije.RezervisanjeEnrichStrategy;
import opcije.InfoEnrichStrategy;
import opcije.NarudzbaNormalizer;
import opcije.RokCompletionBean;
import roba.RobneZaliheSLSBRemote;
import transport.message.PrevoznikSLSBRemote;
import opcije.ArtikliSplitterStrategy;
import opcije.BasicAuthBean;

public class NarudzbaRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		// NAPOMENA: Auth & Author, Izuzeci, Clients(Producer types), Asinh i Paral izvrs, 
		// Automatizacija procesa, Silosi podataka, Timer Analiza
		
		// !! Treba zastititi rute od pristupa drugih komponenti i drugih ruta ali malo kompleksnije podesavanje
		
		onException(RuntimeCamelException.class) // Upravljanje izuzecima
			.log("EXCEPTION OCCURED!"); // Samo ispis bilo kakve greske ili izuzetka bez posebne obrade
		
		//--------------------------------------	EJB		-----------------------------------------
		/** 
		  * Simple way of consuming remote bean with JndiBeanRepository, 
		  * camel's EJB component is deprecated and it isn't supported by new 'camel-core'
		  * NAPOMENA: Moramo imati Remote interface i to u istom paketu i klasi kao na udaljenom serveru
		 */
        JndiBeanRepository repository = new JndiBeanRepository(createEjbSkladisteContext());
        RobneZaliheSLSBRemote rzsb = repository.lookupByNameAndType("ejb:/Skladiste-0.0.1-SNAPSHOT/RobneZaliheSLSB!roba.RobneZaliheSLSBRemote", RobneZaliheSLSBRemote.class);
        getContext().getRegistry().bind("RobneZaliheSLSB", rzsb);
        repository = new JndiBeanRepository(createEjbOtpremaContext());
        PrevoznikSLSBRemote psb = repository.lookupByNameAndType("ejb:/Otprema-0.0.1-SNAPSHOT/PrevoznikSLSB!transport.message.PrevoznikSLSBRemote", PrevoznikSLSBRemote.class);
        getContext().getRegistry().bind("PrevoznikSLSB", psb);
        
        //--------------------------------------	 JMS	-----------------------------------------
	    /** 
	      * 'wildfly-jms-client-bom' dependency - provided by WildFly Camel patch
  		  * 'camel-jms' dependency - provided by WildFly Camel patch
  		  * !!! We must specify user on remote WildFly server in 'guest' group and use date 
  		  * !!! We must specify 'Entries' of queues and topics on WildFly instance in 'java:jboss/exported/jms/..' not only 'java:/jms/..'
  		  * !!! We must use standalone-full-camel.xml config on client server
  		  * Lookup jms/RemoteConnectionFactory and create/add JMSComponent with it
  		 */
		Context initialContext = createJMSContext();
		ConnectionFactory connectionFactory = (ActiveMQJMSConnectionFactory) initialContext.lookup("java:jms/RemoteConnectionFactory");
		ActiveMQComponent component = new ActiveMQComponent();
		component.setConnectionFactory(connectionFactory);
		component.setUsername("jmsClient1"); 	// specify on component for all endpoint
		component.setPassword("secret");		//  or in particular endpoint URL (..?username=X&password=Y)
		getContext().addComponent("jms", component);
		
		//--------------------------------------	 BEAN REGISTRY	 -----------------------------------------
		getContext().getRegistry().bind("NarudzbaNormalizer", new NarudzbaNormalizer());
		getContext().getRegistry().bind("magacinMongoDB", MongoClients.create("mongodb://localhost:27017"));
		getContext().getRegistry().bind("JacksonJsonProvider", new JacksonJsonProvider());
		getContext().getRegistry().bind("BasicAuthBean", new BasicAuthBean());
		
		
		//--------------------------------------	 IMPLEMENTATION	 -----------------------------------------
		
		// EVENT-DRIVEN CONSUMER - automatski dobavlja pristigle poruke
		// COMPETING-CONSUMER - omogucava da se vise poruka koje pristizu obrade istovremeno tj. nezavisno jedna od druge
		/** 
		  * CXF comp is async by default, 
		  * it use default thread pool to parallel process any request independently from each other 
		  * and return response in async manner
		  * ?! direct, SEDA, synchronous=true have no impact on parallel processing of cxf requests
		 */
		from("cxf://http://localhost:8083/wildfly-camel-cdi/webservices/narucivanje?"
        		+ "dataFormat=PAYLOAD"	// type of message's body injected into the route(payload = soap:body)
        		+ "&serviceClass=" + NarudzbaServiceWS.class.getName())
			.choice()
				.when(method("BasicAuthBean","auth(\"CXFWS:123\")"))  // HTTP Basic Authorization
					// vraca PotvrduNarudzbe kao rezultat operacije insert u MongoDB
					.log("USAO WS2")
					.to("direct:operationSelector")
					.log("IZASAO")
				.endChoice()
			.end();
		
		// EVENT-DRIVEN CONSUMER
		// COMPETING-CONSUMER
		/** 
		  * ?! Basic auth username and password query params doesn't work
		  * !! Poruke dok se obradjuju ne mogu biti u kriptovanom stanju vec samo one koje se prenose
		 */
		from("cxfrs://http://localhost:8083/wildfly-camel-cdi/rest?"
 				+ "bindingStyle=SimpleConsumer"		// auto mapping request to message
				+ "&providers=JacksonJsonProvider"
 				+ "&resourceClasses=" + NarudzbaServiceRS.class.getName())
			.choice() 
				.when(method("BasicAuthBean","auth(\"CXFRS:123\")"))  // HTTP Basic Authorization
					.to("direct:operationSelector")
					.log("IZASAO")
				.endChoice()
			.end();

		// DATAFORMAT
		from("direct:dataformatPotvrdaNarudzbe")
			/** 
			  * .setBody(Expression) iz ProcessorDefinition ne prima Object kao param
			  * pa moramo da koristimo .setBody(Object) iz Message putem ovog zasebnog Processor-a
			 */
			// MESSAGE-TRANSLATOR EIP
			.process(new Processor() { // prevodi bson.Document u InputStream za potrebe unmarshal() metode
				@Override
				public void process(Exchange exchange) throws Exception {
					// bson.Document -> JSON String -> InputStream
					exchange.getIn().setBody(new ByteArrayInputStream(exchange.getIn().getBody(org.bson.Document.class).toJson().getBytes()));
				}
			})
			
			.unmarshal().json(JsonLibrary.Jackson, PotvrdaNarudzbe.class); // ocekuje InputStream 
			/**
			  * .marshal().xstream()		Mozemo dodatno objekat prevesti u neki XML format podatka
			  * .marshal().jacksonxml()	ali CXF komponenta zna sama da to ucini od Objekta na osnovu JAXB anotacija njegove klase
			  * .marshal().jaxb()
			  * .marshal().soapjaxb() <- zahteva semu
			 */

		// NORMALIZER EIP + CONTENT-BASED ROUTER EIP
		from("direct:operationSelector")
			.choice()
		        .when(header("operationName").isEqualTo("slanje"))
		        	// SERVICE-ACTIVATOR EIP
		        	.to("bean:NarudzbaNormalizer?method=xmlToObject")
		        	.to("direct:splitArtikli")
					// rezultat operacije insert u MongoDB je bson.Document nije Object, CXF ne ume da radi sa JSON dokumentima
					.to("direct:dataformatPotvrdaNarudzbe")
		        .endChoice()
		        .when(header("operationName").isEqualTo("slanjeNarudzbe"))
		        	.to("bean:NarudzbaNormalizer?method=jsonToObject")
		        	.to("direct:splitArtikli")
		        .endChoice()
		        .when(header("operationName").in("prijem","prijemOtpreme"))
		        	// JMS Durable Pub-Sub Topic for multiple subscribers(CXF clients) with same JMSConsumer(this server, shared)
		        	// we use client provided subscriptionName from HTTP request header auto mapped to IN message header
	        		.to("bean:PrevoznikSLSB?method=ponudaOtprema(${header.subscriptionName})")
		        .endChoice()
			.end();

		// SPLITTER EIP + CONTENT-ENRICHER EIP + CONTENT-BASED ROUTER EIP
		from("direct:splitArtikli")
			// MESSAGE-TRANSLATOR EIP
			.process(new Processor() { // dodajemo info o naruciocu i vaznostiIsporuke u svaki artikal za dalju nezavisnu obradu
				@Override
				public void process(Exchange exchange) throws Exception {
					Narudzba nar = exchange.getIn().getBody(Narudzba.class);
					// Stringove koje koristimo kao parametre metoda moraju biti trim-ovani inace moze napraviti problem
					nar.getNarucilac().setMesto(nar.getNarucilac().getMesto().trim());
					nar.setVaznostIsporuke(nar.getVaznostIsporuke().trim());
					for(Artikal a : nar.getArtikli()) {
						a.setNarucilac(nar.getNarucilac());
						a.setVaznostIsporuke(nar.getVaznostIsporuke());
					}
				}
			})
			/** 
			 * 	Options: parallelProcessing, parallelAggregate, onPrepareRef, shareUnitOfWork 
			 *  Properties: CamelSplitIndex, CamelSplitSize, CamelSplitComplete
			 */
			// NAPOMENA: ostalo neodradjena prioritetna obrada hitnih i obicnih narudzbi, brzina izvrsavanja ne daje na znacaju toj razlici 
			.split(simple("${body.artikli}"), new ArtikliSplitterStrategy()) // simple ognl support access to fields and methods
				.parallelProcessing().threads(1, 5)
				/** 
				  * .parallelAggregate() - treba biti strogo thread-safe
				  * .pollEnrich: .recieve(), .recieveNoWait(), .recieve(timeout)
				  * omogucava nam da kombinujemo originalnu poruku i odgovor eksternog resursa, 
				  * ako bi samo direktno pozvali eksterni resurs izgubili bi originalnu poruku 
				 */
				.enrich("direct:enrichRezervisanjeArtikla", new RezervisanjeEnrichStrategy())	// koristi se ista instanca AggregatorStrategy, ne kreiraju se zasebne  
				.choice()
					.when(header("dostupnost"))
						.enrich("direct:enrichInfoArtikla", new InfoEnrichStrategy())
					.endChoice()
				.end()
				.to("direct:filterDostupnost")
			.end()
			// splitter vraca PotvrduNarudzbe
			.to("mongodb:magacinMongoDB?database=magacin&collection=narudzbe&operation=insert");
			// operacija insert u MongoDB vraca uspesno dodati bson.Document sa _id	
			// mogli smo koristiti enrich da dalje rutom umesto odgovora eksternog resursa prosledimo originalnu poruku izbegavajuci data format

		// CONTENT-ENRICHER EIP + SERVICE ACTIVATOR EIP
		from("direct:enrichRezervisanjeArtikla")
			// SERVICE-ACTIVATOR EIP
			.to("bean:RobneZaliheSLSB?method=rezervisanjeArtikla(${body.kataloskiBroj},${body.kolicina})");

		// CONTENT-ENRICHER EIP
		from("direct:enrichInfoArtikla")
			/** 
			  * .setHeader(String, Expression) iz ProcessorDefinition ne prima Object kao param
			  * pa moramo da koristimo .setHeader(String, Object) iz Message putem zasebnog Processor-a
			 */
			// MESSAGE-TRANSLATOR EIP
			.process(new Processor() { // dodajemo info o artiklu(dostupnost, tip, naziv, tezina) iz MongoDB na osnovu kataloskogBroja
				@Override
				public void process(Exchange exchange) throws Exception {
					String katBr = exchange.getIn().getBody(Artikal.class).getKataloskiBroj().trim();
					exchange.getIn().setHeader(MongoDbConstants.CRITERIA, Filters.eq("kataloskiBroj", katBr));
				}
			})
			
			// Mongo Request Header: CamelMongoDbCriteria=Filter{fieldName='kataloskiBroj', value=BCD}
			.to("mongodb:magacinMongoDB?database=magacin&collection=roba&operation=findOneByQuery");
			// Mongo Response Header: CamelMongoDbResultTotalSize/PageSize=3

		// MESSAGE FILTER EIP
		from("direct:filterDostupnost")
			.filter(simple("${header.dostupnost} == true"))
			.to("direct:aggregateOtprema");

		// AGGREGATOR EIP + MULTICAST EIP
		from("direct:aggregateOtprema")
			// MESSAGE-TRANSLATOR EIP
			.process(new Processor() {	// odredimo tipKamiona na osnovu tipArtikla
				@Override
				public void process(Exchange exchange) throws Exception {
					String tipArtikla = exchange.getIn().getBody(Artikal.class).getTipProizvoda();
					if(tipArtikla != null) {
						if (tipArtikla.matches("voce|povrce|meso|suvomesnato"))
							exchange.getIn().setHeader("tipKamiona", "HLADNJACA");
						else if (tipArtikla.matches("aparat|uredjaj"))
							exchange.getIn().setHeader("tipKamiona", "SUV ZATVOREN");
						else if (tipArtikla.matches("konditorski|pice|zitarice|pasterizovano|hemija"))
							exchange.getIn().setHeader("tipKamiona", "SUV POKRIVEN");
					}
				}
			})
			/** 
			 * 	Options: parallelProcessing, eagerCheckCompletion
			 */
			// NAPOMENA: ostalo neoptimizovano selektovanje ruta jer ne gleda moguce rute vec samo mesto
			.aggregate(simple("${body.narucilac.mesto} + ${header.tipKamiona} + ${body.vaznostIsporuke}"), 	// simple's ognl support
					new OtpremaAggregatorStrategy()	  
			)
				// NAPOMENA: ostalo mogucnost pretovarivanja kamiona jer prvo napuni pa proverava dali je prepunjen
				.completionPredicate(simple("${body.preostalaNosivost} <= 0")) 	// by default completionPredicate use aggregated Exchange
				.completionTimeout(method(new RokCompletionBean(), "complete")) // simple's date:command doesn't return long value
				// .eagerCheckCompletion() - completionPredicate will use incoming Exchange instead of default aggregated Exchange
				.parallelProcessing().threads(1, 5)
			// aggregator vraca Otpremu za dostavljace
			/**  
			  * Multicast EIP allows to route the same message to a number of endpoints and process their responses in a different way
			  * AggregationStrategy to be used to assemble the replies from the multicasts, into a single outgoing message from the Multicast. 
			  * By default Camel will use the last reply as the outgoing message. 
			  * You can also use a POJO as the AggregationStrategy 
			 */
			.multicast()
				//.parallelProcessing()//.parallelAggregate()
				.to("mongodb:magacinMongoDB?database=magacin&collection=otpreme&operation=insert")
				// operacija insert u MongoDB vraca uspesno dodati bson.Document sa _id
				/** 
				  * Users of ActiveMQ 5.x(older) should use ActiveMQ component
		          * Users of WildFly ActiveMQ Artemis(HornetQ) 2.x(successor ) should use the JMS component     
		 		  * JMS component look for sender's message original class on client app to bind message 
		 		  * Message type: Text, Object, Bytes, Map 
		 		 */
				// NAPOMENA: mora se navesti tipPoruke i dali ocekujemo odgovor(po default: InOut)
				.to("jms:topic:ponudaOtprema?jmsMessageType=Text&exchangePattern=InOnly")
			.end();
			
	}
	
	/** 
	  * JNDI applications need a way to communicate various preferences and properties 
      * that define the environment in which naming and directory services are accessed
	  * 'wildfly-ejb-client-bom' dependency
	  * Java EJB-client properties (optimized for lookups beans)
	 */
	private static Context createEjbSkladisteContext() throws NamingException {
    	
    	final Properties properties = new Properties();
    	properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
    	properties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false"); 
    	properties.put("remote.connections", "moja1");
    	properties.put("remote.connection.moja1.host", "localhost");
    	properties.put("remote.connection.moja1.port", "8081"); 
    	properties.put("remote.connection.moja1.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

        return new InitialContext(properties);
        
    }
	private static Context createEjbOtpremaContext() throws NamingException {
    	
    	final Properties properties = new Properties();
    	properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
    	properties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false"); 
    	properties.put("remote.connections", "moja2");
    	properties.put("remote.connection.moja2.host", "localhost");
    	properties.put("remote.connection.moja2.port", "8080"); 
    	properties.put("remote.connection.moja2.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

        return new InitialContext(properties);
        
    }
	
	/** 
	  * 'wildfly-jms-client-bom' dependency
      * Java Naming-client properties (lookup any JNDI)
     */
    private Context createJMSContext() throws NamingException {
    	
	  final Properties env = new Properties();
	  env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
	  env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, "http-remoting://localhost:8080"));
	  //env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", "jmsClient1"));
	  //env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", "secret"));
	  
	  return new InitialContext(env);
	  
	}
}

