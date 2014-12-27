package latmod.core.net;
import latmod.core.tile.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClientTileAction extends MessageLM implements IMessageHandler<MessageClientTileAction, IMessage>
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
	
	public IMessage onMessage(MessageClientTileAction m, MessageContext ctx)
	{
		int x = m.data.getInteger("X");
		int y = m.data.getInteger("Y");
		int z = m.data.getInteger("Z");
		
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		TileEntity te = ep.worldObj.getTileEntity(x, y, z);
		
		if(te instanceof IClientActionTile)
			((IClientActionTile)te).onClientAction(ep, m.data.getString("A"), (NBTTagCompound)m.data.getTag("T"));
		
		return null;
	}
}