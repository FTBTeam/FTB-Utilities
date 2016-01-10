package latmod.ftbu.world;

import latmod.lib.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.*;
import net.minecraft.util.*;

import java.util.List;

public class LMPlayerStats
{
	public final LMPlayerServer owner;
	
	public int deaths;
	public long lastSeen, firstJoined, timePlayed;
	
	public LMPlayerStats(LMPlayerServer p)
	{ owner = p; }
	
	// NBT //
	
	public void readFromNBT(NBTTagCompound tag)
	{
		deaths = tag.getInteger("Deaths");
		lastSeen = tag.getLong("LastSeen");
		firstJoined = tag.getLong("Joined");
		timePlayed = tag.getLong("TimePlayed");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("Deaths", deaths);
		tag.setLong("LastSeen", lastSeen);
		tag.setLong("Joined", firstJoined);
		tag.setLong("TimePlayed", timePlayed);
	}
	
	// Server side //
	
	public void getInfo(List<IChatComponent> info, long ms)
	{
		if(lastSeen > 0L && !owner.isOnline())
			info.add(new ChatComponentTranslation("ftbu:label.last_seen", LMStringUtils.getTimeString(ms - lastSeen)));
		if(firstJoined > 0L)
			info.add(new ChatComponentTranslation("ftbu:label.joined", LMStringUtils.getTimeString(ms - firstJoined)));
		if(deaths > 0) info.add(new ChatComponentTranslation("ftbu:label.deaths", String.valueOf(deaths)));
		if(timePlayed > 0L)
			info.add(new ChatComponentTranslation("stat.playOneMinute").appendSibling(new ChatComponentText(": " + LMStringUtils.getTimeString(timePlayed))));
	}
	
	public int getStat(StatBase s)
	{ return owner.isOnline() ? owner.getPlayer().func_147099_x().writeStat(s) : 0; }
	
	public void refreshStats()
	{
		if(!owner.isOnline()) return;
		long ms = LMUtils.millis();
		
		timePlayed = getStat(StatList.minutesPlayedStat) * 50L;
		deaths = getStat(StatList.deathsStat);
		
		lastSeen = ms;
		if(firstJoined <= 0L) firstJoined = lastSeen;
	}
	
	// - //
	
	public double getDeathsPerHour()
	{
		if(deaths == 0 || timePlayed == 0L) return 0D;
		return (double) deaths / (timePlayed / 3600000D);
	}
	
	public long getLastSeen()
	{ return owner.isOnline() ? LMUtils.millis() : lastSeen; }
	
	public double getLastSeenDeltaInHours()
	{
		if(owner.isOnline()) return 0D;
		return (LMUtils.millis() - getLastSeen()) / 3600000D;
	}
}