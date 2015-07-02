package latmod.ftbu.core;

import java.util.UUID;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.cmd.NameType;
import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.*;

public final class LMPlayer implements Comparable<LMPlayer>
{
	public static final String ACTION_GENERAL = "-";
	public static final String ACTION_GROUPS_CHANGED = "ftbu.groups";
	public static int currentClientPlayerID = 0;
	
	public final int playerID;
	public final GameProfile gameProfile;
	
	public final String uuidString;
	public final FastList<LMPlayer> friends;
	public final ItemStack[] lastArmor;
	private boolean isOnline;
	public int notify;
	public int deaths;
	public EntityPos lastPos;
	private long lastSeen;
	private long firstJoined;
	
	public NBTTagCompound commonData;
	public NBTTagCompound serverData;
	
	@SideOnly(Side.CLIENT)
	public FastList<String> clientInfo;
	
	public LMPlayer(int i, GameProfile gp)
	{
		playerID = i;
		gameProfile = gp;
		
		uuidString = LatCoreMC.toShortUUID(getUUID());
		friends = new FastList<LMPlayer>();
		lastArmor = new ItemStack[5];
		isOnline = false;
		notify = 2;
		
		commonData = new NBTTagCompound();
		serverData = new NBTTagCompound();
	}
	
	public String getName()
	{ return gameProfile.getName(); }
	
	public UUID getUUID()
	{ return gameProfile.getId(); }
	
	public EntityPlayerMP getPlayerMP()
	{ return LatCoreMC.getPlayerMP(getUUID()); }
	
	@SideOnly(Side.CLIENT)
	public EntityPlayerSP getPlayerSP()
	{ return LatCoreMCClient.getPlayerSP(getUUID()); }
	
	public void sendUpdate(String action, boolean updateClient)
	{
		if(!LatCoreMC.isServer()) return;
		
		if(action == null) action = ACTION_GENERAL;
		new LMPlayerEvent.DataChanged(this, Side.SERVER, action).post();
		if(updateClient) MessageLM.NET.sendToAll(new MessageLMPlayerUpdate(this, action));
	}
	
	public void sendInfo(EntityPlayerMP ep)
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		long ms = LatCore.millis();
		
		FastList<String> info = new FastList<String>();
		new LMPlayerEvent.CustomInfo(this, Side.SERVER, info).post();
		tag.setTag("I", NBTHelper.fromStringList(info));
		
		if(lastSeen > 0L) tag.setLong("L", ms - lastSeen);
		if(firstJoined > 0L) tag.setLong("J", ms - firstJoined);
		if(deaths > 0) tag.setShort("D", (short)deaths);
		
