package transport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;

import entiteti.Artikal;
import entiteti.Magacin;
import entiteti.Narucilac;
import entiteti.Otprema;
import entiteti.Posiljka;
import mongoclient.MongoClientSingleton;
import narudzbe.message.PotvrdaItem;
import roba.RobneZaliheSLSBRemote;

/**
 * Session Bean implementation class TransportBean
 */
@Stateless
public class TransportSLSB implements TransportSLSBRemote, TransportSLSBLocal {

	@EJB
	OtpremaDBSingleton tsb;
	
	@EJB
	MongoClientSingleton mcs;
	
	public TransportSLSB() {
		super();
	}
	
	// Pred korak: NarudzbeMDB.onMessage -> Otprema.obradaNarudzbe(artikalNarudzbe,vaznostIsporuke,datumNarudzbe,narucilac)
	// Local.rezervisanjeArtikla(kataloskiBroj, kolicina)
	// Local.odabirTipaKamiona(kataloskiBroj)
	// Local.moguceRute(mesto)
	// Local.tezinaArtikla(kataloskiBroj)
	// OtpremaDBSingleton.predlaganjeOtpreme(tipKamiona, moguceRuteIDs, ukupnaTezinaArtikla, vaznostIsporuke, rokIsporuke);
	// OtpremaDBSingleton.sacuvajOtpremu/azurirajPosiljkeOtpreme(otprema)
	// LOCAL METHOD
	public PotvrdaItem obradaArtiklaNarudzbe(Artikal artikalNarudzbe, String vaznostNarudzbe, 
			Date datumNarudzbe, Narucilac narucilac) {
			
		// Dali je moguce rezervisati artikal(ima li dovoljno na lageru)
		if (rezervisanjeArtikla(artikalNarudzbe.getKataloskiBroj(), artikalNarudzbe.getKolicina())) {
			
			Date rokNarudzbe;
			if (vaznostNarudzbe != null && vaznostNarudzbe.equals("hitna")) {
				Calendar c = Calendar.getInstance();
				c.setTime(datumNarudzbe);
				c.add(Calendar.DATE, 10);
				rokNarudzbe = c.getTime();
			} else {
				Calendar c = Calendar.getInstance();
				c.setTime(datumNarudzbe);
				c.add(Calendar.DATE, 20);
				rokNarudzbe = c.getTime();
			}
			
			String tipKamiona = odabirTipaKamiona(artikalNarudzbe.getKataloskiBroj());
			System.out.println("ODABIR KAMIONA() RETURN: "+ tipKamiona);
			List<String> moguceRuteIDs = moguceRute(narucilac.getMesto());
			System.out.println("MOGUCE RUTE() RETURN: \n"+ moguceRuteIDs);
			Double tezinaArtikla = tezinaArtikla(artikalNarudzbe.getKataloskiBroj());
			System.out.println("TEZINA ARTIKLA() RETURN: "+ tezinaArtikla);
			Double ukupnaTezinaArtikla = tezinaArtikla * artikalNarudzbe.getKolicina();
			System.out.println("UKUPNA TEZINA ARTIKLA() RETURN: "+ ukupnaTezinaArtikla);

			Map<String, Object> predlozenaOtprema = tsb.predlaganjeOtpremeID(tipKamiona,
					moguceRuteIDs,
					ukupnaTezinaArtikla,
					vaznostNarudzbe, 
					rokNarudzbe);
			
			System.out.println("ODABIR OTPREME ID() RETURN: \n"+ predlozenaOtprema);
			
			// Formiranje posiljke na osnovu 'artikla' i 'narudzbe'
			Artikal a = new Artikal(artikalNarudzbe.getKataloskiBroj(), artikalNarudzbe.getKolicina(), tezinaArtikla);
			if(artikalNarudzbe.getNazivProizvoda() != null)
				a.setNazivProizvoda(artikalNarudzbe.getNazivProizvoda());
			if(artikalNarudzbe.getTipProizvoda() != null)
				a.setTipProizvoda(artikalNarudzbe.getTipProizvoda());
			Narucilac n = new Narucilac(
				narucilac.getNazivNarucioca(),
				narucilac.getAdresaNarucioca(),
				narucilac.getKontaktNarucioca(),
				narucilac.getMesto()
			);
			Posiljka p = new Posiljka(a, n);


			if (predlozenaOtprema != null) {	// odgovarajuca 'otprema' vec postoji dodaj posiljku i azuriraj
				System.out.println("VEC POSTIJI OTPREMA!");

//				predlozenaOtpremaID.addPosiljka(p);

				if(tsb.azurirajPosiljkeOtpreme((String)predlozenaOtprema.get("id"), p)) {
					tsb.umanjiNosivostOtpreme((String)predlozenaOtprema.get("id"), ukupnaTezinaArtikla);
					return new PotvrdaItem(
							artikalNarudzbe, 
							(Date) predlozenaOtprema.get("rokIsporuke"),
							(String) predlozenaOtprema.get("tipKamiona"),
							null
						);
				}
			}
			else {	// odgovarajuca 'otprema' ne postoji formiraj novu, dodaj posiljku i sacuvaj
				System.out.println("KREIRANA NOVA OTPREMA!");
				
				Magacin m = new Magacin("PlusNS d.o.o", "Paliceva 56", "1234-56789");
				Otprema otpremaTemp = new Otprema(
					m, 
					tipKamiona, 
					3500.0 - ukupnaTezinaArtikla,
					new ArrayList<Posiljka>(), 
					moguceRuteIDs.get(0),
					vaznostNarudzbe, 
					rokNarudzbe, new Date(), 
					"u obradi"
				);
				
				if(vaznostNarudzbe != null)
					otpremaTemp.setVaznostIsporuke(vaznostNarudzbe);

				otpremaTemp.addPosiljka(p);

				if(tsb.sacuvajOtpremu(otpremaTemp)) {
					return new PotvrdaItem(
							artikalNarudzbe, 
							rokNarudzbe, 
							tipKamiona, 
							null
						);
				}
			}

		}
		// Ako artikal nije dostupan treba ga naruciti
		else {
			// TODO NABAVKA ARTIKLA
			System.out.println("TREBA NARUCITI ARTIKAL: " + artikalNarudzbe.getKataloskiBroj() + "!");
		}

		return null;
	}

