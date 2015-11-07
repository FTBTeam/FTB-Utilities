package latmod.ftbu.mod.client.gui.friends;

import ftb.lib.api.gui.GuiIcons;
import ftb.lib.client.*;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.api.client.EventPlayerAction;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.util.client.FTBULang;
import latmod.ftbu.util.gui.GuiLM;
import latmod.ftbu.world.*;
import latmod.lib.FastList;

public abstract class PlayerAction
{
	public final TextureCoords icon;
	
	public PlayerAction(TextureCoords c)
	{ icon = c; }
	
	public abstract void onClicked(LMPlayerClient p);
	public abstract String getTitle();
	
	public void addMouseOverText(FastList<String> l) { }
	
	public final void render(int ax, int ay, double z)
	{
		FTBLibClient.setTexture(icon.texture);
		GuiLM.drawTexturedRectD(ax, ay, z, 16, 16, icon.minU, icon.minV, icon.maxU, icon.maxV);
	}
	
	// Other players //
	
	public static final PlayerAction friend_add = new PlayerAction(GuiIcons.add)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_ADD_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_add_friend(); }
	};
	
	public static final PlayerAction friend_remove = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_REM_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_rem_friend(); }
	};
	
	public static final PlayerAction friend_deny = new PlayerAction(GuiIcons.remove)
	{
		public void onClicked(LMPlayerClient p)
		{ ClientAction.ACTION_DENY_FRIEND.send(p.playerID); }
		
		public String getTitle()
		{ return FTBULang.Guis.button_deny_friend(); }
	};
	
	public static final PlayerAction mail = new PlayerAction(GuiIcons.feather)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Guis.mail(); }
	};
	
	public static final PlayerAction trade = new PlayerAction(GuiIcons.moneybag)
	{
		public void onClicked(LMPlayerClient p)
		{  }
		
		public String getTitle()
		{ return FTBULang.Guis.trade(); }
	};

	public static FastList<PlayerAction> getActionsFor(LMPlayerClient p)
	{
		LMPlayerClient o = LMWorldClient.inst.getClientPlayer();
		FastList<PlayerAction> list = new FastList<PlayerAction>();
		
		if(p.equalsPlayer(o))
		{
			list.add(PlayerSelfAction.settings);
			list.add(PlayerSelfAction.guide);
			list.add(PlayerSelfAction.info);
			list.add(PlayerSelfAction.claims);
			
			if(FTBLibFinals.DEV)
			{
				//list.add(PlayerSelfAction.mail);
				list.add(PlayerSelfAction.notes);
			}
		}
		else
		{
			boolean isFriend = o.isFriendRaw(p);
			if(!isFriend) list.add(friend_add);
			
			if(FTBLibFinals.DEV)
			{
				//list.add(mail);
				//list.add(trade);
			}
			
			if(isFriend) list.add(friend_remove);
			else if(p.isFriendRaw(o))
				list.add(friend_deny);
		}
		
		new EventPlayerAction(list, p).post();
		return list;
	}
}