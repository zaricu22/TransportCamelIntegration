package prostor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;

import entiteti.Lokacija;
import entiteti.Roba;
import mongoclient.MongoClientSingleton;
import roba.RobneZaliheSLSBLocal;

/**
 * Session Bean implementation class SkladisniProstor
 */
@Stateless
public class SkladisniProstorSLSB implements SkladisniProstorSLSBRemote, SkladisniProstorSLSBLocal {
	
	@EJB
	RobneZaliheSLSBLocal rzb;
	
	@EJB
	SkladisniProstorDBSingleton sps;
	
	@EJB
	MongoClientSingleton mcs;

	public SkladisniProstorSLSB() {
		super();
	}

	// REMOTE METHOD
	@Override
	public List<Lokacija> vratiSlobodnaMesta() {
		return sps.vratiSlobodnaMesta();
	}

	// LOCAL METHOD
	@Override
	public List<Lokacija> vratiLokacijeArtikla(String kataloskiBroj) {
		return sps.vratiLokacijeArtikla(kataloskiBroj.trim());
	}

	// Pre koraci:
	// 		() RobneZaliheBean.evidentiranjeArtikla(roba)
	// 		(List<Roba>) RobneZaliheBean.pregledNeuskladisteneRobe()
	//		(prijemniBroj, List<Lokacija>) Local.izborLokacijeArtikla(roba)
	// LOCAL METHOD
	@Override
	public boolean zauzmiLokacijuArtikla(String idRobe, String idLokacije, Integer prijemniBroj) {
		return sps.zauzmiLokacijuArtikla(idRobe, idLokacije, prijemniBroj);
	}
	
	// Odredjeni 'prijemniBroj' i vrati slobodne 'lokacije'(=sektor,red,kolona) na osnovu odgovarajuceg 'sektora', 'reda' i 'kataloskogBroja' 
	// Local.odrediSektor(tipPorizvoda)
	// Local.odrediRed(tipPorizvoda, tezinaArtikla, brojArtikala, sektor)
	// RobneZaliheBean.odrediPrijemniBroj(moguciRed, sektor, kataloskiBroj)
	// LOCAL METHOD
	@Override
	public Map<Integer, List<Lokacija>> izborLokacijeArtikla(Roba roba) {

		String sektor = odrediSektor(roba.getTipProizvoda());
		Integer moguciRed = odrediRed(roba.getTipProizvoda(), roba.getTezinaArtikla(), roba.getBrojArtikala(), sektor);

		MongoClient client = mcs.getMongoClient();
		List<Lokacija> listaSlobMesta = null;
		Map<Integer, List<Lokacija>> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collectionProstor = datebase.getCollection("prostor");

		// odredjivanje slobodnih mesta u odgovarajucem 'sektoru' i 'redu'
		MongoCursor<Document> cursorProstor = collectionProstor.find(where(
					"this.sektor == '" + sektor + "' && "+ 
					"this.red == "+ moguciRed + "&& "+ 
					"this.popunjenost < this.kapacitet"
				)).iterator();
		
		boolean imaSlobodnoMesto = cursorProstor.hasNext(); // provera dostupnosti makar 1 slobodnog mesta u 'redu'
		Integer prijemniBroj = 0;
		if(imaSlobodnoMesto) { // ako ima slobodnih mesta u redu odredi prijBroj inace vrati null
			prijemniBroj = rzb.odrediPrijemniBroj(moguciRed, sektor, roba.getKataloskiBroj());
		} else
			return null;
		
		if(cursorProstor.hasNext()) {
			// prevodjenje dokum iz baze u objekte
			listaSlobMesta = new ArrayList<Lokacija>();
			while (cursorProstor.hasNext()) {
				Document doc = cursorProstor.next();
				Lokacija lok = konvertujDokumLokacija(doc);
				listaSlobMesta.add(lok);
			}
			
			res = new HashMap<Integer, List<Lokacija>>();
			res.put(prijemniBroj, listaSlobMesta);
		}	
		
		cursorProstor.close();
		
		return res;
	}

	// Odredjivanje sektora 'Magacina' na osnovu tipaProizvoda
	// POMOCNA METODA
	private String odrediSektor(String tipPorizvoda) {
		
		if (tipPorizvoda.matches("voce|povrce|meso|suvomesnato"))
			return "HLADNJACA";
		else if (tipPorizvoda.matches("aparat|uredjaj"))
			return "HANGAR";
		else if (tipPorizvoda.matches("konditorski|pice|zitarice|pasterizovano|hemija"))
			return "REGALNO";
		
		return null;
	}

	// Odredjivanje reda za sektor HLADNJACE na osnovu tipaProizvoda,
	// za ostale sektore na osnovu tezineProizvoda
	// POMOCNA METODA
	private Integer odrediRed(String tipPorizvoda, Double tezinaArtikla, Integer brojArtikala, String sektor) {
		
		if (sektor.equals("HLADNJACA")) {
			if (tipPorizvoda.equals("voce"))
				return 1;
			else if (tipPorizvoda.equals("povrce"))
				return 2;
			else if (tipPorizvoda.equals("meso"))
				return 3;
			else
				return 4;
		} else {
			Double tezinaRobe = tezinaArtikla * brojArtikala;
			if (tezinaRobe < 100)
				return 1;
			else if (tezinaRobe >= 100 && tezinaRobe < 200)
				return 2;
			else if (tezinaRobe >= 200 && tezinaRobe < 300)
				return 3;
			else if (sektor.equals("HANGAR") && tezinaRobe >= 300)
				return 4;
		}
		
		return null;
	}
	
	// POMOCNA METODA
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
	
	@Override
	public boolean test() {
//		System.out.println();
//		Roba roba = new Roba("AAA", "povrce", "BCD", "DDD", new Date(), "ispravno", 20.0, 10);
//		Map<Integer,List<Lokacija>> map = izborLokacijeArtikla(roba);
//		System.out.println(map.keySet().iterator().next());
//		List<Lokacija> list = map.get(map.keySet().iterator().next());
//		for(Lokacija l : list) {
//			System.out.println(l);
//		}
//		return map;
		
		return sps.zauzmiLokacijuArtikla("5f5fb9007c6e0b33300b44bf", "5f64f7fe5335cd1e9c530418", 3);
	}

}
