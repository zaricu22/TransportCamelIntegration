package opcije;


import javax.xml.transform.TransformerException;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.CxfPayload;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.cxf.binding.soap.SoapHeader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import entiteti.Narudzba;

public class NarudzbaNormalizer {
	
	public Narudzba jsonToObject(Exchange exchange) {
		
		// JSON String to Object
		ObjectMapper objectMapper = new ObjectMapper();
		Narudzba nar;
		try {
			nar = objectMapper.readValue(exchange.getIn().getBody().toString(), Narudzba.class);
			return nar;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }
	
	public Narudzba xmlToObject(Exchange exchange) {
		
		// CxfPayload to XML String
		@SuppressWarnings("unchecked")
		CxfPayload<SoapHeader> request = exchange.getIn().getBody(CxfPayload.class);
	    XmlConverter converter = new XmlConverter();
	    String xmlInRequest = "";
		try {
			xmlInRequest = converter.toString(request.getBody().get(0).cloneNode(true), exchange);
		} catch (TransformerException e1) { e1.printStackTrace(); }
	    String[] str = xmlInRequest.split("\\n");
	    StringBuilder xml = new StringBuilder();
	    for (int i = 1; i < str.length-1; i++) {
			xml.append(str[i]);
		}
		
	    // XML String to Object
		XmlMapper xmlMapper = new XmlMapper();
		Narudzba nar;
		try {
			nar = xmlMapper.readValue(xml.toString(), Narudzba.class);
			return nar;
		} catch (JsonMappingException e) { e.printStackTrace(); } 
		catch (JsonProcessingException e) { e.printStackTrace(); }
		
		return null;
    }
	
	public void textToObject(Exchange exchange) {
		
		// Custom String to Object
		// ...
		
    }
}
