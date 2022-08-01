package entiteti;

import java.io.Serializable;

public class Posiljka implements Serializable {
	private static final long serialVersionUID = 1L;
	
	Artikal artikal;	// sadrzi kolicinu
	Narucilac narucilac;	// sadrzi mesto
	
	
	public Posiljka() {
		super();
	}
	public Posiljka(Artikal artikal, Narucilac narucilac) {
		super();
		this.artikal = artikal;
		this.narucilac = narucilac;
	}

	
	public Artikal getArtikal() {
		return artikal;
	}
	public void setArtikal(Artikal artikal) {
		this.artikal = artikal;
	}
	public Narucilac getNarucilac() {
		return narucilac;
	}
	public void setNarucilac(Narucilac narucilac) {
		this.narucilac = narucilac;
	}

	@Override
	public String toString() {
		return "\n\t Posiljka [\n \t\t artikal=" + artikal + ", \n \t\t narucilac=" + narucilac + "\n\t ]";
	}
}
