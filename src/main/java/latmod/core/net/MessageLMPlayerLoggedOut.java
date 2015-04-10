package latmod.core.net;
import latmod.core.LMPlayer;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageLMPlayerLoggedOut extends MessageLM<MessageLMPlayerLoggedOut>
{
	public MessageLMPlayerLoggedOut() { }
	
	public MessageLMPlayerLoggedOut(LMPlayer p)
	{
		data = new NBTTagCompound();
		data.setInteger("P", p.playerID);
	}
	
	public void onMessage(MessageContext ctx)
	{
		int playerID = data.getInteger("P");
		LMPlayer p = LMPlayer.getPlayer(playerID);
		LC.proxy.playerLMLoggedOut(p);
	}
}