package latmod.ftbu.core.world;

import latmod.ftbu.core.util.FastList;

public interface ILMPlayerData
{
	public void onLMPlayerLoaded(LMPlayer p);
	public void onLMPlayerSaved(LMPlayer p);
	public void onLMPlayerChanged(LMPlayer p);
	
	public static class Registry
	{
		private static final FastList<ILMPlayerData> list = new FastList<ILMPlayerData>();
		
		public static void add(ILMPlayerData i)
		{ if(!list.contains(i)) list.add(i); }
		
		public static void remove(ILMPlayerData i)
		{ list.remove(i); }
		
		public static void load(LMPlayer p)
		{ for(ILMPlayerData i : list) i.onLMPlayerLoaded(p); }
		
		public static void save(LMPlayer p)
		{ for(ILMPlayerData i : list) i.onLMPlayerSaved(p); }
		
		public static void changed(LMPlayer p)
		{ for(ILMPlayerData i : list) i.onLMPlayerChanged(p); }
	}
}