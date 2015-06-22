package latmod.ftbu.core.net;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.event.CustomAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageLMPlayerRequestInfo extends MessageLM<MessageLMPlayerRequestInfo>
{
	public int playerID;
	
	public MessageLMPlayerRequestInfo() { }
	
	public MessageLMPlayerRequestInfo(LMPlayer p)
	{
		playerID = p.playerID;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
	}
	
	public IMessage onMessage(MessageLMPlayerRequestInfo m, MessageContext ctx)
	{
		LMPlayer p = LMPlayer.getPlayer(playerID);
		if(p != null) p.sendInfo(ctx.getServerHandler().playerEntity);
		return null;
	}
}