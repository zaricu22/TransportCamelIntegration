package transport.message;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;

import entiteti.Dostavljac;
import transport.OtpremaDBSingleton;


/**
 * Session Bean implementation class PrevObavSLSB
 */
@Stateless
public class PrevoznikSLSB implements PrevoznikSLSBLocal, PrevoznikSLSBRemote {
	
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory")
	JMSContext context;
	
	@Resource(mappedName="java:/jms/topic/ponudaOtprema")
	private javax.jms.Destination ponudaOtpremaDestination;
	
	@Resource(mappedName="java:/jms/topic/ponudaOtprema")
	private javax.jms.Topic ponudaOtpremaTopic;
	
	@EJB
	OtpremaDBSingleton os;

    public PrevoznikSLSB() {
        super();
    }

    // REMOTE METHOD
    @Override
    public List<String> ponudaOtprema(String subscriptionName) {
    	
    	System.out.println("PRETPLATNIK: "+subscriptionName);
    	
    	JMSConsumer consumer = context.createSharedDurableConsumer(ponudaOtpremaTopic, subscriptionName);
    	
    	List<String> listaPonuda = new ArrayList<String>();
    	
    	Message msg = null;
    	msg = consumer.receiveNoWait(); 
    	while(msg != null) {
	    	try {
				if(msg != null && msg.isBodyAssignableTo(String.class)) {
					listaPonuda.add(msg.getBody(String.class));
				}
				msg = consumer.receiveNoWait(); 
			} catch (JMSException e) {
				e.printStackTrace();
			}
    	}
    	
    	consumer.close();
    	
    	System.out.println("REZ: "+listaPonuda);
    	
    	return listaPonuda;
    }
    
    // LOCAL METHOD
    @Override
    public boolean potvrdiOtpremu(String idOtpreme, Dostavljac dostavljac) {
    	return os.potvrdiOtpremu(idOtpreme, dostavljac);
    }
    
    // LOCAL METHOD
    @Override
    public String statusOtpreme(String idOtpreme) {
    	return os.statusOtpreme(idOtpreme);
    }
    
    
}
