package latmod.core;
import java.io.File;
import java.util.UUID;
import java.util.regex.Pattern;

import latmod.core.mod.LC;
import latmod.core.net.*;
import latmod.core.tile.IGuiTile;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import org.apache.logging.log4j.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;

/** Made by LatvianModder */
public class LatCoreMC
{
	// Something, Something, Eclipse, Something... \Minecraft\eclipse\.metadata\.plugins\org.eclipse.debug.core\.launches
	public static final String MC_VERSION = "1.7.10";
	
	public static final Logger logger = LogManager.getLogger("LatCoreMC");
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static final int TOP = ForgeDirection.UP.ordinal();
	public static final int BOTTOM = ForgeDirection.DOWN.ordinal();
	
	public static final boolean isDevEnv = LC.VERSION.equals("@VERSION@");
	
	public static final String FORMATTING = "\u00a7";
	public static final Pattern textFormattingPattern = Pattern.compile("(?i)" + FORMATTING + "[0-9A-FK-OR]");
	
	public static File latmodFolder = null;
	
	public static final Configuration loadConfig(FMLPreInitializationEvent e, String s)
	{ return new Configuration(new File(e.getModConfigurationDirectory(), s)); }
	
	/** Prints message to chat (doesn't translate it) */
	public static final void printChat(ICommandSender ep, Object o, boolean broadcast)
	{
		if(ep != null)
		{
			IChatComponent msg = (o != null && o instanceof IChatComponent) ? (IChatComponent)o : new ChatComponentText("" + o);
			
			if(broadcast && ep instanceof MinecraftServer)
			{
				//for(EntityPlayerMP ep1 : getAllOnlinePlayers()) ep1.addChatMessage(msg);
				((MinecraftServer)ep).getConfigurationManager().sendChatMsg(msg);
			}
			else ep.addChatMessage(msg);
		}
		else logger.info(o);
	}
	
	public static final void printChat(ICommandSender ep, Object o)
	{ printChat(ep, o, false); }
	
	// Registry methods //
	
	public static final void addItem(Item i, String name)
	{ GameRegistry.registerItem(i, name); }
	
	public static final void addBlock(Block b, Class<? extends ItemBlock> c, String name)
	{ GameRegistry.registerBlock(b, c, name); }
	
	public static final void addBlock(Block b, String name)
	{ addBlock(b, ItemBlock.class, name); }
	
	public static final void addTileEntity(Class<? extends TileEntity> c, String s, String... alt)
	{
		if(alt == null || alt.length == 0) GameRegistry.registerTileEntity(c, s);
		else GameRegistry.registerTileEntityWithAlternatives(c, s, alt);
	}
	
	public static final void addEntity(Class<? extends Entity> c, String s, int id, Object mod)
	{ EntityRegistry.registerModEntity(c, s, id, mod, 50, 1, true); }
	
	public static final int getNewEntityID()
	{ return EntityRegistry.findGlobalUniqueEntityId(); }
	
	public static void addWorldGenerator(IWorldGenerator i, int w)
	{ GameRegistry.registerWorldGenerator(i, w); }
	
	public static void addGuiHandler(Object mod, IGuiHandler i)
	{ NetworkRegistry.INSTANCE.registerGuiHandler(mod, i); }
	
	public static void addEventHandler(Object o, boolean forge, boolean cpw, boolean lm)
	{
		if(forge) MinecraftForge.EVENT_BUS.register(o);
		if(cpw) FMLCommonHandler.instance().bus().register(o);
		if(lm) EVENT_BUS.register(o);
	}
	
	public static Fluid addFluid(Fluid f)
	{
		Fluid f1 = FluidRegistry.getFluid(f.getName());
		if(f1 != null) return f1;
		FluidRegistry.registerFluid(f);
		return f;
	}
	
	public static boolean isServer()
	{ return getEffectiveSide().isServer(); }
	
	public static Side getEffectiveSide()
	{ return FMLCommonHandler.instance().getEffectiveSide(); }
	
	public static String getPath(ResourceLocation res)
	{ return "/assets/" + res.getResourceDomain() + "/" + res.getResourcePath(); }
	
	public static Item getItemFromRegName(String s)
	{ return (Item)Item.itemRegistry.getObject(s); }
	
	public static ItemStack getStackFromRegName(String s, int dmg)
	{
		Item i = getItemFromRegName(s);
		if(i != null) return new ItemStack(i, dmg);
		return null;
	}
	
	public static String getRegName(Item item)
	{ return Item.itemRegistry.getNameForObject(item); }
	
	public static String getRegName(ItemStack is)
	{ return (is != null && is.getItem() != null) ? getRegName(is.getItem()) : null; }
	
