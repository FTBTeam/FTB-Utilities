package latmod.ftbu.util;
import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.*;
import ftb.lib.FTBLib;
import latmod.ftbu.api.guide.GuideFile;
import latmod.ftbu.api.item.IItemLM;
import latmod.ftbu.api.tile.IGuiTile;
import latmod.ftbu.mod.*;
import latmod.ftbu.net.*;
import latmod.ftbu.notification.Notification;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.*;

/** Made by LatvianModder */
public final class LatCoreMC // LatCoreMCClient
{
	public static final Random rand = new Random();
	
	// Registry methods //
	
	public static void addItem(IItemLM i)
	{ addItem((Item)i, i.getItemID()); }
	
	public static void addItem(Item i, String name)
	{ GameRegistry.registerItem(i, name); }
	
	public static void addBlock(Block b, Class<? extends ItemBlock> c, String name)
	{ GameRegistry.registerBlock(b, c, name); }
	
	public static void addBlock(Block b, String name)
	{ addBlock(b, ItemBlock.class, name); }
	
	public static void addTileEntity(Class<? extends TileEntity> c, String s, String... alt)
	{
		if(alt == null || alt.length == 0) GameRegistry.registerTileEntity(c, s);
		else GameRegistry.registerTileEntityWithAlternatives(c, s, alt);
	}
	
	public static void addEntity(Class<? extends Entity> c, String s, int id, Object mod)
	{ EntityRegistry.registerModEntity(c, s, id, mod, 50, 1, true); }
	
	public static int getNewEntityID()
	{ return EntityRegistry.findGlobalUniqueEntityId(); }
	
	public static void addWorldGenerator(IWorldGenerator i, int w)
	{ GameRegistry.registerWorldGenerator(i, w); }
	
	public static Fluid addFluid(Fluid f)
	{
		Fluid f1 = FluidRegistry.getFluid(f.getName());
		if(f1 != null) return f1;
		FluidRegistry.registerFluid(f);
		return f;
	}
	
	public static void notifyPlayer(EntityPlayerMP ep, Notification n)
	{ new MessageNotifyPlayer(n).sendTo(ep); }
	
	public static void openGui(EntityPlayer ep, IGuiTile i, NBTTagCompound data)
	{
		TileEntity te = (TileEntity)i;
		if(data == null) data = new NBTTagCompound();
		data.setIntArray("XYZ", new int[] { te.xCoord, te.yCoord, te.zCoord });
		FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.TILE, data);
	}
	
	public static boolean isDedicatedServer()
	{ return FTBUTicks.isDediServer; }

	public static void displayGuide(ICommandSender ics, GuideFile file) 
	{
		if(!FTBLib.isServer()) return;
		
		if(ics instanceof EntityPlayerMP)
		{
			new MessageDisplayGuide(file).sendTo((EntityPlayerMP)ics);
		}
		else
		{
			//TODO: Print guide file to server console
		}
	}
}