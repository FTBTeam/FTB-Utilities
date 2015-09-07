package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.Notification;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.player.ClientNotifications;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class PanelPlayerList extends PanelFriendsGui
{
	public static LMPComparator comparator = LMPComparator.FRIENDS_STATUS;
	private static final FastList<LMPlayerClient> tempPlayerList = new FastList<LMPlayerClient>();
	
	public final TextBoxLM searchBox;
	public final SliderLM scrollBar;
	public final ButtonLM buttonSort;
	public final FastList<ButtonPlayer> playerButtons;
	
	public PanelPlayerList(GuiFriends g)
	{
		super(g);
		width = 120;
		
		searchBox = new TextBoxLM(g, 0, 0, width - 18, 16);
		searchBox.charLimit = 20;
		
		scrollBar = new SliderLM(g, width - 17, 16, 16, 0, 8);
		scrollBar.displayMax = 0;
		scrollBar.isVertical = true;
		
		buttonSort = new ButtonLM(g, width - 17, searchBox.posY, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				comparator = (b == 0) ? comparator.next() : comparator.prev();
				refreshWidgets();
			}
		};
		
		playerButtons = new FastList<ButtonPlayer>();
	}
	
	public boolean isEnabled()
	{ return gui.panelPopupMenu == null; }
	
	public void addWidgets()
	{
		add(searchBox);
		add(scrollBar);
		add(buttonSort);
		
		playerButtons.clear();
		
		tempPlayerList.clear();
		tempPlayerList.addAll(LMWorldClient.inst.players);
		tempPlayerList.remove(LMWorldClient.inst.clientPlayer);
		
		if(!searchBox.text.isEmpty())
		{
			FastList<LMPlayerClient> l = new FastList<LMPlayerClient>();
			
			String s = searchBox.text.trim().toLowerCase();
			for(int i = 0; i < tempPlayerList.size(); i++)
			{
				LMPlayerClient p = tempPlayerList.get(i);
				if(p.getName().toLowerCase().contains(s)) l.add(p);
			}
			
			tempPlayerList.clear();
			tempPlayerList.addAll(l);
		}
		
		tempPlayerList.sort(comparator);
		
		playerButtons.add(new ButtonPlayer(this, LMWorldClient.inst.clientPlayer));
		
		width = 0;
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
		
		if(gui.mouseDWheel != 0)
		{
			int newPos = posY + ((gui.mouseDWheel > 0) ? 28 : -28);
			newPos = Math.min(newPos, 0);
			newPos = Math.max(newPos, -size * 21 + height);
			//scroll = Math.max(playerButtons.get(0).getAY() + scroll, 0);
			//if(playerButtons.get(size - 1).getAY() + scroll < (height)) scroll = 0;
			if(posY != newPos)
			{
				ClientNotifications.add(new Notification("scroll", new ChatComponentText("" + (height - newPos) + " : " + gui.getHeight()), 1000));
				posY = newPos;
			}
		}
		
		for(int i = 0; i < size; i++)
			playerButtons.get(i).renderWidget();
	}
}