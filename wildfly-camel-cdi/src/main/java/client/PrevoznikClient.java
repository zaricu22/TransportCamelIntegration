package client;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import entiteti.Otprema;
import transport.message.PrevoznikSLSBRemote;

public class PrevoznikClient {

	public static void main(String[] args) {
		
		// NAPOMENA: ne moramo da kopiramo Remote interface sa njegovim paketom jer smo uvezli remote projekte
		
		final Properties props = new Properties();
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED","false");
		props.put("remote.connections","moja1");
		props.put("remote.connection.moja1.host","localhost");
		props.put("remote.connection.moja1.port","8080");
		props.put("remote.connection.moja1.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS","false");
	
		// NAPOMENA: udaljeni interfejs se mora nalaziti u istom paketu i klasi kao na udaljenom serveru 
		PrevoznikSLSBRemote bean2 = null;
		try {
			InitialContext ctx = new InitialContext(props);
			Context rootNamingContext = (Context) ctx.lookup("ejb:"); 
			bean2 = (PrevoznikSLSBRemote) rootNamingContext.lookup(
				"/Otprema-0.0.1-SNAPSHOT//PrevoznikSLSB!transport.message.PrevoznikSLSBRemote"
			);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		// bean2.ponudaOtprema();
		
//		System.out.println(bean1.potvrdiOtpremu("5f67904b5335cd1e9c53042c", 
//				new Dostavljac("Dostavljac1", "AdresaDostavljaca 1", "KontaktDostavljaca 1", "Mesto 1")));
		
		List<String> lista = bean2.ponudaOtprema("Pretplatnik2");
		for (String string : lista) {
			System.out.println();
			System.out.println("-------------------------------------------------------------------------------------");
			System.out.println();
			System.out.println(string);
		}

	}
	
}
