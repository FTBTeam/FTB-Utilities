package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.EnumTeamColor;
import com.feed_the_beast.ftblib.lib.client.CachedVertexData;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiHelper;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.misc.ChunkSelectorMap;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiChunkSelectorBase;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.events.chunks.UpdateClientDataEvent;
import com.feed_the_beast.ftbutilities.net.MessageClaimedChunksModify;
import com.feed_the_beast.ftbutilities.net.MessageClaimedChunksRequest;
import com.feed_the_beast.ftbutilities.net.MessageClaimedChunksUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID, value = Side.CLIENT)
public class GuiClaimedChunks extends GuiChunkSelectorBase
{
	public static GuiClaimedChunks instance;
	private static final ClientClaimedChunks.ChunkData[] chunkData = new ClientClaimedChunks.ChunkData[ChunkSelectorMap.TILES_GUI * ChunkSelectorMap.TILES_GUI];
	private static int claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
	private static final ClientClaimedChunks.ChunkData NULL_CHUNK_DATA = new ClientClaimedChunks.ChunkData(new ClientClaimedChunks.Team((short) 0), 0);

	private static final CachedVertexData AREA = new CachedVertexData(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

	@Nullable
	private static ClientClaimedChunks.ChunkData getAt(int x, int y)
	{
		int i = x + y * ChunkSelectorMap.TILES_GUI;
		return i < 0 || i >= chunkData.length ? null : chunkData[i];
	}

	private static boolean hasBorder(ClientClaimedChunks.ChunkData data, @Nullable ClientClaimedChunks.ChunkData with)
	{
		if (with == null)
		{
			with = NULL_CHUNK_DATA;
		}

		return (data.flags != with.flags || data.team != with.team) && !with.isLoaded();
	}

	@SubscribeEvent
	public static void onChunkDataUpdate(UpdateClientDataEvent event)
	{
		MessageClaimedChunksUpdate m = event.getMessage();
		claimedChunks = m.claimedChunks;
		loadedChunks = m.loadedChunks;
		maxClaimedChunks = m.maxClaimedChunks;
		maxLoadedChunks = m.maxLoadedChunks;
		Arrays.fill(chunkData, null);

		for (ClientClaimedChunks.Team team : m.teams.values())
		{
			for (Map.Entry<Integer, ClientClaimedChunks.ChunkData> entry : team.chunks.entrySet())
			{
				int x = entry.getKey() % ChunkSelectorMap.TILES_GUI;
				int z = entry.getKey() / ChunkSelectorMap.TILES_GUI;
				chunkData[x + z * ChunkSelectorMap.TILES_GUI] = entry.getValue();
			}
		}

		AREA.reset();
		EnumTeamColor prevCol = null;
		ClientClaimedChunks.ChunkData data;

		for (int i = 0; i < chunkData.length; i++)
		{
			data = chunkData[i];

			if (data == null)
			{
				continue;
			}

			if (prevCol != data.team.color)
			{
				prevCol = data.team.color;
				AREA.color.set(data.team.color.getColor(), 150);
			}

			AREA.rect((i % ChunkSelectorMap.TILES_GUI) * TILE_SIZE, (i / ChunkSelectorMap.TILES_GUI) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		}

		boolean borderU, borderD, borderL, borderR;

		for (int i = 0; i < chunkData.length; i++)
		{
			data = chunkData[i];

			if (data == null)
			{
				continue;
			}

			int x = i % ChunkSelectorMap.TILES_GUI;
			int dx = x * TILE_SIZE;
			int y = i / ChunkSelectorMap.TILES_GUI;
			int dy = y * TILE_SIZE;

			borderU = y > 0 && hasBorder(data, getAt(x, y - 1));
			borderD = y < (ChunkSelectorMap.TILES_GUI - 1) && hasBorder(data, getAt(x, y + 1));
			borderL = x > 0 && hasBorder(data, getAt(x - 1, y));
			borderR = x < (ChunkSelectorMap.TILES_GUI - 1) && hasBorder(data, getAt(x + 1, y));

			if (data.isLoaded())
			{
				AREA.color.set(255, 80, 80, 230);
			}
			else
			{
				AREA.color.set(80, 80, 80, 230);
			}

			if (borderU)
			{
				AREA.rect(dx, dy, TILE_SIZE, 1);
			}

			if (borderD)
			{
				AREA.rect(dx, dy + TILE_SIZE - 1, TILE_SIZE, 1);
			}

			if (borderL)
			{
				AREA.rect(dx, dy, 1, TILE_SIZE);
			}

			if (borderR)
			{
				AREA.rect(dx + TILE_SIZE - 1, dy, 1, TILE_SIZE);
			}
		}
	}

	private static abstract class ButtonSide extends Button
	{
		public ButtonSide(Panel panel, String text, Icon icon)
		{
			super(panel, text, icon);
			setSize(20, 20);
		}
	}

	private final String currentDimName;

	public GuiClaimedChunks()
	{
		currentDimName = ServerUtils.getDimensionName(Minecraft.getMinecraft().world.provider.getDimension()).getFormattedText();
	}

	@Override
	public void onPostInit()
	{
		new MessageClaimedChunksRequest(startX, startZ).sendToServer();
		ChunkSelectorMap.getMap().resetMap(startX, startZ);
	}

	@Override
	public int getSelectionMode(MouseButton button)
	{
		boolean claim = !isShiftKeyDown();
		boolean flag = button.isLeft();

		if (flag)
		{
			return claim ? MessageClaimedChunksModify.CLAIM : MessageClaimedChunksModify.LOAD;
		}
		else
		{
			return claim ? MessageClaimedChunksModify.UNCLAIM : MessageClaimedChunksModify.UNLOAD;
		}
	}

	@Override
	public void onChunksSelected(Collection<ChunkPos> chunks)
	{
		new MessageClaimedChunksModify(startX, startZ, currentSelectionMode, chunks).sendToServer();
	}

	@Override
	public void drawArea(Tessellator tessellator, BufferBuilder buffer)
	{
		AREA.draw(tessellator, buffer);
	}

	@Override
	public void addCornerButtons(Panel panel)
	{
		panel.add(new ButtonSide(panel, I18n.format("gui.close"), GuiIcons.ACCEPT)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
				getGui().closeGui();
			}
		});

		panel.add(new ButtonSide(panel, I18n.format("selectServer.refresh"), GuiIcons.REFRESH)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
				new MessageClaimedChunksRequest(startX, startZ).sendToServer();
				ChunkSelectorMap.getMap().resetMap(startX, startZ);
			}
		});

		if (maxClaimedChunks >= 0)
		{
			panel.add(new ButtonSide(panel, I18n.format("ftbutilities.lang.chunks.unclaim_all_dim", currentDimName), GuiIcons.REMOVE)
			{
				@Override
				public void onClicked(MouseButton button)
				{
					GuiHelper.playClickSound();
					String s = I18n.format("ftbutilities.lang.chunks.unclaim_all_dim_q", currentDimName);
					openYesNo(s, "", () -> ClientUtils.execClientCommand("/chunks unclaim_all " + Minecraft.getMinecraft().world.provider.getDimension()));
				}
			});

			panel.add(new ButtonSide(panel, I18n.format("ftbutilities.lang.chunks.unclaim_all"), GuiIcons.REMOVE)
			{
				@Override
				public void onClicked(MouseButton button)
				{
					GuiHelper.playClickSound();
					String s = I18n.format("ftbutilities.lang.chunks.unclaim_all_q");
					openYesNo(s, "", () -> ClientUtils.execClientCommand("/chunks unclaim_all"));
				}
			});
		}

		panel.add(new ButtonSide(panel, I18n.format("gui.info"), GuiIcons.INFO)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				GuiHelper.playClickSound();
				handleClick("https://github.com/FTBTeam/FTB-Chunks/wiki");
			}
		});
	}

	@Override
	public void addCornerText(List<String> list, Corner corner)
	{
		if (maxClaimedChunks < 0)
		{
			if (corner == Corner.BOTTOM_RIGHT)
			{
				if (maxClaimedChunks == -2)
				{
					list.add(TextFormatting.RED + I18n.format("ftblib.lang.team.error.no_team"));
				}
				else
				{
					list.add(TextFormatting.RED + I18n.format("feature_disabled_server"));
				}
			}

			return;
		}

		switch (corner)
		{
			case BOTTOM_RIGHT:
				list.add(I18n.format("ftbutilities.lang.chunks.claimed_count", claimedChunks, maxClaimedChunks == Integer.MAX_VALUE ? "\u221E" : Integer.toString(maxClaimedChunks)));
				list.add(I18n.format("ftbutilities.lang.chunks.loaded_count", loadedChunks, maxLoadedChunks == Integer.MAX_VALUE ? "\u221E" : Integer.toString(maxLoadedChunks)));
				break;
		}
	}

	@Override
	public void addButtonText(GuiChunkSelectorBase.MapButton button, List<String> list)
	{
		ClientClaimedChunks.ChunkData data = chunkData[button.index];

		if (data != null)
		{
			list.add(data.team.nameComponent.getFormattedText());
			list.add(TextFormatting.GREEN + I18n.format("ftbutilities.lang.chunks.claimed_area"));

			if (data.isLoaded())
			{
				list.add(TextFormatting.RED + I18n.format("ftbutilities.lang.chunks.upgrade.loaded"));
			}
		}
		else
		{
			list.add(TextFormatting.DARK_GREEN + I18n.format("ftbutilities.lang.chunks.wilderness"));
		}

		if (isCtrlKeyDown())
		{
			list.add(button.chunkPos.toString());
		}
	}
}