package latmod.core;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

import latmod.core.mod.LC;
import latmod.core.util.FastList;
import net.minecraft.block.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;

public class LatCoreMC
{
	public static final String MC_VERSION = "1.7.10";
	
	public static final int ANY = OreDictionary.WILDCARD_VALUE;
	public static final int TOP = ForgeDirection.UP.ordinal();
	public static final int BOTTOM = ForgeDirection.DOWN.ordinal();
	
	public static final int NBT_INT = 3;
	public static final int NBT_STRING = 8;
	public static final int NBT_LIST = 9;
	public static final int NBT_MAP = 10;
	public static final int NBT_INT_ARRAY = 11;
	
	public static final boolean isDevEnv = LC.VERSION.equals("@VERSION@");
	
	public static final Pattern textFormattingPattern = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
	
	public static final int getRotYaw(int rot)
	{
		if(rot == 2) return 180;
		else if(rot == 3) return 0;
		else if(rot == 4) return 90;
		else if(rot == 5) return -90;
		return 0;
	}
	
	public static final int getRotPitch(int rot)
	{
		if(rot == 0) return 90;
		else if(rot == 1) return -90;
		return 0;
	}
	
	public static final Configuration loadConfig(FMLPreInitializationEvent e, String s)
	{ return new Configuration(new File(e.getModConfigurationDirectory(), s)); }
	
	/** Prints message to chat (doesn't translate it) */
	public static final void printChat(ICommandSender ep, Object o)
	{
		if(ep == null) System.out.println(o);
		else ep.addChatMessage(new ChatComponentText("" + o));
	}
	
	// Registry methods //
	
	public static final void addItem(Item i, String name)
	{ GameRegistry.registerItem(i, name); }
	
	public static final void addBlock(Block b, Class<? extends ItemBlock> c, String name)
	{ GameRegistry.registerBlock(b, c, name); }
	
	public static final void addBlock(Block b, String name)
	{ addBlock(b, ItemBlock.class, name); }
	
	public static final void addTileEntity(Class<? extends TileEntity> c, String s)
	{ GameRegistry.registerTileEntity(c, s); }
	
	public static final void addEntity(Class<? extends Entity> c, String s, int id, Object mod)
	{ EntityRegistry.registerModEntity(c, s, id, mod, 50, 1, true); }
	
	public static final int getNewEntityID()
	{ return EntityRegistry.findGlobalUniqueEntityId(); }
	
	public static void addOreDictionary(String name, ItemStack is)
	{
		ItemStack is1 = InvUtils.singleCopy(is);
		if(!getOreDictionary(name).contains(is1))
		OreDictionary.registerOre(name, is1);
	}
	
	public static FastList<ItemStack> getOreDictionary(String name)
	{
		FastList<ItemStack> l = new FastList<ItemStack>();
		l.addAll(OreDictionary.getOres(name));
		return l;
	}
	
	public static void addWorldGenerator(IWorldGenerator i, int w)
	{ GameRegistry.registerWorldGenerator(i, w); }
	
	public static void addGuiHandler(Object mod, IGuiHandler i)
	{ NetworkRegistry.INSTANCE.registerGuiHandler(mod, i); }
	
	public static Fluid addFluid(Fluid f)
	{
		Fluid f1 = FluidRegistry.getFluid(f.getName());
		if(f1 != null) return f1;
		FluidRegistry.registerFluid(f);
		return f;
	}
	
	public static boolean canUpdate()
	{ return getEffectiveSide().isServer(); }
	
	public static Side getEffectiveSide()
	{ return FMLCommonHandler.instance().getEffectiveSide(); }
	
	public static String getPath(ResourceLocation res)
	{ return "/assets/" + res.getResourceDomain() + "/" + res.getResourcePath(); }
	
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
	
	public static Item getItemFromRegName(String s)
	{ return (Item)Item.itemRegistry.getObject(s); }
	
	public static ItemStack getStackFromRegName(String s, int dmg)
	{
		Item i = getItemFromRegName(s);
		if(i != null) return new ItemStack(i, dmg);
		return null;
	}

