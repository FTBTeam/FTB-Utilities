package latmod.core.mod;

import java.util.UUID;

import latmod.core.LatCoreMC;
import latmod.core.mod.net.*;
import latmod.core.util.FastList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;

public class LMPlayer implements Comparable<LMPlayer>
{
	public final UUID uuid;
	public String displayName;
	public String customName;
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
	
	public void readFromNBT(NBTTagCompound tag)
	{
		displayName = tag.getString("Name");
		
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
		tag.setString("Name", displayName);
		
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
	
	public EntityPlayer getPlayer(World w)
	{ return LatCoreMC.getPlayer(w, uuid); }
	
	public int compareTo(LMPlayer o)
	{
		if(displayName == null || o.displayName == null)
			return 0;
		return displayName.compareTo(o.displayName);
	}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(o instanceof String)
		{
			if(customName != null)
				return ((String)o).equalsIgnoreCase(LatCoreMC.removeFormatting(customName));
			return ((String)o).equalsIgnoreCase(displayName);
		}
		if(o instanceof UUID) return ((UUID)o).equals(uuid);
		if(o instanceof EntityPlayer) return ((EntityPlayer)o).getUniqueID().equals(uuid);
		return false;
	}
	
	public void sendUpdate(String channel)
	{
		if(LatCoreMC.canUpdate())
		{
			MinecraftForge.EVENT_BUS.post(new DataChangedEvent(this, Side.SERVER, channel));
			LMNetHandler.INSTANCE.sendToAll(new MessageUpdatePlayerData(this, channel));
		}
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
		
		public DataChangedEvent(LMPlayer p, Side s, String c)
		{ player = p; side = s; channel = c; }
		
		public boolean isChannel(String s)
		{ return channel != null && channel.equals(s); }
	}
}