package entitetiRSWS;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;



@SuppressWarnings("serial")
@XmlRootElement(name = "narudzba")
@XmlAccessorType(XmlAccessType.FIELD)
public class NarudzbaXML implements Serializable {

	private NarucilacXML narucilac;	// sadrzi mesto
	
	@XmlElementWrapper(name="artikli")
    @XmlElement(name="artikal")
	private List<ArtikalXML> artikli;	// sadrzi kolicinu
	
	private String vaznostIsporuke;

	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date datum;
	
	
	public NarudzbaXML() {
		super();
	}
	
	
	public NarucilacXML getNarucilac() {
		return narucilac;
	}
	public void setNarucilac(NarucilacXML narucilac) {
		this.narucilac = narucilac;
	}
	public List<ArtikalXML> getArtikli() {
		return artikli;
	}
	public void setArtikli(List<ArtikalXML> artikli) {
		this.artikli = artikli;
	}
	public String getVaznostIsporuke() {
		return vaznostIsporuke;
	}
	public void setVaznostIsporuke(String vaznostIsporuke) {
		this.vaznostIsporuke = vaznostIsporuke;
	}
	public Date getDatum() {
		return datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
	@Override
	public String toString() {
		return "Narudzba [narucilac=" + narucilac + ", artikli=" + artikli + ", vaznostIsporuke=" + vaznostIsporuke
				+ ", datum=" + datum + "]";
	}
}
