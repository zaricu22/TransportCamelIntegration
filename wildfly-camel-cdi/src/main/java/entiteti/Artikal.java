package entiteti;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Artikal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@XmlTransient
	private Narucilac narucilac;	// sadrzi mesto
	@JsonIgnore
	@XmlTransient
	private String vaznostIsporuke;
	
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
	public Narucilac getNarucilac() {
		return narucilac;
	}
	public void setNarucilac(Narucilac narucilac) {
		this.narucilac = narucilac;
	}
	public String getVaznostIsporuke() {
		return vaznostIsporuke;
	}
	public void setVaznostIsporuke(String vaznostIsporuke) {
		this.vaznostIsporuke = vaznostIsporuke;
	}
	
	
	
	@Override
	public String toString() {
		return "Artikal [\n\t nazivProizvoda=" + nazivProizvoda + ", tipProizvoda="
				+ tipProizvoda + ", kataloskiBroj=" + kataloskiBroj + ", kolicina=" + kolicina + ", tezina(kg)=" + tezina
				+ ", \n\t vaznost=" + vaznostIsporuke + "\n ]";
	}
	
//	@Override
//	public String toString() {
//		return "Artikal [\n\t narucilac=" + narucilac + ", \n\t nazivProizvoda=" + nazivProizvoda + ", tipProizvoda="
//				+ tipProizvoda + ", kataloskiBroj=" + kataloskiBroj + ", kolicina=" + kolicina + ", tezina=" + tezina
//				+ ", \n\t vaznost=" + vaznostIsporuke + "\n ]";
//	}
}
