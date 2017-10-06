package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.util.MapUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public interface IGuidePage extends IStringSerializable
{
	Comparator<Map.Entry<String, IGuidePage>> COMPARATOR = (o1, o2) ->
	{
		int i = o1.getValue().getType().compareTo(o2.getValue().getType());
		return i == 0 ? o1.getValue().getDisplayName().getUnformattedText().compareToIgnoreCase(o2.getValue().getDisplayName().getUnformattedText()) : i;
	};

	default GuideType getType()
	{
		return GuideType.OTHER;
	}

	void fromJson(JsonObject json);

	@Nullable
	IGuidePage getParent();

	default String getFullId()
	{
		IGuidePage parent = getParent();
		String parentId = parent == null ? "" : parent.getFullId();
		return parentId.isEmpty() ? getName() : (parentId + '.' + getName());
	}

	ITextComponent getDisplayName();

	@Nullable
	ITextComponent getTitle();

	IGuidePage setTitle(@Nullable ITextComponent t);

	void println(@Nullable Object o);

	List<IGuideTextLine> getText();

	Map<String, IGuidePage> getChildren();

	default IGuidePage addSub(IGuidePage c)
	{
		getChildren().put(c.getName(), c);
		return c;
	}

	IGuidePage getSub(String id);

	@Nullable
	default IGuidePage getSubRaw(String id)
	{
		int i = id.indexOf('.');

		if (i >= 0)
		{
			IGuidePage page = getChildren().get(id.substring(0, i));
			return page == null ? null : page.getSubRaw(id.substring(i + 1));
		}
		else
		{
			return getChildren().get(id);
		}
	}

	default void clear()
	{
		getText().clear();
		getChildren().clear();
	}

	default void cleanup()
	{
		getChildren().values().forEach(IGuidePage::cleanup);
		getChildren().values().removeIf(IGuidePage::isEmpty);
	}

	default boolean isEmpty()
	{
		if (!getChildren().isEmpty())
		{
			return false;
		}

		for (IGuideTextLine line : getText())
		{
			if (line != null && !line.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	default void sort(boolean tree)
	{
		MapUtils.sortMap(getChildren(), COMPARATOR);

		if (tree)
		{
			getChildren().values().forEach(page -> page.sort(true));
		}
	}

	default void copyFrom(IGuidePage c)
	{
		for (IGuideTextLine l : c.getText())
		{
			getText().add(l == null ? null : l.copy(this));
		}

		for (Map.Entry<String, IGuidePage> entry : c.getChildren().entrySet())
		{
			getSub(entry.getKey()).copyFrom(entry.getValue());
		}
	}

	List<SpecialGuideButton> getSpecialButtons();

	default IGuidePage addSpecialButton(SpecialGuideButton button)
	{
		getSpecialButtons().add(button);
		return this;
	}

	IGuidePage setIcon(Icon icon);

	default Icon getIcon()
	{
		return Icon.EMPTY;
	}

	Widget createWidget(GuiBase gui);

	@Nullable
	IGuideTextLine createLine(@Nullable JsonElement json);
}