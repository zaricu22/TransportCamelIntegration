package opcije;

import java.util.Calendar;
import java.util.Date;

import entiteti.Otprema;

public class RokCompletionBean {
	
	public long complete(/* @Body */ Otprema body /* , Exchange ecxhange , @Header(value="") nesto, 
						@Property(name=""), @Headers, @OutHeaders, @Properties */) {
		
		Date rokIsporuke = body.getRokIsporuke();
		Calendar cal = Calendar.getInstance();
		cal.setTime(rokIsporuke);
		cal.add(Calendar.DATE, -5);
		Calendar calNow = Calendar.getInstance();
		calNow.setTime(new Date());
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		//System.out.println(sdf.format(cal.getTime()) +"  -  "+ sdf.format(calNow.getTime()));
		
		// millis preostalo do 5 dana pre rokIsporuke
		return cal.getTimeInMillis()-calNow.getTimeInMillis(); 
	}
}