	/*public static String getRegName(Item item, boolean removeMCDomain)
	{
		String s = Item.itemRegistry.getNameForObject(item);
		if(s != null && removeMCDomain && s.startsWith("minecraft:"))
			s = s.substring(10); return s;
	}*/
	
	public static String getRegName(Item item)
	{ return Item.itemRegistry.getNameForObject(item); }
	
	public static String getRegName(ItemStack is)
	{ return (is != null && is.getItem() != null) ? getRegName(is.getItem()) : null; }
	
	public static EntityPlayer getPlayer(World w, UUID id)
	{ return w.func_152378_a(id); }
	
	public static void addGamerule(FMLServerStartingEvent e, String s, String s1)
	{
		if(!e.getServer().worldServers[0].getGameRules().hasRule(s))
			e.getServer().worldServers[0].getGameRules().addGameRule(s, s1);
	}
	
	//TODO: Still need to fix this
	@Deprecated
	public static void teleportEntity(Entity e, int dim)
	{
		if ((e.worldObj.isRemote) || (e.isDead) || e.dimension == dim) return;
		
		LatCoreMC.printChat(null, "Teleporting to dimension " + dim);
		
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
		Vec3 pos = Vec3.createVectorHelper(ep.posX, ep.posY + ep.getDefaultEyeHeight(), ep.posZ);
		Vec3 look = ep.getLookVec();
		Vec3 vec = pos.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
		return ep.worldObj.func_147447_a(pos, vec, false, true, false);
	}
	
	public static MovingObjectPosition rayTrace(EntityPlayer ep)
	{ return rayTrace(ep, LC.proxy.getReachDist(ep)); }
	
	public static <K, V> Type getMapType(Type K, Type V)
	{ return new TypeToken<Map<K, V>>() {}.getType(); }
	
	public static <E> Type getListType(Type E)
	{ return new TypeToken<List<E>>() {}.getType(); }
	
	public static String removeFormatting(String s)
	{ return textFormattingPattern.matcher(s).replaceAll(""); }
	
