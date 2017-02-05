package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.IWidget;
import com.feed_the_beast.ftbl.lib.client.CachedVertexData;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.feed_the_beast.ftbu.net.MessageSendWarpList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 13.10.2016.
 */
public class GuiWarps extends GuiLM
{
    public static GuiWarps INSTANCE = null;
    private static final int SIZE = 220;
    private static final int SIZE_2 = SIZE / 2;
    private static final int SIZE_I = SIZE_2 / 4;
    private static final int SIZE_C = SIZE_2 * 3 / 5;

    private static final CachedVertexData BACKGROUND = new CachedVertexData(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
    private static final CachedVertexData CIRCLE_IN = new CachedVertexData(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
    private static final CachedVertexData CIRCLE_OUT = new CachedVertexData(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
    private static final CachedVertexData CENTER = new CachedVertexData(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

    private final ArcButton BUTTON_CANCEL = new ArcButton(MessageSendWarpList.WarpItem.CANCEL, -1);

    static
    {
        BACKGROUND.pos(0D, 0D).color(170, 170, 170, 153);
        CENTER.pos(0D, 0D).color(102, 102, 102, 255);

        for(int i = 0; i <= 360; i += 6)
        {
            double cos = Math.cos(i * MathHelperLM.RAD);
            double sin = Math.sin(i * MathHelperLM.RAD);

            BACKGROUND.pos(cos * SIZE_2, sin * SIZE_2).color(200, 200, 200, 153);
            CIRCLE_OUT.pos(cos * SIZE_2, sin * SIZE_2).color(102, 102, 102, 255);
            CIRCLE_IN.pos(cos * SIZE_C, sin * SIZE_C).color(102, 102, 102, 255);
            CENTER.pos(cos * SIZE_I, sin * SIZE_I).color(102, 102, 102, 255);
        }
    }

    private boolean isLoaded = false;
    private List<ArcButton> buttonsIn, buttonsOut;
    private ArcButton buttonOver = null;
    private CachedVertexData lines;

    private class ArcButton extends ButtonLM
    {
        private final MessageSendWarpList.WarpItem warpItem;
        private final int index;
        private int textX = -1, textY = -1;

        public ArcButton(MessageSendWarpList.WarpItem w, int i)
        {
            super(0, 0, 0, 0);
            setTitle(w.cmd);
            warpItem = w;
            index = i;
        }

        @Override
        public boolean isInside(IWidget w)
        {
            return true;
        }

        @Override
        public void renderWidget(IGui gui)
        {
            if(index == -1)
            {
                return;
            }

            if(textX == -1)
            {
                boolean in = warpItem.innerCircle();
                double s = (in ? buttonsIn : buttonsOut).size();
                double i = index * (360D / s) + (180D / s);
                double d = ((in) ? (SIZE_I + SIZE_C) : (SIZE_C + SIZE_2)) / 2D;
                textX = (int) (Math.cos(i * MathHelperLM.RAD) * d);
                textY = (int) (Math.sin(i * MathHelperLM.RAD) * d) - 2;
            }

            GuiHelper.drawCenteredString(gui.getFont(), warpItem.name, gui.getX() + gui.getWidth() / 2D + textX, gui.getY() + gui.getHeight() / 2D + textY, 0xFFFFFF);
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            if(!warpItem.cmd.isEmpty())
            {
                FTBLibClient.execClientCommand(warpItem.cmd, true);
            }

            gui.closeGui();
        }
    }

    @Override
    public boolean isMouseOver(IWidget w)
    {
        return (w instanceof ArcButton) ? w == buttonOver : super.isMouseOver(w);
    }

    public GuiWarps()
    {
        super(SIZE, SIZE);
    }

    @Override
    public void addWidgets()
    {
        if(isLoaded)
        {
            addAll(buttonsIn);
            addAll(buttonsOut);
            add(BUTTON_CANCEL);
        }
    }

    @Override
    public void drawBackground()
    {
        int ax = getAX() + SIZE_2;
        int ay = getAY() + SIZE_2;

        /*if(FTBUClient.KEY_WARP.isKeyDown())
        {
            return;
        }*/

        if(!isLoaded)
        {
            GuiLoading.renderLoading(ax - SIZE_2, ay - SIZE_2, SIZE, SIZE);
            return;
        }

        GlStateManager.glLineWidth(2F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableCull();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        buffer.setTranslation(ax, ay, 0D);
        BACKGROUND.draw(tessellator, buffer);
        CIRCLE_IN.draw(tessellator, buffer);
        CIRCLE_OUT.draw(tessellator, buffer);
        CENTER.draw(tessellator, buffer);

        if(isLoaded)
        {
            lines.draw(tessellator, buffer);
        }

        buttonOver = BUTTON_CANCEL;
        double dist = MathHelperLM.dist(ax, ay, getMouseX(), getMouseY());
        double rotation = Math.atan2(getMouseY() - ay, getMouseX() - ax) * MathHelperLM.DEG;

        if(rotation < 0D)
        {
            rotation += 360D;
        }

        GlStateManager.glLineWidth(1F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
        buffer.setTranslation(0D, 0D, 0D);

        if(isLoaded)
        {
            if(dist < SIZE_I || dist > SIZE_2)
            {
                GuiHelper.drawCenteredString(getFont(), TextFormatting.BOLD + GuiLang.BUTTON_CANCEL.translate(), ax, ay, 0xFFFFFF);
            }
            else
            {
                if(dist < SIZE_C)
                {
                    buttonOver = buttonsIn.get(((int) (rotation / (360D / buttonsIn.size()))) % buttonsIn.size());
                }
                else
                {
                    buttonOver = buttonsOut.get(((int) (rotation / (360D / buttonsOut.size()))) % buttonsOut.size());
                }
            }

            if(buttonOver.warpItem.isSpecial())
            {
                GuiHelper.drawCenteredString(getFont(), TextFormatting.BOLD + buttonOver.warpItem.name, ax, ay, 0xFFFFFF);
            }
            else
            {
                GuiHelper.drawCenteredString(getFont(), TextFormatting.BOLD + (buttonOver.warpItem.innerCircle() ? "Home" : "Warp"), ax, ay - 5, 0xFFFFFF);
                GuiHelper.drawCenteredString(getFont(), buttonOver.warpItem.name, ax, ay + 5, 0xFFFFFF);
            }
        }
    }

    public void setData(List<MessageSendWarpList.WarpItem> list)
    {
        buttonsIn = new ArrayList<>();
        buttonsOut = new ArrayList<>();

        for(MessageSendWarpList.WarpItem w : list)
        {
            List<ArcButton> l = (w.innerCircle() ? buttonsIn : buttonsOut);
            l.add(new ArcButton(w, l.size()));
        }

        lines = new CachedVertexData(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        int add = 360 / buttonsOut.size();
        for(int i = 0; i < 360; i += add)
        {
            double cos = Math.cos(i * MathHelperLM.RAD);
            double sin = Math.sin(i * MathHelperLM.RAD);

            lines.pos(cos * SIZE_C, sin * SIZE_C, 0D).color(102, 102, 102, 255);
            lines.pos(cos * SIZE_2, sin * SIZE_2, 0D).color(102, 102, 102, 255);
        }

        add = 360 / buttonsIn.size();
        for(int i = 0; i < 360; i += add)
        {
            double cos = Math.cos(i * MathHelperLM.RAD);
            double sin = Math.sin(i * MathHelperLM.RAD);

            lines.pos(cos * SIZE_I, sin * SIZE_I, 0D).color(102, 102, 102, 255);
            lines.pos(cos * SIZE_C, sin * SIZE_C, 0D).color(102, 102, 102, 255);
        }

        isLoaded = true;
        refreshWidgets();
    }

    @Override
    public boolean drawDefaultBackground()
    {
        return false;
    }
}
