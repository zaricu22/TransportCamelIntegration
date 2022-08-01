package entiteti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class Otprema implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	private String id;
	private Magacin magacin;
	private String tipKamiona;
	private Double preostalaNosivost;
	private List<Posiljka> posiljke;	// artikal + narucilac, svaki artikal zasebna posiljka (da se zna za koga ide koliko)
	private String odrediste;
	private String vaznostIsporuke;
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private Date rokIsporuke;
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private Date datum;
	@JsonIgnore
	private String statusObrade;
	@JsonIgnore
	private Dostavljac dostavljac;
	
	
	public Otprema() {
		super();
		posiljke = new ArrayList<Posiljka>();
		magacin = new Magacin("PlusNS d.o.o", "Paliceva 56", "1234-56789");
	}
	public Otprema(Magacin magacin, String tipKamiona, Double preostalaNosivost, List<Posiljka> posiljke,
			String rutaIsporuke, Date datum) {
		super();
		this.magacin = magacin;
		this.tipKamiona = tipKamiona;
		this.preostalaNosivost = preostalaNosivost;
		this.posiljke = posiljke;
		this.odrediste = rutaIsporuke;
		this.datum = datum;
	}
	public Otprema(Magacin magacin, String tipKamiona, Double preostalaNosivost, List<Posiljka> posiljke,
			String rutaIsporuke, Date datum, String statusObrade) {
		super();
		this.magacin = magacin;
		this.tipKamiona = tipKamiona;
		this.preostalaNosivost = preostalaNosivost;
		this.posiljke = posiljke;
		this.odrediste = rutaIsporuke;
		this.datum = datum;
		this.statusObrade = statusObrade;
	}
	public Otprema(Magacin magacin, String tipKamiona, Double preostalaNosivost, List<Posiljka> posiljke,
			String odrediste, String vaznostIsporuke, Date rokIsporuke, Date datum, String statusObrade) {
		super();
		this.magacin = magacin;
		this.tipKamiona = tipKamiona;
		this.preostalaNosivost = preostalaNosivost;
		this.posiljke = posiljke;
		this.odrediste = odrediste;
		this.vaznostIsporuke = vaznostIsporuke;
		this.rokIsporuke = rokIsporuke;
		this.datum = datum;
		this.statusObrade = statusObrade;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Magacin getMagacin() {
		return magacin;
	}
	public void setMagacin(Magacin magacin) {
		this.magacin = magacin;
	}
	public String getTipKamiona() {
		return tipKamiona;
	}
	public void setTipKamiona(String tipKamiona) {
		this.tipKamiona = tipKamiona;
	}
	public Double getPreostalaNosivost() {
		return preostalaNosivost;
	}
	public void setPreostalaNosivost(Double preostalaNosivost) {
		this.preostalaNosivost = preostalaNosivost;
	}
	public List<Posiljka> getPosiljke() {
		return posiljke;
	}
	public void setPosiljke(List<Posiljka> posiljke) {
		this.posiljke = posiljke;
	}
	public void addPosiljka(Posiljka posiljka) {
		this.posiljke.add(posiljka);
	}
	public String getVaznostIsporuke() {
		return vaznostIsporuke;
	}
	public void setVaznostIsporuke(String vaznostIsporuke) {
		this.vaznostIsporuke = vaznostIsporuke;
	}
	public Date getRokIsporuke() {
		return rokIsporuke;
	}
	public void setRokIsporuke(Date rokIsporuke) {
		this.rokIsporuke = rokIsporuke;
	}
	public String getOdrediste() {
		return odrediste;
	}
	public void setOdrediste(String odrediste) {
		this.odrediste = odrediste;
	}
	public Date getDatum() {
		return datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	public String getStatusObrade() {
		return statusObrade;
	}
	public void setStatusObrade(String statusObrade) {
		this.statusObrade = statusObrade;
	}
	public Dostavljac getDostavljac() {
		return dostavljac;
	}
	public void setDostavljac(Dostavljac dostavljac) {
		this.dostavljac = dostavljac;
	}
	
	
	@Override
	public String toString() {
		return "Otprema [tipKamiona=" + tipKamiona + ", preostalaNosivost(kg)=" + preostalaNosivost + ", odrediste=" + odrediste
				+ ", \n\t vaznostIsporuke=" + vaznostIsporuke + ", rokIsporuke=" + rokIsporuke + ", datum=" + datum
				+ "\n" + posiljke 
				+ "]";
	}
	
//	@Override
//	public String toString() {
//		return "Otprema [id=" + id + ", magacin=" + magacin + ", tipKamiona=" + tipKamiona + ", preostalaNosivost="
//				+ preostalaNosivost + ", posiljke=" + posiljke + ", odrediste=" + odrediste
//				+ ", vaznostIsporuke=" + vaznostIsporuke + ", rokIsporuke=" + rokIsporuke + ", datum=" + datum
//				+ ", statusObrade=" + statusObrade + ", dostavljac=" + dostavljac + "]";
//	}
}
