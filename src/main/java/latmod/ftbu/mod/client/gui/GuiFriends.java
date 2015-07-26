package latmod.ftbu.mod.client.gui;

import static net.minecraft.util.EnumChatFormatting.*;

import java.util.Comparator;

import latmod.ftbu.core.FTBULang;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.event.EventLM;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.mod.client.minimap.Waypoints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
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
	
	public final GuiScreen parentScreen;
	public final LMPlayerClient owner;
	public final FastList<Player> players;
	
	public final TextBoxLM searchBox;
	public final ButtonLM buttonSave, buttonSort, buttonPrevPage, buttonNextPage, buttonArmor;
	public final ButtonPlayer pbOwner;
	public final ButtonPlayer[] pbPlayers;
	public int page = 0;
	
	private static LMClientPlayer selectedPlayer = null;
	private static final FastList<ActionButton> actionButtons = new FastList<ActionButton>();
	private static LMPlayerClient staticOwner;
	private static LMPComparator comparator;
	
	public GuiFriends(GuiScreen gui)
	{
		super(null, texPlayers);
		parentScreen = gui;
		hideNEI = true;
		
		owner = LMWorldClient.inst.getPlayer(container.player);
		players = new FastList<Player>();
		staticOwner = owner;
		
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
				if(parentScreen != null)
					mc.displayGuiScreen(parentScreen);
				else
					mc.thePlayer.closeScreen();
			}
		};
		
		buttonSave.title = GREEN + FTBULang.button_close;
		
		buttonSort = new ButtonLM(this, 199, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				if(b == 0)
					comparator = LMPComparator.map.values.get((comparator.listID + 1) % LMPComparator.map.size());
				else
				{
					int i = comparator.listID - 1;
					if(i < 0) i = LMPComparator.map.size() - 1;
					comparator = LMPComparator.map.values.get(i);
				}
				
				refreshPlayers();
				playClickSound();
			}
			
			public void addMouseOverText(FastList<String> l)
			{
				l.add(title);
				if(isShiftKeyDown())
				{
					for(LMPComparator c : LMPComparator.map.values)
						l.add(((c == comparator) ? EnumChatFormatting.BLUE : EnumChatFormatting.GRAY) + c.translatedName);
				}
				else l.add(EnumChatFormatting.BLUE + comparator.translatedName);
			}
		};
		
		buttonSort.title = FTBU.mod.translateClient("button.lmp_comparator");
		
		buttonPrevPage = new ButtonLM(this, 85, 158, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page--;
				refreshPlayers();
				playClickSound();
			}
		};
		
		buttonNextPage = new ButtonLM(this, 199, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page++;
				refreshPlayers();
				playClickSound();
			}
		};
		
		buttonArmor = new ButtonLM(this, 64, 46, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				FTBUClient.hideArmorFG.incValue();
				ClientConfig.Registry.save();
			}
		};
		
		// Player buttons //
		
		pbOwner = new ButtonPlayer(this, -1, 84, 5);
		pbOwner.setPlayer(new Player(owner));
		
		pbPlayers = new ButtonPlayer[7 * 8];
		
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i] = new ButtonPlayer(this, i, 84 + (i % 8) * 19, 25 + (i / 8) * 19);
		
		if(selectedPlayer == null) selectedPlayer = new LMClientPlayer(owner);
		
		LMPComparator.init();
		if(comparator == null)
			comparator = LMPComparator.map.values.get(0);
		
		refreshPlayers();
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.add(searchBox);
		l.add(buttonSave);
		l.add(buttonSort);
		l.add(buttonPrevPage);
		l.add(buttonNextPage);
		l.add(buttonArmor);
		
		l.add(pbOwner);
		l.addAll(pbPlayers);
		
		actionButtons.clear();
		
		if(selectedPlayer.playerLM.equalsPlayer(owner))
		{
			actionButtons.add(new ActionButton(this, PlayerAction.settings, FTBULang.client_config));
			actionButtons.add(new ActionButton(this, PlayerAction.waypoints, Waypoints.clientConfig.getIDS()));
			actionButtons.add(new ActionButton(this, PlayerAction.minimap, "Claimed Chunks"));
			//actionButtons.add(new ActionButton(this, PlayerAction.notes, "Notes [WIP]"));
			//actionButtons.add(new ActionButton(this, PlayerAction.notifications, "Notifications [WIP]"));
		}
		else
		{
			if(owner.isFriendRaw(selectedPlayer.playerLM))
				actionButtons.add(new ActionButton(this, PlayerAction.friend_remove, FTBULang.button_rem_friend));
			else
				actionButtons.add(new ActionButton(this, PlayerAction.friend_add, FTBULang.button_add_friend));
		}
		
		l.addAll(actionButtons);
	}
	
	public void sendUpdate(int c, int u)
	{ LMNetHelper.sendToServer(new MessageManageGroups(owner, c, u)); }
	
	public int maxPages()
	{ return (players.size() / pbPlayers.length) + 1; }
	
	public void drawBackground()
	{
		super.drawBackground();
		
		pbOwner.render();
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i].render();
		
		buttonSave.render(Icons.accept);
		buttonSort.render(Icons.sort);
		Icons.left.render(this, 94, 159, 16, 16);
		Icons.right.render(this, 209, 159, 16, 16);
		
		buttonArmor.render(Icons.jacket);
		if(FTBUClient.hideArmorFG.getB()) buttonArmor.render(Icons.close);
		
		for(ActionButton a : actionButtons)
			a.render(a.action.icon);
		
		if(FTBUClient.hideArmorFG.getB())
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
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		int playerX = guiLeft + 44;
		int playerY = guiTop + 163;
		GuiInventory.func_147046_a(playerX, playerY, 55, playerX - mouseX, playerY - 75 - mouseY, selectedPlayer);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
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
		LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(0));
	}
	
	public void onClientAction(String action)
	{
		refreshPlayers();
	}
	
	public void refreshPlayers()
	{
		refreshWidgets();
		
		players.clear();
		
		FastList<LMPlayerClient> list0 = new FastList<LMPlayerClient>();
		list0.addAll(LMWorldClient.inst.players);
		list0.remove(owner);
		list0.sort(comparator);
		
		for(LMPlayerClient p : list0)
			players.add(new Player(p));
		
		if(!searchBox.text.isEmpty())
		{
			FastList<Player> l = new FastList<Player>();
			
			String s = searchBox.text.trim().toLowerCase();
			for(int i = 0; i < players.size(); i++)
			{
				Player p = players.get(i);
				if(p.player.getName().toLowerCase().contains(s)) l.add(players.get(i));
			}
			
			players.clear();
			players.addAll(l);
		}
		
		if(page < 0) page = maxPages() - 1;
		if(page >= maxPages()) page = 0;
		
		for(int i = 0; i < pbPlayers.length; i++)
		{
			int j = i + page * pbPlayers.length;
			if(j < 0 || j >= players.size()) j =-1;
			
			pbPlayers[i].setPlayer((j == -1) ? null : players.get(j));
		}
	}
	
	public class Player
	{
		public final LMPlayerClient player;
		public final boolean isOwner;
		
		public Player(LMPlayerClient p)
		{
			player = p;
			isOwner = player.equalsPlayer(owner);
		}
		
		public boolean equals(Object o)
		{
			if(o instanceof Player)
				return player.equalsPlayer(((Player)o).player);
			return player.equals(o);
		}
		
		public boolean isOwner()
		{ return owner.equalsPlayer(player); }
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
				LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(player.player.playerID));
			}
			
			refreshWidgets();
		}
		
		public void addMouseOverText(FastList<String> al)
		{
			if(player != null)
			{
				LMPlayerClient p = LMWorldClient.inst.getPlayer(player.player.playerID);
				
				if(p == null) return;
				
				al.add(p.getName());
				if(p.isOnline()) al.add(GREEN + "[" + FTBULang.label_online + "]");
				
				if(!player.isOwner())
				{
					boolean raw1 = p.isFriendRaw(owner);
					boolean raw2 = owner.isFriendRaw(p);
					
					if(raw1 && raw2)
						al.add(GREEN + "[" + FTBULang.label_friend + "]");
					else if(raw1 || raw2)
						al.add((raw1 ? GOLD : BLUE) + "[" + FTBULang.label_pfriend + "]");
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
					FriendStatus status = owner.getStatus(player.player);
					if(status != FriendStatus.NONE) render(icon_status[status.ordinal() - 1]);
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
			LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(selectedPlayer.playerLM.playerID));
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
		
		public static final PlayerAction notes = new PlayerAction(Icons.notes)
		{
			public void onClicked(GuiFriends g)
			{ /* g.mc.displayGuiScreen(new GuiNotes()); */ }
		};
		
		public static final PlayerAction notifications = new PlayerAction(Icons.comment)
		{
			public void onClicked(GuiFriends g)
			{ /* g.mc.displayGuiScreen(new GuiNotifications()); */ }
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
	
	public static class LMPComparator implements Comparator<LMPlayerClient>
	{
		private static final FastMap<String, LMPComparator> map = new FastMap<String, LMPComparator>();
		
		public static void init()
		{
			map.clear();
			Event e = new Event();
			e.add("friends_status", new ByFriendsStatus());
			e.add("name", new ByName());
			e.add("deaths", new ByDeaths());
			e.add("date_joined", new ByJoined());
			e.add("last_seen", new ByLastSeen());
			e.post();
		}
		
		public String translatedName;
		public int listID;
		
		public static class Event extends EventLM
		{
			public void add(String s, LMPComparator c)
			{
				if(!map.keys.contains(s))
				{
					c.listID = map.size();
					c.translatedName = I18n.format("lmp_comparator." + s);
					map.put(s, c);
				}
			}
		}
		
		public int compare(LMPlayerClient o1, LMPlayerClient o2)
		{
			return 0;
		}
		
		public static class ByName extends LMPComparator
		{
			public int compare(LMPlayerClient o1, LMPlayerClient o2)
			{ return o1.getName().compareToIgnoreCase(o2.getName()); }
		}
		
		public static class ByOnlineStatus extends ByName
		{
			public int compare(LMPlayerClient o1, LMPlayerClient o2)
			{
				boolean on0 = o1.isOnline();
				boolean on1 = o2.isOnline();
				
				if(on0 && !on1) return -1;
				if(!on0 && on1) return 1;
				
				return super.compare(o1, o2);
			}
		}
		
		public static class ByFriendsStatus extends ByOnlineStatus
		{
			public int compare(LMPlayerClient o1, LMPlayerClient o2)
			{
				int i = FriendStatus.compare(staticOwner, o1, o2);
				if(i == 0) return super.compare(o1, o2);
				return i;
			}
		}
		
		public static class ByDeaths extends ByName
		{
			public int compare(LMPlayerClient o1, LMPlayerClient o2)
			{
				int i = Integer.compare(o2.deaths, o1.deaths);
				if(i == 0) return super.compare(o1, o2);
				return i;
			}
		}
		
		public static class ByJoined extends ByOnlineStatus
		{
			public int compare(LMPlayerClient o1, LMPlayerClient o2)
			{
				if(o1.firstJoined == 0L) return 1;
				int i = Long.compare(o1.firstJoined, o2.firstJoined);
				if(i == 0) return super.compare(o1, o2);
				return i;
			}
		}
		
		public static class ByLastSeen extends ByOnlineStatus
		{
			public int compare(LMPlayerClient o1, LMPlayerClient o2)
			{
				if(o1.lastSeen == 0L) return 1;
				int i = Long.compare(o1.lastSeen, o2.lastSeen);
				if(i == 0) return super.compare(o1, o2);
				return i;
			}
		}
	}
}