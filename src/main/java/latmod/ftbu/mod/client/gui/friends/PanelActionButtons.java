package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.client.FTBULang;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.client.minimap.Waypoints;

import org.lwjgl.opengl.GL11;

public class PanelActionButtons extends PanelLM<ButtonAction>
{
	public final GuiFriends gui;
	public final LMPlayerClient playerLM;
	
	public PanelActionButtons(GuiFriends g, int x, int y, LMPlayerClient p)
	{
		super(g, x, y, 0, 18);
		gui = g;
		playerLM = p;
	}
	
	public void add(PlayerAction a, String s)
	{
		ButtonAction b = new ButtonAction(gui, height, a, s);
		add(b);
		width = Math.max(width, b.width);
		height += 11;
	}
	
	public void addWidgets()
	{
		width = 0;
		height = 0;
		
		if(playerLM.equalsPlayer(LMWorldClient.inst.clientPlayer))
		{
			add(PlayerAction.settings, FTBULang.client_config);
			add(PlayerAction.notifications, FTBULang.Friends.notifications);
			add(PlayerAction.waypoints, Waypoints.clientConfig.getIDS());
			add(PlayerAction.minimap, FTBULang.Friends.claimed_chunks);
			//actionButtons.add(new ActionButton(this, PlayerAction.notes, "Notes"));
		}
		else
		{
			boolean isFriend = LMWorldClient.inst.clientPlayer.isFriendRaw(playerLM);
			add(PlayerAction.friend_toggle, isFriend ? FTBULang.Friends.button_rem_friend : FTBULang.Friends.button_add_friend);
			
			if(!isFriend && playerLM.isFriendRaw(LMWorldClient.inst.clientPlayer))
				add(PlayerAction.friend_deny, FTBULang.Friends.button_deny_friend);
		}
		
		for(ButtonAction b : widgets)
			b.width = width;
	}

	public void render()
	{
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		int ax = getAX();
		int ay = getAY();
		
		GuiLM.drawRect(ax - 1, ay, ax + width + 1, ay + height, 0xFF666666);
		GL11.glBegin(GL11.GL_LINES);
		
		GL11.glColor4f(0.2F, 0.2F, 0.2F, 1F);
		
		for(int i = 0; i <= widgets.size(); i++)
		{
			GL11.glVertex2f(ax - 1, ay + i * 11);
			GL11.glVertex2f(ax + 1 + width, ay + i * 11);
		}
		
		GL11.glVertex2f(ax + 1 + width, ay);
		GL11.glVertex2f(ax + 1 + width, ay + height);
		
		GL11.glVertex2f(ax - 1, ay + height);
		GL11.glVertex2f(ax - 1, ay);
		
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_BLEND);
		
		for(ButtonAction b : widgets)
			b.render();
	}
	
	public void mousePressed(int b)
	{
		super.mousePressed(b);
		if(b == 0) GuiFriends.actionButtonPanel = null;
	}
}