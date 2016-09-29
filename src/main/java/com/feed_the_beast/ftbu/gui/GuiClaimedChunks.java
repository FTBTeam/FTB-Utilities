package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.PanelLM;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbl.lib.util.LMColorUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksModify;
import com.feed_the_beast.ftbu.net.MessageClaimedChunksRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiClaimedChunks extends GuiLM implements GuiYesNoCallback
{
    static ByteBuffer pixelBuffer = null;
    private static int textureID = -1;
    public static ThreadReloadArea thread = null;
    public static GuiClaimedChunks instance;

    private class MapButton extends ButtonLM
    {
        private final ChunkPos chunkPos;
        private final int index;

        private MapButton(int x, int y, int i)
        {
            super(x, y, 16, 16);
            posX += (i % ClaimedChunks.TILES_GUI) * getWidth();
            posY += (i / ClaimedChunks.TILES_GUI) * getHeight();
            chunkPos = new ChunkPos(startX + (i % ClaimedChunks.TILES_GUI), startZ + (i / ClaimedChunks.TILES_GUI));
            index = i;
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            GuiHelper.playClickSound();

            boolean claim = GuiScreen.isShiftKeyDown();
            boolean flag = button.isLeft();

            if(flag ? (claim ? !chunkData[index].canClaim() : !chunkData[index].canLoad()) : !chunkData[index].isOwner())
            {
                return;
            }

            byte action;

            if(flag)
            {
                action = claim ? MessageClaimedChunksModify.CLAIM : MessageClaimedChunksModify.LOAD;
            }
            else
            {
                action = claim ? MessageClaimedChunksModify.UNCLAIM : MessageClaimedChunksModify.UNLOAD;
            }

            new MessageClaimedChunksModify(startX, startZ, action, Collections.singleton(chunkPos)).sendToServer();
        }

        @Override
        public void addMouseOverText(IGui gui, List<String> l)
        {
            if(chunkData[index].isClaimed())
            {
                l.add(chunkData[index].team.formattedName);
                l.add(TextFormatting.GREEN + FTBULang.CHUNKTYPE_CLAIMED.translate());

                if(chunkData[index].team.isAlly)
                {
                    l.add(chunkData[index].owner);

                    if(chunkData[index].isLoaded())
                    {
                        l.add(TextFormatting.RED + FTBULang.CHUNKTYPE_LOADED.translate());
                    }
                }
            }
            else
            {
                l.add(TextFormatting.DARK_GREEN + FTBULang.CHUNKTYPE_WILDERNESS.translate());
            }
        }

        @Override
        public void renderWidget(IGui gui)
        {
            int ax = getAX();
            int ay = getAY();

            if(chunkData[index].isClaimed())
            {
                FTBLibClient.setTexture(ClaimedChunks.TEX_CHUNK_CLAIMING);
                LMColorUtils.setGLColor(LMColorUtils.getColorFromID(chunkData[index].team.colorID), 180);

                GuiHelper.drawTexturedRect(ax, ay, 16, 16, ClaimedChunks.TEX_FILLED.getMinU(), ClaimedChunks.TEX_FILLED.getMinV(), ClaimedChunks.TEX_FILLED.getMaxU(), ClaimedChunks.TEX_FILLED.getMaxV());

                GlStateManager.color((chunkData[index].isLoaded() && chunkData[index].team.isAlly) ? 1F : 0F, chunkData[index].isOwner() ? 0.27F : 0F, 0F, 0.78F);
                GuiHelper.drawTexturedRect(ax, ay, 16, 16, ClaimedChunks.TEX_BORDER.getMinU(), ClaimedChunks.TEX_BORDER.getMinV(), ClaimedChunks.TEX_BORDER.getMaxU(), ClaimedChunks.TEX_BORDER.getMaxV());
            }

            if(gui.isMouseOver(this))
            {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                GuiHelper.drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }
    }

    public final int startX, startZ;
    private final Map<UUID, ClaimedChunks.Team> teams;
    private ClaimedChunks.Data[] chunkData;
    private int claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
    private final ButtonLM buttonRefresh, buttonClose, buttonUnclaimAll, buttonDepth;
    private final MapButton mapButtons[];
    private final PanelLM panelButtons;
    private String currentDimName;

    public GuiClaimedChunks()
    {
        super(ClaimedChunks.TILES_GUI * 16, ClaimedChunks.TILES_GUI * 16);

        startX = MathHelperLM.chunk(mc.thePlayer.posX) - 7;
        startZ = MathHelperLM.chunk(mc.thePlayer.posX) - 7;

        teams = new HashMap<>();
        chunkData = new ClaimedChunks.Data[ClaimedChunks.TILES_GUI * ClaimedChunks.TILES_GUI];

        for(int i = 0; i < chunkData.length; i++)
        {
            chunkData[i] = new ClaimedChunks.Data();
        }

        currentDimName = mc.theWorld.provider.getDimensionType().getName();

        buttonClose = new ButtonLM(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                closeGui();
            }
        };

        buttonRefresh = new ButtonLM(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                new MessageClaimedChunksRequest(startX, startZ).sendToServer();

                if(thread != null)
                {
                    thread.cancelled = true;
                    thread = null;
                }

                thread = new ThreadReloadArea(mc.theWorld, GuiClaimedChunks.this);
                thread.start();
            }
        };

        buttonUnclaimAll = new ButtonLM(0, 32, 16, 16)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                String s = GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_Q.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM_Q.translate(currentDimName);
                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(GuiClaimedChunks.this, s, "", GuiScreen.isShiftKeyDown() ? 1 : 0));
            }

            @Override
            public void addMouseOverText(IGui gui, List<String> l)
            {
                l.add(GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM.translate(currentDimName));
            }
        };

        buttonDepth = new ButtonLM(0, 48, 16, 16)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                FTBUClientConfig.ENABLE_CHUNK_SELECTOR_DEPTH.setBoolean(!FTBUClientConfig.ENABLE_CHUNK_SELECTOR_DEPTH.getBoolean());
                buttonRefresh.onClicked(gui, button);
            }
        };

        buttonDepth.setTitle("Map Depth"); //TODO: Lang

        panelButtons = new PanelLM(0, 0, 16, 0)
        {
            @Override
            public void addWidgets()
            {
                add(buttonClose);
                add(buttonRefresh);
                add(buttonUnclaimAll);
                add(buttonDepth);

                setHeight(widgets.size() * 16);
            }

            @Override
            public int getAX()
            {
                return getScreenWidth() - 16;
            }

            @Override
            public int getAY()
            {
                return 0;
            }
        };

        mapButtons = new MapButton[ClaimedChunks.TILES_GUI * ClaimedChunks.TILES_GUI];

        for(int i = 0; i < mapButtons.length; i++)
        {
            mapButtons[i] = new MapButton(0, 0, i);
        }
    }

    public void setData(int cc, int lc, int mcc, int mlc, ClaimedChunks.Data[] data, Map<UUID, ClaimedChunks.Team> tms)
    {
        claimedChunks = cc;
        loadedChunks = lc;
        maxClaimedChunks = mcc;
        maxLoadedChunks = mlc;
        chunkData = data;
        teams.putAll(tms);
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
        super.drawBackground();

        if(textureID == -1)
        {
            textureID = TextureUtil.glGenTextures();
        }

        if(pixelBuffer != null)
        {
            //boolean hasBlur = false;
            //int filter = hasBlur ? GL11.GL_LINEAR : GL11.GL_NEAREST;
            GlStateManager.bindTexture(textureID);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, ClaimedChunks.TILES_TEX * 16, ClaimedChunks.TILES_TEX * 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
            pixelBuffer = null;
            thread = null;
        }

        GlStateManager.color(0F, 0F, 0F, 1F);
        GuiHelper.drawBlankRect(posX - 2, posY - 2, getWidth() + 4, getHeight() + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if(thread == null)
        {
            GlStateManager.bindTexture(textureID);
            GuiHelper.drawTexturedRect(posX, posY, ClaimedChunks.TILES_GUI * 16, ClaimedChunks.TILES_GUI * 16, 0D, 0D, ClaimedChunks.UV, ClaimedChunks.UV);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        FTBLibClient.setTexture(ClaimedChunks.TEX_CHUNK_CLAIMING);

        for(MapButton mapButton : mapButtons)
        {
            mapButton.renderWidget(this);
        }

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        int gridR = 128, gridG = 128, gridB = 128, gridA = 50;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        int gridX = mapButtons[0].getAX();
        int gridY = mapButtons[0].getAY();

        for(int x = 0; x <= ClaimedChunks.TILES_GUI; x++)
        {
            buffer.pos(gridX + x * 16, gridY, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + x * 16, gridY + ClaimedChunks.TILES_GUI * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        for(int y = 0; y <= ClaimedChunks.TILES_GUI; y++)
        {
            buffer.pos(gridX, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + ClaimedChunks.TILES_GUI * 16, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();

        int cx = MathHelperLM.chunk(mc.thePlayer.posX);
        int cy = MathHelperLM.chunk(mc.thePlayer.posZ);

        if(cx >= startX && cy >= startZ && cx < startX + ClaimedChunks.TILES_GUI && cy < startZ + ClaimedChunks.TILES_GUI)
        {
            double x = ((cx - startX) * 16D + MathHelperLM.wrap(mc.thePlayer.posX, 16D));
            double y = ((cy - startZ) * 16D + MathHelperLM.wrap(mc.thePlayer.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX + x, posY + y, 0D);
            GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.thePlayer.rotationYaw + 180F, 0F, 0F, 1F);
            FTBLibClient.setTexture(ClaimedChunks.TEX_ENTITY);
            GlStateManager.color(1F, 1F, 1F, mc.thePlayer.isSneaking() ? 0.4F : 0.7F);
            GuiHelper.drawTexturedRect(-8, -8, 16, 16, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();
            GuiHelper.drawPlayerHead(mc.thePlayer.getName(), -2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

        buttonRefresh.render(GuiIcons.REFRESH);
        buttonClose.render(GuiIcons.ACCEPT);
        buttonUnclaimAll.render(GuiIcons.REMOVE);
        buttonDepth.render(FTBUClientConfig.ENABLE_CHUNK_SELECTOR_DEPTH.getBoolean() ? GuiIcons.ACCEPT : GuiIcons.ACCEPT_GRAY);
    }

    @Override
    public void drawForeground()
    {
        String s = FTBULang.LABEL_CCHUNKS_COUNT.translate(claimedChunks, maxClaimedChunks);
        getFont().drawString(s, getScreenWidth() - getFont().getStringWidth(s) - 4, getScreenHeight() - 24, 0xFFFFFFFF);
        s = FTBULang.LABEL_LCHUNKS_COUNT.translate(loadedChunks, maxLoadedChunks);
        getFont().drawString(s, getScreenWidth() - getFont().getStringWidth(s) - 4, getScreenHeight() - 12, 0xFFFFFFFF);

        super.drawForeground();
    }

    @Override
    public void confirmClicked(boolean set, int id)
    {
        if(set)
        {
            if(id == 1)
            {
                FTBLibClient.execClientCommand("/ftb chunks unclaim_all true", false);
            }
            else
            {
                FTBLibClient.execClientCommand("/ftb chunks unclaim_all false", false);
            }
        }

        openGui();
        refreshWidgets();
    }
}