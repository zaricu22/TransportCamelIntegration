package narudzbe.message;



import javax.ejb.Remote;

import entiteti.Narudzba;


@Remote
public interface NarucilacSLSBRemote {
	public String slanjeNarudzbe(Narudzba narudzba);
	public String prijemObavestenja(String nazivNaruioca, String idNarudzbe);
}
