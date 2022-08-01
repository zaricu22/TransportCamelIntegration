package narudzbe.message;

import java.io.Serializable;
import java.util.Date;

import entiteti.Artikal;
import entiteti.Dostavljac;

public class PotvrdaItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	Artikal artikal;
	Date rokIsporuke;
	String tipKamiona;
	Dostavljac dostavljac;

	
	public PotvrdaItem() {
		super();
	}

	public PotvrdaItem(Artikal artikal, Date rokIsporuke, String tipKamiona, Dostavljac dostavljac) {
		super();
		this.artikal = artikal;
		this.rokIsporuke = rokIsporuke;
		this.tipKamiona = tipKamiona;
		this.dostavljac = dostavljac;
	}


	public Artikal getArtikal() {
		return artikal;
	}

	public void setArtikal(Artikal artikal) {
		this.artikal = artikal;
	}

	public Date getRokIsporuke() {
		return rokIsporuke;
	}

	public void setRokIsporuke(Date rokIsporuke) {
		this.rokIsporuke = rokIsporuke;
	}

	public String getTipKamiona() {
		return tipKamiona;
	}

	public void setTipKamiona(String tipKamiona) {
		this.tipKamiona = tipKamiona;
	}

	public Dostavljac getDostavljac() {
		return dostavljac;
	}

	public void setDostavljac(Dostavljac dostavljac) {
		this.dostavljac = dostavljac;
	}

	@Override
	public String toString() {
		return "PotvrdaItem [artikal=" + artikal + ", rokIsporuke=" + rokIsporuke + ", tipKamiona=" + tipKamiona
				+ ", dostavljac=" + dostavljac + "]";
	}
}
