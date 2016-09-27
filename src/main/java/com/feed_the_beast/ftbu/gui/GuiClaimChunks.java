package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.client.TextureCoords;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.PanelLM;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbl.lib.util.LMColorUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.feed_the_beast.ftbu.client.FTBUClientConfig;
import com.feed_the_beast.ftbu.net.MessageRequestChunkData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiClaimChunks extends GuiLM implements GuiYesNoCallback // implements IClientActionGui
{
    static final int TILES_TEX = 16;
    static final int TILES_GUI = 15;
    private static final double UV = (double) TILES_GUI / (double) TILES_TEX;
    private static final ResourceLocation TEX_ENTITY = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/entity.png");
    private static final ResourceLocation TEX_CHUNK_CLAIMING = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/chunk_claiming.png");
    private static final TextureCoords TEX_FILLED = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0D, 0D, 0.5D, 1D);
    private static final TextureCoords TEX_BORDER = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0.5D, 0D, 1D, 1D);
    static ByteBuffer pixelBuffer = null;
    private static int textureID = -1;

    private class MapButton extends ButtonLM
    {
        private final long chunkPos;
        private CachedClientData.ChunkData chunkData;

        private MapButton(int x, int y, int i)
        {
            super(x, y, 16, 16);
            posX += (i % TILES_GUI) * getWidth();
            posY += (i / TILES_GUI) * getHeight();
            chunkPos = Bits.intsToLong(startX + (i % TILES_GUI), startZ + (i / TILES_GUI));
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            if(gui.isMouseOver(panelButtons))
            {
                return;
            }

            if(button.isLeft())
            {
                if(GuiScreen.isShiftKeyDown())
                {
                    FTBLibClient.execClientCommand("/ftb chunks load " + Bits.intFromLongA(chunkPos) + ' ' + Bits.intFromLongB(chunkPos), false);
                }
                else
                {
                    FTBLibClient.execClientCommand("/ftb chunks claim " + Bits.intFromLongA(chunkPos) + ' ' + Bits.intFromLongB(chunkPos), false);
                }
            }
            else if(button.isRight())
            {
                if(GuiScreen.isShiftKeyDown())
                {
                    FTBLibClient.execClientCommand("/ftb chunks unload " + Bits.intFromLongA(chunkPos) + ' ' + Bits.intFromLongB(chunkPos), false);
                }
                else
                {
                    FTBLibClient.execClientCommand("/ftb chunks unclaim " + Bits.intFromLongA(chunkPos) + ' ' + Bits.intFromLongB(chunkPos), false);
                }
            }

            GuiHelper.playClickSound();
        }

        @Override
        public void addMouseOverText(IGui gui, List<String> l)
        {
            if(chunkData != null)
            {
                if(chunkData.team != null)
                {
                    l.add(chunkData.team.formattedName);

                    l.add(TextFormatting.GREEN + FTBULang.CHUNKTYPE_CLAIMED.translate());

                    /*if(team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly())
                    {
                        l.add(chunk.owner.getProfile().getName());

                        if(chunk.loaded)
                        {
                            l.add(TextFormatting.RED + ClaimedChunk.LANG_LOADED.translate());
                        }
                    }*/
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

            chunkData = CachedClientData.CHUNKS.get(chunkPos);

            if(chunkData != null)
            {
                FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);

                if(chunkData.team != null)
                {
                    LMColorUtils.setGLColor(LMColorUtils.getColorFromID(chunkData.team.color.getColorID()), 180);
                }
                else
                {
                    GlStateManager.color(0F, 0F, 0F, 180F / 255F);
                }

                GuiHelper.drawTexturedRect(ax, ay, 16, 16, TEX_FILLED.getMinU(), TEX_FILLED.getMinV(), TEX_FILLED.getMaxU(), TEX_FILLED.getMaxV());

                //GlStateManager.color((chunk.loaded && team != null && team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly()) ? 1F : 0F, chunk.isChunkOwner(ForgeWorldSP.inst.clientPlayer) ? 0.27F : 0F, 0F, 0.78F);
                GuiHelper.drawTexturedRect(ax, ay, 16, 16, TEX_BORDER.getMinU(), TEX_BORDER.getMinV(), TEX_BORDER.getMaxU(), TEX_BORDER.getMaxV());
            }

            if(gui.isMouseOver(this))
            {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                GuiHelper.drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }
    }

    final int startX, startZ;
    private final ButtonLM buttonRefresh, buttonClose, buttonUnclaimAll, buttonDepth;
    private final MapButton mapButtons[];
    private final PanelLM panelButtons;
    public ThreadReloadArea thread = null;
    private String currentDimName;

    public GuiClaimChunks()
    {
        super(TILES_GUI * 16, TILES_GUI * 16);

        startX = MathHelperLM.chunk(mc.thePlayer.posX) - (int) (TILES_GUI * 0.5D);
        startZ = MathHelperLM.chunk(mc.thePlayer.posZ) - (int) (TILES_GUI * 0.5D);

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
                thread = null;
                thread = new ThreadReloadArea(mc.theWorld, GuiClaimChunks.this);
                thread.start();
                new MessageRequestChunkData(startX, startZ, TILES_GUI, TILES_GUI).sendToServer();
                GuiHelper.playClickSound();
            }
        };

        buttonUnclaimAll = new ButtonLM(0, 32, 16, 16)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                String s = GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_Q.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM_Q.translate(currentDimName);
                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(GuiClaimChunks.this, s, "", GuiScreen.isShiftKeyDown() ? 1 : 0));
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

        mapButtons = new MapButton[TILES_GUI * TILES_GUI];
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
        super.drawBackground();

        if(textureID == -1)
        {
            textureID = TextureUtil.glGenTextures();
            new MessageRequestChunkData(startX, startZ, TILES_GUI, TILES_GUI).sendToServer();
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
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, TILES_TEX * 16, TILES_TEX * 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
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
            GuiHelper.drawTexturedRect(posX, posY, TILES_GUI * 16, TILES_GUI * 16, 0D, 0D, UV, UV);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);

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

        for(int x = 0; x <= TILES_GUI; x++)
        {
            buffer.pos(gridX + x * 16, gridY, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + x * 16, gridY + TILES_GUI * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        for(int y = 0; y <= TILES_GUI; y++)
        {
            buffer.pos(gridX, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + TILES_GUI * 16, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();

        int cx = MathHelperLM.chunk(mc.thePlayer.posX);
        int cy = MathHelperLM.chunk(mc.thePlayer.posZ);

        if(cx >= startX && cy >= startZ && cx < startX + TILES_GUI && cy < startZ + TILES_GUI)
        {
            double x = ((cx - startX) * 16D + MathHelperLM.wrap(mc.thePlayer.posX, 16D));
            double y = ((cy - startZ) * 16D + MathHelperLM.wrap(mc.thePlayer.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX + x, posY + y, 0D);
            GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.thePlayer.rotationYaw + 180F, 0F, 0F, 1F);
            FTBLibClient.setTexture(TEX_ENTITY);
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
        /*
        if(ForgeWorldSP.inst != null && ForgeWorldSP.inst.clientPlayer != null)
        {
            FTBUPlayerDataSP d = FTBUPlayerData.get(ForgeWorldSP.inst.clientPlayer).toSP();

            String s = FTBULang.label_cchunks_count.translateFormatted(d.claimedChunks, d.maxClaimedChunks);
            font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 12, 0xFFFFFFFF);
            s = FTBULang.label_lchunks_count.translateFormatted(d.loadedChunks, d.maxLoadedChunks);
            font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 24, 0xFFFFFFFF);
        }
        */

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

            new MessageRequestChunkData(startX, startZ, TILES_GUI, TILES_GUI).sendToServer();
        }

        openGui();
        refreshWidgets();
    }
}