	public static void teleportEntity(Entity e, int dim)
	{
		if ((e.worldObj.isRemote) || (e.isDead) || e.dimension == dim) return;
		
		if(e instanceof EntityPlayerMP)
		{
			e.travelToDimension(dim);
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)e, dim);
			return;
		}
		
		e.worldObj.theProfiler.startSection("changeDimension");
		MinecraftServer ms = MinecraftServer.getServer();
		int dim0 = e.dimension;
		WorldServer ws0 = ms.worldServerForDimension(dim0);
		WorldServer ws1 = ms.worldServerForDimension(dim);
		e.dimension = dim;
		
		e.worldObj.removeEntity(e);
		e.isDead = false;
		e.worldObj.theProfiler.startSection("reposition");
		ms.getConfigurationManager().transferEntityToWorld(e, dim0, ws0, ws1);
		e.worldObj.theProfiler.endStartSection("reloading");
		Entity entity = EntityList.createEntityByName(EntityList.getEntityString(e), ws1);
		
		if (entity != null)
		{
			entity.copyDataFrom(e, true);
			
			/*if(dim == 1)
			{
				ChunkCoordinates chunkcoordinates = worldserver1.getSpawnPoint();
				chunkcoordinates.posY = e.worldObj.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
				entity.setLocationAndAngles((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, entity.rotationYaw, entity.rotationPitch);
			}*/

			ws1.spawnEntityInWorld(entity);
		}
		
		e.isDead = true;
		e.worldObj.theProfiler.endSection();
		ws0.resetUpdateEntityTick();
		ws1.resetUpdateEntityTick();
		e.worldObj.theProfiler.endSection();
	}
	
	public static void openGui(EntityPlayer ep, IGuiTile i, int ID)
	{
		TileEntity te = i.getTile();
		ep.openGui(LC.inst, ID, te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
	}
	
	public static void openClientGui(EntityPlayer ep, IGuiTile i, int ID)
	{ LC.proxy.openClientGui(ep, i, ID); }
	
	public static boolean isWrench(ItemStack is)
	{ return is != null && is.getItem() != null && is.getItem().getHarvestLevel(is, "wrench") != -1; }
	
	public static boolean hasOnlinePlayers()
	{ return !MinecraftServer.getServer().getConfigurationManager().playerEntityList.isEmpty(); }
	
	public static FastMap<UUID, EntityPlayerMP> getAllOnlinePlayers()
	{
		FastMap<UUID, EntityPlayerMP> m = new FastMap<UUID, EntityPlayerMP>();
		
		for(int i = 0; i < MinecraftServer.getServer().getConfigurationManager().playerEntityList.size(); i++)
		{
			EntityPlayerMP ep = (EntityPlayerMP)MinecraftServer.getServer().getConfigurationManager().playerEntityList.get(i);
			m.put(ep.getUniqueID(), ep);
		}
		
		return m;
	}
	
	public static Vertex getSpawnPoint(World w)
	{ ChunkCoordinates c = w.getSpawnPoint(); return new Vertex(c.posX + 0.5D, c.posY + 0.5D, c.posZ + 0.5D); }
	
	public static void remap(MissingMapping m, String id, Item i)
	{ if(m.type == GameRegistry.Type.ITEM && id.equals(m.name)) m.remap(i); }
	
	public static void remap(MissingMapping m, String id, Block b)
	{ if(id.equals(m.name)) { if(m.type == GameRegistry.Type.BLOCK) m.remap(b);
	else if(m.type == GameRegistry.Type.ITEM) m.remap(Item.getItemFromBlock(b)); } }
	
	public static boolean isModInstalled(String s)
	{ return Loader.isModLoaded(s); }
	
	public static FluidStack getFluid(ItemStack is)
	{
		if(is == null || is.getItem() == null) return null;
		
		if(is.getItem() instanceof IFluidContainerItem)
		{
			FluidStack fs = ((IFluidContainerItem)is.getItem()).getFluid(is);
			if(fs != null) return fs;
		}
		
		return FluidContainerRegistry.getFluidForFilledItem(is);
	}
	
	public static boolean isBucket(ItemStack is)
	{ return FluidContainerRegistry.isBucket(is); }
	
	public static String removeFormatting(String s)
	{ return textFormattingPattern.matcher(s).replaceAll(""); }
	
	public static MinecraftServer getServer()
	{ return FMLCommonHandler.instance().getMinecraftServerInstance(); }
	
	public static Exception executeCommand(ICommandSender ics, String cmd, String... args)
	{
		try { getServer().getCommandManager().executeCommand(ics, (cmd + " " + LatCore.unsplit(args, " ")).trim()); }
		catch(Exception e) { return e; } return null;
	}
	
	public static void notifyPlayer(EntityPlayerMP ep, Notification n)
	{
		if(ep != null) MessageLM.NET.sendTo(new MessageNotifyPlayer(n), ep);
		else MessageLM.NET.sendToAll(new MessageNotifyPlayer(n));
	}
}