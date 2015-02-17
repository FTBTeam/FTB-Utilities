package latmod.core.mod.client;

import latmod.core.*;
import latmod.core.event.LMPlayerEvent;
import latmod.core.gui.*;
import latmod.core.mod.LC;
import latmod.core.net.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiManageGroups extends GuiLM
{
	public static final ResourceLocation tex = LC.mod.getLocation("textures/gui/groups.png");
	
	/*
	public static final TextureCoords icon_status =
	{
		new TextureCoords(texPlayers, 161 + 18 * 0, 0),
		new TextureCoords(texPlayers, 161 + 18 * 1, 0),
		new TextureCoords(texPlayers, 161 + 18 * 2, 0),
	};*/
	
	public TextBoxLM searchBox;
	public final LMPlayer owner;
	public boolean changed = false;
	
	public ButtonLM buttonAdd, buttonClose;
	
	public GuiManageGroups(EntityPlayer ep)
	{
		super(new ContainerEmpty(ep, null), tex);
		owner = LMPlayer.getPlayer(ep);
		
		xSize = 105;
		ySize = 124;
		
		widgets.add(searchBox = new TextBoxLM(this, 24, 5, 59, 16)
		{
			public void textChanged()
			{
				refreshActionButtons();
			}
		});
		
		searchBox.charLimit = 12;
		
		// Action gui buttons //
		
		widgets.add(buttonAdd = new ButtonLM(this, -39, 20, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				refreshActionButtons();
			}
			
			public boolean isEnabled()
			{ return false; }
		});
		
		widgets.add(buttonClose = new ButtonLM(this, -20, 20, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
			}
			
			public boolean isEnabled()
			{ return false; }
		});
		
		refreshActionButtons();
	}
	
	public void refreshActionButtons()
	{
		buttonClose.title = "Close";
	}
	
	public void sendUpdate(int c, int u, int g, String gn)
	{
		changed = true;
		MessageLM.NET.sendToServer(new MessageManageGroups(owner, c, u, g, gn));
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		
		buttonAdd.render(Icons.settings);
		buttonClose.render(Icons.cancel);
	}
	
	public void drawText(int mx, int my)
	{
		super.drawText(mx, my);
	}
	
	public void initGui()
	{
		super.initGui();
		LatCoreMC.EVENT_BUS.register(this);
	}
	
	public void onGuiClosed()
	{
		LatCoreMC.EVENT_BUS.unregister(this);
		if(changed) sendUpdate(0, 0, 0, null);
		super.onGuiClosed();
	}
	
	@SubscribeEvent
	public void onClientEvent(LMPlayerEvent.DataChanged e)
	{ refreshActionButtons(); }
	
	protected void keyTyped(char c, int k)
	{
		if(k == LCClient.key.getKeyCode())
			mc.thePlayer.closeScreen();
		
		super.keyTyped(c, k);
	}
	
	public class Player implements Comparable<Player>
	{
		public final LMPlayer player;
		public final boolean isOwner;
		
		public Player(LMPlayer p)
		{
			player = p;
			isOwner = player.equals(owner);
		}
		
		public int compareTo(Player o)
		{
			int s0 = getStatus();
			int s1 = o.getStatus();
			
			if(s0 == 0 && s1 != 0) return 1;
			if(s0 != 0 && s1 == 0) return -1;
			
			if(s0 == s1)
			{
				boolean on0 = player.isOnline();
				boolean on1 = o.player.isOnline();
				
				if(on0 && !on1) return -1;
				if(!on0 && on1) return 1;
				
				String u = player.getDisplayName();
				String u1 = o.player.getDisplayName();
				return u.compareToIgnoreCase(u1);
			}
			
			return Integer.compare(s0, s1);
		}
		
		public boolean equals(Object o)
		{
			if(o instanceof Player)
				return equals(((Player)o).player);
			return player.equals(o);
		}
		
		public boolean isOwner()
		{ return owner.equals(player); }
		
		/** 0 - None, 1 - Friend, 2 - Inviting, 3 - Invited */
		public int getStatus()
		{
			boolean b1 = owner.isFriendRaw(player);
			boolean b2 = player.isFriendRaw(owner);
			
			if(b1 && b2) return 1;
			if(b1 && !b2) return 2;
			if(!b1 && b2) return 3;
			return 0;
		}
	}
}