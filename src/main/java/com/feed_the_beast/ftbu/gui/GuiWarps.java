package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
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
    public static final int SIZE = 200;

    public boolean hasLoaded = false;
    public List<String> homes;
    public List<String> warps;
    private double size = 0D;
    private int state = 0;

    public GuiWarps()
    {
        super(SIZE, SIZE);
    }

    @Override
    public void addWidgets()
    {
    }

    public void drawBackground()
    {
        if(state == 0)
        {
            size += 0.09D;
            if(size >= 1D)
            {
                size = 1D;
                state = 1;
            }
        }
        else if(state == 2)
        {
            size -= 0.09D;

            if(size <= 0D)
            {
                INSTANCE = null;
                closeGui();
                return;
            }
        }

        size = 1D;

        int SIZE_2 = SIZE / 2;

        int ax = getAX() + SIZE_2;
        int ay = getAY() + SIZE_2;

        GlStateManager.disableTexture2D();
        GlStateManager.color(1F, 1F, 1F, 1F);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(ax, ay, 0D).color(170, 170, 170, 153).endVertex();

        for(int i = 0; i <= 360; i += 6)
        {
            float f = (float) (i * MathHelperLM.RAD);
            buffer.pos(ax + MathHelper.cos(f) * size * SIZE_2, ay + MathHelper.sin(f) * size * SIZE_2, 0D).color(200, 200, 200, 153).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();

        getFont().drawString("WIP!", 20, 20, 0xFFFFFF);

        if(hasLoaded)
        {
        }
        else
        {

        }
    }
}
