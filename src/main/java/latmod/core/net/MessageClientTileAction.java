package latmod.core.net;
import latmod.core.tile.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientTileAction extends MessageLM<MessageClientTileAction>
{
	public MessageClientTileAction() { }
	
	public MessageClientTileAction(TileLM t, String s, NBTTagCompound tag)
	{
		data = new NBTTagCompound();
		data.setInteger("X", t.xCoord);
		data.setInteger("Y", t.yCoord);
		data.setInteger("Z", t.zCoord);
		data.setString("A", s);
		if(tag != null) data.setTag("T", tag);
	}
	
	public void onMessage(MessageContext ctx)
	{
		int x = data.getInteger("X");
		int y = data.getInteger("Y");
		int z = data.getInteger("Z");
		
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		TileEntity te = ep.worldObj.getTileEntity(x, y, z);
		
		if(te instanceof IClientActionTile)
			((IClientActionTile)te).onClientAction(ep, data.getString("A"), (NBTTagCompound)data.getTag("T"));
	}
}