package narudzbe;


import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import entiteti.Narudzba;

/**
 * Session Bean implementation class NarudzbeBean
 */
@Stateless
public class NarudzbeSLSB implements NarudzbeSLSBRemote, NarudzbeSLSBLocal {
	
	@EJB
	NarudzbeDBSingleton nsb;

    public NarudzbeSLSB() {
    	super();
    }

    // REMOTE METHOD
	@Override
	public List<Narudzba> pregledNarudzbi(Date start, Date end, String statusObrade) {
		return nsb.pregledNarudzbi(start, end, statusObrade);
	}

	@Override
	public String test() {
		
	
		return null;
	}
}
