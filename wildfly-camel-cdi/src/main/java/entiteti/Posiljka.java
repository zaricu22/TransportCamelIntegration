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
		return "\n Posiljka [\n\t" + narucilac + "\n\t" + artikal + "\n ]";
	}
	
//	@Override
//	public String toString() {
//		return "\n Posiljka [\n artikal=\n" + artikal + ", \n narucilac=" + narucilac + "\n ]";
//	}
}
