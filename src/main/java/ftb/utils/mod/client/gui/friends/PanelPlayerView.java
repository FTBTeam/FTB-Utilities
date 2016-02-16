package ftb.utils.mod.client.gui.friends;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.friends.LMWorldSP;
import ftb.utils.mod.client.FTBUClient;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

public class PanelPlayerView extends PanelFriendsGui
{
	public Player selectedPlayer;
	
	public PanelPlayerView(GuiFriends g)
	{
		super(g);
		selectedPlayer = new Player(LMWorldSP.inst.clientPlayer);
	}
	
	public void addWidgets()
	{
	}
	
	public void renderWidget()
	{
		FTBUClient.hide_armor_fg.set(mouseOver() && Mouse.isButtonDown(1));
		
		if(FTBUClient.hide_armor_fg.get())
		{
			for(int i = 0; i < 4; i++)
				selectedPlayer.inventory.armorInventory[i] = null;
		}
		else
		{
			EntityPlayer ep1 = selectedPlayer.playerLM.getPlayer();
			
			if(ep1 != null)
			{
				selectedPlayer.inventory.mainInventory = ep1.inventory.mainInventory.clone();
				selectedPlayer.inventory.armorInventory = ep1.inventory.armorInventory.clone();
				selectedPlayer.inventory.currentItem = ep1.inventory.currentItem;
			}
			else
			{
				for(int i = 0; i < 4; i++)
					selectedPlayer.inventory.armorInventory[i] = selectedPlayer.playerLM.lastArmor.get(i);
				selectedPlayer.inventory.mainInventory[0] = selectedPlayer.playerLM.lastArmor.get(4);
				selectedPlayer.inventory.currentItem = 0;
			}
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		int playerX = getAX() + width / 2;
		int size = 120;
		int playerY = height / 2 + size - 4;
		FTBLibClient.setTexture(selectedPlayer.getLocationSkin());
		GlStateManager.translate(0F, 0F, 100F);
		GuiInventory.drawEntityOnScreen(playerX, playerY, size, playerX - gui.mouse().x, playerY - (size + (size / 1.625F)) - gui.mouse().y, selectedPlayer);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}