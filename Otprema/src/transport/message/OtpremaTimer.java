package transport.message;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.Calendar;
import java.util.Date;


import mongoclient.MongoClientSingleton;

@Stateless
public class OtpremaTimer {
	
	@Inject
	@JMSConnectionFactory("java:/ConnectionFactory")
	JMSContext context;
	
	@Resource(mappedName="java:/jms/topic/ponudaOtprema")
	private javax.jms.Destination ponudaOtpremaTopic;
	
	@EJB
	MongoClientSingleton mcs;
	
	MongoClient client;
	
	@PostConstruct
	public void init() {
		client = mcs.getMongoClient();
	}

    public OtpremaTimer() {
        super();
    }
	
    // Provera na kraju svakog dana i
    // obavestavanje dostavljaca 5 dana pred istek rokaIsporuke
	@Schedule(second="59", minute="59", hour="23", dayOfWeek="*",
      dayOfMonth="*", month="*", year="*", info="OtpremaTimer")
    private void scheduledTimeout(final Timer t) {
        System.out.println("@Schedule called at: " + new java.util.Date());
        
        MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");
		
		Bson filter = eq("statusObrade","u obradi");
		
		MongoCursor<Document> cursor = collection.find(filter).iterator();
		
		while(cursor.hasNext()) {
			
			Document doc = cursor.next();
			Date rokIsporuke = doc.getDate("rokIsporuke");
			Calendar calendar = Calendar.getInstance(); // by default now
			calendar.add(Calendar.DAY_OF_MONTH, 5);
			Date plusFiveDays = calendar.getTime();
			
			// Manje od 5 dana do rokaIsporuke obavestavamo dostavljace za ponudu
			if(rokIsporuke != null && rokIsporuke.before(plusFiveDays)) {
				
				Bson filter2 = eq("_id", doc.getObjectId("_id"));
				Bson update = set("statusObrade", "rezervisano");
				collection.updateOne(filter2, update);
				
				JMSProducer producer = context.createProducer();
				TextMessage textMsg = context.createTextMessage();
				try {
					textMsg.setText(doc.toJson(JsonWriterSettings.builder().outputMode(JsonMode.SHELL).build()));
					producer.send(ponudaOtpremaTopic, textMsg);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		cursor.close();
    }
}