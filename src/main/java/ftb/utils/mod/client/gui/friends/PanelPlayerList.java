package ftb.utils.mod.client.gui.friends;

import ftb.lib.api.client.GlStateManager;
import ftb.lib.api.friends.*;
import ftb.utils.mod.client.FTBUClient;
import ftb.utils.world.*;
import org.lwjgl.input.Mouse;

import java.util.*;

public class PanelPlayerList extends PanelFriendsGui
{
	private static final ArrayList<LMPlayer> tempPlayerList = new ArrayList<>();
	
	public final ArrayList<ButtonPlayer> playerButtons;
	
	public PanelPlayerList(GuiFriends g)
	{
		super(g);
		width = 120;
		
		playerButtons = new ArrayList<>();
	}
	
	public boolean isEnabled()
	{ return gui.panelPopupMenu == null; }
	
	public void addWidgets()
	{
		tempPlayerList.clear();
		tempPlayerList.addAll(LMWorldClient.inst.playerMap.values());
		
		tempPlayerList.remove(LMWorldClient.inst.clientPlayer);
		
		if(FTBUClient.sort_friends_az.get()) Collections.sort(tempPlayerList, LMPNameComparator.instance);
		else Collections.sort(tempPlayerList, new LMPStatusComparator(LMWorldClient.inst.clientPlayer));
		
		String name = gui.searchBox.getText().toLowerCase();
		if(name.trim().isEmpty()) name = null;
		
		playerButtons.clear();
		if(name == null) playerButtons.add(new ButtonPlayer(this, LMWorldClient.inst.clientPlayer));
		width = (name == null) ? playerButtons.get(0).width : 0;
		
		for(LMPlayer p : tempPlayerList)
		{
			if(name == null || p.getProfile().getName().toLowerCase().contains(name))
			{
				ButtonPlayer b = new ButtonPlayer(this, p.toPlayerSP());
				playerButtons.add(b);
				width = Math.max(width, b.width);
			}
		}
		
		for(ButtonPlayer b : playerButtons)
		{
			b.width = width;
			add(b);
		}
	}
	
	public void renderWidget()
	{
		int size = playerButtons.size();
		if(size == 0) return;
		
		if(gui.mouse().x <= getAX() + width)
		{
			int scroll = 0;
			
			if(gui.mouse().dwheel != 0) scroll = ((gui.mouse().dwheel > 0) ? 28 : -28);
			
			if(Mouse.isButtonDown(0)) scroll += gui.mouse().dy;
			
			//if(Mouse.isButtonDown(1))
			//	scroll -= (int)((guiGuide.mouseY - guiGuide.lastClickY) * 0.1D);
			
			if(scroll != 0)
			{
				int newPos = posY + scroll;
				newPos = Math.min(newPos, 0);
				newPos = Math.max(newPos, (height - 0) - size * 21);
				if(posY != newPos) posY = newPos;
			}
		}
		
		if(playerButtons.size() * 21 < height) posY = 0;
		
		for(int i = 0; i < size; i++)
			playerButtons.get(i).renderWidget();
		
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
}