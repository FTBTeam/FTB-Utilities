package latmod.core.waila;

import java.util.List;

import latmod.core.event.RegisterWailaEvent;
import latmod.core.tile.IWailaTile;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class WailaLMTile extends BasicWailaHandler
{
	public static final String CONFIG_OWNER = "latcoremc.owner";
	
	public WailaLMTile(RegisterWailaEvent e, WailaType t)
	{
		super(e, t);
	}

	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Stack)
			return ((IWailaTile.Stack)te).getWailaStack(data, config);
		
		return null;
	}
	
	public List<String> getWailaHead(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Head)
			((IWailaTile.Head)te).addWailaHead(data, config, l);
		
		return l;
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Body)
			((IWailaTile.Body)te).addWailaBody(data, config, l);
		
		return l;
	}
	
	public List<String> getWailaTail(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Tail)
			((IWailaTile.Tail)te).addWailaTail(data, config, l);
		
		return l;
	}
	
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
	{
		return tag;
	}
}