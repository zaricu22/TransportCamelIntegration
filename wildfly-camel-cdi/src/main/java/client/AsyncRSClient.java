package client;

import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

public class AsyncRSClient {
	
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
			Future<Response> f = client.async().post(Entity.json(body));
			
			while(!f.isDone()) {
			    try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
					
			try {
				System.out.println("\n"+getName()+"\n"+f.get().readEntity(String.class));
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		AsyncRSClient ac = new AsyncRSClient();
		RSThread t1 = ac.new RSThread();
		t1.start();
		RSThread t2 = ac.new RSThread();
		t2.start();
		RSThread t3 = ac.new RSThread();
		t3.start();
		RSThread t4 = ac.new RSThread();
		t4.start();
		RSThread t5 = ac.new RSThread();
		t5.start();
	    
	}
	
}
