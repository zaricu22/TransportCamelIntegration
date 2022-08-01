package entiteti;

import java.io.Serializable;

public class Lokacija implements Serializable{
	private static final long serialVersionUID = 1L;
	
	String sektor;
	int red;
	int kolona;
	int nivo;
	int kapacitet;
	int popunjenost;
	
	
	public Lokacija() {
		super();
	}
	public Lokacija(String sektor, int red, int nivo, int kapacitet, int popunjenost) {
		super();
		this.sektor = sektor;
		this.red = red;
		this.nivo = nivo;
		this.kapacitet = kapacitet;
		this.popunjenost = popunjenost;
	}
	public Lokacija(String sektor, int red, int kolona, int nivo, int kapacitet, int popunjenost) {
		super();
		this.sektor = sektor;
		this.red = red;
		this.kolona = kolona;
		this.nivo = nivo;
		this.kapacitet = kapacitet;
		this.popunjenost = popunjenost;
	}
	
	
	public String getSektor() {
		return sektor;
	}
	public void setSektor(String sektor) {
		this.sektor = sektor;
	}
	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = red;
	}
	public int getKolona() {
		return kolona;
	}
	public void setKolona(int kolona) {
		this.kolona = kolona;
	}
	public int getNivo() {
		return nivo;
	}
	public void setNivo(int nivo) {
		this.nivo = nivo;
	}
	public int getKapacitetPaleta() {
		return kapacitet;
	}
	public void setKapacitetPaleta(int kapacitetPaleta) {
		this.kapacitet = kapacitetPaleta;
	}
	public int getPopunjenost() {
		return popunjenost;
	}
	public void setPopunjenost(int popunjenost) {
		this.popunjenost = popunjenost;
	}
	
	
	@Override
	public String toString() {
		return "Lokacija [sektor=" + sektor + ", red=" + red + ", kolona=" + kolona + ", nivo=" + nivo + ", kapacitet="
				+ kapacitet + ", popunjenost=" + popunjenost + "]";
	}
}
