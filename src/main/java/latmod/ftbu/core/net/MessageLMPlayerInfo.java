package latmod.ftbu.core.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.core.LMNBTUtils;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerInfo extends MessageLM<MessageLMPlayerInfo> implements IClientMessageLM<MessageLMPlayerInfo>
{
	public NBTTagCompound info;
	
	public MessageLMPlayerInfo() { }
	
	public MessageLMPlayerInfo(LMPlayerServer p0)
	{
		info = new NBTTagCompound();
		
		if(p0 == null)
		{
			for(LMPlayerServer p : LMWorldServer.inst.players)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("I", p.getInfo());
				info.setTag("" + p.playerID, tag);
			}
		}
		else
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("I", p0.getInfo());
			info.setTag("D", tag);
			info.setInteger("ID", p0.playerID);
		}
	}
	
	public void fromBytes(ByteBuf bb)
	{
		info = LMNetHelper.readTagCompound(bb);
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeTagCompound(bb, info);
	}
	
	public IMessage onMessage(MessageLMPlayerInfo m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerInfo m, MessageContext ctx)
	{
		if(m.info.hasKey("ID"))
		{
			LMPlayerClient p = LMWorldClient.inst.getPlayer(m.info.getInteger("ID"));
			p.receiveInfo(m.info.getCompoundTag("D").getTagList("I", LMNBTUtils.STRING));
		}
		else
		{
			for(LMPlayerClient p : LMWorldClient.inst.players)
			{
				NBTTagCompound tag = (NBTTagCompound)m.info.getTag("" + p.playerID);
				if(tag != null) p.receiveInfo(tag.getTagList("I", LMNBTUtils.STRING));
			}
		}
		
		LatCoreMCClient.onGuiClientAction();
	}
}