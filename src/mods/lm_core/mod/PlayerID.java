package mods.lm_core.mod;
import java.io.*;
import java.util.*;
import mods.lm_core.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.world.*;

public class PlayerID
{
	public static PlayerID inst = null;
	
	public HashMap<String, Integer> nameToIDMap = null;
	public HashMap<Integer, String> IDToNameMap = null;
	private int nextPlayerID = 0;
	
	public PlayerID()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load e)
	{
		if(LatCore.canUpdate() && e.world.provider.dimensionId == 0)
		{
			WorldServer world = (WorldServer)e.world;
			
			try
	    	{
	    		File file = new File(world.getChunkSaveLocation(), "LatCore.dat");
	    		
	    		if(!file.exists()) System.err.println("LatCore.dat doesn't exist!");
	    		else
	    		{
	    			NBTTagCompound tag = CompressedStreamTools.readCompressed(new FileInputStream(file));
	    			readFromNBT(tag);
	    		}
	    	}
	    	catch(Exception ex)
	    	{ ex.printStackTrace(); }
		}
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound playerIDs = (NBTTagCompound)tag.getTag("PlayerIDs");
		
		nextPlayerID = 0;
		IDToNameMap.clear();
		nameToIDMap.clear();
		
		if(playerIDs != null)
		{
			nextPlayerID = playerIDs.getShort("NextID");
			IDToNameMap.clear();
			nameToIDMap.clear();
			
			NBTTagList playerIDList = playerIDs.getTagList("IDs");
			
			int siz = playerIDList.tagCount();
			for(int i = 0; i < siz; i++)
			{
				NBTTagCompound tag1 = (NBTTagCompound)playerIDList.tagAt(i);
				int id = tag1.getShort("ID");
				String name = tag1.getString("Name");
				addID(id, name);
			}
		}
	}
	
	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save e)
	{
		if(LatCore.canUpdate() && e.world.provider.dimensionId == 0)
		{
			WorldServer world = (WorldServer)e.world;
			
			try
	    	{
	    		File file = new File(world.getChunkSaveLocation(), "LatCore.dat");
	    		
	    		if(!file.exists())
	    		{
	    			if(file.createNewFile()) System.out.println("LatCore.dat created!");
	    			else System.err.println("Failed to create LatCore.dat!");
	    		}
	    		else
	    		{
	    			NBTTagCompound tag = new NBTTagCompound();
	    			writeToNBT(tag);
	    			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(file));
	    		}
	    	}
	    	catch(Exception ex)
	    	{ ex.printStackTrace(); }
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound playerIDs = new NBTTagCompound();
		
		playerIDs.setShort("NextID", (short)nextPlayerID);
		
		NBTTagList playerIDList = new NBTTagList();
		Iterator<Integer> ids = IDToNameMap.keySet().iterator();
		Iterator<String> names = nameToIDMap.keySet().iterator();
		
		while(ids.hasNext() && names.hasNext())
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setShort("ID", ids.next().shortValue());
			tag1.setString("Name", names.next());
			playerIDList.appendTag(tag1);
		}
		
		playerIDs.setTag("IDs", playerIDList);
		
		tag.setTag("PlayerIDs", playerIDs);
	}
	
	public void onStarted()
	{
		nameToIDMap = new HashMap<String, Integer>();
		IDToNameMap = new HashMap<Integer, String>();
	}
	
	public void onStopped()
	{
		nameToIDMap = null;
		IDToNameMap = null;
	}
	
	public String getName(int id)
	{ return IDToNameMap.get(id); }
	
	private void addID(int id, String name)
	{
		nameToIDMap.put(name, id);
		IDToNameMap.put(id, name);
	}
	
	public int get(String name, boolean create)
	{
		Integer i = nameToIDMap.get(name);
		if(i == null)
		{
			if(!create) return 0;
			
			int j = ++nextPlayerID;
			i = new Integer(j);
			addID(i.intValue(), name);
		}
		
		return i.intValue();
	}
	
	public int get(EntityPlayer ep)
	{ return get(ep.username, true); }

	public boolean hasID(String name)
	{ return nameToIDMap.containsKey(name); }
}