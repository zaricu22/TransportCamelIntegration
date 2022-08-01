package transport.message;

import javax.ejb.Local;

import entiteti.Dostavljac;

@Local
public interface PrevoznikSLSBLocal {
	public boolean potvrdiOtpremu(String idOtpreme, Dostavljac dostavljac);
	public String statusOtpreme(String idOtpreme);
}
