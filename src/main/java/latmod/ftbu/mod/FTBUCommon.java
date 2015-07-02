package latmod.ftbu.mod;

import java.util.UUID;

import latmod.ftbu.core.LMProxy;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.tile.TileLM;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class FTBUCommon extends LMProxy // FTBUClient
{
	public boolean isShiftDown() { return false; }
	public boolean isCtrlDown() { return false; }
	public boolean isTabDown() { return false; }
	public boolean inGameHasFocus() { return true; }
	
	public EntityPlayer getClientPlayer()
	{ return null; }
	
	public EntityPlayer getClientPlayer(UUID id)
	{ return null; }
	
	public World getClientWorld()
	{ return null; }
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP)
			return ((EntityPlayerMP)ep).theItemInWorldManager.getBlockReachDistance();
		return 0F;
	}
	
	public void spawnDust(World w, double x, double y, double z, int col) { }
	public void openClientGui(EntityPlayer ep, String id, NBTTagCompound data) { }
	public <M extends MessageLM<?>> void handleClientMessage(IClientMessageLM<M> m, MessageContext ctx) { }
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p) { }
}