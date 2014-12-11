package latmod.core;

import java.util.UUID;

import latmod.core.net.*;
import latmod.core.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;

public class LMPlayer implements Comparable<LMPlayer>
{
	public static enum Custom
	{
		NAME("CustomName"),
		SKIN("CustomSkin"),
		CAPE("CustomCape");
		
		public final int ID;
		public final String key;
		
		Custom(String s)
		{
			ID = ordinal();
			key = s;
		}
	}
	
	public static enum Status
	{
		NONE,
		SELF,
		FRIEND,
		ENEMY;
		
		Status() { }
		
		public boolean isFriend()
		{ return this == FRIEND || this == SELF; }
		
		public boolean isEnemy()
		{ return this == ENEMY; }
		
		public static Status to3(Status s)
		{
			if(s == null || s == NONE || s == SELF)
				return NONE;
			if(s.isFriend()) return FRIEND;
			if(s.isEnemy()) return ENEMY;
			return NONE;
		}
	}
	
	public final UUID uuid;
	public String username;
	private final String[] custom = new String[Custom.values().length];
	public final FastMap<UUID, Status> friends = new FastMap<UUID, Status>();
	private NBTTagCompound customData = null;
	
	public LMPlayer(UUID id)
	{
		uuid = id;
	}
	
	public NBTTagCompound customData()
	{
		if(customData == null)
			customData = new NBTTagCompound();
		return customData;
	}
	
	public boolean hasCustomData()
	{ return customData != null; }
	
	public void setCustom(Custom c, String s)
	{
		String s0 = custom[c.ID];
		
		if(s != null && s.length() > 0)
		{
			custom[c.ID] = s.trim().replace("&k", "").replace("&", LatCoreMC.FORMATTING);
			if(custom[c.ID].length() == 0 || custom[c.ID].equals("null")) custom[c.ID] = null;
		}
		else custom[c.ID] = null;
		
		if(LatCore.isDifferent(s0, custom[c.ID]))
		{
			sendUpdate(c.key);
			
			EntityPlayer ep = getPlayer();
			
			if(ep != null)
			{
				if(c == Custom.NAME)
					ep.refreshDisplayName();
			}
		}
	}
	
	public String getCustom(Custom c)
	{ return custom[c.ID]; }
	
	public String getDisplayName()
	{
		if(hasCustomName())
			return getCustom(Custom.NAME) + EnumChatFormatting.RESET;
		return username;
	}
	
	public boolean hasCustomName()
	{ return getCustom(Custom.NAME) != null && getCustom(Custom.NAME).length() > 0; }
	
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
	
	public Status getRawStatusFor(UUID id)
	{
		if(id == null) return Status.NONE;
		Status s = friends.get(id);
		if(Status.to3(s) == Status.NONE)
			return Status.NONE; return s;
	}
	
	public Status getStatusFor(LMPlayer p)
	{
		if(p == null) return Status.NONE;
		if(p.uuid.equals(uuid)) return Status.SELF;
		
		Status s = getRawStatusFor(p.uuid);
		Status s1 = p.getRawStatusFor(uuid);
		
		if(s == Status.ENEMY || s1 == Status.ENEMY)
			return Status.ENEMY;
		
		if(s == Status.FRIEND && s1 == Status.FRIEND)
			return Status.FRIEND;
		
		return Status.NONE;
	}
	
	public void setStatusFor(UUID id, Status s)
	{
		if(Status.to3(s) == Status.NONE)
			friends.remove(id);
		else
			friends.put(id, s);
	}
	
	public void clearFriends(Status s)
	{
		Status s1 = Status.to3(s);
		
		if(s1 == Status.NONE) friends.clear();
		else if(s1 == Status.FRIEND) for(int i = 0; i < friends.size(); i++)
		{
			if(friends.values.get(i).isFriend())
				friends.remove(friends.keys.get(i));
		}
		else if(s1 == Status.ENEMY) for(int i = 0; i < friends.size(); i++)
		{
			if(!friends.values.get(i).isFriend())
				friends.remove(friends.keys.get(i));
		}
	}
	
	public FastList<LMPlayer> getFriends(Status s)
	{
		FastList<LMPlayer> l = new FastList<LMPlayer>();
		
		Status s1 = Status.to3(s);
		
		for(int i = 0; i < friends.size(); i++)
		{
			boolean add = s1 == Status.NONE;
			if(s1 == Status.FRIEND && !friends.values.get(i).isFriend()) add = false;
			else if(s1 == Status.ENEMY && friends.values.get(i).isFriend()) add = false;
			
			if(add)
			{
				LMPlayer p = LMPlayer.getPlayer(friends.keys.get(i));
				if(p != null) l.add(p);
			}
		}
		
		return l;
	}
	
	// NBT reading / writing
	
	public void readFromNBT(NBTTagCompound tag)
	{
		username = tag.getString("Name");
		
		for(int i = 0; i < custom.length; i++)
		{
			custom[i] = tag.getString(Custom.values()[i].key).trim();
			if(custom[i].length() == 0) custom[i] = null;
		}
		
		friends.clear();
		NBTTagCompound map = tag.getCompoundTag("Friends");
		FastList<String> al = LatCoreMC.getMapKeys(map);
		
		for(int i = 0; i < al.size(); i++)
		{
			String s = al.get(i);
			boolean b = map.getBoolean(s);
			friends.put(UUID.fromString(s), b ? Status.FRIEND : Status.ENEMY);
		}
		
		customData = (NBTTagCompound) tag.getTag("CustomData");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Name", username);
		
		for(int i = 0; i < custom.length; i++)
		{
			if(custom[i] != null && custom[i].length() > 0)
				tag.setString(Custom.values()[i].key, custom[i]);
		}
		
		if(friends.size() > 0)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			for(int i = 0; i < friends.size(); i++)
				tag1.setBoolean(friends.keys.get(i).toString(), friends.values.get(i).isFriend());
			tag.setTag("Friends", tag1);
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
		else if(o == this) return true;
		else if(o instanceof UUID) return ((UUID)o).equals(uuid);
		else if(o instanceof EntityPlayer) return equals(((EntityPlayer)o).getUniqueID());
		else if(o instanceof LMPlayer) return equals(((LMPlayer)o).uuid);
		else if(o instanceof String) return o.equals(username) || ((String)o).equalsIgnoreCase(LatCoreMC.removeFormatting(getDisplayName()));
		else return false;
	}
	
	public boolean isOP()
	{
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