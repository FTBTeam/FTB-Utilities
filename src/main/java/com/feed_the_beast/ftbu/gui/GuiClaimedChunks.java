package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.client.CachedVertexData;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.PanelLM;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.gui.misc.ThreadReloadChunkSelector;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksModify;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiClaimedChunks extends GuiLM
{
    public static GuiClaimedChunks instance;
    private static final Map<UUID, ClaimedChunks.Team> TEAMS = new HashMap<>();
    private static final ClaimedChunks.Data[] chunkData = new ClaimedChunks.Data[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];
    private static int claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
    private static final ClaimedChunks.Data NULL_CHUNK_DATA = new ClaimedChunks.Data();

    private static final CachedVertexData AREA = new CachedVertexData(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
    private static final CachedVertexData GRID = new CachedVertexData(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

    static
    {
        NULL_CHUNK_DATA.owner = "";

        for(int i = 0; i < chunkData.length; i++)
        {
            chunkData[i] = new ClaimedChunks.Data();
        }

        GRID.color.set(128, 128, 128, 50);

        for(int x = 0; x <= GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x++)
        {
            GRID.pos(x * 16, 0D);
            GRID.pos(x * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, 0D);
        }

        for(int y = 0; y <= GuiConfigs.CHUNK_SELECTOR_TILES_GUI; y++)
        {
            GRID.pos(0D, y * 16, 0D);
            GRID.pos(GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, y * 16, 0D);
        }
    }

    private static ClaimedChunks.Data getAt(int x, int y)
    {
        int i = x + y * GuiConfigs.CHUNK_SELECTOR_TILES_GUI;
        return i < 0 || i >= chunkData.length ? NULL_CHUNK_DATA : chunkData[i];
    }

    private static boolean hasBorder(ClaimedChunks.Data data, ClaimedChunks.Data with)
    {
        return (data.flags != with.flags || data.team != with.team) && !with.hasUpgrade(ChunkUpgrade.LOADED);
    }

    public static void setData(int startX, int startZ, int cc, int lc, int mcc, int mlc, ClaimedChunks.Data[] data, Map<UUID, ClaimedChunks.Team> tms)
    {
        claimedChunks = cc;
        loadedChunks = lc;
        maxClaimedChunks = mcc;
        maxLoadedChunks = mlc;
        System.arraycopy(data, 0, chunkData, 0, chunkData.length);
        TEAMS.putAll(tms);

        if(FTBUClient.JM_INTEGRATION != null)
        {
            for(int z = 0; z < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; z++)
            {
                for(int x = 0; x < GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x++)
                {
                    FTBUClient.JM_INTEGRATION.chunkChanged(new ChunkPos(startX + x, startZ + z), data[x + z * GuiConfigs.CHUNK_SELECTOR_TILES_GUI]);
                }
            }
        }

        AREA.reset();
        EnumTeamColor prevCol = null;

        for(int i = 0; i < chunkData.length; i++)
        {
            if(!chunkData[i].hasUpgrade(ChunkUpgrade.CLAIMED))
            {
                continue;
            }

            if(prevCol != chunkData[i].team.color)
            {
                prevCol = chunkData[i].team.color;
                AREA.color.set(chunkData[i].team.color.getColor(), 150);
            }

            AREA.rect((i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * 16, (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * 16, 16, 16);
        }

        boolean borderU, borderD, borderL, borderR;

        for(int i = 0; i < chunkData.length; i++)
        {
            if(!chunkData[i].hasUpgrade(ChunkUpgrade.CLAIMED))
            {
                continue;
            }

            int x = i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI;
            int dx = x * 16;
            int y = i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI;
            int dy = y * 16;

            borderU = y > 0 && hasBorder(chunkData[i], getAt(x, y - 1));
            borderD = y < (GuiConfigs.CHUNK_SELECTOR_TILES_GUI - 1) && hasBorder(chunkData[i], getAt(x, y + 1));
            borderL = x > 0 && hasBorder(chunkData[i], getAt(x - 1, y));
            borderR = x < (GuiConfigs.CHUNK_SELECTOR_TILES_GUI - 1) && hasBorder(chunkData[i], getAt(x + 1, y));

            if(chunkData[i].hasUpgrade(ChunkUpgrade.LOADED))
            {
                AREA.color.set(255, 80, 80, 230);
            }
            else
            {
                AREA.color.set(80, 80, 80, 230);
            }

            if(borderU)
            {
                AREA.rect(dx, dy, 16, 1);
            }

            if(borderD)
            {
                AREA.rect(dx, dy + 15, 16, 1);
            }

            if(borderL)
            {
                AREA.rect(dx, dy, 1, 16);
            }

            if(borderR)
            {
                AREA.rect(dx + 15, dy, 1, 16);
            }
        }
    }

    private class MapButton extends ButtonLM
    {
        private final ChunkPos chunkPos;
        private final int index;
        private boolean isSelected = false;

        private MapButton(int x, int y, int i)
        {
            super(x, y, 16, 16);
            posX += (i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * getWidth();
            posY += (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * getHeight();
            chunkPos = new ChunkPos(startX + (i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI), startZ + (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI));
            index = i;
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            GuiHelper.playClickSound();
            boolean claim = !GuiScreen.isShiftKeyDown();
            boolean flag = button.isLeft();

            if(flag)
            {
                currentSelectionMode = claim ? MessageClaimedChunksModify.CLAIM : MessageClaimedChunksModify.LOAD;
            }
            else
            {
                currentSelectionMode = claim ? MessageClaimedChunksModify.UNCLAIM : MessageClaimedChunksModify.UNLOAD;
            }
        }

        @Override
        public void addMouseOverText(IGui gui, List<String> list)
        {
            if(chunkData[index].hasUpgrade(ChunkUpgrade.CLAIMED))
            {
                list.add(chunkData[index].team.formattedName);
                list.add(TextFormatting.GREEN + ChunkUpgrade.CLAIMED.getLangKey().translate());

                if(chunkData[index].team.isAlly)
                {
                    list.add(chunkData[index].owner);

                    for(IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
                    {
                        if(upgrade != null && chunkData[index].hasUpgrade(upgrade))
                        {
                            list.add(TextFormatting.RED + upgrade.getLangKey().translate());
                        }
                    }
                }
            }
            else
            {
                list.add(TextFormatting.DARK_GREEN + ChunkUpgrade.WILDERNESS.getLangKey().translate());
            }

            if(GuiScreen.isCtrlKeyDown())
            {
                list.add(chunkPos.toString());
            }
        }

        @Override
        public void renderWidget(IGui gui)
        {
            int ax = getAX();
            int ay = getAY();

            if(!isSelected && currentSelectionMode != -1 && isMouseOver(this))
            {
                isSelected = true;
            }

            if(isSelected || gui.isMouseOver(this))
            {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                GuiHelper.drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }
    }

    public final int startX, startZ;
    private final ButtonLM buttonRefresh, buttonClose, buttonUnclaimAll;
    private final MapButton mapButtons[];
    private final PanelLM panelButtons;
    private String currentDimName;
    private byte currentSelectionMode = -1;

    public GuiClaimedChunks()
    {
        super(GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16);

        startX = MathHelperLM.chunk(mc.player.posX) - GuiConfigs.CHUNK_SELECTOR_TILES_GUI2;
        startZ = MathHelperLM.chunk(mc.player.posZ) - GuiConfigs.CHUNK_SELECTOR_TILES_GUI2;

        currentDimName = mc.world.provider.getDimensionType().getName();

        buttonClose = new ButtonLM(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                gui.closeGui();
            }
        };

        buttonClose.setIcon(GuiIcons.ACCEPT);

        buttonRefresh = new ButtonLM(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                new MessageClaimedChunksRequest(startX, startZ).sendToServer();
                ThreadReloadChunkSelector.reloadArea(mc.world, startX, startZ);
            }
        };

        buttonRefresh.setIcon(GuiIcons.REFRESH);

        buttonUnclaimAll = new ButtonLM(0, 32, 16, 16)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                String s = GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_Q.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM_Q.translate(currentDimName);
                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo((set, id) ->
                {
                    if(set)
                    {
                        FTBLibClient.execClientCommand("/ftb chunks unclaim_all " + (id == 1));
                    }

                    gui.openGui();
                    gui.refreshWidgets();
                }, s, "", GuiScreen.isShiftKeyDown() ? 1 : 0));
            }

            @Override
            public void addMouseOverText(IGui gui, List<String> list)
            {
                list.add(GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM.translate(currentDimName));
            }
        };

        buttonUnclaimAll.setIcon(GuiIcons.REMOVE);

        panelButtons = new PanelLM(0, 0, 16, 0)
        {
            @Override
            public void addWidgets()
            {
                add(buttonClose);
                add(buttonRefresh);
                add(buttonUnclaimAll);

                setHeight(getWidgets().size() * 16);
            }

            @Override
            public int getAX()
            {
                return getScreen().getScaledWidth() - 16;
            }

            @Override
            public int getAY()
            {
                return 0;
            }
        };

        mapButtons = new MapButton[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];

        for(int i = 0; i < mapButtons.length; i++)
        {
            mapButtons[i] = new MapButton(0, 0, i);
        }
    }

    @Override
    public void onInit()
    {
        buttonRefresh.onClicked(this, MouseButton.LEFT);
    }

    @Override
    public void addWidgets()
    {
        for(MapButton b : mapButtons)
        {
            add(b);
        }

        add(panelButtons);
    }

    @Override
    public void drawBackground()
    {
        GlStateManager.color(0F, 0F, 0F, 1F);
        GuiHelper.drawBlankRect(posX - 2, posY - 2, getWidth() + 4, getHeight() + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        ThreadReloadChunkSelector.updateTexture();
        GlStateManager.bindTexture(ThreadReloadChunkSelector.getTextureID());
        GuiHelper.drawTexturedRect(posX, posY, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, 0D, 0D, GuiConfigs.CHUNK_SELECTOR_UV, GuiConfigs.CHUNK_SELECTOR_UV);

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        mc.getTextureManager().bindTexture(GuiConfigs.TEX_CHUNK_CLAIMING);

        for(MapButton mapButton : mapButtons)
        {
            mapButton.renderWidget(this);
        }

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(1F);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.setTranslation(mapButtons[0].getAX(), mapButtons[0].getAY(), 0D);
        //GlStateManager.color(1F, 1F, 1F, GuiScreen.isCtrlKeyDown() ? 0.2F : 0.7F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        AREA.draw(tessellator, buffer);
        GRID.draw(tessellator, buffer);
        buffer.setTranslation(0D, 0D, 0D);
        GlStateManager.enableTexture2D();

        int cx = MathHelperLM.chunk(mc.player.posX);
        int cy = MathHelperLM.chunk(mc.player.posZ);

        if(cx >= startX && cy >= startZ && cx < startX + GuiConfigs.CHUNK_SELECTOR_TILES_GUI && cy < startZ + GuiConfigs.CHUNK_SELECTOR_TILES_GUI)
        {
            double x = ((cx - startX) * 16D + MathHelperLM.wrap(mc.player.posX, 16D));
            double y = ((cy - startZ) * 16D + MathHelperLM.wrap(mc.player.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX + x, posY + y, 0D);
            GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.player.rotationYaw + 180F, 0F, 0F, 1F);
            mc.getTextureManager().bindTexture(GuiConfigs.TEX_ENTITY);
            GlStateManager.color(1F, 1F, 1F, mc.player.isSneaking() ? 0.4F : 0.7F);
            GuiHelper.drawTexturedRect(-8, -8, 16, 16, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();
            FTBLibClient.localPlayerHead.draw(-2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    public void mouseReleased(IGui gui)
    {
        super.mouseReleased(gui);

        if(currentSelectionMode != -1)
        {
            Collection<ChunkPos> c = new ArrayList<>();

            for(MapButton b : mapButtons)
            {
                if(b.isSelected)
                {
                    c.add(b.chunkPos);
                    b.isSelected = false;
                }
            }

            new MessageClaimedChunksModify(startX, startZ, currentSelectionMode, c).sendToServer();
            currentSelectionMode = -1;
        }
    }

    @Override
    public void drawForeground()
    {
        String s = FTBULang.LABEL_CCHUNKS_COUNT.translate(claimedChunks, maxClaimedChunks);
        getFont().drawStringWithShadow(s, getScreen().getScaledWidth() - getFont().getStringWidth(s) - 4, getScreen().getScaledHeight() - 24, 0xFFFFFFFF);
        s = FTBULang.LABEL_LCHUNKS_COUNT.translate(loadedChunks, maxLoadedChunks);
        getFont().drawStringWithShadow(s, getScreen().getScaledWidth() - getFont().getStringWidth(s) - 4, getScreen().getScaledHeight() - 12, 0xFFFFFFFF);

        super.drawForeground();
    }
}