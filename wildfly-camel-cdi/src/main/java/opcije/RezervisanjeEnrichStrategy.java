package opcije;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;



public class RezervisanjeEnrichStrategy implements AggregationStrategy {
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		boolean dostupnost = newExchange.getIn().getBody(Boolean.class);
		
		oldExchange.getIn().setHeader("dostupnost", dostupnost);
		return oldExchange;
		
	}

}
