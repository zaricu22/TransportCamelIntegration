package narudzbe.message;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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

/* @MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(
						propertyName = "destination", propertyValue = "java:/jms/queue/prijemNarudzbi"), 
				@ActivationConfigProperty(
						propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				// Posto je Priority Queue po default-u isklj na Message Broker-u(morao bi stalno da menja redosled poruka), 
				// mozemo kreirati vise tipova MDB da efikasno opsluzimo poruke razlicitih prioriteta
				// koristimo messageSelector koji bira poruke na osnovu vrednosti property-a(dodatna polja zaglavlja poruke)
				@ActivationConfigProperty(
						propertyName = "messageSelector", propertyValue = "MsgPriority < 5")
		}, 
		mappedName = "java:/jms/queue/prijemNarudzbi") */
public class NarudzbeMDBObicne implements MessageListener {
	
	@EJB
	TransportSLSBLocal tsb;
	
	@EJB
	NarudzbeDBSingleton nsb;
	
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory")
	JMSContext context;

    public NarudzbeMDBObicne() {
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

		// Obradjujemo 1 po 1 artikal jer se mogu naci u zasebnim isporukama
		Map<String, PotvrdaItem> mapaPotvrda = new HashMap<>();
		int redniBrojArtikla = 1;// privremeno radi testa
		for (Artikal artikalNarudzbe : narudzba.getArtikli()) {
			System.out.println("ARTIKAL " + redniBrojArtikla);
			redniBrojArtikla++;
			// Provera dostupnosti artikla
			// Odabir odgovarajuce otpreme ako vec postoji, u suprotnom kreiranje nove otpreme
			PotvrdaItem potvrdaItem = tsb.obradaArtiklaNarudzbe(artikalNarudzbe, narudzba.getVaznostIsporuke(), narudzba.getDatum(), narudzba.getNarucilac());
			mapaPotvrda.put(artikalNarudzbe.getKataloskiBroj(),potvrdaItem);
			
		}
		
		String potvrdaMsg = null;
		try {
			potvrdaMsg = konvertovanjePotvrdaNarudzbeJson(mapaPotvrda, message.getStringProperty("idNarudzbe"));
		} catch (JMSException e1) {
			e1.printStackTrace();
		}
		
		// Obavestavanje narucioca artikla o obradi artikla
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
					nsb.azurirajStatusNarudzbe(message.getStringProperty("idNarudzbe"), "obradjena");
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

    }
    
	private String konvertovanjePotvrdaNarudzbeJson (Map<String, PotvrdaItem> mapaPotvrda, String idNarudzbe) {
    	
		JsonArrayBuilder artikliBuilder = Json.createArrayBuilder();
		
		for(Map.Entry<String, PotvrdaItem> entry : mapaPotvrda.entrySet()) {
			if(entry.getValue() != null) {
				JsonObjectBuilder artikalBuilder = Json.createObjectBuilder()
						  .add("nazivProizvoda", entry.getValue().getArtikal().getNazivProizvoda())
						  .add("tipProizvoda", entry.getValue().getArtikal().getTipProizvoda())
						  .add("kolicina", entry.getValue().getArtikal().getKolicina())
						  .add("tezina", entry.getValue().getArtikal().getTezina());
				artikliBuilder.add(
					Json.createObjectBuilder()
						  .add("kataloskiBroj", entry.getValue().getArtikal().getKataloskiBroj())
						  .add("tipKamiona", entry.getValue().getTipKamiona())
						  .add("rokIsporuke", new SimpleDateFormat("DD/MM/YYYY").format(entry.getValue().getRokIsporuke()))
						  .add("artikal", artikalBuilder)
				);
			}
			else {
				artikliBuilder.add(
					Json.createObjectBuilder()
						  .add("kataloskiBroj", entry.getKey())
						  .addNull("artikal")
				);
			}
		}
		
		JsonObjectBuilder narudzbaBuilder = Json.createObjectBuilder()
				.add("narudzbaID", idNarudzbe)
				.add("artikli", artikliBuilder);
		
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
