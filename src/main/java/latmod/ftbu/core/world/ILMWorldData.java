package latmod.ftbu.core.world;

import latmod.ftbu.core.Phase;
import latmod.ftbu.core.util.FastList;

public interface ILMWorldData
{
	public void onLMWorldLoaded(Phase p);
	public void onLMWorldSaved();
	
	public static class Registry
	{
		private static final FastList<ILMWorldData> list = new FastList<ILMWorldData>();
		
		public static void add(ILMWorldData i)
		{ if(!list.contains(i)) list.add(i); }
		
		public static void load(Phase p)
		{ for(ILMWorldData i : list) i.onLMWorldLoaded(p); }
		
		public static void save()
		{ for(ILMWorldData i : list) i.onLMWorldSaved(); }
	}
}