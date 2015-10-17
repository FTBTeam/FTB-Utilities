package latmod.ftbu.net;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.tile.IClientActionTile;
import latmod.ftbu.tile.TileLM;
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
	
	public void fromBytes(ByteBuf io)
	{
		x = io.readInt();
		y = io.readInt();
		z = io.readInt();
		action = ByteBufUtils.readUTF8String(io);
		data = ByteBufUtils.readTag(io);
	}
	
	public void toBytes(ByteBuf io)
	{
		io.writeInt(x);
		io.writeInt(y);
		io.writeInt(z);
		ByteBufUtils.writeUTF8String(io, action);
		ByteBufUtils.writeTag(io, data);
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