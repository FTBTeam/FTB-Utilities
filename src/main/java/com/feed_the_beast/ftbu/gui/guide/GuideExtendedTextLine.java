package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.gui.ExtendedTextField;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.google.gson.JsonElement;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class GuideExtendedTextLine extends EmptyGuidePageLine
{
	public final ITextComponent textComponent;

	public GuideExtendedTextLine(@Nullable ITextComponent cc)
	{
		textComponent = cc;
	}

	public GuideExtendedTextLine(JsonElement json)
	{
		textComponent = JsonUtils.deserializeTextComponent(json);
	}

	@Override
	public String getUnformattedText()
	{
		return textComponent == null ? "" : textComponent.getUnformattedText();
	}

	@Override
	public Widget createWidget(GuiBase gui, Panel parent)
	{
		return new ExtendedTextField(0, 0, parent.width, -1, gui.getFont(), textComponent);
	}

	@Override
	public GuideExtendedTextLine copy(IGuidePage page)
	{
		return new GuideExtendedTextLine(textComponent.createCopy());
	}
}