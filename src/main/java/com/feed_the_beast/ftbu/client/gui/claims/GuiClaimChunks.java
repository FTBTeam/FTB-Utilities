package com.feed_the_beast.ftbu.client.gui.claims;

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
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.client.FTBUClient;
import com.feed_the_beast.ftbu.net.MessageAreaRequest;
import com.feed_the_beast.ftbu.net.MessageClaimChunk;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataSP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import latmod.lib.MathHelperLM;
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
    public static final ResourceLocation tex_map_entity = new ResourceLocation(FTBUFinals.MOD_ID, "textures/world/entity.png");
    public static int textureID = -1;
    public static ByteBuffer pixelBuffer = null;

    public class MapButton extends ButtonLM
    {
        public final ChunkDimPos chunkPos;

        public MapButton(int x, int y, int i)
        {
            super(x, y, 16, 16);
            posX += (i % tiles_gui) * widthW;
            posY += (i / tiles_gui) * heightW;
            chunkPos = new ChunkDimPos(currentDim, startX + (i % tiles_gui), startZ + (i / tiles_gui));
        }

        @Override
        public void onClicked(GuiLM gui, MouseButton button)
        {
            if(gui.isMouseOver(panelButtons))
            {
                return;
            }
            if(adminToken != 0L && button.isLeft())
            {
                return;
            }
            boolean ctrl = FTBUClient.loaded_chunks_space_key.getAsBoolean() ? Keyboard.isKeyDown(Keyboard.KEY_SPACE) : GuiScreen.isCtrlKeyDown();

            MessageClaimChunk msg = new MessageClaimChunk();
            msg.token = adminToken;
            msg.pos = chunkPos;
            msg.type = button.isLeft() ? (ctrl ? MessageClaimChunk.ID_LOAD : MessageClaimChunk.ID_CLAIM) : (ctrl ? MessageClaimChunk.ID_UNLOAD : MessageClaimChunk.ID_UNCLAIM);
            msg.sendToServer();
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
                ForgeTeam team = chunk.owner.getTeam();

                if(team != null)
                {
                    FTBLibClient.setGLColor(team.getColor().color, 180);
                }
                else
                {
                    GlStateManager.color(0F, 0F, 0F, 180F / 255F);
                }

                drawBlankRect(ax, ay, 16, 16);

                GlStateManager.disableTexture2D();
                GlStateManager.color(1F, 1F, 1F, 1F);

                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexBuffer = tessellator.getBuffer();

                int red = (chunk.loaded && team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly()) ? 255 : 0;
                int green = chunk.isChunkOwner(ForgeWorldSP.inst.clientPlayer) ? 70 : 0;

                vertexBuffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                vertexBuffer.pos(ax, ay, 0D).color(red, green, 0, 200).endVertex();
                vertexBuffer.pos(ax + 16D, ay, 0D).color(red, green, 0, 200).endVertex();
                vertexBuffer.pos(ax + 16D, ay + 16D, 0D).color(red, green, 0, 200).endVertex();
                vertexBuffer.pos(ax, ay + 16D, 0D).color(red, green, 0, 200).endVertex();
                vertexBuffer.pos(ax, ay, 0D).color(red, green, 0, 200).endVertex();
                tessellator.draw();
            }

            if(gui.isMouseOver(this))
            {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }
    }

    public final long adminToken;
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
        widthW = heightW = tiles_gui * 16;

        adminToken = token;
        playerLM = ForgeWorldSP.inst.clientPlayer;
        startX = MathHelperLM.chunk(mc.thePlayer.posX) - (int) (tiles_gui * 0.5D);
        startZ = MathHelperLM.chunk(mc.thePlayer.posZ) - (int) (tiles_gui * 0.5D);
        currentDim = FTBLibClient.getDim();

        currentDimName = mc.theWorld.provider.getDimensionType().getName();

        buttonClose = new ButtonLM(0, 0, 16, 16)
        {
            @Override
            public void onClicked(GuiLM gui, MouseButton button)
            {
                FTBLibClient.playClickSound();
                closeGui();
            }
        };

        buttonClose.title = GuiLang.button_close.translate();

        buttonRefresh = new ButtonLM(0, 16, 16, 16)
        {
            @Override
            public void onClicked(GuiLM gui, MouseButton button)
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
            public void onClicked(GuiLM gui, MouseButton button)
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

                if(adminToken == 0L)
                {
                    add(buttonUnclaimAll);
                }

                heightW = widgets.size() * 16;
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
        for(int i = 0; i < mapButtons.length; i++)
        {
            add(mapButtons[i]);
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
        drawBlankRect(posX - 2, posY - 2, widthW + 4, heightW + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if(thread == null)
        {
            GlStateManager.bindTexture(textureID);
            drawTexturedRect(posX, posY, tiles_gui * 16, tiles_gui * 16, 0D, 0D, UV, UV);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        //setTexture(tex);

        for(MapButton mapButton : mapButtons)
        {
            mapButton.renderWidget(this);
        }

        GlStateManager.enableTexture2D();

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
            FTBLibClient.setTexture(tex_map_entity);
            GlStateManager.color(1F, 1F, 1F, mc.thePlayer.isSneaking() ? 0.4F : 0.7F);
            drawTexturedRect(-8D, -8D, 16D, 16D, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();
            drawPlayerHead(mc.thePlayer.getName(), -2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

        buttonRefresh.render(GuiIcons.refresh);
        buttonClose.render(GuiIcons.accept);

        if(adminToken == 0L)
        {
            buttonUnclaimAll.render(GuiIcons.remove);
        }
    }

    @Override
    public void drawForeground()
    {
        FTBUPlayerDataSP d = FTBUPlayerData.get(ForgeWorldSP.inst.clientPlayer).toSP();

        String s = FTBULang.label_cchunks_count.translateFormatted(d.claimedChunks + " / " + d.maxClaimedChunks);
        font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 12, 0xFFFFFFFF);
        s = FTBULang.label_lchunks_count.translateFormatted(d.loadedChunks + " / " + d.maxLoadedChunks);
        font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 24, 0xFFFFFFFF);

        super.drawForeground();
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
            new MessageAreaRequest(startX, startZ, tiles_gui, tiles_gui).sendToServer();
        }

        openGui();
        refreshWidgets();
    }
}