		LatCoreMC.printChat(ep, "Sending info: " + tag + " ; " + lastSeen);
		MessageLM.sendTo(ep, new MessageLMPlayerInfo(playerID, tag));
	}
	
	@SideOnly(Side.CLIENT)
	public void receiveInfo(NBTTagCompound tag)
	{
		if(clientInfo == null) clientInfo = new FastList<String>();
		NBTHelper.toStringList(clientInfo, tag.getTagList("I", NBTHelper.STRING));
		
		if(!isOnline() && tag.hasKey("L")) clientInfo.add("Last seen: " + LatCore.getTimeAgo(tag.getLong("L")) + " ago");
		if(tag.hasKey("J")) clientInfo.add("Joined: " + LatCore.getTimeAgo(tag.getLong("J")) + " ago");
		if(deaths > 0) clientInfo.add("Deaths: " + deaths);
		
		new LMPlayerEvent.CustomInfo(this, Side.CLIENT, clientInfo).post();
	}
	
	public boolean isOnline()
	{ return isOnline; }
	
	public void setOnline(boolean b)
	{ isOnline = b; }
	
	public void setLastSeen(long l)
	{
		lastSeen = l;
		if(firstJoined <= 0L)
			firstJoined = l;
	}
	
	public boolean isFriendRaw(LMPlayer p)
	{ return p != null && (playerID == p.playerID || friends.contains(p.playerID)); }
	
	public boolean isFriend(LMPlayer p)
	{ return isFriendRaw(p) && p.isFriendRaw(this); }
	
	// NBT reading / writing
	
	public void readFromNBT(NBTTagCompound tag, boolean server)
	{
		isOnline = tag.getBoolean("On");
		
		friends.clear();
		
		int[] fl = tag.getIntArray("Friends");
		
		if(fl != null && fl.length > 0)
		for(int j = 0; j < fl.length; j++)
		{
			LMPlayer p = getPlayer(fl[j]);
			if(p != null) friends.add(p);
		}
		
		commonData = tag.getCompoundTag("CustomData");
		
		InvUtils.readItemsFromNBT(lastArmor, tag, "LastItems");
		
		if(!tag.hasKey("Notify")) notify = 1;
		else notify = tag.getByte("Notify");
		
		deaths = tag.getShort("Deaths");
		
		if(server)
		{
			serverData = tag.getCompoundTag("ServerData");
			
			if(tag.hasKey("LastPos"))
			{
				if(lastPos == null) lastPos = new EntityPos();
				lastPos.readFromNBT(tag.getCompoundTag("LastPos"));
			}
			else lastPos = null;
			
			lastSeen = tag.getLong("LastSeen");
			firstJoined = tag.getLong("Joined");
		}
	}
	
	public void writeToNBT(NBTTagCompound tag, boolean server)
	{
		if(isOnline) tag.setBoolean("On", isOnline);
		
		if(!friends.isEmpty())
		{
			int[] m = new int[friends.size()];
			
			if(m.length > 0)
			{
				for(int j = 0; j < m.length; j++)
					m[j] = friends.get(j).playerID;
				
				tag.setIntArray("Friends", m);
			}
		}
		
		if(!commonData.hasNoTags()) tag.setTag("CustomData", commonData);
		
		InvUtils.writeItemsToNBT(lastArmor, tag, "LastItems");
		tag.setByte("Notify", (byte)notify);
		
		if(deaths > 0)
			tag.setShort("Deaths", (short)deaths);
		
		if(server)
		{
			if(!serverData.hasNoTags()) tag.setTag("ServerData", serverData);
			
			if(lastPos != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				lastPos.writeToNBT(tag1);
				tag.setTag("LastPos", tag1);
			}
			
			if(lastSeen > 0L) tag.setLong("LastSeen", lastSeen);
			if(firstJoined > 0L) tag.setLong("Joined", firstJoined);
		}
	}
	
	public int compareTo(LMPlayer o)
	{ return Integer.compare(playerID, o.playerID); }
	
	public String toString()
	{ return getName(); }
	
	public int hashCode()
	{ return playerID; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || equalsPlayer(getPlayer(o))); }
	
	public boolean equalsPlayer(LMPlayer p)
	{ return p != null && (p == this || p.playerID == playerID); }
	
	public boolean isOP()
	{ return LatCoreMC.getServer().getConfigurationManager().func_152596_g(gameProfile); }
	
	public NameType getNameType()
	{ return isOnline() ? NameType.ON : NameType.OFF; }
	
	public EntityPos getLastPos()
	{
		if(isOnline())
		{
			EntityPlayerMP ep = getPlayerMP();
			if(ep != null) return new EntityPos(ep);
		}
		
		return lastPos;
	}
	
	// Static //
	
	public static final FastMap<Integer, LMPlayer> map = new FastMap<Integer, LMPlayer>();
	
	public static LMPlayer getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		else if(o instanceof Integer || o instanceof LMPlayer)
		{
			int h = o.hashCode();
			return (h <= 0) ? null : map.get(h);
		}
		else if(o.getClass() == UUID.class)
		{
			UUID id = (UUID)o;
			
			for(int i = 0; i < map.size(); i++)
			{
				LMPlayer p = map.values.get(i);
				if(p.getUUID().equals(id)) return p;
			}
		}
		else if(o instanceof EntityPlayer)
			return getPlayer(((EntityPlayer)o).getUniqueID());
		else if(o instanceof String)
		{
			String s = o.toString();
			
			if(s == null || s.isEmpty()) return null;
			
			for(int i = 0; i < map.size(); i++)
			{
				LMPlayer p = map.values.get(i);
				if(p.getName().equalsIgnoreCase(s)) return p;
			}
			
			return getPlayer(LatCoreMC.getUUIDFromString(s));
		}
		
		return null;
	}
	
	public static int getPlayerID(Object o)
	{
		if(o == null) return 0;
		LMPlayer p = getPlayer(o);
		return (p == null) ? 0 : p.playerID;
	}
	
	public static String[] getAllNames(NameType type)
	{
		if(type == null || type == NameType.NONE) return new String[0];
		
		FastList<String> allOn = new FastList<String>();
		FastList<String> allOff = new FastList<String>();
		
		for(int i = 0; i < map.values.size(); i++)
		{
			LMPlayer p = map.values.get(i);
			
			String s = LatCoreMC.removeFormatting(p.getName());
			
			if(p.isOnline()) allOn.add(s);
			else if(!type.isOnline()) allOff.add(s);
		}
		
		allOn.sort(null);
		
		if(!type.isOnline())
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