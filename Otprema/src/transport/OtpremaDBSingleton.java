package transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import entiteti.Artikal;
import entiteti.Dostavljac;
import entiteti.Magacin;
import entiteti.Narucilac;
import entiteti.Otprema;
import entiteti.Posiljka;
import mongoclient.MongoClientSingleton;

/**
 * Session Bean implementation class OtpremaBean
 */
@Singleton
@LocalBean
public class OtpremaDBSingleton {
	
	@EJB
	MongoClientSingleton mcs;
	
	MongoClient client;
	
	@PostConstruct
	public void init() {
		client = mcs.getMongoClient();
	}
	
	public boolean sacuvajOtpremu(Otprema otprema) {
		
		System.out.println("SACUVAJ OTPREMU(): \n"+otprema);

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");

		Document docOtprema = konvertovanjeOptremaDokum(otprema);

		InsertOneResult ior = collection.insertOne(docOtprema);
		if(ior.getInsertedId() != null)
			return true;

		return false;
	}
	
	public boolean azurirajPosiljkeOtpreme(String idOtpreme, Posiljka p) {
		
		System.out.println("AZURIRAJ OTPREMU(): \n"+idOtpreme+" "+p);

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");

		Document doc = new Document()
				.append("kataloskiBroj", p.getArtikal().getKataloskiBroj())
				.append("kolicina", p.getArtikal().getKolicina())
				.append("nazivNarucioca", p.getNarucilac().getNazivNarucioca())
				.append("adresaNarucioca", p.getNarucilac().getAdresaNarucioca())
				.append("kontaktNarucioca", p.getNarucilac().getKontaktNarucioca())
				.append("mesto", p.getNarucilac().getMesto());
		
		if (p.getArtikal().getNazivProizvoda() != null)
			doc.append("nazivProizvoda", p.getArtikal().getNazivProizvoda());
		if (p.getArtikal().getTipProizvoda() != null)
			doc.append("tipProizvoda", p.getArtikal().getTipProizvoda());
		
		Bson filter = eq("_id", new ObjectId(idOtpreme));
		Bson update = push("posiljke", doc);
		UpdateResult ur = collection.updateOne(filter, update);
		if(ur.getModifiedCount() > 0)
			return true;

		return false;
	}
	
	public boolean umanjiNosivostOtpreme(String idOtpreme, Double umanjenje) {

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");
		
		Bson filter = eq("_id", new ObjectId(idOtpreme));
		Bson update = inc("preostalaNosivost", -umanjenje);
		UpdateResult ur = collection.updateOne(filter, update);
		if(ur.getModifiedCount() > 0)
			return true;

		return false;
	}
	
	public boolean potvrdiOtpremu(String idOtpreme, Dostavljac dostavljac) {
    	
    	MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");
		
		Bson filter = eq("_id", new ObjectId(idOtpreme));
		List<Bson> list = new ArrayList<Bson>();
		list.add(set("statusObrade", "potvrdjeno"));
		list.add(set("nazivDostavljaca", dostavljac.getNazivDostavljaca()));
		list.add(set("adresaDostavljaca", dostavljac.getAdresaDostavljaca()));
		list.add(set("kontaktDostavljaca", dostavljac.getKontaktDostavljaca()));
		Bson combineUpdate = combine(list);
		UpdateResult ur = collection.updateOne(filter, combineUpdate);
		if(ur.getModifiedCount() > 0)
			return true;
    	
    	return false;
    }
	
	public String statusOtpreme(String idOtpreme) {
    	
    	MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");
		
		Bson filter = eq("_id", new ObjectId(idOtpreme));
		MongoCursor<Document> cursor = collection.find(filter).iterator();
		 
		if(cursor.hasNext()) {
			Document doc = cursor.next();
			return doc.getString("statusObrade");
		}
    	
    	return null;
    }
	
