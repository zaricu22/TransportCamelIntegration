package roba;

import java.util.List;

import javax.ejb.Local;

import entiteti.Roba;

@Local
public interface RobneZaliheSLSBLocal {
	public boolean evidentiranjeArtikla(Roba roba);
	public Integer odrediPrijemniBroj(Integer moguciRed, String sektor, String kataloskiBroj);
	public List<Roba> pregledNeuskladisteneRobe();
}
