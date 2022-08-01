package skladiste;

import javax.ejb.Stateless;

/**
 * Session Bean implementation class SkladistnaStatistika
 */
@Stateless
public class StatistikaSkladistaBean implements StatistikaSkladistaBeanRemote, StatistikaSkladistaBeanLocal {


    public StatistikaSkladistaBean() {
    	super();
    }

    // TODO Analiza - Izvestaj skladista robe
}
