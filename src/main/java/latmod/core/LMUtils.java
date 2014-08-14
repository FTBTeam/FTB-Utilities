package latmod.core;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

public class LMUtils
{
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
	
	public static final double[] getMidPoint(double[] pos1, double[] pos2, float p)
	{
		double x = pos2[0] - pos1[0];
		double y = pos2[1] - pos1[1];
		double z = pos2[2] - pos1[2];
		double d = Math.sqrt(x * x + y * y + z * z);
		return new double[] { pos1[0] + (x / d) * (d * p), pos1[1] + (y / d) * (d * p), pos1[2] + (z / d) * (d * p) };
	}
	
	//TODO: Still need to fix this
	@Deprecated
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
	
	public static void dropItem(World w, double x, double y, double z, ItemStack is, int delay)
	{
		if(w == null || is == null || is.stackSize == 0) return;
		
		EntityItem ei = new EntityItem(w, x, y, z, is.copy());
		ei.motionX = w.rand.nextGaussian() * 0.07F;
		ei.motionY = w.rand.nextFloat() * 0.05F;
		ei.motionZ = w.rand.nextGaussian() * 0.07F;
		ei.delayBeforeCanPickup = delay;
		w.spawnEntityInWorld(ei);
	}
	
	public static void dropItem(Entity e, ItemStack is)
	{ dropItem(e.worldObj, e.posX, e.posY, e.posZ, is, 0); }
	
	public static <T> T fromJson(String s, Type t)
	{
		if(s == null || s.length() < 2) s = "{}";
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.fromJson(s, t);
	}
	
	public static <T> T fromJsonFromFile(File f, Type t)
	{
		try
		{
			FileInputStream fis = new FileInputStream(f);
			byte[] b = new byte[fis.available()];
			fis.read(b); fis.close();
			return fromJson(new String(b), t);
		}
		catch(Exception e)
		{ e.printStackTrace(); return null; }
	}
	
	public static String toJson(Object o, boolean asTree)
	{
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		if(asTree)
		{
			StringWriter sw = new StringWriter();
			JsonWriter jw = new JsonWriter(sw);
			jw.setIndent("\t");
			gson.toJson(o, o.getClass(), jw);
			return sw.toString();
		}
		
		return gson.toJson(o);
	}
	
	public static void toJsonFile(File f, Object o)
	{
		String s = toJson(o, true);
		
		try
		{
			if(!f.exists())
			{
				File f0 = f.getParentFile();
				if(!f0.exists()) f0.mkdirs();
				f.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(s.getBytes()); fos.close();
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}

	public static MovingObjectPosition rayTrace(EntityPlayer ep, double d)
	{
		//ep.yOffset
		Vec3 pos = Vec3.createVectorHelper(ep.posX, ep.posY + 1.62D, ep.posZ);
		Vec3 look = ep.getLook(1F);
		Vec3 vec = pos.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
		//return ep.worldObj.rayTraceBlocks_do_do(pos, vec, false, true);
		return ep.worldObj.func_147447_a(pos, vec, false, true, false);
	}
	
	public static Item getItemFromRegName(String s)
	{ return (Item)Item.itemRegistry.getObject(s); }
	
	public static ItemStack getStackFromRegName(String s, int dmg)
	{
		Item i = getItemFromRegName(s);
		if(i != null) return new ItemStack(i, dmg);
		return null;
	}

	public static String getRegName(Item item, boolean removeMCDomain)
	{
		String s = Item.itemRegistry.getNameForObject(item);
		if(s != null && removeMCDomain && s.startsWith("minecraft:"))
			s = s.substring(10); return s;
	}
	
	public static EntityPlayer getPlayer(World w, UUID id)
	{ return w.func_152378_a(id); }
	
	public static <K, V> Type getMapType(Type K, Type V)
	{ return new TypeToken<Map<K, V>>() {}.getType(); }
	
	public static <E> Type getListType(Type E)
	{ return new TypeToken<List<E>>() {}.getType(); }
}