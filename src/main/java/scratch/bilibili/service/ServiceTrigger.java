package scratch.bilibili.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.catalina.startup.HomesUserDatabase;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

public class ServiceTrigger implements Trigger{

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
        Calendar nextExecutionTime =  new GregorianCalendar();
        nextExecutionTime.set(Calendar.HOUR_OF_DAY, 10);
        Date lastActualExecutionTime = triggerContext.lastActualExecutionTime(); 
        nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date().set);
        nextExecutionTime.add(Calendar.MILLISECOND, env.getProperty("myRate", Integer.class)); //you can get the value from wherever you want
        return nextExecutionTime.getTime();
		return null;
	}

}
