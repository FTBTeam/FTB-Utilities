package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.FTBLibCapabilities;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.client.CubeRenderer;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.LMFrustrumUtils;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import latmod.lib.LMColorUtils;
import latmod.lib.MathHelperLM;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.HashSet;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler
{
    public static final ResourceLocation chunkBorderTexture = new ResourceLocation(FTBUFinals.MOD_ID, "textures/world/chunk_border.png");
    public static final ResourceLocation textureLightValueX = new ResourceLocation(FTBUFinals.MOD_ID, "textures/world/light_value_x.png");
    public static final ResourceLocation textureLightValueO = new ResourceLocation(FTBUFinals.MOD_ID, "textures/world/light_value_o.png");

    private static class MobSpawnPos
    {
        public final BlockPos pos;
        public final boolean alwaysSpawns;
        public final int lightValue;

        public MobSpawnPos(BlockPos p, boolean b, int lv)
        {
            pos = p;
            alwaysSpawns = b;
            lightValue = lv;
        }

        @Override
        public int hashCode()
        {
            return pos.hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            return o == this || ((MobSpawnPos) o).pos.equals(pos);
        }
    }

    public boolean renderChunkBounds = false;
    private CubeRenderer chunkBorderRenderer = new CubeRenderer().setHasTexture().setHasNormals().setHasColor();
    private Collection<MobSpawnPos> lightList = new HashSet<>();
    private boolean renderLightValues = false, needsLightUpdate = true;
    private int lastX, lastY = -1, lastZ;

    public void toggleLightLevel()
    {
        renderLightValues = !renderLightValues;
        needsLightUpdate = renderLightValues;

        if(!renderLightValues)
        {
            needsLightUpdate = false;
            lightList.clear();
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e)
    {
        if(e.getItemStack().hasCapability(FTBLibCapabilities.PAINTER_ITEM, null))
        {
            IBlockState paint = e.getItemStack().getCapability(FTBLibCapabilities.PAINTER_ITEM, null).getPaint();

            if(paint != null)
            {
                e.getToolTip().add(String.valueOf(TextFormatting.WHITE) + TextFormatting.BOLD + new ItemStack(paint.getBlock(), 1, paint.getBlock().getMetaFromState(paint)).getDisplayName() + TextFormatting.RESET);
            }
        }

        /*
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
        */
    }

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if(FTBUClient.KEY_GUIDE.isPressed())
        {
            FTBUActions.GUIDE.onClicked(ForgeWorldSP.inst.clientPlayer);
        }

        if(FTBUClient.KEY_CHUNK_BORDER.isPressed())
        {
            renderChunkBounds = !renderChunkBounds;
        }

        if(FTBUClient.KEY_LIGHT_VALUES.isPressed())
        {
            toggleLightLevel();
        }
    }

    @SubscribeEvent
    public void blockChanged(BlockEvent e)
    {
        if(MathHelperLM.distSq(e.getPos().getX() + 0.5D, e.getPos().getY() + 0.5D, e.getPos().getZ() + 0.5D, lastX + 0.5D, lastY + 0.5D, lastZ + 0.5D) <= 4096D)
        {
            needsLightUpdate = true;
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e)
    {
        if(renderChunkBounds || renderLightValues)
        {
            Minecraft mc = FTBLibClient.mc();

            GlStateManager.pushMatrix();
            GlStateManager.translate(-LMFrustrumUtils.renderX, -LMFrustrumUtils.renderY, -LMFrustrumUtils.renderZ);

            FTBLibClient.pushMaxBrightness();

            GlStateManager.enableBlend();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableCull();
            GlStateManager.depthMask(false);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);

            if(renderChunkBounds)
            {
                int x = MathHelperLM.chunk(LMFrustrumUtils.playerX);
                int z = MathHelperLM.chunk(LMFrustrumUtils.playerZ);
                double d = 0.007D;

                FTBLibClient.setTexture(chunkBorderTexture);
                chunkBorderRenderer.setTessellator(Tessellator.getInstance());
                chunkBorderRenderer.setUV(0D, 0D, 16D, 256D);

                for(int cz = z - 1; cz <= z + 1; cz++)
                {
                    for(int cx = x - 1; cx <= x + 1; cx++)
                    {
                        ClaimedChunk chunk = FTBUWorldDataSP.getChunk(new ChunkDimPos(0, cx, cz));
                        chunkBorderRenderer.setSize(cx * 16D + d, 0D, cz * 16D + d, cx * 16D + 16D - d, 256D, cz * 16D + 16D - d);

                        if(chunk == null)
                        {
                            chunkBorderRenderer.color.setRGBA(0, 160, 16, (cx == x && cz == z) ? 200 : 100);
                        }
                        else
                        {
                            int col = chunk.owner.hasTeam() ? chunk.owner.getTeam().getColor().color : 0;
                            chunkBorderRenderer.color.setRGBA(LMColorUtils.getRed(col), LMColorUtils.getGreen(col), LMColorUtils.getBlue(col), (cx == x && cz == z) ? 200 : 100);
                        }

                        chunkBorderRenderer.renderSides();
                    }
                }
            }

            if(renderLightValues && LMFrustrumUtils.playerY >= 0D)
            {
                if(lastY == -1D || MathHelperLM.distSq(LMFrustrumUtils.playerX, LMFrustrumUtils.playerY, LMFrustrumUtils.playerZ, lastX + 0.5D, lastY + 0.5D, lastZ + 0.5D) >= MathHelperLM.SQRT_2 * 2D)
                {
                    needsLightUpdate = true;
                }

                if(needsLightUpdate)
                {
                    needsLightUpdate = false;
                    lightList.clear();

                    lastX = MathHelperLM.floor(LMFrustrumUtils.playerX);
                    lastY = MathHelperLM.floor(LMFrustrumUtils.playerY);
                    lastZ = MathHelperLM.floor(LMFrustrumUtils.playerZ);

                    for(int by = lastY - 16; by <= lastY + 16; by++)
                    {
                        for(int bx = lastX - 16; bx <= lastX + 16; bx++)
                        {
                            for(int bz = lastZ - 16; bz <= lastZ + 16; bz++)
                            {
                                BlockPos pos = new BlockPos(bx, by, bz);
                                Boolean b = FTBLib.canMobSpawn(mc.theWorld, pos);
                                if(b != null)
                                {
                                    int lv = 0;
                                    if(mc.gameSettings.showDebugInfo)
                                    {
                                        lv = mc.theWorld.getLight(pos, true);
                                    }
                                    lightList.add(new MobSpawnPos(pos, b == Boolean.TRUE, lv));
                                }
                            }
                        }
                    }
                }

                if(!lightList.isEmpty())
                {
                    GlStateManager.enableCull();
                    GlStateManager.color(1F, 1F, 1F, 1F);
                    FTBLibClient.setTexture(FTBUClient.light_value_texture_x.getAsBoolean() ? textureLightValueX : textureLightValueO);

                    Tessellator tessellator = Tessellator.getInstance();
                    VertexBuffer buffer = tessellator.getBuffer();

                    buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                    for(MobSpawnPos pos : lightList)
                    {
                        double bx = pos.pos.getX();
                        double by = pos.pos.getY() + 0.03D;
                        double bz = pos.pos.getZ();

                        float green = pos.alwaysSpawns ? 0.2F : 1F;
                        buffer.pos(bx, by, bz).tex(0D, 0D).color(1F, green, 0.2F, 1F).endVertex();
                        buffer.pos(bx, by, bz + 1D).tex(0D, 1D).color(1F, green, 0.2F, 1F).endVertex();
                        buffer.pos(bx + 1D, by, bz + 1D).tex(1D, 1D).color(1F, green, 0.2F, 1F).endVertex();
                        buffer.pos(bx + 1D, by, bz).tex(1D, 0D).color(1F, green, 0.2F, 1F).endVertex();
                    }

                    tessellator.draw();

                    GlStateManager.color(1F, 1F, 1F, 1F);

                    if(mc.gameSettings.showDebugInfo)
                    {
                        for(MobSpawnPos pos : lightList)
                        {
                            double bx = pos.pos.getX() + 0.5D;
                            double by = pos.pos.getY() + 0.14D;
                            double bz = pos.pos.getZ() + 0.5D;

                            if(MathHelperLM.distSq(LMFrustrumUtils.playerX, LMFrustrumUtils.playerY, LMFrustrumUtils.playerZ, bx, by, bz) <= 144D)
                            {
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(bx, by, bz);
                                GlStateManager.rotate((float) (-Math.atan2(bz - LMFrustrumUtils.playerZ, bx - LMFrustrumUtils.playerX) * 180D / Math.PI) + 90F, 0F, 1F, 0F);
                                GlStateManager.rotate(40F, 1F, 0F, 0F);

                                float scale = 1F / 32F;
                                GlStateManager.scale(-scale, -scale, 1F);

                                String s = Integer.toString(pos.lightValue);
                                mc.fontRendererObj.drawString(s, -mc.fontRendererObj.getStringWidth(s) / 2, -5, 0xFFFFFFFF);
                                GlStateManager.popMatrix();
                            }
                        }
                    }
                }
            }

            GlStateManager.color(1F, 1F, 1F, 1F);

            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.shadeModel(GL11.GL_FLAT);

            FTBLibClient.popMaxBrightness();
            GlStateManager.popMatrix();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
    }
}