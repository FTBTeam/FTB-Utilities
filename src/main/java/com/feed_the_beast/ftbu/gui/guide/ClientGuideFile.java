package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.info.InfoPage;
import com.feed_the_beast.ftbl.gui.info.GuiInfo;
import com.feed_the_beast.ftbu.client.FTBUActions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//FIXME
@SideOnly(Side.CLIENT)
public class ClientGuideFile extends InfoPage
{
    public static final ClientGuideFile instance = new ClientGuideFile("ClientConfig");

    public static GuiScreen clientGuideGui = null;

    public ClientGuideFile(String id)
    {
        super(id);
        setTitle(FTBUActions.GUIDE.displayName);
    }

    public static GuiScreen openClientGui(boolean open)
    {
        if(clientGuideGui == null)
        {
            instance.reload();
            clientGuideGui = new GuiInfo(null, ClientGuideFile.instance).getWrapper();
        }
        if(open)
        {
            FTBLibClient.mc().displayGuiScreen(clientGuideGui);
        }

        return clientGuideGui;
    }

    public void reload()
    {
        instance.clear();
        instance.printlnText("WIP!");
    }

    /*
    @Override
    public void refreshGui(GuiInfo gui)
    {
        clientGuideGui = gui;
    }
    */
}