package latmod.core.util;

import java.util.UUID;

import latmod.core.LatCore;
import latmod.core.event.LMPlayerEvent;
import latmod.core.net.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.relauncher.Side;

public class LMPlayer implements Comparable<LMPlayer>
{
	public static final String ACTION_LOGGED_IN = "LoggedIn";
	public static final String ACTION_LOGGED_OUT = "LoggedOut";
	public static final String ACTION_GROUPS_CHANGED = "GroupsChanged";
	
	public static class Group
	{
		public final LMPlayer owner;
		public final String name;
		public final FastList<LMPlayer> members;
		
		public Group(LMPlayer p, String s)
		{
			owner = p;
			name = s;
			members = new FastList<LMPlayer>();
		}
	}
	
	public final int playerID;
	public final UUID uuid;
	public final String username;
	
	public final FastList<LMPlayer> friends = new FastList<LMPlayer>();
	public final FastMap<String, Group> groups = new FastMap<String, Group>();
	public NBTTagCompound customData = new NBTTagCompound();
	private boolean isOnline;
	public boolean isOld;
	
	public LMPlayer(int i, UUID id, String s)
	{ playerID = i; uuid = id; username = s; }
	
	public String getDisplayName()
	{ return username + ""; }
	
	public EntityPlayerMP getPlayerMP()
	{ return LatCoreMC.getAllOnlinePlayers().get(uuid); }
	
	public EntityPlayer getPlayerSP()
	{
		World w = LatCore.proxy.getClientWorld();
		if(w != null) return w.func_152378_a(uuid);
		return null;
	}
	
	public boolean isOnline()
	{ return isOnline; }
	
	public void setOnline(boolean b)
	{ isOnline = b; }
	
	public void sendUpdate(String channel, boolean clientUpdate)
	{
		if(LatCoreMC.isServer())
		{
			new LMPlayerEvent.DataChanged(this, Side.SERVER, channel).post();
			if(clientUpdate) MessageLM.NET.sendToAll(new MessageUpdateLMPlayer(this, channel));
		}
	}
	
	public boolean isFriendRaw(LMPlayer p)
	{ return p != null && (playerID == p.playerID || friends.contains(p.playerID)); }
	
	public boolean isFriend(LMPlayer p)
	{ return isFriendRaw(p) && p.isFriendRaw(this); }
	
	public void sendUpdate(String channel)
	{ sendUpdate(channel, true); }
	
	// NBT reading / writing
	
	public void readFromNBT(NBTTagCompound tag)
	{
		isOnline = tag.getBoolean("On");
		
		friends.clear();
		groups.clear();
		
		NBTTagCompound tag1 = tag.getCompoundTag("Groups");
		
		FastMap<String, NBTTagIntArray> lists = NBTHelper.toFastMapWithType(tag1);
		
		NBTTagIntArray fl = lists.get("Friends");
		
		if(fl != null)
		{
			int[] fla = fl.func_150302_c();
			
			for(int j = 0; j < fla.length; j++)
			{
				LMPlayer p = getPlayer(fla[j]);
				if(p != null) friends.add(p);
			}
		}
		
		lists.remove("Friends");
		
		for(int i = 0; i < lists.size(); i++)
		{
			Group g = new Group(this, lists.keys.get(i));
			int[] l = lists.get(i).func_150302_c();
			
			for(int j = 0; j < l.length; j++)
			{
				LMPlayer p = getPlayer(l[j]);
				if(p != null) g.members.add(p);
			}
			
			groups.put(g.name, g);
		}
		
		customData = tag.getCompoundTag("CustomData");
		
		if(customData.hasKey("IsOld"))
		{
			tag.setBoolean("Old", customData.getBoolean("IsOld"));
			customData.removeTag("IsOld");
		}
		
		isOld = tag.getBoolean("Old");
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("Old", isOld);
		tag.setBoolean("On", isOnline);
		
		if(friends.size() > 0 || groups.size() > 0)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			for(int i = 0; i < groups.size(); i++)
			{
				Group g = groups.get(i);
				
				int[] m = new int[g.members.size()];
				
				if(m.length > 0)
				{
					for(int j = 0; j < m.length; j++)
						m[j] = g.members.get(j).playerID;
					
					tag1.setIntArray(g.name, m);
				}
			}
			
			int[] m = new int[friends.size()];
			
			if(m.length > 0)
			{
				for(int j = 0; j < m.length; j++)
					m[j] = friends.get(j).playerID;
				
				tag1.setIntArray("Friends", m);
			}
			
			tag.setTag("Groups", tag1);
		}
		
		tag.setTag("CustomData", customData);
	}
	
	public int compareTo(LMPlayer o)
	{ return username.compareTo(o.username); }
	
	public String toString()
	{ return username; }
	
	public int hashCode()
	{ return uuid.hashCode(); }
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		else if(o == this) return true;
		else if(o instanceof Integer) return ((Integer)o).intValue() == playerID;
		else if(o instanceof UUID) return ((UUID)o).equals(uuid);
		else if(o instanceof EntityPlayer) return ((EntityPlayer)o).getUniqueID().equals(uuid);
		else if(o instanceof LMPlayer) return playerID == ((LMPlayer)o).playerID;
		else if(o instanceof String) return o.equals(username) || o.equals(uuid.toString());
		else return false;
	}
	
	public boolean isOP()
	{ return LatCoreMC.getServer().func_152358_ax().func_152652_a(uuid) != null; }
	
	// Static //
	
	public static final FastMap<Integer, LMPlayer> map = new FastMap<Integer, LMPlayer>();
	
	public static LMPlayer getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		if(o instanceof LMPlayer) return (LMPlayer)o;
		if(o instanceof Integer) return map.get(o);
		return map.values.getObj(o);
	}
	
	public static String[] getAllNames(boolean online)
	{
		FastList<String> allOn = new FastList<String>();
		FastList<String> allOff = new FastList<String>();
		
		for(int i = 0; i < map.values.size(); i++)
		{
			LMPlayer p = map.values.get(i);
			
			String s = LatCoreMC.removeFormatting(p.username);
			
			if(p.isOnline()) allOn.add(s);
			else if(!online) allOff.add(s);
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

	public FastList<Group> getGroupsFor(Object o)
	{
		FastList<Group> l = new FastList<Group>();
		
		if(o == null || o instanceof FakePlayer) return l;
		
		for(int i = 0; i < groups.values.size(); i++)
		{
			Group g = groups.values.get(i);
			if(g.members.contains(o)) l.add(g);
		}
		
		return l;
	}
}