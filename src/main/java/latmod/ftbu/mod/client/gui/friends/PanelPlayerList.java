package latmod.ftbu.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;

@SideOnly(Side.CLIENT)
public class PanelPlayerList extends PanelFriendsGui
{
	public static LMPComparator comparator = LMPComparator.FRIENDS_STATUS;
	private static final FastList<LMPlayerClient> tempPlayerList = new FastList<LMPlayerClient>();
	
	public final SliderLM scrollBar;
	public final ButtonLM buttonSort;
	public final FastList<ButtonPlayer> playerButtons;
	
	public PanelPlayerList(GuiFriends g)
	{
		super(g);
		width = 120;
		
		scrollBar = new SliderLM(g, 0, 0, 16, 0, 8)
		{
			public boolean isEnabled()
			{ return parentPanel.mouseOver() || mouseOver(); }
		};
		scrollBar.displayMax = 0;
		scrollBar.isVertical = true;
		
		buttonSort = new ButtonLM(g, 0, 0, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				gui.playClickSound();
				comparator = (b == 0) ? comparator.next() : comparator.prev();
				refreshWidgets();
			}
			
			public void addMouseOverText(FastList<String> l)
			{
				l.add(title);
				l.add(comparator.translatedName);
			}
		};
		
		buttonSort.title = FTBU.mod.translateClient("button.lmp_comparator");
		
		playerButtons = new FastList<ButtonPlayer>();
	}
	
	public boolean isEnabled()
	{ return gui.panelPopupMenu == null; }
	
	public void addWidgets()
	{
		//add(scrollBar);
		add(buttonSort);
		
		tempPlayerList.clear();
		tempPlayerList.addAll(LMWorldClient.inst.players);
		tempPlayerList.remove(LMWorldClient.inst.clientPlayer);
		
		/*
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
		}*/
		
		tempPlayerList.sort(comparator);
		
		playerButtons.clear();
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
		
		if(gui.mouseDWheel != 0 && gui.mouseX <= getAX() + width)
		{
			int newPos = posY + ((gui.mouseDWheel > 0) ? 28 : -28);
			newPos = Math.min(newPos, 0);
			newPos = Math.max(newPos, (height - 0) - size * 21);
			if(posY != newPos) posY = newPos;
		}
		
		for(int i = 0; i < size; i++)
			playerButtons.get(i).renderWidget();
		
		buttonSort.render(GuiIcons.sort);
	}
}