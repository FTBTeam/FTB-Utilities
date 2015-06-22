package latmod.ftbu.core;

import java.util.UUID;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.cmd.NameType;
import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.util.Vertex.DimPos;
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
	
	public static class Last
	{
		public double x, y, z;
		public int dim;
		public long seen;
		
		public boolean equalsDimPos(Vertex.DimPos pos)
		{ return x == pos.pos.x && y == pos.pos.y && z == pos.pos.z && dim == pos.dim; }

		public void set(DimPos pos)
		{
			x = pos.pos.x;
			y = pos.pos.y;
			z = pos.pos.z;
			dim = pos.dim;
		}
	}
	
	public final int playerID;
	public final GameProfile gameProfile;
	
	public final String uuidString;
	public final FastList<LMPlayer> friends;
	public final ItemStack[] lastArmor;
	private boolean isOnline;
	public int notify;
	public int deaths;
	public Last last;
	
	public final NBTTagCompound tempData;
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
		tempData = new NBTTagCompound();
	}
	
	public String getName()
	{ return gameProfile.getName(); }
	
	public UUID getUUID()
	{ return gameProfile.getId(); }
	
	public EntityPlayerMP getPlayerMP()
	{ return LatCoreMC.getAllOnlinePlayers().get(getUUID()); }
	
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
		if(!LatCoreMC.isServer()) return;
		
		NBTTagCompound tag = new NBTTagCompound();
		
		FastList<String> info = new FastList<String>();
		if(!isOnline() && last != null) info.add("Last seen: " + LatCore.getTimeAgo(LatCore.millis() - last.seen) + " ago");
		if(deaths > 0) info.add("Deaths: " + deaths);
		new LMPlayerEvent.CustomInfo(this, Side.SERVER, info).post();
		tag.setTag("I", NBTHelper.fromStringList(info));
		
		MessageLM.sendTo(ep, new MessageLMPlayerInfo(playerID, tag));
	}
	
	@SideOnly(Side.CLIENT)
	public void receiveInfo(NBTTagCompound tag)
	{
		if(clientInfo == null) clientInfo = new FastList<String>();
		NBTHelper.toStringList(clientInfo, tag.getTagList("I", NBTHelper.STRING));
		new LMPlayerEvent.CustomInfo(this, Side.CLIENT, clientInfo).post();
	}
	
	public boolean isOnline()
	{ return isOnline; }
	
	public void setOnline(boolean b)
	{ isOnline = b; }
	
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
			
			if(tag.hasKey("Last"))
			{
				if(last == null) last = new Last();
				last.x = tag.getDouble("X");
				last.y = tag.getDouble("Y");
				last.z = tag.getDouble("Z");
				last.dim = tag.getInteger("Dim");
				last.seen = tag.getLong("Seen");
			}
			else last = null;
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
		
		tag.setTag("CustomData", commonData);
		
		InvUtils.writeItemsToNBT(lastArmor, tag, "LastItems");
		tag.setByte("Notify", (byte)notify);
		
		if(deaths > 0)
			tag.setShort("Deaths", (short)deaths);
		
		if(server)
		{
			tag.setTag("ServerData", serverData);
			
			if(last != null)
			{
				tag.setDouble("X", last.x);
				tag.setDouble("Y", last.y);
				tag.setDouble("Z", last.z);
				tag.setInteger("Dim", last.dim);
				tag.setLong("Seen", last.seen);
			}
		}
	}
	
	public int compareTo(LMPlayer o)
	{ return Integer.compare(playerID, o.playerID); }
	
	public String toString()
	{ return getName(); }
	
	public int hashCode()
	{ return playerID; }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof Integer) return o.hashCode() == playerID;
		else if(o instanceof UUID) return getUUID().equals(o);
		else if(o instanceof EntityPlayer) return ((EntityPlayer)o).getUniqueID().equals(getUUID());
		else if(o instanceof LMPlayer) return playerID == o.hashCode();
		else if(o instanceof String) return getName().equalsIgnoreCase(o.toString()) || uuidString.equalsIgnoreCase(o.toString());
		else return false;
	}
	
	public boolean isOP()
	{ return LatCoreMC.getServer().getConfigurationManager().func_152596_g(gameProfile); }
	
	public NameType getNameType()
	{ return isOnline() ? NameType.ON : NameType.OFF; }
	
	// Static //
	
	public static final FastMap<Integer, LMPlayer> map = new FastMap<Integer, LMPlayer>();
	
	public static LMPlayer getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		if(o instanceof LMPlayer) return map.get(o.hashCode());
		if(o instanceof Integer) return (o.hashCode() > 0) ? map.get(o) : null;
		return map.values.getObj(o);
	}
	
	public static int getPlayerID(Object o)
	{
		if(o == null) return 0;
		if(o instanceof Integer) return Math.max(0, o.hashCode());
		LMPlayer p = getPlayer(o);
		return (p == null) ? 0 : p.playerID;
	}
	
	public static String[] getAllNames(NameType type)
	{
		if(type == NameType.NONE) return new String[0];
		
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