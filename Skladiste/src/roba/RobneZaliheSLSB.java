package roba;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import entiteti.Roba;
import mongoclient.MongoClientSingleton;


@Stateless
public class RobneZaliheSLSB implements RobneZaliheSLSBRemote, RobneZaliheSLSBLocal {
	
	@EJB
	RobneZaliheDBSingleton rzs;
	
	@EJB
	MongoClientSingleton mcs;

    public RobneZaliheSLSB() {
        super();
    }

    // Dodavanje robe u bazu
    // LOCAL METHOD
    @Override
	public boolean evidentiranjeArtikla(Roba roba) {
		return rzs.evidentiranjeArtikla(roba);
	}
 
    // LOCAL METHOD
	@Override
 	public Integer odrediPrijemniBroj(Integer moguciRed, String sektor, String kataloskiBroj) {
		return rzs.odrediPrijemniBroj(moguciRed, sektor, kataloskiBroj);
	}
	
	// LOCAL METHOD
	@Override
	public List<Roba> pregledNeuskladisteneRobe() {
		return rzs.pregledNeuskladisteneRobe();
	}
	
	// Obavestimo proizvodjaca da je ponestalo porizvoda
	// REMOTE METHOD
    @Override
	public boolean nabavkaArtikla(String kataloskiBroj) {
		// TODO Nabavka Artikla
    
		return false;
	}
    
    // Smanjenje dostupne kolicine artikla u bazi
    // REMOTE METHOD
	@Override
    public boolean rezervisanjeArtikla(String kataloskiBroj, Integer potrebnaKolicina) {
		return rzs.rezervisanjeArtikla(kataloskiBroj.trim(), potrebnaKolicina);
    }
	 
	// REMOTE METHOD
	@Override
	public String vratiTipArtikla(String kataloskiBroj) {
		return rzs.vratiTipArtikla(kataloskiBroj.trim());
	}

	// REMOTE METHOD
	@Override
	public Double vratiTezinuArtikla(String kataloskiBroj) {
		return rzs.vratiTezinuArtikla(kataloskiBroj.trim());
	}
	
	@Override
	public String test() {
		
		
		return null;
	}
    
}
