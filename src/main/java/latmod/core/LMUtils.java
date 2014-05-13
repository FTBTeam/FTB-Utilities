package latmod.core;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.*;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.common.util.ForgeDirection;

public class LMUtils
{
	public static boolean anyEquals(Object o, Object... o1)
	{
		for(int i = 0; i < o1.length; i++)
		{
			if(o == null && o1[i] == null) return true;
			if(o != null && o1[i] != null)
			{ if(o == o1[i] || o.equals(o1[i])) return true; }
		}
		
		return false;
	}
	
	public static boolean allEquals(Object o, Object... o1)
	{
		for(int i = 0; i < o1.length; i++)
		{
			if((o == null && o1[i] != null) || (o != null && o1[i] == null)) return false;
			if(o != o1[i]) { if(!o.equals(o1[i])) return false; }
		}
		
		return true;
	}
	
	public static ForgeDirection get2DRotation(EntityLivingBase el)
	{
		int i = MathHelper.floor_float(el.rotationYaw * 4F / 360F + 0.5F) & 3;
		if(i == 0) return ForgeDirection.NORTH;
		else if(i == 1) return ForgeDirection.EAST;
		else if(i == 2) return ForgeDirection.SOUTH;
		else if(i == 3) return ForgeDirection.WEST;
		return ForgeDirection.UNKNOWN;
	}
	
	public static ForgeDirection get3DRotation(World w, int x, int y, int z, EntityLivingBase el)
	{ return ForgeDirection.values()[BlockPistonBase.determineOrientation(w, x, y, z, el)]; }
	
	public static String getPath(ResourceLocation res)
	{ return "/assets/" + res.getResourceDomain() + "/" + res.getResourcePath(); }
	
	public static String stripe(Object... i)
	{
		StringBuilder s = new StringBuilder();
		for(int j = 0; j < i.length; j++)
		{ s.append(i[j]);
		if(j != i.length - 1) s.append(", "); }
		return s.toString();
	}
	
	public static String stripeD(double... i)
	{
		StringBuilder s = new StringBuilder();
		for(int j = 0; j < i.length; j++)
		{ s.append(((long)(i[j] * 100D)) / 100D);
		if(j != i.length - 1) s.append(", "); }
		return s.toString();
	}
	
	public static String stripeF(float... i)
	{
		StringBuilder s = new StringBuilder();
		for(int j = 0; j < i.length; j++)
		{ s.append(((int)(i[j] * 100F)) / 100F);
		if(j != i.length - 1) s.append(", "); }
		return s.toString();
	}
	
	public static void setPropertyComment(Configuration config, String category, String property, String... comment)
	{
		ConfigCategory cat = config.getCategory(category);
		Property prop = cat.get(property);
		
		if(prop != null)
		{
			String s = "";
			for(int i = 0; i < comment.length; i++)
			{ s += comment[i]; if(i < comment.length - 1) s += '\n'; }
			prop.comment = s;
		}
	}
	
	public static final double[] getMidPoint(double[] pos1, double[] pos2, float p)
	{
		double x = pos2[0] - pos1[0];
		double y = pos2[1] - pos1[1];
		double z = pos2[2] - pos1[2];
		double d = Math.sqrt(x * x + y * y + z * z);
		return new double[] { pos1[0] + (x / d) * (d * p), pos1[1] + (y / d) * (d * p), pos1[2] + (z / d) * (d * p) };
	}
	
	public static int percent(int i, int max)
	{
		if(i == 0) return 0;
		if(i == max) return 100;
		return (int)((float)i * 100F / (float)max);
	}
	
	//TODO: Still need to fix this
	public static void teleportEntity(Entity e, int dim)
	{
		if ((e.worldObj.isRemote) || (e.isDead) || e.dimension == dim) return;
		
		LatCore.printChat(null, "Teleporting to dimension " + dim);
		
		e.worldObj.theProfiler.startSection("changeDimension");
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		int j = e.dimension;
		WorldServer worldserver = minecraftserver.worldServerForDimension(j);
		WorldServer worldserver1 = minecraftserver.worldServerForDimension(dim);
		e.dimension = dim;
		e.worldObj.removeEntity(e);
		e.isDead = false;
		e.worldObj.theProfiler.startSection("reposition");
		minecraftserver.getConfigurationManager().transferEntityToWorld(e, j, worldserver, worldserver1);
		e.worldObj.theProfiler.endStartSection("reloading");
		Entity entity = EntityList.createEntityByName(EntityList.getEntityString(e), worldserver1);
		if (entity != null)
		{
			entity.copyDataFrom(e, true);
			worldserver1.spawnEntityInWorld(entity);
		}
		e.isDead = true;
		e.worldObj.theProfiler.endSection();
		worldserver.resetUpdateEntityTick();
		worldserver1.resetUpdateEntityTick();
		e.worldObj.theProfiler.endSection();
	}
}