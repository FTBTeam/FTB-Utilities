package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbu.client.FTBUClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by LatvianModder on 13.10.2016.
 */
public class GuiWarps extends GuiLM
{
    public static GuiWarps INSTANCE = null;
    private static final int SIZE = 200;
    private static final int SIZE_2 = SIZE / 2;

    public boolean hasLoaded = false;
    public List<String> homes;
    public List<String> warps;

    public GuiWarps()
    {
        super(SIZE, SIZE);
    }

    @Override
    public void addWidgets()
    {
    }

    @Override
    public void drawBackground()
    {
        int ax = getAX() + SIZE_2;
        int ay = getAY() + SIZE_2;

        if(FTBUClient.KEY_WARP.isKeyDown())
        {
            return;
        }

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(ax, ay, 0D).color(170, 170, 170, 153).endVertex();

        for(int i = 0; i <= 360; i += 6)
        {
            float f = (float) (i * MathHelperLM.RAD);
            buffer.pos(ax + MathHelper.cos(f) * SIZE_2, ay + MathHelper.sin(f) * SIZE_2, 0D).color(200, 200, 200, 153).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();

        if(hasLoaded)
        {
            getFont().drawString("WIP!", 20, 20, 0xFFFFFF);
        }
        else
        {
            GuiLoading.renderLoading(ax - SIZE_2, ay - SIZE_2, SIZE, SIZE);
        }
    }

    @Override
    public boolean drawDefaultBackground()
    {
        return false;
    }
}
