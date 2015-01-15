package latmod.core.client.playerdeco;

import latmod.core.*;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class PlayerDecorator
{
	public static final FastMap<String, Class<? extends PlayerDecorator>> map = new FastMap<String, Class<? extends PlayerDecorator>>();
	
	static
	{
		map.put("latmod", PDLatMod.class);
		map.put("ftb", PDFTB.class);
		map.put("part", PDParticles.class);
		map.put("dust", PDDustCol.class);
	}
	
	private String name = null; public String toString()
	{ if(name == null) name = map.getKey(getClass()); return name; }
	
	public static PlayerDecorator getFromLine(String line) // ThreadCheckPlayerDecorators
	{
		String[] s = LatCore.split(line, ":");
		
		Class<? extends PlayerDecorator> c = map.get(s[0]);
		if(c == null) return null;
		
		try
		{
			PlayerDecorator pd = c.newInstance();
			
			if(s.length == 2)
			{
				FastMap<String, String> data = new FastMap<String, String>();
				
				String[] s1 = LatCore.split(s[1], ";");
				
				for(int i = 0; i < s1.length; i++)
				{
					String[] s2 = LatCore.split(s1[i], "=");
					if(s2.length == 2) data.put(s2[0], s2[1]);
				}
				
				pd.onDataLoaded(data);
			}
			
			return pd;
		}
		catch(Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public boolean hasMoved(Entity e)
	{
		if(e.prevPosX != e.posX) return true;
		if(e.prevPosY != e.posY) return true;
		if(e.prevPosZ != e.posZ) return true;
		return false;
	}
	
	public abstract void onDataLoaded(FastMap<String, String> data);
	public abstract void onPlayerRender(RenderPlayerEvent.Specials.Post e);
	
	public String getS(FastMap<String, String> map, String key, String def)
	{ String s = map.get(key); return (s == null) ? def : s; }
	
	public Number getN(FastMap<String, String> map, String key, Number def)
	{ return new Double(Double.parseDouble(getS(map, key, def + ""))); }
}