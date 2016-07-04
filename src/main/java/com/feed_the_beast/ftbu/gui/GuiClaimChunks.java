package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.ForgePlayerSPSelf;
import com.feed_the_beast.ftbl.api.ForgeTeam;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.client.gui.GuiLang;
import com.feed_the_beast.ftbl.api.client.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.client.gui.widgets.PanelLM;
import com.feed_the_beast.ftbl.net.MessageRequestSelfUpdate;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.TextureCoords;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.net.MessageAreaRequest;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataSP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import com.latmod.lib.math.MathHelperLM;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiClaimChunks extends GuiLM implements GuiYesNoCallback // implements IClientActionGui
{
    public static final int tiles_tex = 16;
    public static final int tiles_gui = 15;
    public static final double UV = (double) tiles_gui / (double) tiles_tex;
    public static final ResourceLocation TEX_ENTITY = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/entity.png");
    public static final ResourceLocation TEX_CHUNK_CLAIMING = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/chunk_claiming.png");
    public static final TextureCoords TEX_FILLED = new TextureCoords(TEX_CHUNK_CLAIMING, 0D, 0D, 0.5D, 1D);
    public static final TextureCoords TEX_BORDER = new TextureCoords(TEX_CHUNK_CLAIMING, 0.5D, 0D, 1D, 1D);

    public static int textureID = -1;
    public static ByteBuffer pixelBuffer = null;

    public class MapButton extends ButtonLM
    {
        public final ChunkDimPos chunkPos;

        public MapButton(int x, int y, int i)
        {
            super(x, y, 16, 16);
            posX += (i % tiles_gui) * width;
            posY += (i / tiles_gui) * height;
            chunkPos = new ChunkDimPos(currentDim, startX + (i % tiles_gui), startZ + (i / tiles_gui));
        }

        @Override
        public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button)
        {
            if(gui.isMouseOver(panelButtons))
            {
                return;
            }

            if(button.isLeft())
            {
                if(GuiScreen.isShiftKeyDown())
                {
                    FTBLibClient.execClientCommand("/ftb chunks load " + chunkPos.chunkXPos + ' ' + chunkPos.chunkZPos, false);
                }
                else
                {
                    FTBLibClient.execClientCommand("/ftb chunks claim " + chunkPos.chunkXPos + ' ' + chunkPos.chunkZPos, false);
                }
            }
            else if(button.isRight())
            {
                if(GuiScreen.isShiftKeyDown())
                {
                    FTBLibClient.execClientCommand("/ftb chunks unload " + chunkPos.chunkXPos + ' ' + chunkPos.chunkZPos, false);
                }
                else
                {
                    FTBLibClient.execClientCommand("/ftb chunks unclaim " + chunkPos.chunkXPos + ' ' + chunkPos.chunkZPos, false);
                }
            }

            FTBLibClient.playClickSound();
        }

        @Override
        public void addMouseOverText(GuiLM gui, List<String> l)
        {
            ClaimedChunk chunk = FTBUWorldDataSP.getChunk(chunkPos);

            if(chunk != null)
            {
                ForgeTeam team = chunk.owner.getTeam();

                if(team != null)
                {
                    l.add(team.getColor().textFormatting + team.getTitle());

                    l.add(TextFormatting.GREEN + ClaimedChunk.LANG_CLAIMED.translate());

                    if(team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly())
                    {
                        l.add(chunk.owner.getProfile().getName());

                        if(chunk.loaded)
                        {
                            l.add(TextFormatting.RED + ClaimedChunk.LANG_LOADED.translate());
                        }
                    }
                }
            }
            else
            {
                l.add(TextFormatting.DARK_GREEN + ClaimedChunk.LANG_WILDERNESS.translate());
            }
        }

        @Override
        public void renderWidget(GuiLM gui)
        {
            ClaimedChunk chunk = FTBUWorldDataSP.getChunk(chunkPos);

            double ax = getAX();
            double ay = getAY();

            if(chunk != null)
            {
                FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);

                ForgeTeam team = chunk.owner.getTeam();

                if(team != null)
                {
                    FTBLibClient.setGLColor(team.getColor().color, 180);
                }
                else
                {
                    GlStateManager.color(0F, 0F, 0F, 180F / 255F);
                }

                drawTexturedRect(ax, ay, 16, 16, TEX_FILLED.minU, TEX_FILLED.minV, TEX_FILLED.maxU, TEX_FILLED.maxV);

                GlStateManager.color((chunk.loaded && team != null && team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly()) ? 1F : 0F, chunk.isChunkOwner(ForgeWorldSP.inst.clientPlayer) ? 0.27F : 0F, 0F, 0.78F);
                drawTexturedRect(ax, ay, 16, 16, TEX_BORDER.minU, TEX_BORDER.minV, TEX_BORDER.maxU, TEX_BORDER.maxV);
            }

            if(gui.isMouseOver(this))
            {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }
    }

    public final ForgePlayerSPSelf playerLM;
    public final int startX, startZ;
    public final int currentDim;
    public final ButtonLM buttonRefresh, buttonClose, buttonUnclaimAll;
    public final MapButton mapButtons[];
    public final PanelLM panelButtons;
    public ThreadReloadArea thread = null;
    public String currentDimName;

    public GuiClaimChunks(long token)
    {
        width = height = tiles_gui * 16;

        playerLM = ForgeWorldSP.inst.clientPlayer;
        startX = MathHelperLM.chunk(mc.thePlayer.posX) - (int) (tiles_gui * 0.5D);
        startZ = MathHelperLM.chunk(mc.thePlayer.posZ) - (int) (tiles_gui * 0.5D);
        currentDim = FTBLibClient.getDim();

        currentDimName = mc.theWorld.provider.getDimensionType().getName();

        buttonClose = new ButtonLM(0, 0, 16, 16)
        {
            @Override
            public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button)
            {
                FTBLibClient.playClickSound();
                closeGui();
            }
        };

        buttonClose.title = GuiLang.button_close.translate();

        buttonRefresh = new ButtonLM(0, 16, 16, 16)
        {
            @Override
            public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button)
            {
                thread = new ThreadReloadArea(mc.theWorld, GuiClaimChunks.this);
                thread.start();
                new MessageAreaRequest(startX, startZ, tiles_gui, tiles_gui).sendToServer();
                new MessageRequestSelfUpdate().sendToServer();
                FTBLibClient.playClickSound();
            }
        };

        buttonRefresh.title = GuiLang.button_refresh.translate();

        buttonUnclaimAll = new ButtonLM(0, 32, 16, 16)
        {
            @Override
            public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button)
            {
                FTBLibClient.playClickSound();
                String s = GuiScreen.isShiftKeyDown() ? FTBULang.button_claims_unclaim_all_q.translate() : FTBULang.button_claims_unclaim_all_dim_q.translateFormatted(currentDimName);
                FTBLibClient.mc().displayGuiScreen(new GuiYesNo(GuiClaimChunks.this, s, "", GuiScreen.isShiftKeyDown() ? 1 : 0));
            }

            @Override
            public void addMouseOverText(GuiLM gui, List<String> l)
            {
                l.add(GuiScreen.isShiftKeyDown() ? FTBULang.button_claims_unclaim_all.translate() : FTBULang.button_claims_unclaim_all_dim.translateFormatted(currentDimName));
            }
        };

        panelButtons = new PanelLM(0, 0, 16, 0)
        {
            @Override
            public void addWidgets()
            {
                add(buttonClose);
                add(buttonRefresh);
                add(buttonUnclaimAll);

                height = widgets.size() * 16;
            }

            @Override
            public double getAX()
            {
                return screen.getScaledWidth_double() - 16D;
            }

            @Override
            public double getAY()
            {
                return 0D;
            }
        };

        mapButtons = new MapButton[tiles_gui * tiles_gui];
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
        if(currentDim != FTBLibClient.getDim())
        {
            mc.thePlayer.closeScreen();
            return;
        }

        super.drawBackground();

        if(textureID == -1)
        {
            textureID = TextureUtil.glGenTextures();
            new MessageAreaRequest(startX, startZ, tiles_gui, tiles_gui).sendToServer();
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
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, tiles_tex * 16, tiles_tex * 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
            pixelBuffer = null;
            thread = null;
        }

        GlStateManager.color(0F, 0F, 0F, 1F);
        drawBlankRect(posX - 2, posY - 2, width + 4, height + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if(thread == null)
        {
            GlStateManager.bindTexture(textureID);
            drawTexturedRect(posX, posY, tiles_gui * 16, tiles_gui * 16, 0D, 0D, UV, UV);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);

        for(MapButton mapButton : mapButtons)
        {
            mapButton.renderWidget(this);
        }

        int cx = MathHelperLM.chunk(mc.thePlayer.posX);
        int cy = MathHelperLM.chunk(mc.thePlayer.posZ);

        if(cx >= startX && cy >= startZ && cx < startX + tiles_gui && cy < startZ + tiles_gui)
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
            drawTexturedRect(-8D, -8D, 16D, 16D, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();
            drawPlayerHead(mc.thePlayer.getName(), -2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

        buttonRefresh.render(GuiIcons.refresh);
        buttonClose.render(GuiIcons.accept);
        buttonUnclaimAll.render(GuiIcons.remove);
    }

    @Override
    public void drawForeground()
    {
        if(ForgeWorldSP.inst != null && ForgeWorldSP.inst.clientPlayer != null)
        {
            FTBUPlayerDataSP d = FTBUPlayerData.get(ForgeWorldSP.inst.clientPlayer).toSP();

            String s = FTBULang.label_cchunks_count.translateFormatted(d.claimedChunks + " / " + d.maxClaimedChunks);
            font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 12, 0xFFFFFFFF);
            s = FTBULang.label_lchunks_count.translateFormatted(d.loadedChunks + " / " + d.maxLoadedChunks);
            font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 24, 0xFFFFFFFF);
        }

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

            new MessageAreaRequest(startX, startZ, tiles_gui, tiles_gui).sendToServer();
        }

        openGui();
        refreshWidgets();
    }
}