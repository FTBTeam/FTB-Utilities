package ftb.utils.net;

import ftb.lib.FTBLib;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageLM;
import ftb.lib.mod.net.MessageLMPlayerUpdate;
import ftb.utils.world.FTBUPlayerData;
import ftb.utils.world.FTBUPlayerDataMP;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageButtonPressed extends MessageLM<MessageButtonPressed>
{
	public static final byte RENDER_BADGE = 1;
	public static final byte CHAT_LINKS = 2;
	public static final byte CLAIMED_CHUNKS_SETTINGS = 3;
	
	public byte buttonID;
	public int state;
	
	public MessageButtonPressed() { }
	
	public MessageButtonPressed(byte id, int s)
	{
		buttonID = id;
		state = s;
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		buttonID = io.readByte();
		state = io.readByte();
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeByte(buttonID);
		io.writeByte(state);
	}
	
	@Override
	public IMessage onMessage(MessageButtonPressed m, MessageContext ctx)
	{
		ForgePlayerMP p = ForgeWorldMP.inst.getPlayer(ctx.getServerHandler().playerEntity);
		FTBUPlayerDataMP d = FTBUPlayerDataMP.get(p);
		
		if(m.buttonID == RENDER_BADGE)
		{
			d.setFlag(FTBUPlayerData.RENDER_BADGE, m.state == 1);
			p.sendUpdate();
		}
		else if(m.buttonID == CHAT_LINKS)
		{
			d.setFlag(FTBUPlayerData.CHAT_LINKS, m.state == 1);
			return new MessageLMPlayerUpdate(p, true);
		}
		else if(m.buttonID == CLAIMED_CHUNKS_SETTINGS)
		{
			FTBLib.printChat(p.getPlayer(), "Settings Gui is temporarily replaced with /lmplayer_settings!");
			//ConfigRegistry.tempMap.put(provider.getID(), provider);
			//new MessageEditConfig(LMAccessToken.generate(owner.getPlayer()), true, provider).sendTo(owner.getPlayer());
		}
		
		return null;
	}
}