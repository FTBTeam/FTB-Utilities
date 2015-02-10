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
	public static final ResourceLocation texGroups = LC.mod.getLocation("textures/gui/groups.png");
	
	public static final TextureCoords icon_status[] =
	{
		new TextureCoords(texPlayers, 163 + 18 * 0, 0),
		new TextureCoords(texPlayers, 163 + 18 * 1, 0),
		new TextureCoords(texPlayers, 163 + 18 * 2, 0),
	};
	
	public static final TextureCoords icon_online = new TextureCoords(texPlayers, 163, 18);
	
	public TextBoxLM searchBox;
	public ButtonLM buttonClear, buttonSave, buttonPrevPage, buttonNextPage;
	public ButtonPlayer pbOwner;
	public ButtonPlayer[] pbPlayers;
	public int page = 0;
	public final LMPlayer owner;
	public final FastList<Player> players = new FastList<Player>();
	public boolean changed = false;
	
	public GuiFriends(EntityPlayer ep)
	{
		super(new ContainerEmpty(ep, null), texPlayers);
		owner = LMPlayer.getPlayer(ep);
		
		xSize = 163;
		ySize = 184;
		
		widgets.add(searchBox = new TextBoxLM(this, 25, 5, 94, 18)
		{
			public void textChanged()
			{
				updateButtons();
			}
		});
		
		searchBox.charLimit = 15;
		
		widgets.add(buttonClear = new ButtonLM(this, 121, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
			}
			
			public void onButtonDoublePressed(int b)
			{
				if(isShiftKeyDown())
				{
					playClickSound();
					sendUpdate(MessageManageGroups.C_RESET, null);
				}
			}
			
			public void addMouseOverText(FastList<String> l)
			{
				l.add(RED + "Clear All Friends");
				l.add("Double click this button");
				l.add("with Shift key down");
			}
		});
		
		buttonClear.doubleClickRequired = true;
		
		widgets.add(buttonSave = new ButtonLM(this, 140, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				changed = true;
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		});
		
		buttonSave.title = GREEN + "Close";
		
		widgets.add(buttonPrevPage = new ButtonLM(this, 7, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page--;
				playClickSound();
				updateButtons();
			}
		});
		
		buttonPrevPage.title = "Prev Page";
		
		widgets.add(buttonNextPage = new ButtonLM(this, 121, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page++;
				playClickSound();
				updateButtons();
			}
		});
		
		buttonNextPage.title = "Next Page";
		
		widgets.add(pbOwner = new ButtonPlayer(this, -1, 6, 5));
		pbOwner.setPlayer(new Player(owner));
		
		pbPlayers = new ButtonPlayer[7 * 8];
		
		for(int i = 0; i < pbPlayers.length; i++)
			widgets.add(pbPlayers[i] = new ButtonPlayer(this, i, 6 + (i % 8) * 19, 25 + (i / 8) * 19));
		
		updateButtons();
	}
	
	public void sendUpdate(int c, String d)
	{ changed = true; MessageLM.NET.sendToServer(new MessageManageGroups(owner, c, d)); }
	
	public int maxPages()
	{ return (players.size() / pbPlayers.length) + 1; }
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		
		pbOwner.render();
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i].render();
		
		setTexture(texture);
		
		buttonClear.render(Icons.cancel);
		buttonSave.render(Icons.accept);
	}
	
	public void drawText(int mx, int my)
	{
		searchBox.render(30, 10, 0xFFA7A7A7);
		drawCenteredString(fontRendererObj, (page + 1) + " / " + maxPages(), guiLeft + 81, guiTop + 163, 0xFF5A5A5A);
		
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
		if(changed) sendUpdate(0, null);
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
		public Player player;
		
		public ButtonPlayer(GuiLM g, int i, int x, int y)
		{
			super(g, x, y, 18, 18);
			doubleClickRequired = true;
		}
		
		public void setPlayer(Player p)
		{ player = p; }
		
		public void onButtonPressed(int b)
		{
		}
		
		public void onButtonDoublePressed(int b)
		{
			if(player != null && !player.isOwner())
			{
				if(isShiftKeyDown())
					sendUpdate(MessageManageGroups.C_ADD_FRIEND, player.player.username);
				else if(isCtrlKeyDown())
					sendUpdate(MessageManageGroups.C_REM_FRIEND, player.player.username);
			}
		}
		
		public void addMouseOverText(FastList<String> al)
		{
			if(player != null)
			{
				al.add(player.player.getDisplayName());
				
				if(!player.isOwner())
				{
					if(player.player.isOnline()) al.add("[Online]");
					
					if(player.player.isFriend(owner))
					{
						al.add("");
						al.add("Groups:");
						al.add(GREEN + "Friends");
						// Add other groups //
					}
				}
				else
				{
					al.add("");
					al.add("Double " + GREEN + "Shift" + RESET + " click");
					al.add("To add as friend");
					al.add("Double " + RED + "Ctrl" + RESET + " click");
					al.add("To remove friend");
					
				}
			}
		}
		
		public void render()
		{
			if(player != null)
			{
				background = null;
				
				drawPlayerHead(player.player.username, GuiFriends.this.guiLeft + posX + 1, GuiFriends.this.guiTop + posY + 1, 16, 16);
				
				if(!player.isOwner())
				{
					if(player.player.isOnline()) render(icon_online);
					
					int status = player.getStatus();
					if(status > 0) render(icon_status[status - 1]);
				}
			}
		}
	}
}