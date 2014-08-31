package latmod.core.mod.net;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import latmod.core.mod.LMPlayer;
import latmod.core.mod.LMPlayer.DataChangedEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;

public class MessageUpdatePlayerData implements IMessage, IMessageHandler<MessageUpdatePlayerData, IMessage>
{
	public UUID uuid;
	public NBTTagCompound data;
	public String channel;
	
	public MessageUpdatePlayerData() { }
	
	public MessageUpdatePlayerData(LMPlayer p, String s)
	{
		uuid = p.uuid;
		data = new NBTTagCompound();
		p.writeToNBT(data);
		data.setString("UUID", p.uuid.toString());
		channel = s;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		uuid = UUID.fromString(LMNetHandler.readString(bb));
		data = LMNetHandler.readNBTTagCompound(bb);
		channel = LMNetHandler.readString(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHandler.writeString(bb, uuid.toString());
		LMNetHandler.writeNBTTagCompound(bb, data);
		LMNetHandler.writeString(bb, channel);
	}
	
	public IMessage onMessage(MessageUpdatePlayerData message, MessageContext ctx)
	{
		LMPlayer p = LMPlayer.getPlayer(message.uuid);
		
		if(p == null)
		{
			p = new LMPlayer(message.uuid);
			LMPlayer.list.add(p);
		}
		
		p.readFromNBT(message.data);
		MinecraftForge.EVENT_BUS.post(new DataChangedEvent(p, Side.CLIENT, message.channel));
		
		return null;
	}
}