package client;

import java.util.Properties;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;

public class NarucilacJMSClient {
	
	public static void main(String[] args) {
		final Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, "http-remoting://localhost:8080"));
		Context initialContext = null;
		JMSContext context = null;
		Destination prijemNarudzbiQueue = null;
		Destination obavestenjeNaruciocaQueue = null;
		try {
			initialContext = new InitialContext(env);
			ActiveMQJMSConnectionFactory factory = ((ActiveMQJMSConnectionFactory)initialContext.lookup("java:jms/RemoteConnectionFactory"));
			factory.setUser("jmsClient1");
			factory.setPassword("secret");
			context = factory.createContext();
			prijemNarudzbiQueue = (Destination) initialContext.lookup("java:jms/queue/prijemNarudzbi");
			obavestenjeNaruciocaQueue = (Destination) initialContext.lookup("java:jms/queue/obavestenjeNarucioca");
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		StringBuilder narudzba = new StringBuilder();
		narudzba.append(""
				+ "Narucilac: \n"
					+ "\t nazivNarucioca: Narucilac2, \n"
					+ "\t adresaNarucioca: adresaNaruioca2, \n"
					+ "\t kontaktNarucioca: kontaktNarucioca2, \n"
					+ "\t mesto: Sombor; \n"
				+ "vaznostIsporuke: obicna; \n"
				+ "datum: 2021-02-17 13:50:45; \n"
				+ "artikli: [ \n"
					+ "\t nazivProizvoda: Proizvod2, \n"
					+ "\t tipProizvoda: povrce, \n"
					+ "\t kataloskiBroj: BCD, \n"
					+ "\t kolicina: 50, \n"
					+ "\t tezina: 20.0; \n"
					+ "\t nazivProizvoda: Proizvod3, \n"
					+ "\t tipProizvoda: povrce, \n"
					+ "\t kataloskiBroj: AFGD, \n"
					+ "\t kolicina: 10, \n"
					+ "\t tezina: 20.0; \n"
				+ "] ");
		
		TextMessage message = context.createTextMessage();
		try {
			message.setText(narudzba.toString());
			message.setIntProperty("MsgPriority", 4); // default JMSPriority value
			message.setStringProperty("nazivNarucioca", "MARKET 1");
			message.setJMSReplyTo(obavestenjeNaruciocaQueue);

			JMSProducer producer = context.createProducer();
			producer.send(prijemNarudzbiQueue, message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
