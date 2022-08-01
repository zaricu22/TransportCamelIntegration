package roba;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Updates.inc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import entiteti.Roba;
import mongoclient.MongoClientSingleton;

/**
 * Session Bean implementation class RobneZaliheSingleton
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@LocalBean
public class RobneZaliheDBSingleton {

	@EJB
	MongoClientSingleton mcs;
	
	MongoClient client;
	
	@PostConstruct
	public void init() {
		client = mcs.getMongoClient();
	}
	
    public RobneZaliheDBSingleton() {
        super();
    }
    
    public boolean evidentiranjeArtikla(Roba roba) {

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("roba");
		
		Document doc = new Document();
		doc.append("nazivProizvoda", roba.getNazivProizvoda());
		doc.append("tipProizvoda", roba.getTipProizvoda());
		doc.append("kataloskiBroj", roba.getKataloskiBroj());
		doc.append("proizvodjac", roba.getProizvodjac());
		doc.append("datumProizvodnje", roba.getDatumProizvodnje());
		doc.append("rokUpotrebe", roba.getRokUpotrebe());
		doc.append("stanje", roba.getStanje());
		doc.append("kvalitet", roba.getKvalitet());
		doc.append("pakovanjeArtikla", roba.getPakovanjeArtikla());
		doc.append("tezinaArtikla", roba.getTezinaArtikla());
		doc.append("brojArtikala", roba.getBrojArtikala());
		doc.append("brojProizvodaUArtiklu", roba.getBrojProizvodaUArtiklu());
		doc.append("prijemniBroj", roba.getPrijemniBroj());
		
		InsertOneResult ior = collection.insertOne(doc);
		if(ior.getInsertedId() != null)
			return true;
		
		return false;
	}
    
    public Integer odrediPrijemniBroj(Integer moguciRed, String sektor, String kataloskiBroj) {

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collectionRoba = datebase.getCollection("roba");
		
		Integer prijemniBroj = null;
		// pronalazenje artikla datog kataloskogBroja sa najvecim prijemnimBrojem 
		AggregateIterable<Document> iterableRoba = collectionRoba.aggregate(Arrays.asList(
            		Aggregates.lookup("prostor", "lokacija" , "_id", "lokacija"),
            		Aggregates.match(and(eq("lokacija.red",moguciRed),eq("lokacija.sektor",sektor),eq("kataloskiBroj",kataloskiBroj))),
                    Aggregates.group("$prijemniBroj"),
                    Aggregates.sort(eq("_id", -1)) )); 	// '_id' grupe je vrednost polja grupisanja (ovo nije '_id' pocetnih dokum vec krajnjih agregiranih)
		Document docRoba = iterableRoba.first();
		
		if(docRoba != null) 	// ako postoji takav artikal vrati njegov prijemniBroj + 1 inace ostaje prijemniBroj = 0 kao prvi artikal
			prijemniBroj = docRoba.getInteger("_id") + 1;
		
		return prijemniBroj;
	}
    
    @Lock(LockType.READ)
    public List<Roba> pregledNeuskladisteneRobe() {

		List<Roba> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("roba");
		
		MongoCursor<Document> cursor = collection.find(not(exists("lokacija"))).iterator();
		
		res = new ArrayList<Roba>(); 
		while(cursor.hasNext()) {
			Document doc = cursor.next();
			Roba r = konvertujDokumRoba(doc);
			res.add(r);
		}
		
		cursor.close();
		
		return res;
		
	}

    public boolean rezervisanjeArtikla(String kataloskiBroj, Integer potrebnaKolicina) {

		MongoClient client = mcs.getMongoClient();
		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collectionRoba = datebase.getCollection("roba");
		
		MongoCursor<Document> cursor = collectionRoba.find(eq("kataloskiBroj",kataloskiBroj)).sort(new Document("prijemniBroj",-1)).iterator();
		
		// Ako ima robe za celu kolicinu rezervisi ako ne nista
		Integer ukupnaKolicinaArtikala = 0;
		// Mapa -> idArtikla:dostupnaKolicina
		Map<String,Integer> mapaKolicinaRobe = new HashMap<String,Integer>();
		while(cursor.hasNext()) {
			Document doc = cursor.next();
			
			ukupnaKolicinaArtikala += doc.getInteger("brojArtikala");
			mapaKolicinaRobe.put(doc.getObjectId("_id").toString(), doc.getInteger("brojArtikala"));
		}
		
		cursor.close(); 
		
		Integer preostalaKolicina = potrebnaKolicina;
		boolean uspesnoAzurirano = false;
		if(ukupnaKolicinaArtikala >= potrebnaKolicina) {	// dali ima dovoljno robe
			
			Iterator<Map.Entry<String, Integer>> artikliBaze = mapaKolicinaRobe.entrySet().iterator();
			while(preostalaKolicina > 0) {	// dok ne rezervisemo svu zeljenu kolicinu artikla
				Map.Entry<String,Integer> trenutniArtikalBaze = artikliBaze.next();
				String idTrenutniArtikla = trenutniArtikalBaze.getKey();
				Integer kolicinaTrenutnogArtikla = trenutniArtikalBaze.getValue();
				Bson filter = eq("_id", new ObjectId(idTrenutniArtikla));
				Bson update = null;
				Integer umanjenje = null;	// privremeno radi testiranja
				
				if(kolicinaTrenutnogArtikla != 0) {
					if(kolicinaTrenutnogArtikla <= preostalaKolicina) {
						umanjenje = -kolicinaTrenutnogArtikla;
						update = inc("brojArtikala", -kolicinaTrenutnogArtikla);
						preostalaKolicina -= kolicinaTrenutnogArtikla;
					}
					else {
						umanjenje = -preostalaKolicina;
						update = inc("brojArtikala", -preostalaKolicina);
						preostalaKolicina -= preostalaKolicina;
					}
					
					UpdateResult ur = collectionRoba.updateOne(filter, update);
					if(ur.getModifiedCount() > 0) 
						uspesnoAzurirano = true;
					else
						uspesnoAzurirano = false;
				}
			}
			
		}
		
		// necemo ovde nabavku robe vec u Transportnom Bean 
		// koji inicira ovu proveru pozivajuci ovu metodu

		return uspesnoAzurirano;
    }
    
    @Lock(LockType.READ)
	public String vratiTipArtikla(String kataloskiBroj) {

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("roba");
		

		MongoCursor<Document> cursor = collection.find(eq("kataloskiBroj", kataloskiBroj)).iterator();
		
		if(cursor.hasNext()) {
			Document doc = cursor.next();
			cursor.close();
			return doc.getString("tip");
		}
		
		cursor.close();
		
		return null;
	}
    
    @Lock(LockType.READ)
	public Double vratiTezinuArtikla(String kataloskiBroj) {
		
		Double res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("roba");

		MongoCursor<Document> cursor = collection.find(eq("kataloskiBroj", kataloskiBroj)).iterator();

		if (cursor.hasNext()) {
			Document doc = cursor.next();
			res = doc.getDouble("tezinaArtikla");
		}
		
		cursor.close();

		return res;
	}
	
	private Roba konvertujDokumRoba(Document doc) {
		Roba r = new Roba(
			doc.getString("nazivProizvoda"), 
			doc.getString("tipProizvoda"), 
			doc.getString("kataloskiBroj"), 
			doc.getString("proizvodjac"), 
			doc.getDate("datumProizvodnje"), 
			doc.getString("stanje"), 
			doc.getDouble("tezinaArtikla"), 
			doc.getInteger("brojArtikala")
		);
		if(doc.getDate("rokUpotrebe") != null)
			r.setRokUpotrebe(doc.getDate("rokUpotrebe"));
		if(doc.getString("kvalitet") != null)
			r.setKvalitet(doc.getString("kvalitet"));
		if(doc.getString("pakovanjeArtikla") != null)
			r.setPakovanjeArtikla(doc.getString("pakovanjeArtikla"));
		if(doc.getInteger("brojProizvodaUArtiklu") != null)
			r.setBrojProizvodaUArtiklu(doc.getInteger("brojProizvodaUArtiklu"));
		if(doc.getInteger("prijemniBroj") != null)
			r.setPrijemniBroj(doc.getInteger("prijemniBroj"));
		if(doc.getObjectId("lokacijaId").toString() != null)
			r.setLokacijaId(doc.getObjectId("lokacijaId").toString());
		
		return r;
	}
}
