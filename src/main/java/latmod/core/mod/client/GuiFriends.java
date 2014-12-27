package latmod.core.mod.client;

import java.util.ArrayList;

import latmod.core.*;
import latmod.core.event.LMPlayerEvent;
import latmod.core.gui.*;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiFriends extends GuiLM
{
	public static final ResourceLocation texPlayers = LC.mod.getLocation("textures/gui/players.png");
	public static final ResourceLocation texGroups = LC.mod.getLocation("textures/gui/groups.png");
	
	public static final TextureCoords icon_friend = new TextureCoords(texPlayers, 163, 0);
	public static final TextureCoords icon_pending = new TextureCoords(texPlayers, 181, 0);
	public static final TextureCoords icon_offline = new TextureCoords(texPlayers, 163, 18);
	
	public TextBoxLM searchBox;
	public ButtonLM buttonClear, buttonSave, buttonPrevPage, buttonNextPage;
	public ButtonPlayer buttonOwner;
	public ButtonPlayer[] buttonOtherPlayers;
	public int page = 0;
	
	public LMPlayer owner;
	
	public GuiFriends(EntityPlayer ep)
	{
		super(new ContainerEmpty(ep, null), texPlayers);
		owner = LMPlayer.getPlayer(ep);
		
		xSize = 163;
		ySize = 184;
		
		widgets.add(searchBox = new TextBoxLM(this, 25, 5, 94, 18));
		searchBox.charLimit = 15;
		
		widgets.add(buttonClear = new ButtonLM(this, 121, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
			}
		});
		
		widgets.add(buttonSave = new ButtonLM(this, 140, 6, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
			}
		});
		
		widgets.add(buttonPrevPage = new ButtonLM(this, 7, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				page--;
			}
		});
		
		widgets.add(buttonNextPage = new ButtonLM(this, 121, 159, 35, 16)
		{
			public void onButtonPressed(int b)
			{
				playClickSound();
				page++;
			}
		});
		
		widgets.add(buttonOwner = new ButtonPlayer(this, -1, 6, 5));
		buttonOwner.update(owner.username, 0, false);
		
		buttonOtherPlayers = new ButtonPlayer[8 * 7];
		
		for(int i = 0; i < buttonOtherPlayers.length; i++)
			widgets.add(buttonOtherPlayers[i] = new ButtonPlayer(this, i, 6 + (i % 8) * 19, 25 + (i / 8) * 19));
		
		buttonOtherPlayers[0].update("Baphometis", 1, true);
		buttonOtherPlayers[1].update("HaniiPuppy", 1, true);
		buttonOtherPlayers[2].update("tfox83", 2, true);
		buttonOtherPlayers[3].update("_Drealock_", 2, false);
		buttonOtherPlayers[4].update("Ordo1776", 0, false);
		buttonOtherPlayers[5].update("annijamic", 2, false);
		buttonOtherPlayers[6].update("Latvian_Modder", 0, false);
		buttonOtherPlayers[7].update("armixmen", 1, false);
		buttonOtherPlayers[8].update("happy_aivita", 1, false);
	}
	
	public void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		super.drawGuiContainerBackgroundLayer(f, mx, my);
		
		buttonOwner.renderHead();
		for(int i = 0; i < buttonOtherPlayers.length; i++)
			buttonOtherPlayers[i].renderHead();
		
		setTexture(texture);
	}
	
	public void drawScreen(int mx, int my, float f)
	{
		super.drawScreen(mx, my, f);
		
		searchBox.render(30, 10, 0xFFA7A7A7);
		
		ArrayList<String> al = new ArrayList<String>();
		
		if(buttonClear.mouseOver(mx, my))
		{
			al.add(EnumChatFormatting.RED + "Clear All Friends");
			al.add("Double click this button");
			al.add("with Shift key down");
		}
		if(buttonSave.mouseOver(mx, my)) al.add(EnumChatFormatting.GREEN + "Save");
		if(buttonPrevPage.mouseOver(mx, my)) al.add("Prev Page");
		if(buttonNextPage.mouseOver(mx, my)) al.add("Next Page");
		if(buttonOwner.mouseOver(mx, my)) al.add("");
		
		if(!al.isEmpty()) drawHoveringText(al, mx, my, fontRendererObj);
		
		drawCenteredString(fontRendererObj, (page + 1) + " / " + 10, guiLeft + 81, guiTop + 163, 0xFF5A5A5A);
	}
	
	public void initGui()
	{
		super.initGui();
		LatCoreMC.EVENT_BUS.register(this);
	}
	
	public void onGuiClosed()
	{
		LatCoreMC.EVENT_BUS.unregister(this);
		super.onGuiClosed();
	}
	
	@SubscribeEvent
	public void onClientEvent(LMPlayerEvent.DataChanged e)
	{
	}
	
	public class ButtonPlayer extends ButtonLM
	{
		public int id;
		public String username = null;
		public int friend = 0;
		public boolean online = false;
		
		public ButtonPlayer(GuiLM g, int i, int x, int y)
		{
			super(g, x, y, 18, 18);
			id = i;
		}
		
		public void update(String s, int f, boolean b)
		{
			username = s;
			friend = f;
			online = b;
		}
		
		public void onButtonPressed(int b)
		{
			if(id != -1 && username != null)
			{
				LatCoreMC.printChat(GuiFriends.this.container.player, username);
			}
		}
		
		public void renderHead()
		{ if(username != null) drawPlayerHead(username, GuiFriends.this.guiLeft + posX + 1, GuiFriends.this.guiTop + posY + 1, 16, 16); }
	}
}