package transport.message;

import java.util.List;

import javax.ejb.Remote;


@Remote
public interface PrevoznikSLSBRemote {
	public List<String> ponudaOtprema(String subscriptionName);
}
