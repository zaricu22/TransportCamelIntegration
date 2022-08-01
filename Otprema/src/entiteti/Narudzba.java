package entiteti;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Narudzba implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Narucilac narucilac;	// sadrzi mesto
	private List<Artikal> artikli;	// sadrzi kolicinu
	private String vaznostIsporuke;
	private Date datum;
	private String statusObrade;
	
	
	public Narudzba() {
		super();
	}
	public Narudzba(Narucilac narucilac, List<Artikal> artikli, Date datum) {
		super();
		this.narucilac = narucilac;
		this.artikli = artikli;
		this.datum = datum;
	}
	public Narudzba(Narucilac narucilac, List<Artikal> artikli, Date datum, String statusObrade) {
		super();
		this.narucilac = narucilac;
		this.artikli = artikli;
		this.datum = datum;
		this.statusObrade = statusObrade;
	}
	public Narudzba(Narucilac narucilac, List<Artikal> artikli, String vaznostIsporuke, Date datum) {
		super();
		this.narucilac = narucilac;
		this.artikli = artikli;
		this.vaznostIsporuke = vaznostIsporuke;
		this.datum = datum;
	}
	public Narudzba(Narucilac narucilac, List<Artikal> artikli, String vaznostIsporuke, Date datum, String statusObrade) {
		super();
		this.narucilac = narucilac;
		this.artikli = artikli;
		this.vaznostIsporuke = vaznostIsporuke;
		this.datum = datum;
		this.statusObrade = statusObrade;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Narucilac getNarucilac() {
		return narucilac;
	}
	public void setNarucilac(Narucilac narucilac) {
		this.narucilac = narucilac;
	}
	public List<Artikal> getArtikli() {
		return artikli;
	}
	public void setArtikli(List<Artikal> artikli) {
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
	public String getStatusObrade() {
		return statusObrade;
	}
	public void setStatusObrade(String statusObrade) {
		this.statusObrade = statusObrade;
	}
	
	@Override
	public String toString() {
		return "Narudzba [id=" + id + ", narucilac=" + narucilac + ", artikli=" + artikli + ", vaznostIsporuke="
				+ vaznostIsporuke + ", datum=" + datum + ", statusObrade=" + statusObrade + "]";
	}
}
