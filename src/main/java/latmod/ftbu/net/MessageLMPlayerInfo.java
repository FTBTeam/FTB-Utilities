package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.core.util.ByteIOStream;
import latmod.ftbu.util.LMNBTUtils;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerInfo extends MessageLM<MessageLMPlayerInfo>
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
	
	public void readData(ByteIOStream io) throws Exception
	{
		info = LMNetHelper.readTagCompound(io);
	}
	
	public void writeData(ByteIOStream io) throws Exception
	{
		LMNetHelper.writeTagCompound(io, info);
	}
	
	public IMessage onMessage(MessageLMPlayerInfo m, MessageContext ctx)
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
		return null;
	}
}