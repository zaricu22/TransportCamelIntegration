package opcije;

import java.util.Date;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import entiteti.Artikal;
import entiteti.PotvrdaNarudzbe;

public class ArtikliSplitterStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		Artikal trenutniArtikal = newExchange.getIn().getBody(Artikal.class);
		
		PotvrdaNarudzbe potvrda = null;
		if(oldExchange == null) {	// na pocetku nemamo staru poruku, vec samo novu poruku
			potvrda = new PotvrdaNarudzbe();
			potvrda.setVaznostIsporuke(trenutniArtikal.getVaznostIsporuke());
			potvrda.setDatum(new Date());
			potvrda.setNarucilac(trenutniArtikal.getNarucilac());
		}
		else {	// koristimo postojecu potvrdu iz stare poruke(poslednja vracena poruka ovom metodom) 
			potvrda = oldExchange.getIn().getBody(PotvrdaNarudzbe.class);
		}
		
		boolean dostupnostArtikla = (boolean) newExchange.getIn().getHeader("dostupnost");
		if(dostupnostArtikla) {
			potvrda.addDostupniArtikli(trenutniArtikal);
		} 
		else {
			potvrda.addNedostupniArtikli(trenutniArtikal);
		}

		newExchange.getIn().setBody(potvrda); // getOut se izbegava, prakticno uvek se override In message
			
		return newExchange;	// auto postace oldExchange pri narednom pozivu ove metode 
		
	}
	
}
