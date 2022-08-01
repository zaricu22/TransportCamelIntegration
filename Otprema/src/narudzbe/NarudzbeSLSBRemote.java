package narudzbe;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import entiteti.Narudzba;

@Remote
public interface NarudzbeSLSBRemote {
	public List<Narudzba> pregledNarudzbi(Date start, Date end, String statusObrade);
	public String test();
}