	@Lock(LockType.READ)	// radi boljeg konkurentnog pristupa metodi
	public List<String> vratiRutu(String idRute) {

		List<String> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("rute");

		MongoCursor<Document> cursor = collection.find(eq("_id", new ObjectId(idRute))).iterator();

		if(cursor.hasNext()) {
			Document doc = cursor.next();
			res = doc.getList("mesta", String.class);
		}
		
		cursor.close();

		return res;
	}

	@Lock(LockType.READ)
	public Map<String, Object> predlaganjeOtpremeID(String kamionNarudzbe, List<String> moguceRuteNarudzbe,
			Double ukupnaTezinaArtikla, String vaznostIsporuke, Date rokNarudzbe) {
		
		System.out.println("ODABIR OTPREME(): \n"+kamionNarudzbe+" \n"+moguceRuteNarudzbe+" \n"+ukupnaTezinaArtikla+" \n"+vaznostIsporuke+" \n"+rokNarudzbe);
		
		List<ObjectId> listaRuteId = new ArrayList<ObjectId>();
		for (String objectId : moguceRuteNarudzbe) {
			listaRuteId.add(new ObjectId(objectId));
		}

		Map<String, Object> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");
		
		Document query; // koristimo Document umesto cistog Bson-a zbog if-slucajeva
		// hitne isporuke rokIsporuke == 1 nedelja
		if(vaznostIsporuke != null && vaznostIsporuke.equals("hitna")) {
			
			query = new Document()
				.append("statusObrade", "u obradi")
				.append("vaznostIsporuke", "hitna")
				.append("tipKamiona", kamionNarudzbe)
				.append("preostalaNosivost", new Document().append("$gt", ukupnaTezinaArtikla))
				.append("rutaIsporuke", new Document().append("$in", listaRuteId));
			if(rokNarudzbe != null)
				query.append("rokIsporuke", new Document().append("$gte", rokNarudzbe));
		}
		else {		// ostale rokIsporuke > 2 nedelje
			query = new Document()
				.append("statusObrade", "u obradi")
				.append("vaznostIsporuke", "obicna")
				.append("tipKamiona", kamionNarudzbe)
				.append("preostalaNosivost", new Document().append("$gt", ukupnaTezinaArtikla))
				.append("rutaIsporuke", new Document().append("$in", listaRuteId));
			if(rokNarudzbe != null)
				query.append("rokIsporuke", new Document().append("$lte", rokNarudzbe));
		}
		Document projection = new Document()
				.append("_id", 1)
				.append("rokIsporuke", 1)
				.append("tipKamiona", 1);

		MongoCursor<Document> cursor = collection.find(query).projection(projection).limit(1).iterator();

		if(cursor.hasNext()) {
			res = new HashMap<String, Object>();
			Document doc = cursor.next();
			res.put("id",  doc.getObjectId("_id").toString());
			res.put("rokIsporuke",  doc.getDate("rokIsporuke"));
			res.put("tipKamiona",  doc.getString("tipKamiona"));
			return res;
		}
		
		cursor.close();

		return res;
	}

	@Lock(LockType.READ)
	public List<Otprema> pregledOtprema(Date start, Date end, String statusObrade) {
		
		List<Otprema> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("otpreme");
		
		Bson filter;
		if(statusObrade != null && !statusObrade.equals(""))
			filter = and(gte("datum", start), lte("datum", end), eq ("statusObrade",statusObrade));
		else
			filter = and(gte("datum", start), lte("datum", end));
		

		MongoCursor<Document> cursor = collection.find(filter).iterator();

		res = new ArrayList<Otprema>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			
			Otprema otpr = konvertovanjeDokumOtprema(doc);
			
			res.add(otpr);
		}

		cursor.close();
		
