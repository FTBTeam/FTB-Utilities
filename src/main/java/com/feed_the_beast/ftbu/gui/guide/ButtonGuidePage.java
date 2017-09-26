package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbu.api.guide.IGuideGui;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonGuidePage extends Button
{
	public final IGuidePage page;
	public String hover;
	private boolean mouseOver;
	private boolean prevMouseOver = false;
	private boolean isSmall;

	public ButtonGuidePage(GuiBase g, IGuidePage p, boolean small)
	{
		super(0, 0, 0, 0);
		page = p;
		isSmall = small;
		setHeight((p.getIcon().isEmpty() || isSmall) ? 13 : 18);
		updateTitle(g);
	}

	@Override
	public void onClicked(GuiBase gui, MouseButton button)
	{
		GuiHelper.playClickSound();
		((IGuideGui) gui).setSelectedPage(page);
	}

	public void updateTitle(GuiBase gui)
	{
		ITextComponent titleC = page.getDisplayName().createCopy();

		if (gui instanceof IGuideGui && ((IGuideGui) gui).getSelectedPage() == page)
		{
			titleC.getStyle().setBold(true);
		}

		if (mouseOver)
		{
			titleC.getStyle().setUnderlined(true);
		}

		setTitle(titleC.getFormattedText());
		hover = null;
		setWidth(gui.getFont().getStringWidth(getTitle(gui)) + (page.getIcon().isEmpty() ? 0 : height));

		if (width > getParentPanel().width)
		{
			hover = page.getDisplayName().getFormattedText();
		}
	}

	@Override
	public void addMouseOverText(GuiBase gui, List<String> list)
	{
		if (hover != null)
		{
			list.add(hover);
		}
	}

	@Override
	public void renderWidget(GuiBase gui)
	{
		mouseOver = gui.isMouseOver(this);

		if (mouseOver)
		{
			getParentPanel().setOffset(false);
			mouseOver = gui.isMouseOver(getParentPanel());
			getParentPanel().setOffset(true);
		}

		if (prevMouseOver != mouseOver)
		{
			updateTitle(gui);
			prevMouseOver = mouseOver;
		}

		int ay = getAY();
		int ax = getAX();

		if (!page.getIcon().isEmpty())
		{
			GlStateManager.color(1F, 1F, 1F, 1F);
			page.getIcon().draw(ax + 1, ay + 1, isSmall ? 8 : 16, isSmall ? 8 : 16, Color4I.NONE);
			gui.drawString(getTitle(gui), ax + (isSmall ? 13 : 19), ay + (isSmall ? 1 : 6));
		}
		else
		{
			GlStateManager.color(1F, 1F, 1F, 1F);
			gui.drawString(getTitle(gui), ax + 1, ay + 1);
		}

		GlStateManager.color(1F, 1F, 1F, 1F);
	}
}