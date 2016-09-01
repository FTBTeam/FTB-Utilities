package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.guide.local.InfoPageLocalGuideRepoList;
import com.latmod.lib.math.MathHelperLM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
    private static final ResourceLocation TEXTURE_LIGHT_VALUE_X = new ResourceLocation(FTBUFinals.MOD_ID, "textures/world/light_value_x.png");
    private static final ResourceLocation TEXTURE_LIGHT_VALUE_O = new ResourceLocation(FTBUFinals.MOD_ID, "textures/world/light_value_o.png");

    private static class MobSpawnPos extends BlockPos
    {
        private final boolean alwaysSpawns;
        private final int lightValue;

        public MobSpawnPos(BlockPos p, boolean b, int lv)
        {
            super(p);
            alwaysSpawns = b;
            lightValue = lv;
        }
    }

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

    /*
    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent e)
    {
        if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
        {
            e.toolTip.add(EnumChatFormatting.RED + "Banned item");
        }
    }
    */

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if(FTBUClient.KEY_GUIDE.isPressed())
        {
            InfoPageLocalGuideRepoList.openGui();
        }

        if(FTBUClient.KEY_CHUNK_BORDER.isPressed())
        {
            Minecraft.getMinecraft().debugRenderer.func_190075_b();
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
        if(Minecraft.getMinecraft().debugRenderer.func_190074_a() || renderLightValues)
        {
            Minecraft mc = Minecraft.getMinecraft();
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();

            GlStateManager.pushMatrix();
            GlStateManager.translate(-FTBLibClient.renderX, -FTBLibClient.renderY, -FTBLibClient.renderZ);

            FTBLibClient.pushMaxBrightness();

            GlStateManager.enableBlend();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.enableCull();
            GlStateManager.depthMask(false);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);

            if(Minecraft.getMinecraft().debugRenderer.func_190074_a())
            {
                int x = MathHelperLM.chunk(FTBLibClient.playerX);
                int z = MathHelperLM.chunk(FTBLibClient.playerZ);
                GlStateManager.cullFace(GlStateManager.CullFace.BACK);
                GlStateManager.disableCull();
                GlStateManager.disableTexture2D();
                GlStateManager.glLineWidth(2F);
                buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(x * 16D + 8D, 0D, z * 16D + 8D).color(0F, 0.63F, 0.06F, 1F).endVertex();
                buffer.pos(x * 16D + 8D, 256D, z * 16D + 8D).color(0F, 0.63F, 0.06F, 1F).endVertex();
                tessellator.draw();
                GlStateManager.glLineWidth(1F);
                GlStateManager.enableCull();
                GlStateManager.enableTexture2D();
            }

            if(renderLightValues && FTBLibClient.playerY >= 0D)
            {
                if(lastY == -1D || MathHelperLM.distSq(FTBLibClient.playerX, FTBLibClient.playerY, FTBLibClient.playerZ, lastX + 0.5D, lastY + 0.5D, lastZ + 0.5D) >= MathHelperLM.SQRT_2 * 2D)
                {
                    needsLightUpdate = true;
                }

                if(needsLightUpdate)
                {
                    needsLightUpdate = false;
                    lightList.clear();

                    lastX = MathHelper.floor_double(FTBLibClient.playerX);
                    lastY = MathHelper.floor_double(FTBLibClient.playerY);
                    lastZ = MathHelper.floor_double(FTBLibClient.playerZ);

                    for(int by = lastY - 20; by <= lastY + 3; by++)
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
                    GlStateManager.color(1F, 1F, 1F, 1F);
                    FTBLibClient.setTexture(FTBUClient.light_value_texture_x.getAsBoolean() ? TEXTURE_LIGHT_VALUE_X : TEXTURE_LIGHT_VALUE_O);

                    buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                    for(MobSpawnPos pos : lightList)
                    {
                        double bx = pos.getX();
                        double by = pos.getY() + 0.03D;
                        double bz = pos.getZ();

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
                            double bx = pos.getX() + 0.5D;
                            double by = pos.getY() + 0.14D;
                            double bz = pos.getZ() + 0.5D;

                            if(MathHelperLM.distSq(FTBLibClient.playerX, FTBLibClient.playerY, FTBLibClient.playerZ, bx, by, bz) <= 144D)
                            {
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(bx, by, bz);
                                GlStateManager.rotate((float) (-Math.atan2(bz - FTBLibClient.playerZ, bx - FTBLibClient.playerX) * 180D / Math.PI) + 90F, 0F, 1F, 0F);
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

            GlStateManager.depthMask(true);
            GlStateManager.shadeModel(GL11.GL_FLAT);

            FTBLibClient.popMaxBrightness();
            GlStateManager.popMatrix();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        }
    }
}