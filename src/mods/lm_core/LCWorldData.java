package mods.lm_core;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class LCWorldData extends WorldSavedData
{
	private HashMap<Integer, String> IDToNameMap = new HashMap<Integer, String>();
	private HashMap<String, Integer> nameToIDMap = new HashMap<String, Integer>();
	private int nextPlayerID = 0;
	
	public LCWorldData(String s)
	{ super(s); }
	
	public void readFromNBT(NBTTagCompound tag)
	{
		nextPlayerID = tag.getShort("NextID");
		
		IDToNameMap.clear();
		nameToIDMap.clear();
		
		NBTTagList playerIDList = tag.getTagList("PlayerIDs");
		
		int siz = playerIDList.tagCount();
		for(int i = 0; i < siz; i++)
		{
			NBTTagCompound tag1 = (NBTTagCompound)playerIDList.tagAt(i);
			int id = tag1.getShort("ID");
			String name = tag1.getString("Name");
			addID(id, name);
		}
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setShort("NextID", (short)nextPlayerID);
		
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
		
		tag.setTag("PlayerIDs", playerIDList);
	}
	
	public void save(World w)
	{
		markDirty();
		w.setItemData(DATA_NAME, this);
	}
	
	public int getPlayerID(EntityPlayer ep)
	{
		Integer i = nameToIDMap.get(ep.username);
		if(i == null)
		{
			int j = ++nextPlayerID;
			i = new Integer(j);
			addID(i.intValue(), ep.username);
			save(ep.worldObj);
		}
		
		return i.intValue();
	}
	
	public int getPlayerID(String name)
	{ Integer i = nameToIDMap.get(name);
	return (i == null) ? 0 : i.intValue(); }
	
	public String getPlayerName(int id)
	{ return IDToNameMap.get(id); }
	
	private void addID(int id, String name)
	{
		IDToNameMap.put(id, name);
		nameToIDMap.put(name, id);
	}
	
	private static LCWorldData dataInstance = null;
	public static final String DATA_NAME = "latmod_core";
	
	public static final LCWorldData getData(World w)
	{
		if(dataInstance == null)
		{
			try { dataInstance = (LCWorldData)w.loadItemData(LCWorldData.class, DATA_NAME); }
			catch(Exception e) { e.printStackTrace(); }
			
			if(dataInstance == null)
			{
				dataInstance = new LCWorldData(DATA_NAME);
				dataInstance.save(w);
			}
		}
		
		return dataInstance;
	}

	public boolean hasPlayerID(EntityPlayer ep)
	{ return nameToIDMap.containsKey(ep.username); }
}