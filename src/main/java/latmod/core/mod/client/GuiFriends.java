package latmod.core.mod.client;

import static net.minecraft.util.EnumChatFormatting.*;
import latmod.core.*;
import latmod.core.event.LMPlayerEvent;
import latmod.core.gui.*;
import latmod.core.mod.LC;
import latmod.core.net.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiFriends extends GuiLM
{
	public static final ResourceLocation texPlayers = LC.mod.getLocation("textures/gui/players.png");
	public static final ResourceLocation texActions = LC.mod.getLocation("textures/gui/actions.png");
	public static final ResourceLocation texView = LC.mod.getLocation("textures/gui/view.png");
	
	public static final TextureCoords icon_status[] =
	{
		new TextureCoords(texPlayers, 161 + 18 * 0, 0),
		new TextureCoords(texPlayers, 161 + 18 * 1, 0),
		new TextureCoords(texPlayers, 161 + 18 * 2, 0),
	};
	
	public TextBoxLM searchBox;
	public ButtonLM buttonSave, buttonPrevPage, buttonNextPage;
	public ButtonPlayer pbOwner;
	public ButtonPlayer[] pbPlayers;
	public int page = 0;
	public final LMPlayer owner;
	public final FastList<Player> players = new FastList<Player>();
	public boolean changed = false;
	
	public LMPlayer playerActionsOpen = null;
	public boolean hasViewOpen = false;
	public ButtonLM buttonAdd, buttonGroup, buttonClose;
	public ButtonLM buttonView, buttonTrade, buttonMail;
	public ButtonLM buttonViewLeft, buttonViewRight, buttonViewClose;
	
	public GuiFriends(EntityPlayer ep)
	{
		super(new ContainerEmpty(ep, null), texPlayers);
		owner = LMPlayer.getPlayer(ep);
		
		xSize = 161;
		ySize = 184;
		
		widgets.add(searchBox = new TextBoxLM(this, 24, 5, 113, 18)
		{
			public void textChanged()
			{
				updateButtons();
			}
		});
		
		searchBox.charLimit = 15;
		
		widgets.add(buttonSave = new ButtonLM(this, 139, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				changed = true;
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		});
		
		buttonSave.title = GREEN + "Close";
		
		widgets.add(buttonPrevPage = new ButtonLM(this, 7, 158, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page--;
				playClickSound();
				updateButtons();
			}
		});
		
		buttonPrevPage.title = "Prev Page";
		
		widgets.add(buttonNextPage = new ButtonLM(this, 120, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page++;
				playClickSound();
				updateButtons();
			}
		});
		
		buttonNextPage.title = "Next Page";
		
		// Action gui buttons //
		
		widgets.add(buttonAdd = new ButtonLM(this, -58, 20, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				
				if(owner.isFriendRaw(playerActionsOpen))
					sendUpdate(MessageManageGroups.C_REM_FRIEND, playerActionsOpen.playerID, null);
				else
					sendUpdate(MessageManageGroups.C_ADD_FRIEND, playerActionsOpen.playerID, null);
				
				refreshActionButtons();
			}
			
			public boolean isEnabled()
			{ return playerActionsOpen != null && !playerActionsOpen.equals(owner); }
		});
		
		widgets.add(buttonGroup = new ButtonLM(this, -39, 20, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
			}
			
			public boolean isEnabled()
			{ return playerActionsOpen != null && !playerActionsOpen.equals(owner); }
		});
		
		widgets.add(buttonClose = new ButtonLM(this, -20, 20, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				playerActionsOpen = null;
			}
			
			public boolean isEnabled()
			{ return playerActionsOpen != null; }
		});
		
		buttonClose.title = "Close";
		
		widgets.add(buttonView = new ButtonLM(this, -58, 39, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				hasViewOpen = !hasViewOpen;
				refreshActionButtons();
			}
			
			public boolean isEnabled()
			{ return playerActionsOpen != null; }
		});
		
		widgets.add(buttonTrade = new ButtonLM(this, -39, 39, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
			}
			
			public boolean isEnabled()
			{ return playerActionsOpen != null && !playerActionsOpen.equals(owner); }
		});
		
		widgets.add(buttonMail = new ButtonLM(this, -20, 39, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
			}
			
			public boolean isEnabled()
			{ return playerActionsOpen != null && !playerActionsOpen.equals(owner); }
		});
		
		refreshActionButtons();
		
		// Player buttons //
		
		widgets.add(pbOwner = new ButtonPlayer(this, -1, 5, 5));
		pbOwner.setPlayer(new Player(owner));
		
		pbPlayers = new ButtonPlayer[7 * 8];
		
		for(int i = 0; i < pbPlayers.length; i++)
			widgets.add(pbPlayers[i] = new ButtonPlayer(this, i, 5 + (i % 8) * 19, 25 + (i / 8) * 19));
		
		updateButtons();
	}
	
	public void refreshActionButtons()
	{
		if(playerActionsOpen == null) return;
		
		if(owner.equals(playerActionsOpen))
		{
			buttonAdd.title = null;
			buttonGroup.title = null;
		}
		else
		{
			buttonAdd.title = owner.isFriendRaw(playerActionsOpen) ? "Remove from friends" : "Add as friend";
			buttonGroup.title = "[WIP] Edit Groups";
			buttonView.title = "View player";
			buttonTrade.title = "[WIP] Trade";
			buttonMail.title = "[WIP] Mail";
		}
	}
	
	public void sendUpdate(int c, int u, String g)
	{
		changed = true;
		MessageLM.NET.sendToServer(new MessageManageGroups(owner, c, u, g));
	}
	
	public int maxPages()
	{ return (players.size() / pbPlayers.length) + 1; }
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		
		pbOwner.render();
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i].render();
		
		if(playerActionsOpen != null)
		{
			setTexture(texActions);
			drawTexturedModalRect(guiLeft - 65, guiTop + 13, 0, 0, 65, 49);
			
			if(hasViewOpen)
			{
				setTexture(texView);
				drawTexturedModalRect(guiLeft - 65, guiTop + 63, 0, 0, 65, 106);
			}
			
			setTexture(texture);
			
			if(owner.equals(playerActionsOpen))
				buttonAdd.render(Icons.toggle_on);
			else
				buttonAdd.render(owner.isFriendRaw(playerActionsOpen) ? Icons.remove : Icons.add);
			
			buttonGroup.render(buttonGroup.isEnabled() ? Icons.Friends.groups : Icons.Friends.groups_gray);
			buttonView.render(buttonView.isEnabled() ? Icons.Friends.view : Icons.Friends.view_gray);
			buttonTrade.render(buttonTrade.isEnabled() ? Icons.Friends.trade : Icons.Friends.trade_gray);
			buttonMail.render(buttonMail.isEnabled() ? Icons.Friends.mail : Icons.Friends.mail_gray);
			
			buttonClose.render(Icons.cancel);
		}
		
		buttonSave.render(Icons.accept);
	}
	
	public void drawText(int mx, int my)
	{
		searchBox.render(30, 10, 0xFFA7A7A7);
		drawCenteredString(fontRendererObj, (page + 1) + " / " + maxPages(), guiLeft + 81, guiTop + 163, 0xFF444444);
		
		if(playerActionsOpen != null)
		{
			String s = playerActionsOpen.getDisplayName();
			drawString(fontRendererObj, s, guiLeft - fontRendererObj.getStringWidth(s) - 2, guiTop + 3, 0xFFFFFFFF);
		}
		
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
		if(changed) sendUpdate(0, 0, null);
		super.onGuiClosed();
	}
	
	@SubscribeEvent
	public void onClientEvent(LMPlayerEvent.DataChanged e)
	{ updateButtons(); }
	
	public void updateButtons()
	{
		players.clear();
		
		for(int i = 0; i < LMPlayer.map.values.size(); i++)
		{
			LMPlayer p = LMPlayer.map.values.get(i);
			if(!p.equals(owner)) players.add(new Player(p));
		}
		
		if(!searchBox.text.isEmpty())
		{
			FastList<Player> l = new FastList<Player>();
			
			String s = searchBox.text.trim().toLowerCase();
			for(int i = 0; i < players.size(); i++)
			{
				String s1 = LatCoreMC.removeFormatting(players.get(i).player.getDisplayName().toLowerCase());
				if(s1.contains(s)) l.add(players.get(i));
			}
			
			players.clear();
			players.addAll(l);
		}
		
		if(page < 0) page = maxPages() - 1;
		if(page >= maxPages()) page = 0;
		
		players.sort(null);
		
		for(int i = 0; i < pbPlayers.length; i++)
		{
			int j = i + page * pbPlayers.length;
			if(j < 0 || j >= players.size()) j =-1;
			
			pbPlayers[i].setPlayer((j == -1) ? null : players.get(j));
		}
		
		refreshActionButtons();
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
				return u.compareTo(u1);
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
	
	public class ButtonPlayer extends ButtonLM
	{
		public Player player = null;
		
		public ButtonPlayer(GuiLM g, int i, int x, int y)
		{ super(g, x, y, 18, 18); }
		
		public void setPlayer(Player p)
		{ player = p; }
		
		public void onButtonPressed(int b)
		{
			if(player != null)
				playerActionsOpen = player.player;
			else playerActionsOpen = null;
			
			refreshActionButtons();
		}
		
		public void addMouseOverText(FastList<String> al)
		{
			if(player != null)
			{
				al.add(player.player.getDisplayName());
				if(player.player.isOnline()) al.add(GREEN + "[Online]");
				
				if(!player.isOwner())
				{
					boolean raw1 = player.player.isFriendRaw(owner);
					boolean raw2 = owner.isFriendRaw(player.player);
					
					if(raw1 && raw2)
						al.add(GREEN + "[Friend]");
					else if(raw1 || raw2)
						al.add((raw1 ? GOLD : BLUE) + "[Pending Friend]");
					
					FastList<LMPlayer.Group> g = owner.getGroupsFor(player.player);
					
					if(g.size() > 0)
					{
						al.add("");
						al.add("Groups:");
						
						for(int i = 0; i < g.size(); i++)
							al.add(g.get(i).name);
					}
				}
			}
		}
		
		public void render()
		{
			if(player != null)
			{
				background = null;
				
				drawPlayerHead(player.player.username, GuiFriends.this.guiLeft + posX + 1, GuiFriends.this.guiTop + posY + 1, 16, 16);
				
				if(player.player.isOnline()) render(Icons.Friends.online);
				
				if(!player.isOwner())
				{
					int status = player.getStatus();
					if(status > 0) render(icon_status[status - 1]);
				}
			}
		}
	}
}