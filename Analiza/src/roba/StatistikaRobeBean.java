package roba;

import javax.ejb.Stateless;

/**
 * Session Bean implementation class RobnaStatistika
 */
@Stateless
public class StatistikaRobeBean implements StatistikaRobeBeanRemote, StatistikaRobeBeanLocal {


    public StatistikaRobeBean() {
    	super();
    }

    // TODO Analiza - Izvestaj robnih zaliha
}
