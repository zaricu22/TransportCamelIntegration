package prostor;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import entiteti.Lokacija;
import entiteti.Roba;

@Local
public interface SkladisniProstorSLSBLocal {
	public List<Lokacija> vratiLokacijeArtikla(String kataloskiBroj);
	public Map<Integer, List<Lokacija>> izborLokacijeArtikla(Roba roba);
	public boolean zauzmiLokacijuArtikla(String idRobe, String idLokacije, Integer prijemniBroj);
}
