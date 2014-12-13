package latmod.latcore.client;

import latmod.core.FastMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class PlayerDecorator
{
	public static final FastMap<String, PlayerDecorator> map = new FastMap<String, PlayerDecorator>();
	
	static
	{
		map.put("latmod", new PDLatMod());
		map.put("reddust", new PDParticles("reddust"));
		map.put("townaura", new PDParticles("townaura"));
		map.put("smoke", new PDParticles("smoke"));
		map.put("flames", new PDParticles("flame"));
	}
	
	private String name = null; public String toString()
	{ if(name == null) name = map.getKey(this); return name; }
	
	public boolean hasMoved(Entity e)
	{
		if(e.prevPosX != e.posX) return true;
		if(e.prevPosY != e.posY) return true;
		if(e.prevPosZ != e.posZ) return true;
		return false;
	}
	
	public void spawnPart(World w, String s, double x, double y, double z, double a, double b, double c)
	{ if(Minecraft.getMinecraft().inGameHasFocus) w.spawnParticle(s, x, y, z, a, b, c); }
	
	public void spawnPart(World w, String s, double x, double y, double z)
	{ spawnPart(w, s, x, y, z, 0D, 0D, 0D); }
	
	public abstract void onPlayerRender(RenderPlayerEvent.Specials.Post e);
}