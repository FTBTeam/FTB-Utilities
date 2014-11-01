package latmod.core.net;
import io.netty.buffer.ByteBuf;
import latmod.core.tile.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageClientTileAction implements IMessage, IMessageHandler<MessageClientTileAction, IMessage>
{
	public int posX, posY, posZ;
	public String action;
	public NBTTagCompound extraData;
	
	public MessageClientTileAction() { }
	
	public MessageClientTileAction(TileLM t, String s, NBTTagCompound tag)
	{
		posX = t.xCoord;
		posY = t.yCoord;
		posZ = t.zCoord;
		action = s;
		extraData = tag;
	}
	
	public void fromBytes(ByteBuf data)
	{
		posX = data.readInt();
		posY = data.readInt();
		posZ = data.readInt();
		action = LMNetHandler.readString(data);
		extraData = LMNetHandler.readNBTTagCompound(data);
	}
	
	public void toBytes(ByteBuf data)
	{
		data.writeInt(posX);
		data.writeInt(posY);
		data.writeInt(posZ);
		LMNetHandler.writeString(data, action);
		LMNetHandler.writeNBTTagCompound(data, extraData);
	}
	
	public IMessage onMessage(MessageClientTileAction message, MessageContext ctx)
	{
		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		TileEntity te = ep.worldObj.getTileEntity(message.posX, message.posY, message.posZ);
		
		if(te instanceof IClientActionTile)
			((IClientActionTile)te).onClientAction(ep, message.action, message.extraData);
		
		return null;
	}
}