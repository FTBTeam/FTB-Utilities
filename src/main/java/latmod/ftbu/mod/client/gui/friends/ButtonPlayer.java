package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.EnumChatFormatting;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ButtonPlayer extends ButtonLM
{
	public final Player player;
	public final PanelPlayerList panel;
	
	public ButtonPlayer(PanelPlayerList pl, LMPlayerClient p)
	{
		super(pl.gui, 0, pl.playerButtons.size() * 21, 34 + pl.gui.getFontRenderer().getStringWidth(p.getName()), 20);
		player = new Player(p);
		panel = pl;
	}
	
	public void onButtonPressed(int b)
	{
		if(player != null && player.playerLM != null)
		{
			panel.gui.panelPlayerView.selectedPlayer = new Player(player.playerLM);
			panel.gui.panelPlayerView.selectedPlayer.func_152121_a(MinecraftProfileTexture.Type.SKIN, AbstractClientPlayer.getLocationSkin(player.playerLM.getName()));
			panel.gui.panelPlayerView.selectedPlayer.inventory.currentItem = 0;
			LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(player.playerLM.playerID));
			if(b != 0) panel.gui.panelPopupMenu = new PanelPopupPlayerActions((GuiFriends)gui, gui.mouseX - gui.mainPanel.getAX() + 3, gui.mouseY - gui.mainPanel.getAY() - 3, player.playerLM);
		}
		
		gui.refreshWidgets();
	}
	
	public void addMouseOverText(FastList<String> al)
	{
		//al.add(player.playerLM.getName());
		if(player.playerLM.isOnline()) al.add(EnumChatFormatting.GREEN + "[" + FTBULang.Friends.label_online + "]");
		
		if(!player.isOwner)
		{
			boolean raw1 = player.playerLM.isFriendRaw(LMWorldClient.inst.clientPlayer);
			boolean raw2 = LMWorldClient.inst.clientPlayer.isFriendRaw(player.playerLM);
			
			if(raw1 && raw2)
				al.add(EnumChatFormatting.GREEN + "[" + FTBULang.Friends.label_friend + "]");
			else if(raw1 || raw2)
				al.add((raw1 ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE) + "[" + FTBULang.Friends.label_pfriend + "]");
		}
		
		if(player.playerLM.clientInfo != null && !player.playerLM.clientInfo.isEmpty())
			al.addAll(player.playerLM.clientInfo);
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		
		if(ay + height < 0 || ay > gui.height) return;
		
		gui.drawBlankRect(ax, ay, width, height, (parentPanel.isEnabled() && mouseOver()) ? 0x66FFFFFF : 0x66666666);
		
		boolean raw1 = player.playerLM.isFriendRaw(LMWorldClient.inst.clientPlayer);
		boolean raw2 = LMWorldClient.inst.clientPlayer.isFriendRaw(player.playerLM);
		
		int statusCol = 0xFF000000;
		if(raw1 && raw2) statusCol = 0xFF2EBD2E;
		else if(raw1 || raw2) statusCol = raw1 ? 0xFFE0BE00 : 0xFF00B6ED;
		gui.drawBlankRect(ax + 1, ay + 1, 18, 18, statusCol);
		
		GuiLM.drawPlayerHead(player.playerLM.getName(), ax + 2, ay + 2, 16, 16, gui.getZLevel());
		if(player.playerLM.isOnline())
		{
			gui.drawBlankRect(ax + width - 8, ay, 8, 8, 0xFF00BB00);
			gui.drawBlankRect(ax + width - 7, ay + 1, 6, 6, 0xFF00FF00);
		}
		
		gui.getFontRenderer().drawString(player.playerLM.getName(), ax + 22, ay + 6, 0xFFFFFFFF);
		
		/*FIXME: GuiLM.drawPlayerHead(player.playerLM.getName(), gui.getPosX(posX + 1), gui.getPosY(posY + 1), 16, 16, gui.getZLevel());
		
		if(player.playerLM.isOnline()) render(GuiIcons.online);
		
		if(!player.isOwner)
		{
			FriendStatus status = LMWorldClient.inst.clientPlayer.getStatus(player.playerLM);
			if(status != FriendStatus.NONE) render(GuiFriends.icon_status[status.ordinal() - 1]);
		}
		*/
	}
}