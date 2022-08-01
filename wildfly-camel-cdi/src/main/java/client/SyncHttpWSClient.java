package client;

import java.util.Base64;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;


public class SyncHttpWSClient {
	
	class RSThread extends Thread {
		@Override
		public void run() {
			System.out.println(getName());
			
			String body = "<x:Envelope\r\n" + 
					"    xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\n" + 
					"    xmlns:wil=\"http://wildfly_camel_cdi.camel/\">\r\n" + 
					"    <x:Header/>\r\n" + 
					"    <x:Body>\r\n" + 
					"        <wil:slanje>\r\n" + 
					"            <narudzba>\r\n" + 
					"				<narucilac>\r\n" + 
					"					<nazivNarucioca> Narucilac2 </nazivNarucioca>\r\n" + 
					"					<adresaNarucioca> adresaNaruioca2 </adresaNarucioca>\r\n" + 
					"					<kontaktNarucioca> kontaktNarucioca2 </kontaktNarucioca>\r\n" + 
					"					<mesto> Sombor </mesto>\r\n" + 
					"				</narucilac>\r\n" + 
					"				<vaznostIsporuke> obicna </vaznostIsporuke>\r\n" + 
					"				<datum> 2021-02-17 13:50:45 </datum>\r\n" + 
					"				<artikli> \r\n" + 
					"			        <artikal>\r\n" + 
					"			        	<kataloskiBroj> BCD </kataloskiBroj>\r\n" + 
					"			        	<kolicina> 50 </kolicina>\r\n" + 
					"			        </artikal>\r\n" + 
					"			        <artikal>\r\n" + 
					"			        	<kataloskiBroj> AFGD </kataloskiBroj>\r\n" + 
					"			        	<kolicina> 10 </kolicina>\r\n" + 
					"			        </artikal>\r\n" + 
					"				</artikli>\r\n" + 
					"			</narudzba>\r\n" + 
					"        </wil:slanje>\r\n" + 
					"    </x:Body>\r\n" + 
					"</x:Envelope>";

			
			WebClient client = WebClient.create("http://localhost:8083/wildfly-camel-cdi");
			client.path("webservices/narucivanje");
			client.header("Authorization", Base64.getEncoder().encodeToString("CXFWS:123".getBytes()));
			client.type("application/xml").accept("application/xml");
			Response r = client.post(body);
			
			System.out.println("\n"+getName()+"\n"+r.readEntity(String.class));
					
			
		}
	}

	public static void main(String[] args) throws Exception {
		SyncHttpWSClient sc = new SyncHttpWSClient();
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
