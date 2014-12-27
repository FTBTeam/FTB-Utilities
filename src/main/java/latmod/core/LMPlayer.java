package latmod.core;

import java.util.UUID;

import latmod.core.event.LMPlayerEvent;
import latmod.core.net.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.relauncher.Side;

public class LMPlayer implements Comparable<LMPlayer>
{
	public static final String TAG_CUSTOM_NAME = "CustomName";
	
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
	
	public final UUID uuid;
	public final String username;
	private String customName;
	public final FastList<LMPlayer> friends = new FastList<LMPlayer>();
	public final FastMap<String, Group> groups = new FastMap<String, Group>();
	public NBTTagCompound customData = new NBTTagCompound();
	private boolean isOnline;
	public boolean isOld;
	
	public LMPlayer(UUID id, String s)
	{
		uuid = id;
		username = s;
	}
	
	public void setCustomName(String s)
	{
		String s0 = customName + "";
		
		if(s != null && s.length() > 0)
		{
			customName = s.trim().replace("&k", "").replace("&", LatCoreMC.FORMATTING);
			if(customName.length() == 0 || customName.equals("null")) customName = null;
		}
		else customName = null;
		
		if(LatCore.isDifferent(s0, customName))
		{
			sendUpdate(TAG_CUSTOM_NAME);
			
			EntityPlayer ep = getPlayer();
			
			if(ep != null)
			{
				ep.refreshDisplayName();
				
				NBTTagCompound data = new NBTTagCompound();
				data.setString("UUID", uuid.toString());
				LMNetHandler.INSTANCE.sendToAll(new MessageCustomServerAction(TAG_CUSTOM_NAME, data));
			}
		}
	}
	
	public String getDisplayName()
	{ if(hasCustomName()) return customName + EnumChatFormatting.RESET; return username + ""; }
	
	public boolean hasCustomName()
	{ return customName != null && !customName.isEmpty(); }
	
	public EntityPlayerMP getPlayer()
	{ return LatCoreMC.getAllOnlinePlayers().get(uuid); }
	
	public boolean isOnline()
	{ return isOnline; }
	
	public void setOnline(boolean b)
	{ isOnline = b; }
	
	public void sendUpdate(String channel, boolean clientUpdate)
	{
		if(LatCoreMC.isServer())
		{
			new LMPlayerEvent.DataChanged(this, Side.SERVER, channel).post();
			if(clientUpdate) LMNetHandler.INSTANCE.sendToAll(new MessageUpdateLMPlayer(this, channel));
		}
	}
	
	public boolean isFriendRaw(LMPlayer p)
	{ return p != null && (uuid.equals(p.uuid) || friends.contains(p.uuid)); }
	
	public boolean isFriend(LMPlayer p)
	{ return isFriendRaw(p) && p.isFriendRaw(this); }
	
	public void sendUpdate(String channel)
	{ sendUpdate(channel, true); }
	
	public FastList<Group> getGroupsFor(UUID id)
	{
		FastList<Group> al = new FastList<Group>();
		return al;
	}
	
	// NBT reading / writing
	
	public void readFromNBT(NBTTagCompound tag)
	{
		if(tag.hasKey("On")); isOnline = tag.getBoolean("On");
		customName = tag.getString(TAG_CUSTOM_NAME).trim();
		if(customName.isEmpty()) customName = null;
		
		friends.clear();
		groups.clear();
		
		if(tag.hasKey("Friends"))
		{
			NBTTagCompound map = tag.getCompoundTag("Friends");
			FastList<String> al = NBTHelper.getMapKeys(map);
			
			for(int i = 0; i < al.size(); i++)
			{
				String s = al.get(i);
				boolean b = map.getBoolean(s);
				if(b)
				{
					LMPlayer p = LMPlayer.getPlayer(s);
					if(p != null) friends.add(p);
				}
			}
			
			tag.removeTag("Friends");
		}
		else
		{
			NBTTagCompound tag1 = tag.getCompoundTag("Groups");
			
			FastMap<String, NBTTagList> lists = NBTHelper.toFastMapWithType(tag1);
			
			NBTTagList fl = lists.get("Friends");
			
			if(fl != null) for(int j = 0; j < fl.tagCount(); j++)
			{
				LMPlayer p = LMPlayer.getPlayer(fl.getStringTagAt(j));
				if(p != null) friends.add(p);
			}
			
			lists.remove("Friends");
			
			for(int i = 0; i < lists.size(); i++)
			{
				Group g = new Group(this, lists.keys.get(i));
				NBTTagList l = lists.get(i);
				
				for(int j = 0; j < l.tagCount(); j++)
				{
					LMPlayer p = LMPlayer.getPlayer(l.getStringTagAt(j));
					if(p != null) g.members.add(p);
				}
				
				groups.put(g.name, g);
			}
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
		
		if(customName != null)
			tag.setString(TAG_CUSTOM_NAME, customName);
		
		if(friends.size() > 0 || groups.size() > 0)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			for(int i = 0; i < groups.size(); i++)
			{
				Group g = groups.get(i);
				NBTTagList list = new NBTTagList();
				for(int j = 0; j < g.members.size(); j++)
					list.appendTag(new NBTTagString(g.members.get(j).uuid.toString()));
				tag1.setTag(g.name, list);
			}
			
			NBTTagList friendsList = new NBTTagList();
			for(int i = 0; i < friends.size(); i++)
				friendsList.appendTag(new NBTTagString(friends.get(i).toString()));
			tag1.setTag("Friends", friendsList);
			
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
		else if(o instanceof UUID) return ((UUID)o).equals(uuid);
		else if(o instanceof EntityPlayer) return equals(((EntityPlayer)o).getUniqueID());
		else if(o instanceof LMPlayer) return equals(((LMPlayer)o).uuid);
		else if(o instanceof String) return o.equals(username) || uuid.toString().equals(o) || ((String)o).equalsIgnoreCase(LatCoreMC.removeFormatting(getDisplayName()));
		else return false;
	}
	
	public boolean isOP()
	{ return LatCoreMC.getServer().func_152358_ax().func_152652_a(uuid) != null; }
	
	// Static //
	
	public static final FastList<LMPlayer> list = new FastList<LMPlayer>();
	
	public static LMPlayer getPlayer(Object o)
	{
		if(o == null) return null;
		if(o instanceof FakePlayer) return null;
		if(o instanceof LMPlayer) return (LMPlayer)o;
		return list.getObj(o);
	}
	
	public static String[] getAllNames(boolean online, boolean display)
	{
		FastList<String> allOn = new FastList<String>();
		FastList<String> allOff = new FastList<String>();
		
		for(int i = 0; i < list.size(); i++)
		{
			LMPlayer p = list.get(i);
			
			String s = LatCoreMC.removeFormatting(display ? p.getDisplayName() : p.username);
			
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
}