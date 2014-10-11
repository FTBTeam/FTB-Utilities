package latmod.core.mod;

import java.util.UUID;

import latmod.core.LatCoreMC;
import latmod.core.mod.net.*;
import latmod.core.util.FastList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
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
	}
	
	public String getDisplayName()
	{
		if(customName != null && customName.length() > 0)
			return customName + EnumChatFormatting.RESET;
		return username;
	}
	
	public boolean hasCustomName()
	{ return customName != null; }
	
	public EntityPlayer getPlayer(World w)
	{ return LatCoreMC.getPlayer(w, uuid); }
	
	public void sendUpdate(World w, String channel)
	{
		if(LatCoreMC.canUpdate())
		{
			new DataChangedEvent(this, Side.SERVER, channel, w).post();
			LMNetHandler.INSTANCE.sendToAll(new MessageUpdatePlayerData(this, channel));
		}
	}
	
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
			NBTTagList l = tag.getTagList("Whitelist", LatCoreMC.NBT_STRING);
			for(int i = 0; i < l.tagCount(); i++)
				whitelist.add(UUID.fromString(l.getStringTagAt(i)));
		}
		
		{
			blacklist.clear();
			NBTTagList l = tag.getTagList("Blacklist", LatCoreMC.NBT_STRING);
			for(int i = 0; i < l.tagCount(); i++)
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
		if(o instanceof String) return ((String)o).equalsIgnoreCase(LatCoreMC.removeFormatting(getDisplayName())) || o.equals(username);
		if(o instanceof UUID) return ((UUID)o).equals(uuid);
		if(o instanceof EntityPlayer) return ((EntityPlayer)o).getUniqueID().equals(uuid);
		return false;
	}
	
	// Static //
	
	public static final FastList<LMPlayer> list = new FastList<LMPlayer>();
	
	public static LMPlayer getPlayer(Object o)
	{ return list.getObj(o); }
	
	public static class DataChangedEvent extends Event
	{
		public final LMPlayer player;
		public final Side side;
		public final String channel;
		public final World world;
		
		public DataChangedEvent(LMPlayer p, Side s, String c, World w)
		{ player = p; side = s; channel = c; world = w; }
		
		public boolean isChannel(String s)
		{ return channel != null && channel.equals(s); }
		
		public void post()
		{ MinecraftForge.EVENT_BUS.post(this); }
		
		public EntityPlayer getPlayer()
		{ return player.getPlayer(world); }
	}
}