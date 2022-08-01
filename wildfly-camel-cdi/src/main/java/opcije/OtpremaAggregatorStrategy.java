package opcije;

import java.util.Calendar;
import java.util.Date;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import entiteti.Artikal;
import entiteti.Narucilac;
import entiteti.Otprema;
import entiteti.Posiljka;

public class OtpremaAggregatorStrategy implements AggregationStrategy {
	
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		Otprema otprema = null;
		if(oldExchange == null) {	// na pocetku nemamo staru poruku, vec samo novu poruku
			otprema = new Otprema();
			otprema.setTipKamiona(newExchange.getIn().getHeader("tipKamiona", String.class));
			otprema.setPreostalaNosivost(3000.0);
			String vaznostIsporuke = newExchange.getIn().getBody(Artikal.class).getVaznostIsporuke();
			otprema.setVaznostIsporuke(vaznostIsporuke);
			String odrediste = newExchange.getIn().getBody(Artikal.class).getNarucilac().getMesto();
			otprema.setOdrediste(odrediste);
			
			Calendar cal = Calendar.getInstance();
	        cal.setTime(new Date());
			if(vaznostIsporuke.equals("hitna"))
				cal.add(Calendar.DATE, 7);
			else if(vaznostIsporuke.equals("obicna"))
				cal.add(Calendar.DATE, 21);
			otprema.setRokIsporuke(cal.getTime());
		}
		else {	// koristimo postojecu potvrdu iz stare poruke(poslednja vracena poruka ovom metodom) 
			otprema = oldExchange.getIn().getBody(Otprema.class);
		}
		
		Artikal trenutniArtikal = newExchange.getIn().getBody(Artikal.class);
		Narucilac trenutniNarucilac = trenutniArtikal.getNarucilac();
		Posiljka posiljka = new Posiljka(trenutniArtikal, trenutniNarucilac);
		otprema.addPosiljka(posiljka);
		otprema.setPreostalaNosivost(
				otprema.getPreostalaNosivost() - trenutniArtikal.getKolicina() * trenutniArtikal.getTezina()
		);
		otprema.setDatum(new Date());
		// VAZNOST ISPORUKE + DATUM => ROK ISPORUKE
		
		System.out.println(otprema);
		
		newExchange.getIn().setBody(otprema);
			
		return newExchange;	// auto postace oldExchange pri narednom pozivu ove metode 
		
	}
	
}

/* OTPREMA:
	private String tipKamiona;
	private Double preostalaNosivost;
	private List<Posiljka> posiljke;	// artikal + narucilac, svaki artikal zasebna posiljka (da se zna za koga ide koliko)
	private String rutaIsporukeId;
	private String vaznostIsporuke;
	private Date rokIsporuke;
	private Date datum; 
*/

