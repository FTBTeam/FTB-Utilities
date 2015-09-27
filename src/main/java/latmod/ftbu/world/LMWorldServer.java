package latmod.ftbu.world;

import java.io.File;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import latmod.core.util.*;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.util.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class LMWorldServer extends LMWorld<LMPlayerServer>
{
	public static LMWorldServer inst = null;
	
	public final WorldServer worldObj;
	public final File latmodFolder;
	public final FastMap<String, EntityPos> warps;
	public NBTTagCompound customData;
	
	public LMWorldServer(UUID id, WorldServer w, File f)
	{
		super(Side.SERVER, id, LMStringUtils.fromUUID(id));
		worldObj = w;
		latmodFolder = f;
		warps = new FastMap<String, EntityPos>();
		customData = new NBTTagCompound();
	}
	
	public World getMCWorld()
	{ return LatCoreMC.getServerWorld(); }
	
	public void load(NBTTagCompound tag)
	{
		warps.clear();
		
		NBTTagCompound tagWarps = (NBTTagCompound)tag.getTag("Warps");
		
		if(tagWarps != null && !tagWarps.hasNoTags())
		{
			FastList<String> l = LMNBTUtils.getMapKeys(tagWarps);
			
			for(int i = 0; i < l.size(); i++)
			{
				int[] a = tagWarps.getIntArray(l.get(i));
				setWarp(l.get(i), a[0], a[1], a[2], a[3]);
			}
		}
		
		customData = tag.getCompoundTag("Custom");
	}
	
	public void save(NBTTagCompound tag)
	{
		NBTTagCompound tagWarps = new NBTTagCompound();
		for(int i = 0; i < warps.size(); i++)
			tagWarps.setIntArray(warps.keys.get(i), warps.values.get(i).toIntArray());
		tag.setTag("Warps", tagWarps);
		tag.setTag("Custom", customData);
	}
	
	public void writePlayersToNet(NBTTagCompound tag, int selfID)
	{
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < players.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayerServer p = players.get(i);
			p.writeToNet(tag1, p.playerID == selfID);
			new EventLMPlayerServer.DataSaved(p).post();
			tag1.setLong("MID", p.getUUID().getMostSignificantBits());
			tag1.setLong("LID", p.getUUID().getLeastSignificantBits());
			tag1.setString("N", p.getName());
			tag1.setInteger("PID", p.playerID);
			
			list.appendTag(tag1);
		}
		
		tag.setTag("Players", list);
	}
	
	public void writePlayersToServer(NBTTagCompound tag)
	{
		for(int i = 0; i < players.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayerServer p = players.get(i);
			p.writeToServer(tag1);
			new EventLMPlayerServer.DataSaved(p).post();
			tag1.setString("UUID", p.uuidString);
			tag1.setString("Name", p.getName());
			
			tag.setTag(p.playerID + "", tag1);
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
			players.get(i).onPostLoaded();
	}
	
	// Warps //
	
	public String[] listWarps()
	{ return warps.keys.toArray(new String[0]); }
	
	public EntityPos getWarp(String s)
	{ return warps.get(s); }
	
	public boolean setWarp(String s, int x, int y, int z, int dim)
	{ return warps.put(s, new EntityPos(x + 0.5D, y + 0.5D, z + 0.5D, dim)); }
	
	public boolean remWarp(String s)
	{ return warps.remove(s); }
}