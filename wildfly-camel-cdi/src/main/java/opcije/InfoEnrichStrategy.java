package opcije;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.bson.Document;

import entiteti.Artikal;

public class InfoEnrichStrategy implements AggregationStrategy {
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		Document doc = newExchange.getIn().getBody(Document.class);

		String nazivArtikla = "";
		String tipArtikla = "";
		double tezinaArtikla = 0.0;
		String trazeniKatBr = oldExchange.getIn().getBody(Artikal.class).getKataloskiBroj().trim();
		String nadjeniKatBr = doc.getString("kataloskiBroj");
		if(trazeniKatBr.equals(nadjeniKatBr)) {
			nazivArtikla = doc.getString("naziv");
			tipArtikla = doc.getString("tip");
			tezinaArtikla = doc.getDouble("tezinaArtikla");
		}
		
		Artikal tekuciArtikal = oldExchange.getIn().getBody(Artikal.class);
		tekuciArtikal.setTipProizvoda(tipArtikla);
		tekuciArtikal.setNazivProizvoda(nazivArtikla);
		tekuciArtikal.setTezina(tezinaArtikla);
		
		return oldExchange;
		
	}
}
