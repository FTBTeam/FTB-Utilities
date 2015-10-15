package latmod.ftbu.world;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.net.MessageLMWorldUpdate;
import latmod.ftbu.util.*;
import latmod.lib.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class LMWorldServer extends LMWorld // LMWorldClient
{
	public static LMWorldServer inst = null;
	
	public final WorldServer worldObj;
	public final File latmodFolder;
	public final Warps warps;
	public NBTTagCompound customData;
	
	public LMWorldServer(UUID id, WorldServer w, File f)
	{
		super(Side.SERVER, id, LMStringUtils.fromUUID(id));
		worldObj = w;
		latmodFolder = f;
		warps = new Warps();
		customData = new NBTTagCompound();
	}
	
	public World getMCWorld()
	{ return LatCoreMC.getServerWorld(); }
	
	public LMWorldServer getServerWorld()
	{ return this; }
	
	public LMPlayerServer getPlayer(Object o)
	{
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerMP();
	}
	
	public void load(NBTTagCompound tag)
	{
		warps.readFromNBT(tag, "Warps");
		customData = tag.getCompoundTag("Custom");
		settings.readFromNBT(tag.getCompoundTag("Settings"), true);
	}
	
	public void save(NBTTagCompound tag)
	{
		warps.writeToNBT(tag, "Warps");
		tag.setTag("Custom", customData);
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToNBT(settingsTag, true);
		tag.setTag("Settings", settingsTag);
	}
	
	public void writeDataToNet(NBTTagCompound tag, int selfID)
	{
		if(selfID > 0)
		{
			NBTTagList list = new NBTTagList();
			
			for(int i = 0; i < players.size(); i++)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				
				LMPlayerServer p = players.get(i).toPlayerMP();
				p.writeToNet(tag1, p.playerID == selfID);
				new EventLMPlayerServer.DataSaved(p).post();
				tag1.setLong("MID", p.getUUID().getMostSignificantBits());
				tag1.setLong("LID", p.getUUID().getLeastSignificantBits());
				tag1.setString("N", p.getName());
				tag1.setInteger("PID", p.playerID);
				
				list.appendTag(tag1);
			}
			
			tag.setTag("PLIST", list);
		}
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToNBT(settingsTag, false);
		tag.setTag("CFG", settingsTag);
	}
	
	public void writePlayersToServer(NBTTagCompound tag)
	{
		for(int i = 0; i < players.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayerServer p = players.get(i).toPlayerMP();
			p.writeToServer(tag1);
			new EventLMPlayerServer.DataSaved(p).post();
			tag1.setString("UUID", p.uuidString);
			tag1.setString("Name", p.getName());
			tag.setTag(Integer.toString(p.playerID), tag1);
		}
	}
	
	public void readPlayersFromServer(NBTTagCompound tag)
	{
		players.clear();
		
		FastMap<String, NBTTagCompound> map = LMNBTUtils.toFastMapWithType(tag);
		
		for(int i = 0; i < map.size(); i++)
		{
			int id = Integer.parseInt(map.keys.get(i));
			NBTTagCompound tag1 = map.values.get(i);
			LMPlayerServer p = new LMPlayerServer(this, id, new GameProfile(LMStringUtils.fromString(tag1.getString("UUID")), tag1.getString("Name")));
			p.readFromServer(tag1);
			players.add(p);
		}
		
		for(int i = 0; i < players.size(); i++)
			players.get(i).toPlayerMP().onPostLoaded();
	}
	
	public void update()
	{ new MessageLMWorldUpdate(this).sendTo(null); }
}