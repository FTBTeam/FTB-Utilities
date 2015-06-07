package latmod.ftbu.core;

import java.util.UUID;

import latmod.ftbu.FTBU;
import latmod.ftbu.core.cmd.NameType;
import latmod.ftbu.core.event.LMPlayerEvent;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.relauncher.*;

public class LMPlayer implements Comparable<LMPlayer>
{
	public static final String ACTION_GROUPS_CHANGED = "latcore.groups";
	
	public final int playerID;
	public final UUID uuid;
	public final String username;
	
	public final String uuidString;
	public final FastList<LMPlayer> friends;
	public final ItemStack[] lastArmor;
	
	public final NBTTagCompound tempData;
	public NBTTagCompound commonData;
	public NBTTagCompound serverData;
	private boolean isOnline;
	
	@SideOnly(Side.CLIENT)
	public FastList<String> clientInfo;
	
	public LMPlayer(int i, UUID id, String s)
	{
		playerID = i;
		uuid = id;
		username = s;
		
		uuidString = uuid.toString();
		friends = new FastList<LMPlayer>();
		lastArmor = new ItemStack[5];
		
		commonData = new NBTTagCompound();
		serverData = new NBTTagCompound();
		tempData = new NBTTagCompound();
	}
	
	public EntityPlayerMP getPlayerMP()
	{ return LatCoreMC.getAllOnlinePlayers().get(uuid); }
	
	@SideOnly(Side.CLIENT)
	public EntityPlayerSP getPlayerSP()
	{
		World w = FTBU.proxy.getClientWorld();
		
		if(w != null)
		{
			EntityPlayer ep = w.func_152378_a(uuid);
			if(ep != null && ep instanceof EntityPlayerSP)
				return (EntityPlayerSP)ep;
		}
		
		return null;
	}
	
	public void sendUpdate(String action, boolean updateClient)
	{
		if(LatCoreMC.isServer())
		{
			new LMPlayerEvent.DataChanged(this, action).post();
			if(updateClient) MessageLM.NET.sendToAll(new MessageLMPlayerUpdate(this, action));
		}
	}
	
	public void updateInfo(EntityPlayerMP ep)
	{
		FastList<String> info = new FastList<String>();
		new LMPlayerEvent.CustomInfo(this, info).post();
		if(ep == null) MessageLM.NET.sendToAll(new MessageLMPlayerInfo(playerID, info));
		else MessageLM.NET.sendTo(new MessageLMPlayerInfo(playerID, info), ep);
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
		if(server) serverData = tag.getCompoundTag("ServerData");
		
		InvUtils.readItemsFromNBT(lastArmor, tag, "LastItems");
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
		if(server) tag.setTag("ServerData", serverData);
		
		InvUtils.writeItemsToNBT(lastArmor, tag, "LastItems");
	}
	
	public int compareTo(LMPlayer o)
	{ return Integer.compare(playerID, o.playerID); }
	
	public String toString()
	{ return username; }
	
	public int hashCode()
	{ return playerID; }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof Integer) return ((Integer)o).intValue() == playerID;
		else if(o instanceof UUID) return ((UUID)o).equals(uuid);
		else if(o instanceof EntityPlayer) return ((EntityPlayer)o).getUniqueID().equals(uuid);
		else if(o instanceof LMPlayer) return playerID == o.hashCode();
		else if(o instanceof String) return username.equalsIgnoreCase(o.toString()) || uuidString.equalsIgnoreCase(o.toString());
		else return false;
	}
	
	public boolean isOP()
	{ return LatCoreMC.getServer().func_152358_ax().func_152652_a(uuid) != null; }
	
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
	
	public static String[] getAllNames(NameType type)
	{
		if(type == NameType.NONE) return new String[0];
		
		FastList<String> allOn = new FastList<String>();
		FastList<String> allOff = new FastList<String>();
		
		for(int i = 0; i < map.values.size(); i++)
		{
			LMPlayer p = map.values.get(i);
			
			String s = LatCoreMC.removeFormatting(p.username);
			
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