package entitetiRSWS;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class NarudzbaJSON implements Serializable {
	private static final long serialVersionUID = 1L;

	private NarucilacJSON narucilac;	// sadrzi mesto
	private List<ArtikalJSON> artikli;	// sadrzi kolicinu
	private String vaznostIsporuke;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date datum;
	
	
	public NarudzbaJSON() {
		super();
	}
	
	public NarucilacJSON getNarucilac() {
		return narucilac;
	}
	public void setNarucilac(NarucilacJSON narucilac) {
		this.narucilac = narucilac;
	}
	public List<ArtikalJSON> getArtikli() {
		return artikli;
	}
	public void setArtikli(List<ArtikalJSON> artikli) {
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
