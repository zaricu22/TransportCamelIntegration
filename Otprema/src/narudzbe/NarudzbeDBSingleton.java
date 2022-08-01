package narudzbe;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.Date;
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
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import entiteti.Artikal;
import entiteti.Narucilac;
import entiteti.Narudzba;
import mongoclient.MongoClientSingleton;

/**
 * Session Bean implementation class NarudzbeSingleton
 */
@Singleton
@LocalBean
public class NarudzbeDBSingleton {
	
	@EJB
	MongoClientSingleton mcs;
	
	MongoClient client;
	
	@PostConstruct
	public void init() {
		client = mcs.getMongoClient();
	}
	
    public NarudzbeDBSingleton() {
        super();
    }
	
    public String sacuvajNarudzbu(Narudzba narudzba) {

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("narudzbe");

		Document docNarudzba = konvertovanjeNarudzbaDokum(narudzba);

		InsertOneResult ior = collection.insertOne(docNarudzba);
		if(ior.getInsertedId() != null)
			return ior.getInsertedId().asObjectId().getValue().toString();

		return null;
	}
    
    public boolean azurirajStatusNarudzbe(String idNarudzbe, String status) {
		
		System.out.println("AZURIRAJ NARUDZBU(): \n"+idNarudzbe+" "+status);

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("narudzbe");
		
		Bson filter = eq("_id", new ObjectId(idNarudzbe));
		Bson update = set("statusObrade", status);
		UpdateResult ur = collection.updateOne(filter, update);
		if(ur.getModifiedCount() > 0) {
			System.out.println("USEPSNO AZURIRANJE!");
			return true;
		}

		return false;
	}
    
    @Lock(LockType.READ)
    public List<Narudzba> pregledNarudzbi(Date start, Date end, String statusObrade) {
    	
		List<Narudzba> res = null;
	
		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("narudzbe");
		
		Bson filter;
		if(statusObrade != null && !statusObrade.equals(""))
			filter = and(gte("datum", start), lte("datum", end), eq ("statusObrade",statusObrade));
		else
			filter = and(gte("datum", start), lte("datum", end));

		MongoCursor<Document> cursor = collection.find(filter).iterator();

		res = new ArrayList<Narudzba>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			
			Narucilac narucilac = new Narucilac(doc.getString("nazivNarucioca"),doc.getString("adresaNarucioca"),doc.getString("kontaktNarucioca"), doc.getString("mesto"));
			@SuppressWarnings("unchecked")
			List<Document> nizDokum = (ArrayList<Document>) doc.get("artikli");
			List<Artikal> listaArtikala = new ArrayList<Artikal>();
			for(Document dokumArtikal : nizDokum) {
				Artikal artikal = new Artikal(
						dokumArtikal.getString("kataloskiBroj"), 
						dokumArtikal.getInteger("kolicina")
					);
				if(dokumArtikal.containsKey("nazivProizvoda"))
					artikal.setNazivProizvoda(dokumArtikal.getString("nazivProizvoda"));
				if(dokumArtikal.containsKey("tipProizvoda"))
					artikal.setTipProizvoda(dokumArtikal.getString("tipProizvoda"));
				listaArtikala.add(artikal);
			}
			
			Narudzba nardz = new Narudzba(narucilac, listaArtikala, doc.getDate("datum"), doc.getString("statusObrade"));
			if(doc.containsKey("vaznostIsporuke"))
				nardz.setVaznostIsporuke(doc.getString("vaznostIsporuke"));
			
			res.add(nardz);
		}

		cursor.close();
		
		return res;
	}

    private Document konvertovanjeNarudzbaDokum(Narudzba narudzba) {
    	Document docNarudzba = new Document();
    	
    	if(narudzba.getId() == null)
    		docNarudzba.append("_id", new ObjectId());
    	
    	docNarudzba.append("nazivNarucioca", narudzba.getNarucilac().getNazivNarucioca())
			.append("adresaNarucioca", narudzba.getNarucilac().getAdresaNarucioca())
			.append("kontaktNarucioca", narudzba.getNarucilac().getKontaktNarucioca())
			.append("mesto", narudzba.getNarucilac().getMesto())
			.append("datum", narudzba.getDatum());
    	if(narudzba.getVaznostIsporuke() != null)
    		docNarudzba.append("vaznostIsporuke", narudzba.getVaznostIsporuke());
    	if(narudzba.getStatusObrade() != null)
    		docNarudzba.append("statusObrade", narudzba.getStatusObrade());
    	else
    		docNarudzba.append("statusObrade", "u obradi");
    	
    	List<Document> artikli = new ArrayList<Document>();
    	for(Artikal artikal : narudzba.getArtikli()) {
    		Document docArtikal = new Document()
    			.append("kataloskiBroj", artikal.getKataloskiBroj())
    			.append("kolicina", artikal.getKolicina());
    		if(artikal.getNazivProizvoda() != null)
    			docArtikal.append("nazivProizvoda", artikal.getNazivProizvoda());
    		if(artikal.getTipProizvoda() != null)
    			docArtikal.append("tipProizvoda", artikal.getTipProizvoda());
    		artikli.add(docArtikal);
    	}
    	docNarudzba.append("artikli", artikli);
    			
    	return docNarudzba;
    }
}
