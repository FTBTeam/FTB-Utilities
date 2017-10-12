package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbu.api.guide.IGuideGui;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
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

	public ButtonGuidePage(GuiBase gui, IGuidePage p, boolean small)
	{
		super(gui, 0, 0, 0, 0);
		page = p;
		isSmall = small;
		setHeight((p.getIcon().isEmpty() || isSmall) ? 13 : 18);
		updateTitle();
	}

	@Override
	public void onClicked(MouseButton button)
	{
		GuiHelper.playClickSound();
		((IGuideGui) gui).setSelectedPage(page);
	}

	public void updateTitle()
	{
		ITextComponent titleC = page.getDisplayName().createCopy();

		if (mouseOver)
		{
			titleC.getStyle().setUnderlined(true);
		}

		setTitle(titleC.getFormattedText());
		hover = null;
		setWidth(gui.getStringWidth(getTitle()) + (page.getIcon().isEmpty() ? 0 : height));

		if (width > getParentPanel().width)
		{
			hover = page.getDisplayName().getFormattedText();
		}
	}

	@Override
	public void addMouseOverText(List<String> list)
	{
		if (hover != null)
		{
			list.add(hover);
		}
	}

	@Override
	public void renderWidget()
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
			updateTitle();
			prevMouseOver = mouseOver;
		}

		int ay = getAY();
		int ax = getAX();
		boolean selected = gui instanceof IGuideGui && ((IGuideGui) gui).getSelectedPage() == page;

		if (!page.getIcon().isEmpty())
		{
			page.getIcon().draw(ax + 1, ay + 1, isSmall ? 8 : 16, isSmall ? 8 : 16);

			if (selected)
			{
				gui.drawString(getTitle(), ax + (isSmall ? 14 : 20), ay + (isSmall ? 2 : 7), gui.getTheme().getContentColor().mutable().addBrightness(-30), 0);
			}

			gui.drawString(getTitle(), ax + (isSmall ? 13 : 19), ay + (isSmall ? 1 : 6));
		}
		else
		{
			if (selected)
			{
				gui.drawString(getTitle(), ax + 2, ay + 2, gui.getTheme().getContentColor().mutable().addBrightness(-30), 0);
			}

			gui.drawString(getTitle(), ax + 1, ay + 1);
		}
	}
}