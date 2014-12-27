package latmod.core.mod.client;

import java.util.UUID;

import latmod.core.*;
import latmod.core.event.LMPlayerEvent;
import latmod.core.gui.*;
import latmod.core.mod.LC;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
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
	private long clearMillis = 0L;
	public final LMPlayer owner;
	public final FastList<Player> players = new FastList<Player>();
	public boolean changed = false;
	
	public GuiFriends(EntityPlayer ep)
	{
		super(new ContainerEmpty(ep, null), texPlayers);
		owner = LMPlayer.getPlayer(ep);
		
		clearMillis = Minecraft.getSystemTime();
		
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
				if(isShiftKeyDown())
				{
					if(Minecraft.getSystemTime() - clearMillis < 300)
					{
						playClickSound();
						LC.proxy.displayMessage(EnumChatFormatting.RED + "Friends cleared", "", new ItemStack(Items.iron_sword), 2000L);
					}
					else clearMillis = Minecraft.getSystemTime();
				}
			}
		});
		
		widgets.add(buttonSave = new ButtonLM(this, 140, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				changed = true;
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
		});
		
		widgets.add(buttonPrevPage = new ButtonLM(this, 7, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page--;
				playClickSound();
				updateButtons();
			}
		});
		
		widgets.add(buttonNextPage = new ButtonLM(this, 121, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				page++;
				playClickSound();
				updateButtons();
			}
		});
		
		widgets.add(pbOwner = new ButtonPlayer(this, -1, 6, 5));
		pbOwner.setPlayer(new Player(owner));
		
		pbPlayers = new ButtonPlayer[7 * 8];
		
		for(int i = 0; i < pbPlayers.length; i++)
			widgets.add(pbPlayers[i] = new ButtonPlayer(this, i, 6 + (i % 8) * 19, 25 + (i / 8) * 19));
		
		updateButtons();
	}
	
	public int maxPages()
	{ return (players.size() / pbPlayers.length) + 1; }
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		
		pbOwner.render();
		for(int i = 0; i < pbPlayers.length; i++)
			pbPlayers[i].render();
		
		setTexture(texture);
	}
	
	public void drawScreen(int mx, int my, float f)
	{
		super.drawScreen(mx, my, f);
		
		searchBox.render(30, 10, 0xFFA7A7A7);
		
		FastList<String> al = new FastList<String>();
		
		if(buttonClear.mouseOver(mx, my))
		{
			al.add(EnumChatFormatting.RED + "Clear All Friends");
			al.add("Double click this button");
			al.add("with Shift key down");
		}
		if(buttonSave.mouseOver(mx, my)) al.add(EnumChatFormatting.GREEN + "Save");
		if(buttonPrevPage.mouseOver(mx, my)) al.add("Prev Page");
		if(buttonNextPage.mouseOver(mx, my)) al.add("Next Page");
		if(pbOwner.mouseOver(mx, my)) pbOwner.addInfo(al);
		
		for(int i = 0; i < pbPlayers.length; i++)
			if(pbPlayers[i].mouseOver(mx, my)) pbPlayers[i].addInfo(al);
		
		if(!al.isEmpty()) drawHoveringText(al, mx, my, fontRendererObj);
		
		drawCenteredString(fontRendererObj, (page + 1) + " / " + maxPages(), guiLeft + 81, guiTop + 163, 0xFF5A5A5A);
	}
	
	public void initGui()
	{
		super.initGui();
		LatCoreMC.EVENT_BUS.register(this);
	}
	
	public void onGuiClosed()
	{
		LatCoreMC.EVENT_BUS.unregister(this);
		if(changed)
		LC.proxy.displayMessage(EnumChatFormatting.GREEN + "Players saved", "", new ItemStack(Items.diamond), 2000L);
		super.onGuiClosed();
	}
	
	@SubscribeEvent
	public void onClientEvent(LMPlayerEvent.DataChanged e)
	{ updateButtons(); }
	
	public void updateButtons()
	{
		players.clear();
		
		for(int i = 0; i < LMPlayer.list.size(); i++)
		{
			LMPlayer p = LMPlayer.list.get(i);
			
			if(!p.equals(owner))
				players.add(new Player(p));
		}
		
		/*
		players.add(new Player("Baphometis", true));
		players.add(new Player("HaniiPuppy", true));
		players.add(new Player("tfox83", true));
		players.add(new Player("_Draelock_", false));
		players.add(new Player("Ordo1776", false));
		players.add(new Player("annijamic", false));
		players.add(new Player("armixmen", false));
		players.add(new Player("happy_aivita", false));
		
		players.add(new Player("UnwiseDeadkilla", false));
		players.add(new Player("omaxkpo", true));
		players.add(new Player("Tatsu011", false));
		players.add(new Player("Skimphy", false));
		players.add(new Player("Maelstraz", false));
		players.add(new Player("polraudio", false));
		players.add(new Player("Calst85", true));
		players.add(new Player("gaffercake", false));
		
		players.add(new Player("Stickyricky24", false));
		players.add(new Player("Gekkarto", false));
		players.add(new Player("ObamaStoleMyKFC", true));
		players.add(new Player("LoveMakerr", false));
		players.add(new Player("wolfofmibu66", false));
		players.add(new Player("dapandaman", false));
		players.add(new Player("RetroMaster2011", false));
		players.add(new Player("orranmargath", false));
		
		players.add(new Player("JAY247", false));
		players.add(new Player("vintagepaper", true));
		*/
		
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
		
		public Player(String username, boolean online)
		{
			this(new LMPlayer(new UUID(0L, 0L), username));
			//if(ParticleHelper.rand.nextBoolean());
			//player.friends.add(owner);
			player.setOnline(online);
		}
		
		public int compareTo(Player o)
		{
			int s = getStatus();
			int s1 = o.getStatus();
			
			if(s == s1)
			{
				boolean on = player.isOnline();
				boolean on1 = o.player.isOnline();
				
				if(on == on1)
				{
					String u = player.getDisplayName();
					String u1 = o.player.getDisplayName();
					
					return u.compareTo(u1);
				}
				
				return Boolean.compare(on1, on);
			}
			
			return Integer.compare(s, s1);
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
			boolean b1 = owner.isFriend(player);
			boolean b2 = player.isFriend(owner);
			
			if(!b1 && !b2) return 0;
			if(b1 && b2) return 1;
			if(b1) return 2;
			if(b2) return 3;
			return 0;
		}
	}
	
	public class ButtonPlayer extends ButtonLM
	{
		public Player player;
		
		public ButtonPlayer(GuiLM g, int i, int x, int y)
		{
			super(g, x, y, 18, 18);
		}
		
		public void setPlayer(Player p)
		{ player = p; }
		
		public void onButtonPressed(int b)
		{
			if(player != null && !player.isOwner())
			{
				LC.proxy.displayMessage("Click!", player.player.getDisplayName(), new ItemStack(Items.skull, 1, 3), 500L);
			}
		}
		
		public void addInfo(FastList<String> al)
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
						al.add(EnumChatFormatting.GREEN + "Friends");
						// Add other groups //
					}
				}
			}
		}
		
		public void render()
		{
			if(player != null)
			{
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