	public static MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 start, Vec3 end, FastList<AxisAlignedBB> boxes)
	{
		if(boxes == null || boxes.isEmpty()) return null;
		
		MovingObjectPosition current = null;
		double dist = Double.POSITIVE_INFINITY;
		
		for(int i = 0; i < boxes.size(); i++)
		{
			AxisAlignedBB aabb = boxes.get(i);
			
			if(aabb != null)
			{
				MovingObjectPosition mop = collisionRayTrace(w, x, y, z, start, end, aabb);
				
				if(mop != null)
				{
					double d1 = mop.hitVec.squareDistanceTo(start);
					if(current == null || d1 < dist)
					{
						current = mop;
						current.subHit = i;
						dist = d1;
					}
				}
			}
		}
		
		return current;
	}
	
	public static MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 start, Vec3 end, AxisAlignedBB aabb)
	{
		Vec3 pos = start.addVector(-x, -y, -z);
		Vec3 rot = end.addVector(-x, -y, -z);
		
		Vec3 xmin = pos.getIntermediateWithXValue(rot, aabb.minX);
		Vec3 xmax = pos.getIntermediateWithXValue(rot, aabb.maxX);
		Vec3 ymin = pos.getIntermediateWithYValue(rot, aabb.minY);
		Vec3 ymax = pos.getIntermediateWithYValue(rot, aabb.maxY);
		Vec3 zmin = pos.getIntermediateWithZValue(rot, aabb.minZ);
		Vec3 zmax = pos.getIntermediateWithZValue(rot, aabb.maxZ);
		
		if (!isVecInsideYZBounds(xmin, aabb)) xmin = null;
		if (!isVecInsideYZBounds(xmax, aabb)) xmax = null;
		if (!isVecInsideXZBounds(ymin, aabb)) ymin = null;
		if (!isVecInsideXZBounds(ymax, aabb)) ymax = null;
		if (!isVecInsideXYBounds(zmin, aabb)) zmin = null;
		if (!isVecInsideXYBounds(zmax, aabb)) zmax = null;
		Vec3 v = null;
		
		if (xmin != null && (v == null || pos.squareDistanceTo(xmin) < pos.squareDistanceTo(v))) v = xmin;
		if (xmax != null && (v == null || pos.squareDistanceTo(xmax) < pos.squareDistanceTo(v))) v = xmax;
		if (ymin != null && (v == null || pos.squareDistanceTo(ymin) < pos.squareDistanceTo(v))) v = ymin;
		if (ymax != null && (v == null || pos.squareDistanceTo(ymax) < pos.squareDistanceTo(v))) v = ymax;
		if (zmin != null && (v == null || pos.squareDistanceTo(zmin) < pos.squareDistanceTo(v))) v = zmin;
		if (zmax != null && (v == null || pos.squareDistanceTo(zmax) < pos.squareDistanceTo(v))) v = zmax;
		if (v == null) return null; else
		{
			int side = -1;

			if (v == xmin) side = 4;
			if (v == xmax) side = 5;
			if (v == ymin) side = 0;
			if (v == ymax) side = 1;
			if (v == zmin) side = 2;
			if (v == zmax) side = 3;
			
			return new MovingObjectPosition(x, y, z, side, v.addVector(x, y, z));
		}
	}
	
	private static boolean isVecInsideYZBounds(Vec3 v, AxisAlignedBB aabb)
	{ return v == null ? false : v.yCoord >= aabb.minY && v.yCoord <= aabb.maxY && v.zCoord >= aabb.minZ && v.zCoord <= aabb.maxZ; }
	
	private static boolean isVecInsideXZBounds(Vec3 v, AxisAlignedBB aabb)
	{ return v == null ? false : v.xCoord >= aabb.minX && v.xCoord <= aabb.maxX && v.zCoord >= aabb.minZ && v.zCoord <= aabb.maxZ; }
	
	private static boolean isVecInsideXYBounds(Vec3 v, AxisAlignedBB aabb)
	{ return v == null ? false : v.xCoord >= aabb.minX && v.xCoord <= aabb.maxX && v.yCoord >= aabb.minY && v.yCoord <= aabb.maxY; }
	
	private static Field addedTileEntityList = null;
	
	@SuppressWarnings("all")
	public static FastList<TileEntity> getAllTiles(World w, Class<? extends TileEntity> filter)
	{
		if(addedTileEntityList == null)
		{
			try { addedTileEntityList = World.class.getDeclaredField("addedTileEntityList"); }
			catch(Exception e) { e.printStackTrace(); }
		}
		
		FastList<TileEntity> al = new FastList<TileEntity>();

		if(addedTileEntityList != null)
		{
			try
			{
				List<TileEntity> l = (List<TileEntity>)addedTileEntityList.get(w);
				
				if(l.size() > 0) for(TileEntity te : l)
				{
					if(te != null && !te.isInvalid() && (filter == null || filter.isAssignableFrom(te.getClass())))
						al.add(te);
				}
			}
			catch(Exception e)
			{ e.printStackTrace(); }
		}
		
		return al;
	}
	
	public static FastList<TileEntity> getAllTilesInAABB(World w, Class<? extends TileEntity> filter, AxisAlignedBB aabb)
	{
		FastList<TileEntity> al = getAllTiles(w, filter);
		FastList<TileEntity> al1 = new FastList<TileEntity>();
		
		for(int i = 0; i < al.size(); i++)
		{
			TileEntity t = al.get(i);
			if(t.xCoord >= aabb.minX && t.yCoord >= aabb.minY && t.zCoord >= aabb.minZ
			&& t.xCoord <= aabb.maxX && t.yCoord <= aabb.maxY && t.zCoord <= aabb.maxZ)
				al1.add(t);
		}
		
		return al1;
	}

	public static final ForgeDirection getDir(int s)
	{
		if(s >= 0 && s < ForgeDirection.VALID_DIRECTIONS.length)
			return ForgeDirection.VALID_DIRECTIONS[s];
		return ForgeDirection.UNKNOWN;
	}
}