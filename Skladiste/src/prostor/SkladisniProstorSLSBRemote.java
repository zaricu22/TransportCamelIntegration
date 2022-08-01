package prostor;

import java.util.List;

import javax.ejb.Remote;

import entiteti.Lokacija;

@Remote
public interface SkladisniProstorSLSBRemote {
	public List<Lokacija> vratiSlobodnaMesta();
	public boolean test();
}
