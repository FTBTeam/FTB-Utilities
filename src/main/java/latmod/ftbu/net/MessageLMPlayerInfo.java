package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.FastList;
import net.minecraft.util.IChatComponent;

public class MessageLMPlayerInfo extends MessageByteArray<MessageLMPlayerInfo>
{
	public MessageLMPlayerInfo() { }
	
	public MessageLMPlayerInfo(int playerID)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(playerID);
		io.writeInt(p == null ? 0 : p.playerID);
		if(p == null) return;
		FastList<IChatComponent> info = new FastList<IChatComponent>();
		p.getInfo(info);
		io.writeUByte(Math.min(info.size(), 255));
		for(int i = 0; i < info.size(); i++)
			io.writeString(IChatComponent.Serializer.func_150696_a(info.get(i)));
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageLMPlayerInfo m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.io.readInt());
		if(p == null) return null;
		int size = m.io.readUByte();
		FastList<IChatComponent> info = new FastList<IChatComponent>();
		for(int i = 0; i < size; i++)
			info.add(IChatComponent.Serializer.func_150699_a(m.io.readString()));
		p.receiveInfo(info);
		LatCoreMCClient.onGuiClientAction();
		return null;
	}
}