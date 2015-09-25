package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
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
	
	public void fromBytes(ByteBuf bb)
	{
		x = bb.readInt();
		y = bb.readInt();
		z = bb.readInt();
		action = LMNetHelper.readString(bb);
		data = LMNetHelper.readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(x);
		bb.writeInt(y);
		bb.writeInt(z);
		LMNetHelper.writeString(bb, action);
		LMNetHelper.writeTagCompound(bb, data);
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