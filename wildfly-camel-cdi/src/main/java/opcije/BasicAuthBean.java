package opcije;

import java.util.Base64;

import org.apache.camel.Header;

public class BasicAuthBean {
	
	public boolean auth(String requiredCred, @Header(value="Authorization") String recievedCred) throws Exception {
		return Base64.getEncoder().encodeToString(requiredCred.getBytes()).trim().equals(recievedCred.trim());
	}
}
