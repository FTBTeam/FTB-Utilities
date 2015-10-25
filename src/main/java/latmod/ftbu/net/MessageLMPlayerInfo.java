package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.LMNetworkWrapper;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.FastList;
import net.minecraft.util.IChatComponent;

public class MessageLMPlayerInfo extends MessageFTBU
{
	public MessageLMPlayerInfo() { super(DATA_LONG); }
	
	public MessageLMPlayerInfo(int playerID)
	{
		this();
		LMPlayerServer p = LMWorldServer.inst.getPlayer(playerID);
		io.writeInt(p == null ? 0 : p.playerID);
		if(p == null) return;
		FastList<IChatComponent> info = new FastList<IChatComponent>();
		p.getInfo(info);
		io.writeUByte(Math.min(info.size(), 255));
		for(int i = 0; i < info.size(); i++)
			io.writeString(IChatComponent.Serializer.func_150696_a(info.get(i)));
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		if(p == null) return null;
		int size = io.readUByte();
		FastList<IChatComponent> info = new FastList<IChatComponent>();
		for(int i = 0; i < size; i++)
			info.add(IChatComponent.Serializer.func_150699_a(io.readString()));
		p.receiveInfo(info);
		LatCoreMCClient.onGuiClientAction();
		return null;
	}
}