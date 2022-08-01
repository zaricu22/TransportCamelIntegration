package entiteti;

import java.io.Serializable;

public class Narucilac implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String nazivNarucioca;
	private String adresaNarucioca;
	private String kontaktNarucioca;
	private String mesto;
	
	
	public Narucilac() {
		super();
	}
	public Narucilac(String nazivNarucioca, String adresaNarucioca, String kontaktNarucioca, String mesto) {
		super();
		this.nazivNarucioca = nazivNarucioca;
		this.adresaNarucioca = adresaNarucioca;
		this.kontaktNarucioca = kontaktNarucioca;
		this.mesto = mesto;
	}

	
	public String getNazivNarucioca() {
		return nazivNarucioca;
	}
	public void setNazivNarucioca(String nazivNarucioca) {
		this.nazivNarucioca = nazivNarucioca;
	}
	public String getAdresaNarucioca() {
		return adresaNarucioca;
	}
	public void setAdresaNarucioca(String adresaNarucioca) {
		this.adresaNarucioca = adresaNarucioca;
	}
	public String getKontaktNarucioca() {
		return kontaktNarucioca;
	}
	public void setKontaktNarucioca(String kontaktNarucioca) {
		this.kontaktNarucioca = kontaktNarucioca;
	}
	public String getMesto() {
		return mesto;
	}
	public void setMesto(String mesto) {
		this.mesto = mesto;
	}

	@Override
	public String toString() {
		return "Narucilac [nazivNarucioca=" + nazivNarucioca + ", adresaNarucioca=" + adresaNarucioca
				+ ", kontaktNarucioca=" + kontaktNarucioca + ", mesto=" + mesto + "]";
	}
}
