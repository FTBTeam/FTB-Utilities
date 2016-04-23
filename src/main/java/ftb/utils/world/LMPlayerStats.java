package ftb.utils.world;

import latmod.lib.LMStringUtils;
import latmod.lib.LMUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class LMPlayerStats
{
	public int deaths;
	public long firstJoined;
	public long lastSeen;
	public long timePlayed;
	
	public void refresh(LMPlayerServer player, boolean force)
	{
		StatisticsFile file = player.getStatFile(force);
		if(file == null) return;
		
		long ms = LMUtils.millis();
		
		timePlayed = file.writeStat(StatList.minutesPlayedStat) * 50L;
		deaths = file.writeStat(StatList.deathsStat);
		
		lastSeen = ms;
		if(firstJoined <= 0L) firstJoined = lastSeen;
	}
	
	public void getInfo(LMPlayerServer owner, List<IChatComponent> info, long ms)
	{
		if(lastSeen > 0L && !owner.isOnline())
			info.add(new ChatComponentTranslation("ftbu.top.last_seen").appendText(": " + LMStringUtils.getTimeString(ms - lastSeen)));
		
		if(firstJoined > 0L)
			info.add(new ChatComponentTranslation("ftbu.top.first_joined").appendText(": " + LMStringUtils.getTimeString(ms - firstJoined)));
		
		if(deaths > 0)
			info.add(new ChatComponentTranslation("ftbu.top.deaths").appendText(": " + String.valueOf(deaths)));
		
		if(timePlayed > 0L)
			info.add(new ChatComponentTranslation("stat.playOneMinute").appendSibling(new ChatComponentText(": " + LMStringUtils.getTimeString(timePlayed))));
	}
	
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
	
	public double getDeathsPerHour()
	{
		if(deaths == 0 || timePlayed == 0L) return 0D;
		return (double) deaths / (timePlayed / 3600000D);
	}
	
	public long getLastSeen(LMPlayerServer p)
	{ return p.isOnline() ? LMUtils.millis() : lastSeen; }
	
	public double getLastSeenDeltaInHours(LMPlayerServer p)
	{
		if(p.isOnline()) return 0D;
		return (LMUtils.millis() - getLastSeen(p)) / 3600000D;
	}
}