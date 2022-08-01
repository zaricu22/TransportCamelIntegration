package client;

import java.util.Base64;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;


public class SyncRSClient {

	class RSThread extends Thread {
		@Override
		public void run() {
			System.out.println(getName());
			
			String body = "{\r\n" + 
					"	\"narucilac\":{\r\n" + 
					"		\"nazivNarucioca\": \"Narucilac2\",\r\n" + 
					"		\"adresaNarucioca\": \"adresaNaruioca2\",\r\n" + 
					"		\"kontaktNarucioca\": \"kontaktNarucioca2\",\r\n" + 
					"		\"mesto\": \"Sombor\"\r\n" + 
					"	},\r\n" + 
					"	\"vaznostIsporuke\": \"obicna\",\r\n" + 
					"	\"datum\": \"2021-02-17 13:50:45\",\r\n" + 
					"	\"artikli\": [\r\n" + 
					"        {\r\n" + 
					"        	\"kataloskiBroj\": \"BCD\",\r\n" + 
					"        	\"kolicina\": 50\r\n" + 
					"        },\r\n" + 
					"        {\r\n" + 
					"        	\"kataloskiBroj\": \"AFGD\",\r\n" + 
					"        	\"kolicina\": 10\r\n" + 
					"        }\r\n" + 
					"	]\r\n" + 
					"}";

			
			WebClient client = WebClient.create("http://localhost:8083/wildfly-camel-cdi");
			client.path("rest/narucivanje/slanje");
			client.header("Authorization", Base64.getEncoder().encodeToString("CXFRS:123".getBytes()));
			client.type("application/json").accept("application/json");
			Response r = client.post(body);

			System.out.println("\n"+getName()+"\n"+r.readEntity(String.class));
					
			
		}
	}

	public static void main(String[] args) throws Exception {
		SyncRSClient sc = new SyncRSClient();
		RSThread t1 = sc.new RSThread();
		t1.start();
		RSThread t2 = sc.new RSThread();
		t2.start();
		RSThread t3 = sc.new RSThread();
		t3.start();
		RSThread t4 = sc.new RSThread();
		t4.start();
		RSThread t5 = sc.new RSThread();
		t5.start();
	    
	}
	
}
