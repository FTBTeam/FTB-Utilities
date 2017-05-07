package com.feed_the_beast.ftbu.api.chunks;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.feed_the_beast.ftbl.api.IForgePlayer;

public interface IPlayerInChunk {

	IForgePlayer getPlayer();
	
	String getEnterTime();
	
	Calendar getLeaveTimeCalendar();
	
	Calendar getEnterTimeCalendar();
	
	void playerLeft();
	
	Map<TimeUnit, Long> getdifference();
	
	String getLeaveTime();
	
	String getStayTime();
	
}