		return res;
	}
	
	private Otprema konvertovanjeDokumOtprema(Document doc){

		Magacin magacin = new Magacin(doc.getString("nazivPreduzeca"), doc.getString("adresaMagacina"),
				doc.getString("kontaktMagacina"));

		List<Document> nizDokum = (ArrayList<Document>) doc.get("posiljke");
		List<Posiljka> listaPosiljaka = new ArrayList<Posiljka>();
		for (Document dokumPosiljka : nizDokum) {
			Narucilac narucilac = new Narucilac(dokumPosiljka.getString("nazivNarucioca"),
					dokumPosiljka.getString("adresaNarucioca"), dokumPosiljka.getString("kontaktNarucioca"),
					dokumPosiljka.getString("mesto"));
			Artikal artikal = new Artikal(dokumPosiljka.getString("kataloskiBroj"),
					dokumPosiljka.getInteger("kolicina"));
			if (dokumPosiljka.containsKey("nazivProizvoda"))
				artikal.setNazivProizvoda(dokumPosiljka.getString("nazivProizvoda"));
			if (dokumPosiljka.containsKey("tipProizvoda"))
				artikal.setTipProizvoda(dokumPosiljka.getString("tipProizvoda"));
			Posiljka posiljka = new Posiljka(artikal, narucilac);
			listaPosiljaka.add(posiljka);
		}

		Otprema otpr = new Otprema(magacin, doc.getString("tipKamiona"), doc.getDouble("preostalaNosivost"), listaPosiljaka,
				doc.getObjectId("rutaIsporuke").toString(), doc.getDate("datum"), doc.getString("statusObrade"));
		otpr.setId(doc.getObjectId("_id").toString());
		if (doc.containsKey("vaznostIsporuke"))
			otpr.setVaznostIsporuke(doc.getString("vaznostIsporuke"));
		if (doc.containsKey("rokIsporuke"))
			otpr.setRokIsporuke(doc.getDate("rokIsporuke"));
	
		return otpr;
	}

	private Document konvertovanjeOptremaDokum(Otprema otprema) {
		Document docOtprema = new Document();
		
		if(otprema.getId() == null)
			docOtprema.append("_id", new ObjectId());
		
		List<Document> docPosiljke = new ArrayList<Document>();

		for (Posiljka posiljka : otprema.getPosiljke()) {
			Document doc = new Document()
					.append("kataloskiBroj", posiljka.getArtikal().getKataloskiBroj())
					.append("kolicina", posiljka.getArtikal().getKolicina())
					.append("nazivNarucioca", posiljka.getNarucilac().getNazivNarucioca())
					.append("adresaNarucioca", posiljka.getNarucilac().getAdresaNarucioca())
					.append("kontaktNarucioca", posiljka.getNarucilac().getKontaktNarucioca())
					.append("mesto", posiljka.getNarucilac().getMesto());
			
			if (posiljka.getArtikal().getNazivProizvoda() != null)
				doc.append("nazivProizvoda", posiljka.getArtikal().getNazivProizvoda());
			if (posiljka.getArtikal().getTipProizvoda() != null)
				doc.append("tipProizvoda", posiljka.getArtikal().getTipProizvoda());

			docPosiljke.add(doc);
		}

		docOtprema.append("nazivPreduzeca", otprema.getMagacin().getNazivPreduzeca())
			.append("adresaMagacina", otprema.getMagacin().getAdresaMagacina())
			.append("kontaktMagacina", otprema.getMagacin().getKontaktMagacina())
			.append("tipKamiona", otprema.getTipKamiona())
			.append("preostalaNosivost", otprema.getPreostalaNosivost())
			.append("datum", otprema.getDatum())
			.append("statusObrade", otprema.getStatusObrade())
			.append("posiljke", docPosiljke)
			.append("rutaIsporuke", new ObjectId(otprema.getRutaIsporukeId()));

		if (otprema.getVaznostIsporuke() != null)
			docOtprema.append("vaznostIsporuke", otprema.getVaznostIsporuke());
		if (otprema.getRokIsporuke() != null)
			docOtprema.append("rokIsporuke", otprema.getRokIsporuke());
		if (otprema.getDatum() != null)
			docOtprema.append("datum", otprema.getDatum());
		
		return docOtprema;
	}

}