	// TransportSingleton.pregledOtprema(startDate, endDate, statusObrade)
	// REMOTE METHOD
	@Override
	public List<Otprema> pregledOtprema(Date start, Date end, String statusObrade) {
		return tsb.pregledOtprema(start, end, statusObrade);
	}
		
	// LOCAL METHOD
	@Override
	public List<String> moguceRute(String mestoNarucioca) {
			
		System.out.println("MOGUCE RUTE(): "+mestoNarucioca);

		MongoClient client = mcs.getMongoClient();
		List<String> res = null;

		MongoDatabase datebase = client.getDatabase("magacin");
		MongoCollection<Document> collection = datebase.getCollection("rute");

		MongoCursor<Document> cursor = collection.find(eq("mesta", mestoNarucioca)).iterator();

		res = new ArrayList<String>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			String idRute = doc.getObjectId("_id").toString();
			res.add(idRute);
		}

		cursor.close();
		
		return res;
	}
		
	// RobneZaliheBeanRemote.rezervisanjeArtikla(kataloskiBroj, kolicina)
	// POMOCNA METODA
	private boolean rezervisanjeArtikla(String kataloskiBroj, Integer kolicina) {
		
		final Properties props = new Properties();
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		props.put("remote.connections", "default");
		props.put("remote.connection.default.host", "localhost");
		props.put("remote.connection.default.port", "8081");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

		RobneZaliheSLSBRemote bean = null;
		InitialContext ctx = null;
		Context rootNamingContext = null;
		try {
			ctx = new InitialContext(props);
			rootNamingContext = (Context) ctx.lookup("ejb:");
			bean = (RobneZaliheSLSBRemote) ctx
					.lookup("ejb:/Skladiste-0.0.1-SNAPSHOT/RobneZaliheSLSB!roba.RobneZaliheSLSBRemote");
			 return bean.rezervisanjeArtikla(kataloskiBroj, kolicina);
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rootNamingContext != null)
					rootNamingContext.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			try {
				if (ctx != null)
					ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		
		return false;
    }
	
	// RobneZaliheBeanRemote.vratiTezinuArtikla(kataloskiBroj)
	// POMOCNA METODA
	private Double tezinaArtikla(String kataloskiBroj) {
		
		System.out.println("TEZINA ARTIKLA(): "+kataloskiBroj);

		Double res = null;

		
		final Properties props = new Properties();
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		props.put("remote.connections", "default");
		props.put("remote.connection.default.host", "localhost");
		props.put("remote.connection.default.port", "8081");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

		RobneZaliheSLSBRemote bean = null;
		InitialContext ctx = null;
		Context rootNamingContext = null;
		try {
			ctx = new InitialContext(props);
			rootNamingContext = (Context) ctx.lookup("ejb:");
			bean = (RobneZaliheSLSBRemote) ctx
					.lookup("ejb:/Skladiste-0.0.1-SNAPSHOT/RobneZaliheSLSB!roba.RobneZaliheSLSBRemote");
			res = bean.vratiTezinuArtikla(kataloskiBroj);
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rootNamingContext != null)
					rootNamingContext.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			try {
				if (ctx != null)
					ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	// RobneZaliheBeanRemote.vratiTipArtikla(kataloskiBroj)
	// POMOCNA METODA
	private String odabirTipaKamiona(String kataloskiBroj) {
		
		System.out.println("ODABIR KAMIONA(): "+kataloskiBroj);
		
		String tipProizvoda = null;
		
		
		final Properties props = new Properties();
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		props.put("remote.connections", "default");
		props.put("remote.connection.default.host", "localhost");
		props.put("remote.connection.default.port", "8081");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

		RobneZaliheSLSBRemote bean = null;
		InitialContext ctx = null;
		Context rootNamingContext = null;
		try {
			ctx = new InitialContext(props);
			rootNamingContext = (Context) ctx.lookup("ejb:");
			bean = (RobneZaliheSLSBRemote) ctx
					.lookup("ejb:/Skladiste-0.0.1-SNAPSHOT/RobneZaliheSLSB!roba.RobneZaliheSLSBRemote");
			tipProizvoda = bean.vratiTipArtikla(kataloskiBroj);
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rootNamingContext != null)
					rootNamingContext.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			try {
				if (ctx != null)
					ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		
			
		if(tipProizvoda != null) {
			if (tipProizvoda.matches("voce|povrce|meso|suvomesnato"))
				return "HLADNJACA";
			else if (tipProizvoda.matches("aparat|uredjaj"))
				return "HANGAR";
			else if (tipProizvoda.matches("konditorski|pice|zitarice|pasterizovano|hemija"))
				return "REGALNO";
		}

		return null;
	}

	@Override
	public Otprema test() {

//		RobneZaliheBeanRemote bean = null;
//
//		final Properties props = new Properties();
//		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
//		props.put(Context.PROVIDER_URL, "remote+http://localhost:8081");
//		InitialContext ctx;
//		try {
//			ctx = new InitialContext(props);
//			bean = (RobneZaliheBeanRemote) ctx
//					.lookup("ejb:/Skladiste-0.0.1-SNAPSHOT/RobneZaliheBean!roba.RobneZaliheBeanRemote");
//			return bean.dostupnostArtikla("BCD");
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}

		
//		List<Posiljka> lista = new ArrayList<Posiljka>();
//		lista.add(
//			new Posiljka(
//				new Artikal("BCD", 2), 
//				new Narucilac("Narucilac1", "adresaNarucioca1", "kontaktNarucioca1")
//			)
//		);
//		lista.add(
//			new Posiljka(
//				new Artikal("BCD", 2), 
//				new Narucilac("Narucilac2", "adresaNarucioca2", "kontaktNarucioca2")
//			)
//		);
//		
//		Otprema otprema = new Otprema(
//			new Magacin("MojePreduzece1", "MojaAdresa1", "MojKontakt1"), 
//			"MojTipKamiona1", 
//			3500.0, 
//			200.0, 
//			lista, 
//			"5f69fd05c7495518842d464d", 
//			new Date(),
//			"u obradi"
//		);
//		
//		osb.sacuvajOtpremu(otprema);

		
// 		osb.vratiRutu("5f69fd05c7495518842d464d")

		
//		Date start = null;
//		Date end = null;
//		try {
//			start = new SimpleDateFormat("dd/MM/yyyy").parse("25/09/2020");  
//			end = new SimpleDateFormat("dd/MM/yyyy").parse("29/09/2020");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}  
//		
//		osb.pregledOtprema(start, end);

		
//		Date date1 = null;
//		try {
//			date1 = new SimpleDateFormat("dd/MM/yyyy").parse("10/10/2020");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		osb.odabirOtpreme("hladnjaca", Arrays.asList("5f69fb22c7495518842d464c", "5f69fd0ac7495518842d464e"),
//				2000.0, "obicna", date1);

		
//		List<Artikal> listaArtikala = new ArrayList<Artikal>();
//		listaArtikala.add(new Artikal("Proizvod2", "povrce", "BCD", 10, 20.0));
//		NarudzbaMessage nrdzb = new NarudzbaMessage(
//			new Narucilac("Narucilac1", "adresaNaruioca1", "kontaktNarucioca1", "Zrenjanin"), 
//			listaArtikala, 
//			"obicna",
//			new Date()
//		);
//		
//		obradaNarudzbe(nrdzb)
				

		return null;
	}
}
