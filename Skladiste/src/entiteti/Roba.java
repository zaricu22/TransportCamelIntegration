package entiteti;

import java.io.Serializable;
import java.util.Date;

public class Roba implements Serializable{
	private static final long serialVersionUID = 1L;
	
	String nazivProizvoda;
	String tipProizvoda;
	String kataloskiBroj;
	String proizvodjac;
	Date datumProizvodnje;
	Date rokUpotrebe;
	String stanje;
	String kvalitet;
	String pakovanjeArtikla;
	double tezinaArtikla;
	int brojArtikala;
	int brojProizvodaUArtiklu;
	int prijemniBroj;
	String lokacijaId;
	
	public Roba() {
		super();
	}
	public Roba(String nazivProizvoda, String tipProizvoda, String kataloskiBroj, String proizvodjac,
			Date datumProizvodnje, String stanje, double tezinaArtikla, int brojArtikala) {
		super();
		this.nazivProizvoda = nazivProizvoda;
		this.tipProizvoda = tipProizvoda;
		this.kataloskiBroj = kataloskiBroj;
		this.proizvodjac = proizvodjac;
		this.datumProizvodnje = datumProizvodnje;
		this.stanje = stanje;
		this.tezinaArtikla = tezinaArtikla;
		this.brojArtikala = brojArtikala;
	}
	public Roba(String nazivProizvoda, String tipProizvoda, String kataloskiBroj, String proizvodjac,
			Date datumProizvodnje, Date rokUpotrebe, String stanje, String kvalitet, String pakovanjeArtikla,
			double tezinaArtikla, int brojArtikala, int brojProizvodaUArtiklu, int prijemniBroj, String lokacijaId) {
		super();
		this.nazivProizvoda = nazivProizvoda;
		this.tipProizvoda = tipProizvoda;
		this.kataloskiBroj = kataloskiBroj;
		this.proizvodjac = proizvodjac;
		this.datumProizvodnje = datumProizvodnje;
		this.rokUpotrebe = rokUpotrebe;
		this.stanje = stanje;
		this.kvalitet = kvalitet;
		this.pakovanjeArtikla = pakovanjeArtikla;
		this.tezinaArtikla = tezinaArtikla;
		this.brojArtikala = brojArtikala;
		this.brojProizvodaUArtiklu = brojProizvodaUArtiklu;
		this.prijemniBroj = prijemniBroj;
		this.lokacijaId = lokacijaId;
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
	public String getProizvodjac() {
		return proizvodjac;
	}
	public void setProizvodjac(String proizvodjac) {
		this.proizvodjac = proizvodjac;
	}
	public Date getDatumProizvodnje() {
		return datumProizvodnje;
	}
	public void setDatumProizvodnje(Date datumProizvodnje) {
		this.datumProizvodnje = datumProizvodnje;
	}
	public Date getRokUpotrebe() {
		return rokUpotrebe;
	}
	public void setRokUpotrebe(Date rokUpotrebe) {
		this.rokUpotrebe = rokUpotrebe;
	}
	public String getStanje() {
		return stanje;
	}
	public void setStanje(String stanje) {
		this.stanje = stanje;
	}
	public String getKvalitet() {
		return kvalitet;
	}
	public void setKvalitet(String kvalitet) {
		this.kvalitet = kvalitet;
	}
	public String getPakovanjeArtikla() {
		return pakovanjeArtikla;
	}
	public void setPakovanjeArtikla(String pakovanjeArtikla) {
		this.pakovanjeArtikla = pakovanjeArtikla;
	}
	public double getTezinaArtikla() {
		return tezinaArtikla;
	}
	public void setTezinaArtikla(double tezinaArtikla) {
		this.tezinaArtikla = tezinaArtikla;
	}
	public int getBrojArtikala() {
		return brojArtikala;
	}
	public void setBrojArtikala(int brojArtikala) {
		this.brojArtikala = brojArtikala;
	}
	public int getBrojProizvodaUArtiklu() {
		return brojProizvodaUArtiklu;
	}
	public void setBrojProizvodaUArtiklu(int brojProizvodaUArtiklu) {
		this.brojProizvodaUArtiklu = brojProizvodaUArtiklu;
	}
	public int getPrijemniBroj() {
		return prijemniBroj;
	}
	public void setPrijemniBroj(int prijemniBroj) {
		this.prijemniBroj = prijemniBroj;
	}
	public String getLokacijaId() {
		return lokacijaId;
	}
	public void setLokacijaId(String lokacijaId) {
		this.lokacijaId = lokacijaId;
	}
	
	
	@Override
	public String toString() {
		return "Roba [nazivProizvoda=" + nazivProizvoda + ", tipProizvoda=" + tipProizvoda + ", kataloskiBroj="
				+ kataloskiBroj + ", proizvodjac=" + proizvodjac + ", datumProizvodnje=" + datumProizvodnje
				+ ", rokUpotrebe=" + rokUpotrebe + ", stanje=" + stanje + ", kvalitet=" + kvalitet
				+ ", pakovanjeArtikla=" + pakovanjeArtikla + ", tezinaArtikla=" + tezinaArtikla + ", brojArtikala="
				+ brojArtikala + ", brojProizvodaUArtiklu=" + brojProizvodaUArtiklu + ", prijemniBroj=" + prijemniBroj
				+ ", lokacijaId=" + lokacijaId + "]";
	}
}
