package entiteti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import entitetiRSWS.DateAdapter;

@XmlRootElement(name = "potvrda")
@XmlAccessorType(XmlAccessType.FIELD)
public class PotvrdaNarudzbe implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@XmlTransient
	private String _id;
	
	@XmlElement(name="narucilac")
	private Narucilac narucilac;	// sadrzi mesto
	
	@XmlElementWrapper(name="dostupniArtikli")
    @XmlElement(name="artikalDostupni")
	ArrayList<Artikal> dostupniArtikli;
	@XmlElementWrapper(name="nedostupniArtikli")
    @XmlElement(name="artikalNedostupni")
	ArrayList<Artikal> nedostupniArtikli;
	
	String vaznostIsporuke;
	
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date datum;
	
	public PotvrdaNarudzbe() {
		dostupniArtikli = new ArrayList<Artikal>();
		nedostupniArtikli = new ArrayList<Artikal>();
	}
	public PotvrdaNarudzbe(ArrayList<Artikal> dostupniArtikli, ArrayList<Artikal> nedostupniArtikli) {
		super();
		this.dostupniArtikli = dostupniArtikli;
		this.nedostupniArtikli = nedostupniArtikli;
	}

	
	public ArrayList<Artikal> getDostupniArtikli() {
		return dostupniArtikli;
	}
	public void setDostupniArtikli(ArrayList<Artikal> dostupni) {
		this.dostupniArtikli = dostupni;
	}
	public void addDostupniArtikli(Artikal artikal) {
		this.dostupniArtikli.add(artikal);
	}
	public ArrayList<Artikal> getNedostupniArtikli() {
		return nedostupniArtikli;
	}
	public void setNedostupniArtikli(ArrayList<Artikal> nedostupni) {
		this.nedostupniArtikli = nedostupni;
	}
	public void addNedostupniArtikli(Artikal artikal) {
		this.nedostupniArtikli.add(artikal);
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Narucilac getNarucilac() {
		return narucilac;
	}
	public void setNarucilac(Narucilac narucilac) {
		this.narucilac = narucilac;
	}
	public Date getDatum() {
		return datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	public String getVaznostIsporuke() {
		return vaznostIsporuke;
	}
	public void setVaznostIsporuke(String vaznostIsporuke) {
		this.vaznostIsporuke = vaznostIsporuke;
	}
	
	
	@Override
	public String toString() {
		return "\n PotvrdaNarudzbe [ id=" + _id + ", \n\t narucilac=" + narucilac + ", \n dostupniArtikli=" + dostupniArtikli
				+ ", \n nedostupniArtikli=" + nedostupniArtikli + ", \n vaznostIsporuke=" + vaznostIsporuke 
				+", \n\t datum=" + datum + "\n ]";
	}	
}
