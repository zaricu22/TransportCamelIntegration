package mongoclient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Session Bean implementation class MongoClientSingleton
 */
@Singleton
@LocalBean
public class MongoClientSingleton {
	
	private MongoClient client = null;
	
	@Lock(LockType.READ)
	public MongoClient getMongoClient(){	
		return client;
	}
	
	@PostConstruct
	public void initializeSB() {
		try {
			client = MongoClients.create("mongodb://localhost:27017");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@PreDestroy 
	public void destroySB() {
		try {
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

    public MongoClientSingleton() {
        super();
    }

}
