package entiteti;

import java.io.Serializable;

public class Dostavljac implements Serializable{
private static final long serialVersionUID = 1L;
	
	private String nazivDostavljaca;
	private String adresaDostavljaca;
	private String kontaktDostavljaca;
	private String mesto;
	
	
	public Dostavljac() {
		super();
	}
	public Dostavljac(String nazivDostavljaca, String adresaDostavljaca, String kontaktDostavljaca, String mesto) {
		super();
		this.nazivDostavljaca = nazivDostavljaca;
		this.adresaDostavljaca = adresaDostavljaca;
		this.kontaktDostavljaca = kontaktDostavljaca;
		this.mesto = mesto;
	}

	
	public String getNazivDostavljaca() {
		return nazivDostavljaca;
	}
	public void setNazivDostavljaca(String nazivDostavljaca) {
		this.nazivDostavljaca = nazivDostavljaca;
	}
	public String getAdresaDostavljaca() {
		return adresaDostavljaca;
	}
	public void setAdresaDostavljaca(String adresaDostavljaca) {
		this.adresaDostavljaca = adresaDostavljaca;
	}
	public String getKontaktDostavljaca() {
		return kontaktDostavljaca;
	}
	public void setKontaktDostavljaca(String kontaktDostavljaca) {
		this.kontaktDostavljaca = kontaktDostavljaca;
	}
	public String getMesto() {
		return mesto;
	}
	public void setMesto(String mesto) {
		this.mesto = mesto;
	}

	@Override
	public String toString() {
		return "Dostavljaca [nazivDostavljaca=" + nazivDostavljaca + ", adresaDostavljaca=" + adresaDostavljaca
				+ ", kontaktDostavljaca=" + kontaktDostavljaca + ", mesto=" + mesto + "]";
	}
}
