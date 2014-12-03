package latmod.core;

import java.util.UUID;

import latmod.core.net.*;
import latmod.core.util.FastList;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;

public class LMPlayer implements Comparable<LMPlayer>
{
	public final UUID uuid;
	public String username;
	private String customName;
	public String customSkin;
	public String customCape;
	public final FastList<UUID> whitelist;
	public final FastList<UUID> blacklist;
	private NBTTagCompound customData = null;
	
	public LMPlayer(UUID id)
	{
		uuid = id;
		whitelist = new FastList<UUID>();
		blacklist = new FastList<UUID>();
	}
	
	public NBTTagCompound customData()
	{
		if(customData == null)
			customData = new NBTTagCompound();
		return customData;
	}
	
	public boolean hasCustomData()
	{ return customData != null; }
	
	public void setCustomName(String s)
	{
		if(s != null && s.length() > 0)
		{
			s = s.trim().replace("&k", "").replace("&", "\u00a7");
			if(s.length() == 0 || s.equals("null")) s = null;
		}
		
		customName = s;
		
		EntityPlayer ep = getPlayer();
		if(ep != null) ep.refreshDisplayName();
	}
	
	public String getDisplayName()
	{
		if(customName != null && customName.length() > 0)
			return customName + EnumChatFormatting.RESET;
		return username;
	}
	
	public boolean hasCustomName()
	{ return customName != null; }
	
	public EntityPlayerMP getPlayer()
	{
		for(int i = 0; i < MinecraftServer.getServer().getConfigurationManager().playerEntityList.size(); i++)
		{
			EntityPlayerMP ep = (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(i);
			if(ep.getUniqueID().equals(uuid)) return ep;
		}
		
		return null;
	}
	
	public boolean isOnline()
	{ return getPlayer() != null; }
	
	public void sendUpdate(String channel, boolean clientUpdate)
	{
		if(LatCoreMC.isServer())
		{
			new DataChangedEvent(this, Side.SERVER, channel).post();
			if(clientUpdate) LMNetHandler.INSTANCE.sendToAll(new MessageUpdatePlayerData(this, channel));
		}
	}
	
	public void sendUpdate(String channel)
	{ sendUpdate(channel, true); }
	
	// NBT reading / writing
	
	public void readFromNBT(NBTTagCompound tag)
	{
		username = tag.getString("Name");
		
		customName = tag.getString("CustomName");
		if(customName.trim().length() == 0) customName = null;
		
		customSkin = tag.getString("CustomSkin");
		if(customSkin.trim().length() == 0) customSkin = null;
		
		customCape = tag.getString("CustomCape");
		if(customCape.trim().length() == 0) customCape = null;
		
		{
			whitelist.clear();
			NBTTagList l = (NBTTagList)tag.getTag("Whitelist");
			if(l != null) for(int i = 0; i < l.tagCount(); i++)
				whitelist.add(UUID.fromString(l.getStringTagAt(i)));
		}
		
		{
			blacklist.clear();
			NBTTagList l = (NBTTagList)tag.getTag("Blacklist");
			if(l != null) for(int i = 0; i < l.tagCount(); i++)
				blacklist.add(UUID.fromString(l.getStringTagAt(i)));
		}
		
		customData = (NBTTagCompound) tag.getTag("CustomData");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Name", username);
		
		if(customName != null && customName.trim().length() > 0)
			tag.setString("CustomName", customName);
		
		if(customSkin != null && customSkin.trim().length() > 0)
			tag.setString("CustomSkin", customSkin);
		
		if(customCape != null && customCape.trim().length() > 0)
			tag.setString("CustomCape", customCape);
		
		if(whitelist.size() > 0)
		{
			NBTTagList l = new NBTTagList();
			
			for(int i = 0; i < whitelist.size(); i++)
				l.appendTag(new NBTTagString(whitelist.get(i).toString()));
			
			if(l.tagCount() > 0)
				tag.setTag("Whitelist", l);
		}
		
		if(blacklist.size() > 0)
		{
			NBTTagList l = new NBTTagList();
			
			for(int i = 0; i < blacklist.size(); i++)
				l.appendTag(new NBTTagString(blacklist.get(i).toString()));
			
			if(l.tagCount() > 0)
				tag.setTag("Blacklist", l);
		}
		
		if(customData != null)
			tag.setTag("CustomData", customData);
	}
	
	public int compareTo(LMPlayer o)
	{
		if(username == null || o.username == null)
			return 0;
		return username.compareTo(o.username);
	}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(o instanceof String) return o.equals(username) || ((String)o).equalsIgnoreCase(LatCoreMC.removeFormatting(getDisplayName()));
		if(o instanceof UUID) return ((UUID)o).equals(uuid);
		if(o instanceof EntityPlayer) return equals(((EntityPlayer)o).getUniqueID());
		if(o instanceof LMPlayer) return equals(((LMPlayer)o).uuid);
		return false;
	}
	
	// Static //
	
	public static final FastList<LMPlayer> list = new FastList<LMPlayer>();
	
	public static LMPlayer getPlayer(Object o)
	{
		if(o instanceof LMPlayer)
			return (LMPlayer)o;
		return list.getObj(o);
	}
	
	private static class LMPlayerEvent extends Event
	{
		public final LMPlayer player;
		
		public LMPlayerEvent(LMPlayer p)
		{ player = p; }
		
		public void post()
		{ MinecraftForge.EVENT_BUS.post(this); }
	}
	
	public static class DataChangedEvent extends LMPlayerEvent
	{
		public final Side side;
		public final String channel;
		
		public DataChangedEvent(LMPlayer p, Side s, String c)
		{ super(p); side = s; channel = c; }
		
		public boolean isChannel(String s)
		{ return channel != null && channel.equals(s); }
	}
	
	public static class DataLoadedEvent extends LMPlayerEvent
	{
		public DataLoadedEvent(LMPlayer p)
		{ super(p); }
	}
	
	public static class DataSavedEvent extends LMPlayerEvent
	{
		public DataSavedEvent(LMPlayer p)
		{ super(p); }
	}
	
	public static class LMPlayerLoggedInEvent extends LMPlayerEvent
	{
		public final EntityPlayer entityPlayer;
		public final boolean firstTime;
		
		public LMPlayerLoggedInEvent(LMPlayer p, EntityPlayer ep, boolean b)
		{ super(p); entityPlayer = ep; firstTime = b; }
	}
	
	public static String[] getAllDisplayNames(boolean online)
	{
		FastList<String> allOn = new FastList<String>();
		FastList<String> allOff = new FastList<String>();
		
		for(int i = 0; i < list.size(); i++)
		{
			LMPlayer p = list.get(i);
			
			String s = LatCoreMC.removeFormatting(p.getDisplayName());
			
			if(p.isOnline())
				allOn.add(s);
			else if(!online)
				allOff.add(s);
		}
		
		allOn.sort(null);
		
		if(!online)
		{
			allOff.sort(null);
			
			for(int i = 0; i < allOff.size(); i++)
			{
				String s = allOff.get(i);
				if(!allOn.contains(s)) allOn.add(s);
			}
		}
		
		return allOn.toArray(new String[0]);
	}
}