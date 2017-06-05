package com.feed_the_beast.ftbu.api_impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbu.api.chunks.IPlayerInChunk;

public class PlayerInChunk implements IPlayerInChunk {
	private Calendar enterTimeCalendar = Calendar.getInstance();
	private Calendar leaveTimeCalendar;
	private String stayTime;
	private IForgePlayer playerInChunk;
	
	public PlayerInChunk(IForgePlayer player) {
		playerInChunk = player;
	}
	
	public PlayerInChunk(Calendar entertime, Calendar leavetime, String staytime, IForgePlayer player) {
		enterTimeCalendar = entertime;
		leaveTimeCalendar = leavetime;
		stayTime = staytime;
		playerInChunk = player;
	}
	
	@Override
	public IForgePlayer getPlayer() {
		return playerInChunk;
	}
	
	@Override
	public String getEnterTime() {
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(enterTimeCalendar.getTime());
	}
	
	@Override
	@Nullable
	public Calendar getLeaveTimeCalendar() {
		return leaveTimeCalendar;
	}
	
	@Override
	@Nullable
	public Calendar getEnterTimeCalendar() {
		return enterTimeCalendar;
	}
	
	@Override
	public void playerLeft() {
		leaveTimeCalendar = Calendar.getInstance();
		final Map<TimeUnit, Long> difference = getdifference();
		int days;
		int hours;
		int minutes;
		int seconds;
		if (difference.get(TimeUnit.DAYS) == null) {
			days = 0;
		} else {
			days = difference.get(TimeUnit.DAYS).intValue();
		}
		if (difference.get(TimeUnit.HOURS) == null) {
			hours = 0;
		} else {
			hours = difference.get(TimeUnit.HOURS).intValue();
		}
		if (difference.get(TimeUnit.MINUTES) == null) {
			minutes = 0;
		} else {
			minutes = difference.get(TimeUnit.MINUTES).intValue();
		}
		if (difference.get(TimeUnit.SECONDS) == null) {
			seconds = 0;
		} else {
			seconds = difference.get(TimeUnit.SECONDS).intValue();
		}
		//calendar wouldn't work
		stayTime = days + " days, "+ hours +" hours, "+ minutes +" minutes, "+ seconds +" seconds";
	}
	
	@Override
	public Map<TimeUnit,Long> getdifference() {
	    long diffInMilliseconds = leaveTimeCalendar.getTimeInMillis() - enterTimeCalendar.getTimeInMillis();
	    List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
	    Collections.reverse(units);
	    Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
	    long millisecondsRest = diffInMilliseconds;
	    for ( TimeUnit unit : units ) {
	        long diff = unit.convert(millisecondsRest,TimeUnit.MILLISECONDS);
	        long diffInMilliesForUnit = unit.toMillis(diff);
	        millisecondsRest = millisecondsRest - diffInMilliesForUnit;
	        result.put(unit,diff);
	    }
	    return result;
	}
	
	@Override
	@Nullable
	public String getLeaveTime() {
		if (leaveTimeCalendar != null) {
			return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(leaveTimeCalendar.getTime());
		} else {
			return null;
		}
	}
	
	@Override
	@Nullable
	public String getStayTime() {
		if (stayTime != null) {
			return stayTime;
		} else {
			return null;
		}
	}
}
