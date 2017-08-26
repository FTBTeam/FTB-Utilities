package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.gui.IDrawableObject;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.client.CombinedIcon;
import com.feed_the_beast.ftbl.lib.client.DrawableItem;
import com.feed_the_beast.ftbl.lib.client.ImageProvider;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftbl.lib.gui.SimpleButton;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiEditConfig;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiSelectors;
import com.feed_the_beast.ftbl.lib.gui.misc.IGuiFieldCallback;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.net.MessageEditNBTResponse;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class GuiEditNBT extends GuiBase
{
	private static IDrawableObject getIcon(String name)
	{
		return new CombinedIcon(Button.ICON_BACKGROUND, ImageProvider.get(FTBUFinals.MOD_ID + ":textures/gui/nbt/" + name + ".png"));
	}

	public static final IDrawableObject NBT_BYTE = getIcon("byte");
	public static final IDrawableObject NBT_SHORT = getIcon("short");
	public static final IDrawableObject NBT_INT = getIcon("int");
	public static final IDrawableObject NBT_LONG = getIcon("long");
	public static final IDrawableObject NBT_FLOAT = getIcon("float");
	public static final IDrawableObject NBT_DOUBLE = getIcon("double");
	public static final IDrawableObject NBT_STRING = getIcon("string");
	public static final IDrawableObject NBT_LIST = getIcon("list");
	public static final IDrawableObject NBT_LIST_CLOSED = getIcon("list_closed");
	public static final IDrawableObject NBT_LIST_OPEN = getIcon("list_open");
	public static final IDrawableObject NBT_MAP = getIcon("map");
	public static final IDrawableObject NBT_MAP_CLOSED = getIcon("map_closed");
	public static final IDrawableObject NBT_MAP_OPEN = getIcon("map_open");
	public static final IDrawableObject NBT_BYTE_ARRAY = getIcon("byte_array");
	public static final IDrawableObject NBT_BYTE_ARRAY_CLOSED = getIcon("byte_array_closed");
	public static final IDrawableObject NBT_BYTE_ARRAY_OPEN = getIcon("byte_array_open");
	public static final IDrawableObject NBT_INT_ARRAY = getIcon("int_array");
	public static final IDrawableObject NBT_INT_ARRAY_CLOSED = getIcon("int_array_closed");
	public static final IDrawableObject NBT_INT_ARRAY_OPEN = getIcon("int_array_open");

	public abstract class ButtonNBT extends Button
	{
		public final ButtonNBTCollection parent;
		public String key;

		public ButtonNBT(@Nullable ButtonNBTCollection b, String k)
		{
			super(b == null ? 0 : b.posX + 10, 0, 10, 10);
			parent = b;
			key = k;
			setTitle(key);
		}

		public void updateChildren(boolean first)
		{
		}

		public void addChildren()
		{
		}

		public boolean canCreateNew(int id)
		{
			return false;
		}

		@Override
		public void addMouseOverText(GuiBase gui, List<String> list)
		{
		}

		@Override
		public void renderWidget(GuiBase gui)
		{
			int ax = getAX();
			int ay = getAY();

			if (selected == this)
			{
				GuiHelper.drawBlankRect(ax, ay, width, height, Color4I.WHITE_A[33]);
			}

			getIcon(gui).draw(ax + 1, ay + 1, 8, 8, Color4I.NONE);
			gui.drawString(getTitle(gui), ax + 11, ay + 1);
		}
	}

	public class ButtonNBTPrimitive extends ButtonNBT implements IGuiFieldCallback
	{
		private NBTBase nbt;

		public ButtonNBTPrimitive(ButtonNBTCollection b, String k, NBTBase n)
		{
			super(b, k);
			nbt = n;

			switch (nbt.getId())
			{
				case Constants.NBT.TAG_BYTE:
					setIcon(NBT_BYTE);
					break;
				case Constants.NBT.TAG_SHORT:
					setIcon(NBT_SHORT);
					break;
				case Constants.NBT.TAG_INT:
					setIcon(NBT_INT);
					break;
				case Constants.NBT.TAG_LONG:
					setIcon(NBT_LONG);
					break;
				case Constants.NBT.TAG_FLOAT:
					setIcon(NBT_FLOAT);
					break;
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					setIcon(NBT_DOUBLE);
					break;
				case Constants.NBT.TAG_STRING:
					setIcon(NBT_STRING);
					break;
			}

			parent.setTag(key, nbt);
			updateTitle();
		}

		public void updateTitle()
		{
			Object title = "";

			switch (nbt.getId())
			{
				case Constants.NBT.TAG_BYTE:
				case Constants.NBT.TAG_SHORT:
				case Constants.NBT.TAG_INT:
					title = ((NBTPrimitive) nbt).getInt();
					break;
				case Constants.NBT.TAG_LONG:
					title = ((NBTPrimitive) nbt).getLong();
					break;
				case Constants.NBT.TAG_FLOAT:
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					title = ((NBTPrimitive) nbt).getDouble();
					break;
				case Constants.NBT.TAG_STRING:
					title = ((NBTTagString) nbt).getString();
					break;
			}

			setTitle(key + ": " + title);
			setWidth(12 + getFont().getStringWidth(key + ": " + title));
		}

		@Override
		public void onClicked(GuiBase gui, IMouseButton button)
		{
			selected = this;
			panelTopLeft.refreshWidgets();

			if (button.isRight())
			{
				edit();
			}
		}

		public void edit()
		{
			switch (nbt.getId())
			{
				case Constants.NBT.TAG_BYTE:
				case Constants.NBT.TAG_SHORT:
				case Constants.NBT.TAG_INT:
					GuiSelectors.selectJson(new PropertyInt(((NBTPrimitive) nbt).getInt()), this);
					break;
				case Constants.NBT.TAG_LONG:
					GuiSelectors.selectJson(new PropertyString(Long.toString(((NBTPrimitive) nbt).getLong())), this);
					break;
				case Constants.NBT.TAG_FLOAT:
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					GuiSelectors.selectJson(new PropertyDouble(((NBTPrimitive) nbt).getDouble()), this);
					break;
				case Constants.NBT.TAG_STRING:
					GuiSelectors.selectJson(new PropertyString(((NBTTagString) nbt).getString()), this);
					break;
			}
		}

		@Override
		public void onCallback(IConfigValue value, boolean set)
		{
			if (set)
			{
				switch (nbt.getId())
				{
					case Constants.NBT.TAG_BYTE:
					case Constants.NBT.TAG_SHORT:
					case Constants.NBT.TAG_INT:
						nbt = new NBTTagInt(value.getInt());
						break;
					case Constants.NBT.TAG_LONG:
						nbt = new NBTTagLong(Long.parseLong(value.getString()));
						break;
					case Constants.NBT.TAG_FLOAT:
					case Constants.NBT.TAG_DOUBLE:
					case Constants.NBT.TAG_ANY_NUMERIC:
						nbt = new NBTTagDouble(value.getDouble());
						break;
					case Constants.NBT.TAG_STRING:
						nbt = new NBTTagString(value.getString());
						break;
				}

				parent.setTag(key, nbt);
				updateTitle();
			}

			GuiEditNBT.this.openGui();
		}
	}

	public abstract class ButtonNBTCollection extends ButtonNBT
	{
		public boolean collapsed;
		public final Map<String, ButtonNBT> children;
		public final IDrawableObject iconOpen, iconClosed;

		public ButtonNBTCollection(@Nullable ButtonNBTCollection b, String key, IDrawableObject open, IDrawableObject closed)
		{
			super(b, key);
			iconOpen = open;
			iconClosed = closed;
			setCollapsed(false);
			setWidth(width + 2 + getFont().getStringWidth(key));
			children = new LinkedHashMap<>();
		}

		@Override
		public void addChildren()
		{
			if (!collapsed)
			{
				for (ButtonNBT button : children.values())
				{
					panelNbt.add(button);
					button.addChildren();
				}
			}
		}

		@Override
		public void onClicked(GuiBase gui, IMouseButton button)
		{
			if (gui.getMouseX() <= getAX() + height)
			{
				setCollapsed(!collapsed);
				panelNbt.refreshWidgets();
			}
			else
			{
				selected = this;
				panelTopLeft.refreshWidgets();
			}
		}

		public void setCollapsed(boolean c)
		{
			collapsed = c;
			setIcon(collapsed ? iconClosed : iconOpen);
		}

		public abstract NBTBase getTag(String k);

		public abstract void setTag(String k, @Nullable NBTBase base);
	}

	public class ButtonNBTMap extends ButtonNBTCollection
	{
		private NBTTagCompound map;
		private IDrawableObject hoverIcon = ImageProvider.NULL;

		public ButtonNBTMap(@Nullable ButtonNBTCollection b, String key, NBTTagCompound m)
		{
			super(b, key, NBT_MAP_OPEN, NBT_MAP_CLOSED);
			map = m;
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			List<String> list = new ArrayList<>(map.getKeySet());
			list.sort(StringUtils.IGNORE_CASE_COMPARATOR);

			for (String s : list)
			{
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
			}

			updateHoverIcon();

			if (first && !hoverIcon.isNull())
			{
				setCollapsed(true);
			}
		}

		private void updateHoverIcon()
		{
			hoverIcon = ImageProvider.NULL;
			ItemStack stack = (map.hasKey("id", Constants.NBT.TAG_STRING) && map.hasKey("Count") && map.hasKey("Damage")) ? new ItemStack(map) : ItemStack.EMPTY;

			if (!stack.isEmpty())
			{
				hoverIcon = new DrawableItem(stack);
			}

			setWidth(12 + getFont().getStringWidth(getTitle(GuiEditNBT.this)) + (hoverIcon.isNull() ? 0 : 10));
		}

		@Override
		public void addMouseOverText(GuiBase gui, List<String> list)
		{
			if (!hoverIcon.isNull())
			{
				if (hoverIcon instanceof DrawableItem)
				{
					list.add(((DrawableItem) hoverIcon).stack.getDisplayName());
				}
			}
		}

		@Override
		public void renderWidget(GuiBase gui)
		{
			super.renderWidget(gui);

			if (!hoverIcon.isNull())
			{
				int ax = getAX();
				int ay = getAY();
				hoverIcon.draw(ax + 12 + getFont().getStringWidth(getTitle(gui)), ay + 1, 8, 8, Color4I.NONE);
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return map.getTag(k);
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			if (base != null)
			{
				map.setTag(k, base);
			}
			else
			{
				map.removeTag(k);
			}

			updateHoverIcon();

			if (parent != null)
			{
				parent.setTag(key, map);
			}
		}

		@Override
		public boolean canCreateNew(int id)
		{
			return true;
		}
	}

	public class ButtonNBTList extends ButtonNBTCollection
	{
		private NBTTagList list;

		public ButtonNBTList(ButtonNBTCollection p, String key, NBTTagList l)
		{
			super(p, key, NBT_LIST_OPEN, NBT_LIST_CLOSED);
			list = l;
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			for (int i = 0; i < list.tagCount(); i++)
			{
				String s = Integer.toString(i);
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return list.get(Integer.parseInt(k));
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				if (base != null)
				{
					list.appendTag(base);
				}
			}
			else if (base != null)
			{
				list.set(id, base);
			}
			else
			{
				list.removeTag(id);
			}

			if (parent != null)
			{
				parent.setTag(key, list);
			}
		}

		@Override
		public boolean canCreateNew(int id)
		{
			return list.hasNoTags() || list.getTagType() == id;
		}
	}

	public class ButtonNBTByteArray extends ButtonNBTCollection
	{
		private NBTTagByteArray list;

		public ButtonNBTByteArray(ButtonNBTCollection p, String key, NBTTagByteArray l)
		{
			super(p, key, NBT_BYTE_ARRAY_OPEN, NBT_BYTE_ARRAY_CLOSED);
			list = l;
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			for (int i = 0; i < list.getByteArray().length; i++)
			{
				String s = Integer.toString(i);
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagByte(list.getByteArray()[Integer.parseInt(k)]);
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);
			TByteArrayList list1 = new TByteArrayList(list.getByteArray());

			if (id == -1)
			{
				if (base != null)
				{
					list1.add(((NBTPrimitive) base).getByte());
				}
			}
			else if (base != null)
			{
				list1.set(id, ((NBTPrimitive) base).getByte());
			}
			else
			{
				list1.removeAt(id);
			}

			list = new NBTTagByteArray(list1.toArray());

			if (parent != null)
			{
				parent.setTag(key, list);
			}
		}

		@Override
		public boolean canCreateNew(int id)
		{
			return id == Constants.NBT.TAG_BYTE;
		}
	}

	public class ButtonNBTIntArray extends ButtonNBTCollection
	{
		private TIntArrayList list;

		public ButtonNBTIntArray(ButtonNBTCollection p, String key, NBTTagIntArray l)
		{
			super(p, key, NBT_INT_ARRAY_OPEN, NBT_INT_ARRAY_CLOSED);
			list = new TIntArrayList(l.getIntArray());
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			for (int i = 0; i < list.size(); i++)
			{
				String s = Integer.toString(i);
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagInt(list.get(Integer.parseInt(k)));
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				if (base != null)
				{
					list.add(((NBTPrimitive) base).getInt());
				}
			}
			else if (base != null)
			{
				list.set(id, ((NBTPrimitive) base).getInt());
			}
			else
			{
				list.removeAt(id);
			}

			if (parent != null)
			{
				parent.setTag(key, new NBTTagIntArray(list.toArray()));
			}
		}

		@Override
		public boolean canCreateNew(int id)
		{
			return id == Constants.NBT.TAG_INT;
		}
	}

	private ButtonNBT getFrom(ButtonNBTCollection b, String key)
	{
		NBTBase nbt = b.getTag(key);

		switch (nbt.getId())
		{
			case Constants.NBT.TAG_COMPOUND:
				return new ButtonNBTMap(b, key, (NBTTagCompound) nbt);
			case Constants.NBT.TAG_LIST:
				return new ButtonNBTList(b, key, (NBTTagList) nbt);
			case Constants.NBT.TAG_BYTE_ARRAY:
				return new ButtonNBTByteArray(b, key, (NBTTagByteArray) nbt);
			case Constants.NBT.TAG_INT_ARRAY:
				return new ButtonNBTIntArray(b, key, (NBTTagIntArray) nbt);
			default:
				return new ButtonNBTPrimitive(b, key, nbt);
		}
	}

	public SimpleButton newTag(String t, IDrawableObject icon, Supplier<NBTBase> supplier)
	{
		return new SimpleButton(t, icon, (gui, button) ->
		{
			if (selected instanceof ButtonNBTMap)
			{
				GuiSelectors.selectJson(new PropertyString("_unnamed"), (value, set) ->
				{
					if (set && !value.getString().isEmpty())
					{
						((ButtonNBTCollection) selected).setTag(value.getString(), supplier.get());
						selected.updateChildren(false);
						panelNbt.refreshWidgets();
					}

					GuiEditNBT.this.openGui();
				});
			}
			else if (selected instanceof ButtonNBTCollection)
			{
				((ButtonNBTCollection) selected).setTag("-1", supplier.get());
				selected.updateChildren(false);
				panelNbt.refreshWidgets();
			}
		});
	}

	private final NBTTagCompound info;
	private final ButtonNBTMap buttonNBTRoot;
	private ButtonNBT selected = null;
	public final Panel panelTopLeft, panelTopRight, panelNbt;
	public final PanelScrollBar scroll;
	private int shouldClose = 0;

	public GuiEditNBT(NBTTagCompound i, NBTTagCompound nbt)
	{
		super(0, 0);
		info = i;
		buttonNBTRoot = new ButtonNBTMap(null, "ROOT", nbt);
		buttonNBTRoot.updateChildren(true);
		selected = buttonNBTRoot;

		panelTopLeft = new Panel(0, 2, 0, 16)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(GuiLang.BUTTON_DELETE, selected == buttonNBTRoot ? GuiIcons.REMOVE_GRAY : GuiIcons.REMOVE, (gui, button) ->
				{
					if (selected != buttonNBTRoot)
					{
						selected.parent.setTag(selected.key, null);
						selected.parent.updateChildren(false);
						selected = selected.parent;
						panelNbt.refreshWidgets();
						panelTopLeft.refreshWidgets();
					}
				}));

				boolean canRename = selected.parent instanceof ButtonNBTMap;

				add(new SimpleButton("Rename", canRename ? GuiIcons.INFO : GuiIcons.INFO_GRAY, (gui, button) ->
				{
					if (canRename)
					{
						GuiSelectors.selectJson(new PropertyString(selected.key), (value, set) ->
						{
							if (set)
							{
								String s = value.getString();

								if (!s.isEmpty())
								{
									ButtonNBTCollection parent = selected.parent;
									String s0 = selected.key;
									NBTBase nbt = parent.getTag(s0);
									parent.setTag(s0, null);
									parent.setTag(s, nbt);
									parent.updateChildren(false);
									selected = parent.children.get(s);
									panelNbt.refreshWidgets();
								}
							}

							GuiEditNBT.this.openGui();
						});
					}
				}));

				if (selected instanceof ButtonNBTPrimitive)
				{
					add(new SimpleButton(GuiLang.BUTTON_EDIT, GuiIcons.FEATHER, (gui, button) -> ((ButtonNBTPrimitive) selected).edit()));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_COMPOUND))
				{
					add(newTag("Compound", NBT_MAP, NBTTagCompound::new));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_LIST))
				{
					add(newTag("List", NBT_LIST, NBTTagList::new));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_STRING))
				{
					add(newTag("String", NBT_STRING, () -> new NBTTagString("")));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_BYTE))
				{
					add(newTag("Byte", NBT_BYTE, () -> new NBTTagByte((byte) 0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_SHORT))
				{
					add(newTag("Short", NBT_SHORT, () -> new NBTTagShort((short) 0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_INT))
				{
					add(newTag("Int", NBT_INT, () -> new NBTTagInt(0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_LONG))
				{
					add(newTag("Long", NBT_LONG, () -> new NBTTagLong(0L)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_FLOAT))
				{
					add(newTag("Float", NBT_FLOAT, () -> new NBTTagFloat(0F)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_DOUBLE))
				{
					add(newTag("Double", NBT_DOUBLE, () -> new NBTTagDouble(0D)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_BYTE_ARRAY))
				{
					add(newTag("Byte Array", NBT_BYTE_ARRAY, () -> new NBTTagByteArray(new byte[0])));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_INT_ARRAY))
				{
					add(newTag("Int Array", NBT_INT_ARRAY, () -> new NBTTagIntArray(new int[0])));
				}
			}

			@Override
			public void updateWidgetPositions()
			{
				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopLeft.addFlags(Panel.FLAG_DEFAULTS);

		panelTopRight = new Panel(0, 2, 0, 16)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(GuiLang.BUTTON_COLLAPSE_ALL, GuiIcons.REMOVE, (gui, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(true);
						}
					}

					scroll.setValue(gui, 0);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(GuiLang.BUTTON_EXPAND_ALL, GuiIcons.ADD, (gui, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(false);
						}
					}

					scroll.setValue(gui, 0);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(GuiLang.BUTTON_CANCEL, GuiIcons.CANCEL, (gui, button) ->
				{
					shouldClose = 2;
					gui.closeGui();
				}));

				add(new SimpleButton(GuiLang.BUTTON_ACCEPT, GuiIcons.ACCEPT, (gui, button) ->
				{
					shouldClose = 1;
					gui.closeGui();
				}));

				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopRight.addFlags(Panel.FLAG_DEFAULTS);

		panelNbt = new Panel(0, 21, 0, 0)
		{
			@Override
			public void addWidgets()
			{
				add(buttonNBTRoot);
				buttonNBTRoot.addChildren();
				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				scroll.setElementSize(align(WidgetLayout.VERTICAL) + 2);
			}
		};

		panelNbt.addFlags(Panel.FLAG_DEFAULTS);

		scroll = new PanelScrollBar(0, 20, 16, 0, 10, panelNbt)
		{
			@Override
			public boolean shouldRender(GuiBase gui)
			{
				return true;
			}
		};
	}

	@Override
	public void addWidgets()
	{
		addAll(panelTopLeft, panelTopRight, panelNbt, scroll);
	}

	@Override
	public boolean isFullscreen()
	{
		return true;
	}

	@Override
	public void updateWidgetPositions()
	{
		panelTopLeft.updateWidgetPositions();
		panelTopRight.updateWidgetPositions();
		panelTopRight.posX = width - panelTopRight.width;

		panelNbt.setHeight(height - 20);
		panelNbt.setWidth(width - scroll.width);

		scroll.posX = width - scroll.width;
		scroll.setHeight(panelNbt.height);
	}

	@Override
	public void onClosed()
	{
		if (shouldClose == 1)
		{
			new MessageEditNBTResponse(info, buttonNBTRoot.map).sendToServer();
		}
	}

	@Override
	public void drawBackground()
	{
		GuiHelper.drawBlankRect(0, 0, width, 20, GuiEditConfig.COLOR_BACKGROUND);
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
}