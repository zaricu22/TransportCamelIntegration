package roba;

import javax.ejb.Remote;


@Remote
public interface RobneZaliheSLSBRemote {
	public boolean rezervisanjeArtikla(String kataloskiBroj, Integer kolicina);
	public boolean nabavkaArtikla(String kataloskiBroj);
	public String vratiTipArtikla(String kataloskiBroj);
	public Double vratiTezinuArtikla(String kataloskiBroj);
	public String test();
}

