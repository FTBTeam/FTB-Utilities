package com.feed_the_beast.ftbu.client.gui.claims;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerSPSelf;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.GuiLang;
import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.gui.widgets.PanelLM;
import com.feed_the_beast.ftbl.net.MessageRequestSelfUpdate;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.net.MessageAreaRequest;
import com.feed_the_beast.ftbu.net.MessageClaimChunk;
import com.feed_the_beast.ftbu.world.ChunkType;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataSP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import latmod.lib.MathHelperLM;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiClaimChunks extends GuiLM implements GuiYesNoCallback // implements IClientActionGui
{
    public static final int tiles_tex = 16;
    public static final int tiles_gui = 15;
    public static final double UV = (double) tiles_gui / (double) tiles_tex;
    public static final ResourceLocation tex_map_entity = new ResourceLocation("ftbl", "textures/world/entity.png");
    public static int textureID = -1;
    public static ByteBuffer pixelBuffer = null;

    public static class MapButton extends ButtonLM
    {
        public final GuiClaimChunks gui;
        public final ChunkDimPos chunkPos;

        public MapButton(GuiClaimChunks g, int x, int y, int i)
        {
            super(g, x, y, 16, 16);
            gui = g;
            posX += (i % tiles_gui) * width;
            posY += (i / tiles_gui) * height;
            chunkPos = new ChunkDimPos(gui.currentDim, g.startX + (i % tiles_gui), g.startY + (i / tiles_gui));
        }

        @Override
        public void onClicked(MouseButton button)
        {
            if(gui.panelButtons.mouseOver()) { return; }
            if(gui.adminToken != 0L && button.isLeft()) { return; }
            boolean ctrl = FTBUClient.loaded_chunks_space_key.getAsBoolean() ? Keyboard.isKeyDown(Keyboard.KEY_SPACE) : isCtrlKeyDown();

            MessageClaimChunk msg = new MessageClaimChunk();
            msg.token = gui.adminToken;
            msg.pos = chunkPos;
            msg.type = button.isLeft() ? (ctrl ? MessageClaimChunk.ID_LOAD : MessageClaimChunk.ID_CLAIM) : (ctrl ? MessageClaimChunk.ID_UNLOAD : MessageClaimChunk.ID_UNCLAIM);
            msg.sendToServer();
            FTBLibClient.playClickSound();
        }

        @Override
        public void addMouseOverText(List<String> l)
        {
            ChunkType type = FTBUWorldDataSP.getType(chunkPos);

            if(type != ChunkType.UNLOADED)
            {
                l.add(type.getChatColor(null) + type.langKey.translate());
                ChunkType.PlayerClaimed pc = type.asClaimed();

                if(pc != null)
                {
                    ForgePlayer owner = pc.chunk.getOwner();

                    if(owner != null)
                    {
                        l.add(type.getChatColor(owner) + owner.getProfile().getName());
                        if(pc.chunk.getFlag(ClaimedChunk.CHUNKLOADED))
                        {
                            l.add(TextFormatting.GOLD + I18n.format("ftbu.chunktype.chunkloaded"));
                        }
                    }
                }
            }
        }

        @Override
        public void renderWidget()
        {
            if(mouseOver())
            {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                drawBlankRect(getAX(), getAY(), gui.getZLevel(), 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }
    }

    public final long adminToken;
    public final ForgePlayerSPSelf playerLM;
    public final int startX, startY;
    public final int currentDim;
    public final ButtonLM buttonRefresh, buttonClose, buttonUnclaimAll;
    public final MapButton mapButtons[];
    public final PanelLM panelButtons;
    public ThreadReloadArea thread = null;
    public String currentDimName;

    public GuiClaimChunks(long token)
    {
        super(null, null);
        mainPanel.width = mainPanel.height = tiles_gui * 16;

        adminToken = token;
        playerLM = ForgeWorldSP.inst.clientPlayer;
        startX = MathHelperLM.chunk(mc.thePlayer.posX) - (int) (tiles_gui * 0.5D);
        startY = MathHelperLM.chunk(mc.thePlayer.posZ) - (int) (tiles_gui * 0.5D);
        currentDim = FTBLibClient.getDim();

        currentDimName = mc.theWorld.provider.getDimensionType().getName();

        buttonClose = new ButtonLM(this, 0, 0, 16, 16)
        {
            @Override
            public void onClicked(MouseButton button)
            {
                FTBLibClient.playClickSound();
                FTBLibClient.openGui(null);
            }
        };

        buttonRefresh = new ButtonLM(this, 0, 16, 16, 16)
        {
            @Override
            public void onClicked(MouseButton button)
            {
                thread = new ThreadReloadArea(mc.theWorld, GuiClaimChunks.this);
                thread.start();
                new MessageAreaRequest(startX, startY, tiles_gui, tiles_gui).sendToServer();
                new MessageRequestSelfUpdate().sendToServer();
                FTBLibClient.playClickSound();
            }
        };

        buttonRefresh.title = GuiLang.button_refresh.translate();

        buttonUnclaimAll = new ButtonLM(this, 0, 32, 16, 16)
        {
            @Override
            public void onClicked(MouseButton button)
            {
                FTBLibClient.playClickSound();
                String s = isShiftKeyDown() ? FTBULang.button_claims_unclaim_all_q.translate() : FTBULang.button_claims_unclaim_all_dim_q.translateFormatted(currentDimName);
                FTBLibClient.openGui(new GuiYesNo(GuiClaimChunks.this, s, "", isShiftKeyDown() ? 1 : 0));
            }

            @Override
            public void addMouseOverText(List<String> l)
            {
                l.add(isShiftKeyDown() ? FTBULang.button_claims_unclaim_all.translate() : FTBULang.button_claims_unclaim_all_dim.translateFormatted(currentDimName));
            }
        };

        panelButtons = new PanelLM(this, 0, 0, 16, 0)
        {
            @Override
            public void addWidgets()
            {
                add(buttonClose);
                add(buttonRefresh);

                if(adminToken == 0L)
                {
                    add(buttonUnclaimAll);
                }

                height = widgets.size() * 16;
            }

            @Override
            public int getAX()
            { return gui.getGui().width - 16; }

            @Override
            public int getAY()
            { return 0; }
        };

        mapButtons = new MapButton[tiles_gui * tiles_gui];
        for(int i = 0; i < mapButtons.length; i++)
        {
            mapButtons[i] = new MapButton(this, 0, 0, i);
        }
    }

    @Override
    public void initLMGui()
    {
        buttonRefresh.onClicked(MouseButton.LEFT);
    }

    @Override
    public void addWidgets()
    {
        mainPanel.addAll(mapButtons);
        mainPanel.add(panelButtons);
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
            new MessageAreaRequest(startX, startY, tiles_gui, tiles_gui).sendToServer();
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
        drawBlankRect(mainPanel.posX - 2, mainPanel.posY - 2, zLevel, mainPanel.width + 4, mainPanel.height + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if(thread == null)
        {
            GlStateManager.bindTexture(textureID);
            drawTexturedRectD(mainPanel.posX, mainPanel.posY, zLevel, tiles_gui * 16, tiles_gui * 16, 0D, 0D, UV, UV);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        //setTexture(tex);

        GlStateManager.disableTexture2D();

        for(int y = 0; y < tiles_gui; y++)
        {
            for(int x = 0; x < tiles_gui; x++)
            {
                int cx = x + startX;
                int cy = y + startY;

                ChunkType type = FTBUWorldDataSP.getType(new ChunkDimPos(currentDim, cx, cy));

                if(type.drawGrid())
                {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                    FTBLibClient.setGLColor(type.getAreaColor(playerLM), 180);
                    GuiLM.drawTexturedRectD(mainPanel.getAX() + x * 16, mainPanel.getAY() + y * 16, zLevel, 16, 16, 0D, 0D, 1D, 1D);
                }
            }
        }

        GlStateManager.enableTexture2D();

        int cx = MathHelperLM.chunk(mc.thePlayer.posX);
        int cy = MathHelperLM.chunk(mc.thePlayer.posZ);

        if(cx >= startX && cy >= startY && cx < startX + tiles_gui && cy < startY + tiles_gui)
        {
            double x = ((cx - startX) * 16D + MathHelperLM.wrap(mc.thePlayer.posX, 16D));
            double y = ((cy - startY) * 16D + MathHelperLM.wrap(mc.thePlayer.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(mainPanel.posX + x, mainPanel.posY + y, 0D);
            GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.thePlayer.rotationYaw + 180F, 0F, 0F, 1F);
            FTBLibClient.setTexture(tex_map_entity);
            GlStateManager.color(1F, 1F, 1F, mc.thePlayer.isSneaking() ? 0.4F : 0.7F);
            GuiLM.drawTexturedRectD(-8, -8, zLevel, 16, 16, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();
            GuiLM.drawPlayerHead(mc.thePlayer.getName(), -2, -2, 4, 4, zLevel);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.color(1F, 1F, 1F, 1F);
        for(MapButton mapButton : mapButtons) { mapButton.renderWidget(); }
        GlStateManager.color(1F, 1F, 1F, 1F);

        buttonRefresh.render(GuiIcons.refresh);
        buttonClose.render(GuiIcons.accept);

        if(adminToken == 0L)
        {
            buttonUnclaimAll.render(GuiIcons.remove);
        }
    }

    @Override
    public void drawText(List<String> l)
    {
        FTBUPlayerDataSP d = FTBUPlayerData.get(ForgeWorldSP.inst.clientPlayer).toSP();

        String s = FTBULang.label_cchunks_count.translateFormatted(d.claimedChunks + " / " + d.maxClaimedChunks);
        fontRendererObj.drawString(s, width - fontRendererObj.getStringWidth(s) - 4, height - 12, 0xFFFFFFFF);
        s = FTBULang.label_lchunks_count.translateFormatted(d.loadedChunks + " / " + d.maxLoadedChunks);
        fontRendererObj.drawString(s, width - fontRendererObj.getStringWidth(s) - 4, height - 24, 0xFFFFFFFF);

        super.drawText(l);
    }

    @Override
    public void onLMGuiClosed()
    {
    }

    @Override
    public void confirmClicked(boolean set, int id)
    {
        if(set && adminToken == 0L)
        {
            MessageClaimChunk msg = new MessageClaimChunk();
            msg.token = GuiClaimChunks.this.adminToken;
            msg.pos = new ChunkDimPos(GuiClaimChunks.this.currentDim, 0, 0);
            msg.type = (id == 1) ? MessageClaimChunk.ID_UNCLAIM_ALL_DIMS : MessageClaimChunk.ID_UNCLAIM_ALL;
            msg.sendToServer();
            new MessageAreaRequest(startX, startY, tiles_gui, tiles_gui).sendToServer();
        }

        FTBLibClient.openGui(this);
        refreshWidgets();
    }
}