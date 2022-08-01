package transport;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import entiteti.Artikal;
import entiteti.Narucilac;
import narudzbe.message.PotvrdaItem;

@Local
public interface TransportSLSBLocal {
	public List<String> moguceRute(String mestoNarucioca);
	public PotvrdaItem obradaArtiklaNarudzbe(Artikal artikalNarudzbe, String vaznostIsporuke, 
			Date datumNarudzbe, Narucilac narucilac);
}
