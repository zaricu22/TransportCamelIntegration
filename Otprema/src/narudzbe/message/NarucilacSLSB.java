package narudzbe.message;



import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;


import entiteti.Narudzba;
import narudzbe.NarudzbeDBSingleton;


/**
 * Session Bean implementation class NarudzbeSender
 */
@Stateless
public class NarucilacSLSB implements NarucilacSLSBRemote {
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory")
	JMSContext context;
	
	@Resource(mappedName="java:/jms/queue/prijemNarudzbi")
	private javax.jms.Destination prijemNarudzbiQueue;
	
	@Resource(mappedName="java:/jms/queue/obavestenjeNarucioca")
	private javax.jms.Destination obavestenjeNaruciocaQueue;
	
	@EJB
	NarudzbeDBSingleton ns;
	
    public NarucilacSLSB() {
    	super();
    }
    
    // TODO Autentifikacija odakle se uzima nazivNarucioca a ne da svako svakome moze da uzima odgovore

    // Slanje narudzbe u queue i potvrda da je narudzba uspesno poslata
    // REMOTE METHOD
    public String slanjeNarudzbe(Narudzba narudzba) {
    	String idNarudzbe = ns.sacuvajNarudzbu(narudzba);
    	
    	if(idNarudzbe != null) {
    		ObjectMessage message = context.createObjectMessage();
			try {
				message.setObject(narudzba);
				// iako messageSelector bi trebalo da gleda i header fields i header properties(iz nekog razloga gleda samo drugo)
				// moramo rucno setovati header property 
				if(narudzba.getVaznostIsporuke().equals("hitna"))
					message.setIntProperty("MsgPriority", 9);
				message.setIntProperty("MsgPriority", 4); // default JMSPriority value
				message.setStringProperty("idNarudzbe", idNarudzbe);
				message.setStringProperty("nazivNarucioca", narudzba.getNarucilac().getNazivNarucioca());
				message.setJMSReplyTo(obavestenjeNaruciocaQueue);

				JMSProducer producer = context.createProducer();
				producer.send(prijemNarudzbiQueue, message);
				
				return idNarudzbe;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	return null;
    }
    
    
    // Mogucnost pracenja obradjenih artikala
    // REMOTE METHOD
    @Override
    public String prijemObavestenja(String nazivNarucioca, String idNarudzbe) {
    	// createConsumer(Destination destination, String messageSelector)
    	JMSConsumer consumer = context.createConsumer(obavestenjeNaruciocaQueue, 
    			"nazivNarucioca = '"+nazivNarucioca+"' AND idNarudzbe = '"+idNarudzbe+"'");
    	
    	String obavestenje = null;
    	
    	// Synchronous message recieving, zato sto nam je vazno da vrati rezultat odmah
    	Message msg;
    	msg = consumer.receiveNoWait(); // ako nema trenutno dostupne poruke nece blokirati program
    	try {
			if(msg != null &&msg.isBodyAssignableTo(String.class)) {
				obavestenje = msg.getBody(String.class);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
    	
    	consumer.close();

    	return obavestenje;
    }
    
    
}
