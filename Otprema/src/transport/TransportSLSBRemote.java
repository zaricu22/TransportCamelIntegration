package transport;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import entiteti.Otprema;

@Remote
public interface TransportSLSBRemote {
	public List<Otprema> pregledOtprema(Date start, Date end, String statusObrade);
	public Otprema test();
}
