package prostor;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import entiteti.Lokacija;
import mongoclient.MongoClientSingleton;

/**
 * Session Bean implementation class SkladisniProstorSingleton
 */
@Singleton
@LocalBean
public class SkladisniProstorDBSingleton {
	
	@EJB
	MongoClientSingleton mcs;
	
	MongoClient client;
	
	@PostConstruct
	public void init() {
		client = mcs.getMongoClient();
	}
	
    public SkladisniProstorDBSingleton() {
        super();
    }
    
    @Lock(LockType.READ)
    public List<Lokacija> vratiSlobodnaMesta() {

		List<Lokacija> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("prostor");

		MongoCursor<Document> cursor = collection.find(Filters.where("this.popunjenost < this.kapacitet"))
				.iterator();

		res = new ArrayList<Lokacija>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			Lokacija lok = konvertujDokumLokacija(doc);
			res.add(lok);
		}

		cursor.close();
		
		return res;
	}

    @Lock(LockType.READ)
    public List<Lokacija> vratiLokacijeArtikla(String kataloskiBroj) {

		List<Lokacija> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collectionRoba = datebase.getCollection("roba");
		MongoCollection<Document> collectionProstor = datebase.getCollection("prostor");

		MongoCursor<Document> cursorRoba = collectionRoba.find(Filters.eq("kataloskiBroj", kataloskiBroj))
				.iterator();

		res = new ArrayList<Lokacija>();
		while (cursorRoba.hasNext()) {
			Document docRoba = cursorRoba.next();

			Document docProstor = collectionProstor.find(Filters.eq("_id", docRoba.getObjectId("lokacija")))
					.first();
			Lokacija lok = konvertujDokumLokacija(docProstor);
			res.add(lok);
		}

		cursorRoba.close();
		
		return res;
	}

    public boolean zauzmiLokacijuArtikla(String idRobe, String idLokacije, Integer prijemniBroj) {

        MongoCollection<Document> collectionRoba = client.getDatabase("magacin").getCollection("roba");
        MongoCollection<Document> collectionProstor = client.getDatabase("magacin").getCollection("prostor");

        // Azuriranje lokacije 'robe'
		Bson filter = eq("_id", new ObjectId("5f5fb9007c6e0b33300b44bf"));
		Bson update = set("lokacija", new ObjectId("5f64f7fe5335cd1e9c530418"));
		UpdateResult ur = collectionRoba.updateOne(filter, update);
		System.out.println("UR1 :"+ur.getMatchedCount());
		if(ur.getMatchedCount() > 0) {
			// Azuriranje prijemnogBroja 'robe'
			filter = eq("_id", new ObjectId("5f5fb9007c6e0b33300b44bf"));
			update = set("prijemniBroj", 3);
			ur = collectionRoba.updateOne(filter, update);
			if(ur.getMatchedCount() > 0) {
				// Azuriranje popunjenosti 'lokacije'
				filter = eq("_id", new ObjectId("5f64f7fe5335cd1e9c530418"));
				update = inc("popunjenost", 1);
				ur = collectionProstor.updateOne(filter, update);
				if(ur.getMatchedCount() > 0) {
					return true;
				}
			}
		}  
		
		return true;
	}
    
    private Lokacija konvertujDokumLokacija(Document doc) {
		Lokacija lok = new Lokacija(
				doc.getString("sektor"), 
				doc.getInteger("red").intValue(),
				doc.getInteger("nivo").intValue(), 
				doc.getInteger("kapacitet").intValue(),
				doc.getInteger("popunjenost").intValue());
		if(doc.getInteger("kolona") != null)
			lok.setKolona(doc.getInteger("kolona"));
		
		return lok;
	}
    
    
}
