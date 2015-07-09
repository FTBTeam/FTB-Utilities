package latmod.ftbu.mod.client.gui;

import static net.minecraft.util.EnumChatFormatting.*;
import latmod.ftbu.core.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.minimap.Waypoints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiFriends extends GuiLM implements IClientActionGui
{
	public static final ResourceLocation texPlayers = FTBU.mod.getLocation("textures/gui/players.png");
	
	public static final TextureCoords icon_status[] =
	{
		new TextureCoords(texPlayers, 18 * 0, 181, 18, 18),
		new TextureCoords(texPlayers, 18 * 1, 181, 18, 18),
		new TextureCoords(texPlayers, 18 * 2, 181, 18, 18),
	};
	
	public final LMPlayerClient owner;
	public final FastList<Player> players;
	
	public final TextBoxLM searchBox;
	public final ButtonLM buttonSave, buttonSort, buttonPrevPage, buttonNextPage;
	public final ButtonPlayer pbOwner;
	public final ButtonPlayer[] pbPlayers;
	public int page = 0;
	
	private static LMClientPlayer selectedPlayer = null;
	private static boolean hideArmor = false;
	private static boolean sortAZ = false;
	private static final FastList<ActionButton> actionButtons = new FastList<ActionButton>();
	
	public GuiFriends()
	{
		super(new ContainerEmpty.ClientGui(), texPlayers);
		
		owner = LMWorld.client.getPlayer(container.player);
		players = new FastList<Player>();
		
		xSize = 240;
		ySize = 181;
		
		searchBox = new TextBoxLM(this, 103, 5, 94, 18)
		{
			public void textChanged()
			{
				refreshPlayers();
			}
		};
		
		searchBox.charLimit = 15;
		
		buttonSave = new ButtonLM(this, 218, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				mc.thePlayer.closeScreen();
			}
		};
		
		buttonSave.title = GREEN + FTBULang.button_close;
		
		buttonSort = new ButtonLM(this, 199, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				sortAZ = !sortAZ;
				refreshPlayers();
				playClickSound();
			}
		};
		
		buttonPrevPage = new ButtonLM(this, 85, 158, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page--;
				refreshPlayers();
				playClickSound();
			}
		};
		
		buttonPrevPage.title = "Prev Page";
		
		buttonNextPage = new ButtonLM(this, 199, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page++;
				refreshPlayers();
				playClickSound();
			}
		};
		
		buttonNextPage.title = "Next Page";
		
		// Player buttons //
		
		pbOwner = new ButtonPlayer(this, -1, 84, 5);
		pbOwner.setPlayer(new Player(owner));
		
		pbPlayers = new ButtonPlayer[7 * 8];
		
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i] = new ButtonPlayer(this, i, 84 + (i % 8) * 19, 25 + (i / 8) * 19);
		
		if(selectedPlayer == null) selectedPlayer = new LMClientPlayer(owner);
		
		refreshPlayers();
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		buttonSort.title = BLUE + "Sort: " + (sortAZ ? "A-Z" : "Friends");
		
		l.add(searchBox);
		l.add(buttonSave);
		l.add(buttonSort);
		l.add(buttonPrevPage);
		l.add(buttonNextPage);
		
		l.add(pbOwner);
		l.addAll(pbPlayers);
		
		actionButtons.clear();
		
		if(selectedPlayer.playerLM.equalsPlayer(owner))
		{
			actionButtons.add(new ActionButton(this, PlayerAction.settings, FTBULang.button_settings));
			actionButtons.add(new ActionButton(this, PlayerAction.waypoints, Waypoints.clientConfig.getIDS()));
			actionButtons.add(new ActionButton(this, PlayerAction.minimap, "Claimed Chunks"));
		}
		else
		{
			if(owner.isFriendRaw(selectedPlayer.playerLM))
				actionButtons.add(new ActionButton(this, PlayerAction.friend_remove, FTBU.mod.translateClient("button.remFriend")));
			else
				actionButtons.add(new ActionButton(this, PlayerAction.friend_add, FTBULang.button_add_friend));
		}
		
		actionButtons.add(new ActionButton(this, PlayerAction.hide_armor, "Hide Armor: " + (hideArmor ? FTBULang.button_enabled : FTBULang.button_disabled)));
		
		l.addAll(actionButtons);
	}
	
	public void sendUpdate(int c, int u)
	{ MessageLM.NET.sendToServer(new MessageManageGroups(owner, c, u)); }
	
	public int maxPages()
	{ return (players.size() / pbPlayers.length) + 1; }
	
	public void drawBackground()
	{
		super.drawBackground();
		
		pbOwner.render();
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i].render();
		
		if(selectedPlayer != null)
		{
			/*
			setTexture(texture);
			
			if(owner.equals(selectedPlayer))
				buttonAdd.render(Icons.settings);
			else
				buttonAdd.render(owner.isFriendRaw(selectedPlayer) ? Icons.remove : Icons.add);
			
			buttonInfo.render(Icons.info);
			buttonHideArmor.render(hideArmor ? Icons.player_gray : Icons.player);
			buttonClose.render(Icons.close);
			*/
		}
		
		buttonSave.render(Icons.accept);
		buttonSort.render(Icons.sort);
		Icons.left.render(this, 94, 159, 16, 16);
		Icons.right.render(this, 209, 159, 16, 16);
		
		for(ActionButton a : actionButtons)
			a.render(a.action.icon);
		
		if(hideArmor)
		{
			for(int i = 0; i < 4; i++)
				selectedPlayer.inventory.armorInventory[i] = null;
		}
		else
		{
			EntityPlayer ep1 = selectedPlayer.playerLM.getPlayerSP();
			
			if(ep1 != null)
			{
				selectedPlayer.inventory.mainInventory = ep1.inventory.mainInventory.clone();
				selectedPlayer.inventory.armorInventory = ep1.inventory.armorInventory.clone();
				selectedPlayer.inventory.mainInventory[0] = ep1.inventory.getCurrentItem();
			}
			else
			{
				for(int i = 0; i < 4; i++)
					selectedPlayer.inventory.armorInventory[i] = selectedPlayer.playerLM.lastArmor[i];
				selectedPlayer.inventory.mainInventory[0] = selectedPlayer.playerLM.lastArmor[4];
			}
		}
		
		int playerX = guiLeft + 44;
		int playerY = guiTop + 163;
		GuiInventory.func_147046_a(playerX, playerY, 55, playerX - mouseX, playerY - 75 - mouseY, selectedPlayer);
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	public void drawText(FastList<String> l)
	{
		searchBox.render(107, 10, 0xFFA7A7A7);
		drawCenteredString(fontRendererObj, (page + 1) + " / " + maxPages(), guiLeft + 159, guiTop + 163, 0xFF444444);
		
		String s = selectedPlayer.playerLM.getName();
		drawString(fontRendererObj, s, guiLeft + 5, guiTop - 10, 0xFFFFFFFF);
		
		super.drawText(l);
	}
	
	public void initGui()
	{
		super.initGui();
		MessageLM.NET.sendToServer(new MessageLMPlayerRequestInfo(owner));
	}
	
	public void onClientAction(String action)
	{
		refreshPlayers();
	}
	
	public void refreshPlayers()
	{
		refreshWidgets();
		
		players.clear();
		
		for(int i = 0; i < LMWorld.client.players.size(); i++)
		{
			LMPlayerClient p = LMWorld.client.players.get(i);
			if(!p.equalsPlayer(owner)) players.add(new Player(p));
		}
		
		if(!searchBox.text.isEmpty())
		{
			FastList<Player> l = new FastList<Player>();
			
			String s = searchBox.text.trim().toLowerCase();
			for(int i = 0; i < players.size(); i++)
			{
				Player p = players.get(i);
				if(p.player.getName().toLowerCase().contains(s)) l.add(players.get(i));
				else
				{
					for(int j = 0; j < p.player.clientInfo.size(); j++)
					{
						if(p.player.clientInfo.get(j).toLowerCase().contains(s))
						{ l.add(players.get(i)); break; }
					}
				}
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
		public final LMPlayerClient player;
		public final boolean isOwner;
		
		public Player(LMPlayerClient p)
		{
			player = p;
			isOwner = player.equalsPlayer(owner);
		}
		
		public int compareTo(Player o)
		{
			if(sortAZ) return player.getName().compareToIgnoreCase(o.player.getName());
			
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
				
				return player.getName().compareToIgnoreCase(o.player.getName());
			}
			
			return Integer.compare(s0, s1);
		}
		
		public boolean equals(Object o)
		{
			if(o instanceof Player)
				return player.equalsPlayer(((Player)o).player);
			return player.equals(o);
		}
		
		public boolean isOwner()
		{ return owner.equalsPlayer(player); }
		
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
			if(player != null && player.player != null)
			{
				selectedPlayer = new LMClientPlayer(player.player);
				selectedPlayer.func_152121_a(MinecraftProfileTexture.Type.SKIN, AbstractClientPlayer.getLocationSkin(selectedPlayer.playerLM.getName()));
				selectedPlayer.inventory.currentItem = 0;
				MessageLM.NET.sendToServer(new MessageLMPlayerRequestInfo(selectedPlayer.playerLM));
			}
			
			refreshWidgets();
		}
		
		public void addMouseOverText(FastList<String> al)
		{
			if(player != null)
			{
				LMPlayerClient p = LMWorld.client.getPlayer(player.player.playerID);
				
				if(p == null) return;
				
				al.add(p.getName());
				if(p.isOnline()) al.add(GREEN + "[" + FTBU.mod.translateClient("label.online") + "]");
				
				if(!player.isOwner())
				{
					boolean raw1 = p.isFriendRaw(owner);
					boolean raw2 = owner.isFriendRaw(p);
					
					if(raw1 && raw2)
						al.add(GREEN + "[" + FTBU.mod.translateClient("label.friend") + "]");
					else if(raw1 || raw2)
						al.add((raw1 ? GOLD : BLUE) + "[" + FTBU.mod.translateClient("label.pfriend") + "]");
				}
				
				if(p.clientInfo != null && !p.clientInfo.isEmpty())
					al.addAll(p.clientInfo);
			}
		}
		
		public void render()
		{
			if(player != null)
			{
				background = null;
				
				drawPlayerHead(player.player.getName(), GuiFriends.this.guiLeft + posX + 1, GuiFriends.this.guiTop + posY + 1, 16, 16, zLevel);
				
				if(player.player.isOnline()) render(Icons.online);
				
				if(!player.isOwner())
				{
					int status = player.getStatus();
					if(status > 0) render(icon_status[status - 1]);
				}
			}
		}
	}
	
	public static class ActionButton extends ButtonLM
	{
		public final GuiFriends gui;
		public final PlayerAction action;
		
		public ActionButton(GuiFriends g, PlayerAction a, String s)
		{
			super(g, 7 + (actionButtons.size() % 4) * 19, 6 + (actionButtons.size() / 4) * 20, 16, 16);
			gui = g;
			action = a;
			title = s;
		}
		
		public void onButtonPressed(int b)
		{
			gui.playClickSound();
			action.onClicked((GuiFriends)gui);
			MessageLM.NET.sendToServer(new MessageLMPlayerRequestInfo(selectedPlayer.playerLM));
		}
	}
	
	public abstract static class PlayerAction
	{
		public final TextureCoords icon;
		
		public PlayerAction(TextureCoords c)
		{ icon = c; }
		
		public abstract void onClicked(GuiFriends g);
		
		// Self //
		
		public static final PlayerAction settings = new PlayerAction(Icons.settings)
		{
			public void onClicked(GuiFriends g)
			{ g.mc.displayGuiScreen(new GuiClientConfig(g)); }
		};
		
		public static final PlayerAction waypoints = new PlayerAction(Icons.compass)
		{
			public void onClicked(GuiFriends g)
			{ g.mc.displayGuiScreen(new GuiWaypoints()); }
		};
		
		public static final PlayerAction minimap = new PlayerAction(Icons.map)
		{
			public void onClicked(GuiFriends g)
			{ g.mc.displayGuiScreen(new GuiMinimap()); }
		};
		
		// Other players //
		
		public static final PlayerAction friend_add = new PlayerAction(Icons.add)
		{
			public void onClicked(GuiFriends g)
			{
				g.sendUpdate(MessageManageGroups.C_ADD_FRIEND, selectedPlayer.playerLM.playerID);
				g.refreshPlayers();
			}
		};
		
		public static final PlayerAction friend_remove = new PlayerAction(Icons.remove)
		{
			public void onClicked(GuiFriends g)
			{
				g.sendUpdate(MessageManageGroups.C_REM_FRIEND, selectedPlayer.playerLM.playerID);
				g.refreshPlayers();
			}
		};
		
		// Common //
		
		public static final PlayerAction hide_armor = new PlayerAction(Icons.jacket)
		{
			public void onClicked(GuiFriends g)
			{
				hideArmor = !hideArmor;
				g.refreshWidgets();
			}
		};
	}
	
	public static class LMClientPlayer extends AbstractClientPlayer
	{
		private static final ChunkCoordinates coords000 = new ChunkCoordinates(0, 0, 0);
		public final LMPlayerClient playerLM;
		
		public LMClientPlayer(LMPlayerClient p)
		{
			super(Minecraft.getMinecraft().theWorld, p.gameProfile);
			playerLM = p;
		}
		
		public void addChatMessage(IChatComponent i) { }
		
		public boolean canCommandSenderUseCommand(int i, String s)
		{ return false; }
		
		public ChunkCoordinates getPlayerCoordinates()
		{ return coords000; }
		
		public boolean isInvisibleToPlayer(EntityPlayer ep)
		{ return true; }
	}
}