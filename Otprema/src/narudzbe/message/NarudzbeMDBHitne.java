package narudzbe.message;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import entiteti.Artikal;
import entiteti.Narudzba;
import narudzbe.NarudzbeDBSingleton;
import transport.TransportSLSBLocal;

@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(
						propertyName = "destination", propertyValue = "java:/jms/queue/prijemNarudzbi"), 
				@ActivationConfigProperty(
						propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				@ActivationConfigProperty(
						propertyName = "messageSelector", propertyValue = "MsgPriority >= 5")
		}, 
		mappedName = "java:/jms/queue/prijemNarudzbi")
public class NarudzbeMDBHitne implements MessageListener {
	
	@EJB
	TransportSLSBLocal tsb;
	
	@EJB
	NarudzbeDBSingleton nsb;
	
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory")
	JMSContext context;
	
    public NarudzbeMDBHitne() {
    	super();
    }
	
    public void onMessage(Message message) {
    	
    	ObjectMessage objectMessage = (ObjectMessage) message;
    	Narudzba narudzba = null;
		try {
			narudzba = (Narudzba) objectMessage.getObject();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		List<PotvrdaItem> listaPotvrda = new ArrayList<PotvrdaItem>();
		for (Artikal artikalNarudzbe : narudzba.getArtikli()) {
			PotvrdaItem potvrdaItem = tsb.obradaArtiklaNarudzbe(artikalNarudzbe, narudzba.getVaznostIsporuke(), narudzba.getDatum(), narudzba.getNarucilac());
			listaPotvrda.add(potvrdaItem);
		}
		
		String potvrdaMsg = null;
		try {
			potvrdaMsg = konvertovanjePotvrdaJson(listaPotvrda, message.getStringProperty("idNarudzbe"));
		} catch (JMSException e1) {
			e1.printStackTrace();
		}
		
		ObjectMessage resMsg = context.createObjectMessage();
		if(potvrdaMsg != null) {
			try {
					resMsg.setJMSCorrelationID(message.getJMSMessageID());
					resMsg.setStringProperty("nazivNarucioca", message.getStringProperty("nazivNarucioca"));
					resMsg.setStringProperty("idNarudzbe", message.getStringProperty("idNarudzbe"));
					resMsg.setObject(potvrdaMsg);
					
					JMSProducer producer = context.createProducer();
					producer.send(message.getJMSReplyTo(), resMsg);
					
					System.out.println("POVRATNA PORUKA NARUCIOCU!");
					narudzba.setStatusObrade("obradjena");
					nsb.azurirajStatusNarudzbe(narudzba.getId(), "obradjena");
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		
    }
    
    private String konvertovanjePotvrdaJson (List<PotvrdaItem> listaPotvrda, String idNarudzbe) {
    	JsonArrayBuilder artikliBuilder = Json.createArrayBuilder();
		JsonObjectBuilder narudzbaBuilder = Json.createObjectBuilder()
				.add("narudzbaID", idNarudzbe)
				.add("artikli", artikliBuilder);
		
		for(PotvrdaItem potvrdaItem : listaPotvrda) {
			JsonObjectBuilder artikalBuilder = Json.createObjectBuilder()
					  .add("nazivProizvoda", potvrdaItem.getArtikal().getNazivProizvoda())
					  .add("tipProizvoda", potvrdaItem.getArtikal().getTipProizvoda())
					  .add("kolicina", potvrdaItem.getArtikal().getKolicina())
					  .add("tezina", potvrdaItem.getArtikal().getTezina());
			artikliBuilder.add(
				Json.createObjectBuilder()
					  .add("kataloskiBroj", potvrdaItem.getArtikal().getKataloskiBroj())
					  .add("artikal", artikalBuilder)
					  .add("tipKamiona", potvrdaItem.getTipKamiona())
					  .add("rokIsporuke", new SimpleDateFormat("DD/MM/YYYY").format(potvrdaItem.getRokIsporuke()))
			);
		}
		
		Map<String, Boolean> config = new HashMap<>();
		config.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(config);
		String jsonString = null;
		try(Writer writer = new StringWriter()) {
		    writerFactory.createWriter(writer).write(narudzbaBuilder.build());
		    jsonString = writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	return jsonString;
    }
    
}
