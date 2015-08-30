package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.EnumChatFormatting;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class ButtonPlayer extends ButtonLM
{
	public Player player = null;
	
	public ButtonPlayer(GuiFriends g, int i, int x, int y)
	{ super(g, x, y, 18, 18); }
	
	public boolean isEnabled()
	{ return GuiFriends.actionButtonPanel == null; }
	
	public void setPlayer(Player p)
	{ player = p; }
	
	public void onButtonPressed(int b)
	{
		if(player != null && player.player != null)
		{
			GuiFriends.selectedPlayer = new LMClientPlayer(player.player);
			GuiFriends.selectedPlayer.func_152121_a(MinecraftProfileTexture.Type.SKIN, AbstractClientPlayer.getLocationSkin(GuiFriends.selectedPlayer.playerLM.getName()));
			GuiFriends.selectedPlayer.inventory.currentItem = 0;
			LMNetHelper.sendToServer(new MessageLMPlayerRequestInfo(player.player.playerID));
			
			if(b != 0)
				GuiFriends.actionButtonPanel = new PanelActionButtons((GuiFriends)gui, gui.mouseX - gui.mainPanel.getAX(), gui.mouseY - gui.mainPanel.getAY(), player.player);
		}
		
		gui.refreshWidgets();
	}
	
	public void addMouseOverText(FastList<String> al)
	{
		if(player != null)
		{
			LMPlayerClient p = LMWorldClient.inst.getPlayer(player.player.playerID);
			
			if(p == null) return;
			
			al.add(p.getName());
			if(p.isOnline()) al.add(EnumChatFormatting.GREEN + "[" + FTBULang.Friends.label_online + "]");
			
			if(!player.isOwner)
			{
				boolean raw1 = p.isFriendRaw(LMWorldClient.inst.clientPlayer);
				boolean raw2 = LMWorldClient.inst.clientPlayer.isFriendRaw(p);
				
				if(raw1 && raw2)
					al.add(EnumChatFormatting.GREEN + "[" + FTBULang.Friends.label_friend + "]");
				else if(raw1 || raw2)
					al.add((raw1 ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE) + "[" + FTBULang.Friends.label_pfriend + "]");
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
			
			GuiLM.drawPlayerHead(player.player.getName(), gui.getPosX(posX + 1), gui.getPosY(posY + 1), 16, 16, gui.getZLevel());
			
			if(player.player.isOnline()) render(GuiIcons.online);
			
			if(!player.isOwner)
			{
				FriendStatus status = LMWorldClient.inst.clientPlayer.getStatus(player.player);
				if(status != FriendStatus.NONE) render(GuiFriends.icon_status[status.ordinal() - 1]);
			}
		}
	}
}