package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.client.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.info.InfoPage;
import com.feed_the_beast.ftbl.gui.info.GuiInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.AbstractMap;

//FIXME
@SideOnly(Side.CLIENT)
public class ClientGuideFile extends InfoPage
{
    public static final ClientGuideFile INSTANCE = new ClientGuideFile();

    public static GuiScreen clientGuideGui = null;

    public ClientGuideFile()
    {
        //setTitle(FTBUActions.GUIDE.displayName);
    }

    public static GuiScreen openClientGui(boolean open)
    {
        if(clientGuideGui == null)
        {
            INSTANCE.reload();
            clientGuideGui = new GuiInfo(null, new AbstractMap.SimpleEntry<>("client_guide", ClientGuideFile.INSTANCE)).getWrapper();
        }
        if(open)
        {
            Minecraft.getMinecraft().displayGuiScreen(clientGuideGui);
        }

        return clientGuideGui;
    }

    @Override
    public ButtonLM createSpecialButton(GuiInfo gui)
    {
        return new ButtonLM(0, 0, 16, 16, "Browse Guides") //TODO: Lang
        {
            @Override
            public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton b)
            {
            }

            @Override
            public void renderWidget(@Nonnull GuiLM gui)
            {
                GlStateManager.enableTexture2D();
                GlStateManager.color(1F, 1F, 1F, 1F);
                render(GuiIcons.globe);
            }
        };
    }

    public void reload()
    {
        INSTANCE.clear();
        INSTANCE.printlnText("WIP!");
    }

    /*
    @Override
    public void refreshGui(GuiInfo gui)
    {
        clientGuideGui = gui;
    }
    */
}