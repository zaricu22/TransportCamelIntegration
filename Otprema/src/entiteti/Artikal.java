package entiteti;

import java.io.Serializable;

public class Artikal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String nazivProizvoda;
	private String tipProizvoda;
	private String kataloskiBroj;
	private Integer kolicina;
	
//	private String proizvodjac;
//	private Date datumProizvodnje;
//	private Date rokUpotrebe;
//	private Integer brojProizUArtiklu;
	private Double tezina;
	
	
	public Artikal() {
		super();
	}
	public Artikal(String kataloskiBroj, Integer kolicina) {
		super();
		this.kataloskiBroj = kataloskiBroj;
		this.kolicina = kolicina;
	}
	public Artikal(String kataloskiBroj, Integer kolicina, Double tezina) {
		super();
		this.kataloskiBroj = kataloskiBroj;
		this.kolicina = kolicina;
		this.tezina = tezina;
	}
	public Artikal(String nazivProizvoda, String tipProizvoda, String kataloskiBroj, Integer kolicina) {
		super();
		this.nazivProizvoda = nazivProizvoda;
		this.tipProizvoda = tipProizvoda;
		this.kataloskiBroj = kataloskiBroj;
		this.kolicina = kolicina;
	}
	public Artikal(String nazivProizvoda, String tipProizvoda, String kataloskiBroj, Integer kolicina, Double tezina) {
		super();
		this.nazivProizvoda = nazivProizvoda;
		this.tipProizvoda = tipProizvoda;
		this.kataloskiBroj = kataloskiBroj;
		this.kolicina = kolicina;
		this.tezina = tezina;
	}

	
	public String getNazivProizvoda() {
		return nazivProizvoda;
	}
	public void setNazivProizvoda(String nazivProizvoda) {
		this.nazivProizvoda = nazivProizvoda;
	}
	public String getTipProizvoda() {
		return tipProizvoda;
	}
	public void setTipProizvoda(String tipProizvoda) {
		this.tipProizvoda = tipProizvoda;
	}
	public String getKataloskiBroj() {
		return kataloskiBroj;
	}
	public void setKataloskiBroj(String kataloskiBroj) {
		this.kataloskiBroj = kataloskiBroj;
	}
	public Integer getKolicina() {
		return kolicina;
	}
	public void setKolicina(Integer kolicina) {
		this.kolicina = kolicina;
	}
	public Double getTezina() {
		return tezina;
	}
	public void setTezina(Double tezina) {
		this.tezina = tezina;
	}
	
	
	@Override
	public String toString() {
		return "\n\t Artikal [nazivProizvoda=" + nazivProizvoda + ", tipProizvoda=" + tipProizvoda + ", kataloskiBroj="
				+ kataloskiBroj + ", kolicina=" + kolicina + ", tezina=" + tezina + "]";
	}
}
