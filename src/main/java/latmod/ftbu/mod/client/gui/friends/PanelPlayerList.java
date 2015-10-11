package latmod.ftbu.mod.client.gui.friends;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.world.*;
import latmod.lib.FastList;

@SideOnly(Side.CLIENT)
public class PanelPlayerList extends PanelFriendsGui
{
	private static final FastList<LMPlayerClient> tempPlayerList = new FastList<LMPlayerClient>();
	
	public final FastList<ButtonPlayer> playerButtons;
	
	public PanelPlayerList(GuiFriends g)
	{
		super(g);
		width = 120;
		
		playerButtons = new FastList<ButtonPlayer>();
	}
	
	public boolean isEnabled()
	{ return gui.panelPopupMenu == null; }
	
	public void addWidgets()
	{
		tempPlayerList.clear();
		tempPlayerList.addAll(LMWorldClient.inst.players);
		tempPlayerList.remove(LMWorldClient.inst.clientPlayer);
		tempPlayerList.sort(LMPNameComparator.instance);
		
		playerButtons.clear();
		playerButtons.add(new ButtonPlayer(this, LMWorldClient.inst.clientPlayer));
		
		width = playerButtons.get(0).width;
		for(LMPlayerClient p : tempPlayerList)
		{
			ButtonPlayer b = new ButtonPlayer(this, p);
			playerButtons.add(b);
			width = Math.max(width, b.width);
		}
		
		for(ButtonPlayer b : playerButtons)
		{ b.width = width; add(b); }
	}
	
	public void renderWidget()
	{
		int size = playerButtons.size();
		if(size == 0) return;
		
		if(gui.mouseX <= getAX() + width)
		{
			int scroll = 0;
			
			if(gui.mouseDWheel != 0)
				scroll = ((gui.mouseDWheel > 0) ? 28 : -28);
			
			if(Mouse.isButtonDown(0))
				scroll += gui.mouseDY;
			
			if(scroll != 0)
			{
				int newPos = posY + scroll;
				newPos = Math.min(newPos, 0);
				newPos = Math.max(newPos, (height - 0) - size * 21);
				if(posY != newPos) posY = newPos;
			}
		}
		
		if(playerButtons.size() * 21 < height)
			posY = 0;
		
		for(int i = 0; i < size; i++)
			playerButtons.get(i).renderWidget();
	}
}