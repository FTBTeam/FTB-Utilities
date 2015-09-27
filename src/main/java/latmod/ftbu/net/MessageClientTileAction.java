package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.tile.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class MessageClientTileAction extends MessageLM<MessageClientTileAction>
{
	public int x, y, z;
	public String action;
	public NBTTagCompound data;
	
	public MessageClientTileAction() { }
	
	public MessageClientTileAction(TileLM t, String s, NBTTagCompound tag)
	{
		x = t.xCoord;
		y = t.yCoord;
		z = t.zCoord;
		action = s;
		data = tag;
	}
	
	public void readData(ByteIOStream io) throws Exception
	{
		x = io.readInt();
		y = io.readInt();
		z = io.readInt();
		action = io.readString();
		data = LMNetHelper.readTagCompound(io);
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		io.writeInt(x);
		io.writeInt(y);
		io.writeInt(z);
		io.writeString(action);
		LMNetHelper.writeTagCompound(io, data);
	}
	
	public IMessage onMessage(MessageClientTileAction m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		TileEntity te = ep.worldObj.getTileEntity(m.x, m.y, m.z);
		
		if(te instanceof IClientActionTile)
			((IClientActionTile)te).onClientAction(ep, m.action, m.data);
		
		return null;
	}
}