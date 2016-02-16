package ftb.utils.net;

import ftb.lib.FTBLib;
import ftb.lib.api.friends.LMPlayerMP;
import ftb.utils.mod.handlers.ftbl.FTBUPlayerData;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public abstract class ClientAction
{
	public static final ClientAction NULL = new ClientAction()
	{
		public boolean onAction(NBTTagCompound extra, LMPlayerMP owner)
		{ return false; }
	};
	
	public static final ClientAction REQUEST_SELF_UPDATE = new ClientAction()
	{
		public boolean onAction(NBTTagCompound extra, LMPlayerMP owner)
		{
			.sendTo(owner.getPlayer());
			return false;
		}
	};
	
	public static final ClientAction BUTTON_RENDER_BADGE = new ClientAction()
	{
		public boolean onAction(NBTTagCompound extra, LMPlayerMP owner)
		{
			//owner.renderBadge = extra == 1;
			return true;
		}
	};
	
	public static final ClientAction BUTTON_CHAT_LINKS = new ClientAction()
	{
		public boolean onAction(NBTTagCompound extra, LMPlayerMP owner)
		{
			FTBUPlayerData.get(owner).setFlag(FTBUPlayerData.CHAT_LINKS, extra.getBoolean("CL"));
			return true;
		}
	};
	
	public static final ClientAction BUTTON_CLAIMED_CHUNKS_SETTINGS = new ClientAction()
	{
		public boolean onAction(NBTTagCompound extra, final LMPlayerMP owner)
		{
			FTBLib.printChat(owner.getPlayer(), "Settings Gui is temporarily replaced with /lmplayer_settings!");
			//ConfigRegistry.tempMap.put(provider.getID(), provider);
			//new MessageEditConfig(LMAccessToken.generate(owner.getPlayer()), true, provider).sendTo(owner.getPlayer());
			return false;
		}
	};
	
	private static final HashMap<Byte, ClientAction> map = new HashMap<>();
	
	private static void register(int i, ClientAction c)
	{
		c.ID = (byte) i;
		map.put(c.getID(), c);
	}
	
	static
	{
		register(0, NULL);
		register(1, ADD_FRIEND);
		register(2, REM_FRIEND);
		register(3, DENY_FRIEND);
		register(4, REQUEST_PLAYER_INFO);
		register(5, REQUEST_SERVER_INFO);
		register(6, REQUEST_SELF_UPDATE);
		register(7, REQUEST_BADGE);
		register(8, BUTTON_RENDER_BADGE);
		register(9, BUTTON_CHAT_LINKS);
		register(10, BUTTON_CLAIMED_CHUNKS_SETTINGS);
	}
	
	private byte ID;
	
	private ClientAction() { }
	
	public byte getID()
	{ return ID; }
	
	public abstract boolean onAction(NBTTagCompound extra, LMPlayerMP owner);
	
	public void send(NBTTagCompound extra)
	{ new MessageClientAction(this, extra).sendToServer(); }
	
	public static ClientAction get(byte id)
	{
		if(id == 0) return NULL;
		ClientAction a = map.get(id);
		if(a == null) return NULL;
		return a;
	}
}
