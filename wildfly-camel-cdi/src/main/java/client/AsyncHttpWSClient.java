package client;

import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;


public class AsyncHttpWSClient {

	class RSThread extends Thread {
		@Override
		public void run() {
			System.out.println(getName());
			String thNum = getName().split("-")[1].trim();
			
			String body = "<x:Envelope\r\n" + 
					"    xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\n" + 
					"    xmlns:wil=\"http://wildfly_camel_cdi.camel/\">\r\n" + 
					"    <x:Header/>\r\n" + 
					"    <x:Body>\r\n" + 
					"        <wil:slanje>\r\n" + 
					"            <narudzba>\r\n" + 
					"               <narucilac>\r\n" + 
					"                   <nazivNarucioca> Narucilac"+thNum+" </nazivNarucioca>\r\n" + 
					"                   <adresaNarucioca> adresaNaruioca"+thNum+" </adresaNarucioca>\r\n" + 
					"                   <kontaktNarucioca> kontaktNarucioca"+thNum+" </kontaktNarucioca>\r\n" + 
					"                   <mesto> Sombor </mesto>\r\n" + 
					"               </narucilac>\r\n" + 
					"               <vaznostIsporuke> obicna </vaznostIsporuke>\r\n" + 
					"               <datum> 2021-02-17 13:50:45 </datum>\r\n" + 
					"               <artikli> \r\n" + 
					"                   <artikal>\r\n" + 
					"                       <kataloskiBroj> BCD </kataloskiBroj>\r\n" + 
					"                       <kolicina> 50 </kolicina>\r\n" + 
					"                   </artikal>\r\n" + 
					"                   <artikal>\r\n" + 
					"                       <kataloskiBroj> AFGD </kataloskiBroj>\r\n" + 
					"                       <kolicina> 10 </kolicina>\r\n" + 
					"                   </artikal>\r\n" + 
					"               </artikli>\r\n" + 
					"            </narudzba>\r\n" + 
					"        </wil:slanje>\r\n" + 
					"    </x:Body>\r\n" + 
					"</x:Envelope>";

			System.out.println(body);
			
//			WebClient client = WebClient.create("http://localhost:8083/wildfly-camel-cdi");
//			client.path("webservices/narucivanje");
//			client.header("Authorization", Base64.getEncoder().encodeToString("CXFWS:123".getBytes()));
//			Future<Response> f = client.async().post(Entity.xml(body));
//			
//			while(!f.isDone()) {
//			    try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//					
//			try {
//				System.out.println("\n"+getName()+"\n"+f.get().readEntity(String.class));
//			} catch (InterruptedException | ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	public static void main(String[] args) throws Exception {
		AsyncHttpWSClient ac = new AsyncHttpWSClient();
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
