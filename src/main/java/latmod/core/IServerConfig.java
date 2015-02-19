package latmod.core;

import latmod.core.util.FastMap;
import net.minecraft.nbt.NBTTagCompound;

public interface IServerConfig
{
	public String getConfigName();
	public void readConfig(NBTTagCompound tag);
	public void writeConfig(NBTTagCompound tag);
	
	public static class Registry
	{
		private static final FastMap<String, IServerConfig> map = new FastMap<String, IServerConfig>();
		
		public static void add(IServerConfig i)
		{
			if(i != null && i.getConfigName() != null)
			{
				map.put(i.getConfigName(), i);
				LatCoreMC.logger.info("Added IServerConfig '" + i.getConfigName() + "'");
			}
		}
		
		public static void remove(IServerConfig i)
		{
			if(i != null && i.getConfigName() != null)
				map.remove(i.getConfigName());
		}
		
		public static void readFromNBT(NBTTagCompound tag)
		{
			for(int i = 0; i < map.size(); i++)
			{
				NBTTagCompound tag1 = (NBTTagCompound)tag.getTag(map.keys.get(i));
				if(tag1 != null) map.values.get(i).readConfig(tag1);
			}
		}
		
		public static void writeToNBT(NBTTagCompound tag)
		{
			for(int i = 0; i < map.size(); i++)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				map.values.get(i).writeConfig(tag1);
				if(!tag1.hasNoTags()) tag.setTag(map.keys.get(i), tag1);
			}
		}
	}
}