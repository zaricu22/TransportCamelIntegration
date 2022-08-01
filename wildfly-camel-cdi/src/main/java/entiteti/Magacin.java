package entiteti;

import java.io.Serializable;

public class Magacin implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String nazivPreduzeca;
	private String adresaMagacina;
	private String kontaktMagacina;
	
	public Magacin(String nazivPreduzeca, String adresaMagacina, String kontaktMagacina) {
		super();
		this.nazivPreduzeca = nazivPreduzeca;
		this.adresaMagacina = adresaMagacina;
		this.kontaktMagacina = kontaktMagacina;
	}

	public String getNazivPreduzeca() {
		return nazivPreduzeca;
	}
	public void setNazivPreduzeca(String nazivPreduzeca) {
		this.nazivPreduzeca = nazivPreduzeca;
	}
	public String getAdresaMagacina() {
		return adresaMagacina;
	}
	public void setAdresaMagacina(String adresaMagacina) {
		this.adresaMagacina = adresaMagacina;
	}
	public String getKontaktMagacina() {
		return kontaktMagacina;
	}
	public void setKontaktMagacina(String kontaktMagacina) {
		this.kontaktMagacina = kontaktMagacina;
	}

	@Override
	public String toString() {
		return "Magacin [nazivPreduzeca=" + nazivPreduzeca + ", adresaMagacina=" + adresaMagacina + ", kontaktMagacina="
				+ kontaktMagacina + "]";
	}
}
