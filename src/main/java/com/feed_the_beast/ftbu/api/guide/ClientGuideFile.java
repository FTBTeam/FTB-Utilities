package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.PackModes;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbl.api.info.InfoPage;
import com.feed_the_beast.ftbl.gui.info.GuiInfo;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.client.FTBUActions;
import latmod.lib.LMFileUtils;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class ClientGuideFile extends InfoPage
{
    public static final ClientGuideFile instance = new ClientGuideFile("ClientConfig");

    public static GuiInfo clientGuideGui = null;

    public ClientGuideFile(String id)
    {
        super(id);
        setTitle(new TextComponentTranslation(FTBUActions.GUIDE.getLangKey()));
    }

    public static GuiInfo openClientGui(boolean open)
    {
        if(clientGuideGui == null)
        {
            clientGuideGui = new GuiInfo(null, ClientGuideFile.instance);
        }
        if(open)
        {
            FTBLibClient.mc().displayGuiScreen(clientGuideGui);
        }
        return clientGuideGui;
    }

    public void reload(ReloadEvent e)
    {
        if(FTBLib.DEV_ENV)
        {
            FTBU.logger.info("Guide reloaded @ " + e.world.getSide() + " as " + e.world.getMode());
        }

        clear();

        File file = PackModes.instance().getCommon().getFile("guide/");
        if(file.exists() && file.isDirectory())
        {
            File[] f = file.listFiles();
            if(f != null && f.length > 0)
            {
                Arrays.sort(f, LMFileUtils.fileComparator);
                for(int i = 0; i < f.length; i++)
                {
                    //FIXME: loadFromFiles(this, f[i]);
                }
            }
        }

        file = e.world.getMode().getFile("guide/");
        if(file.exists() && file.isDirectory())
        {
            File[] f = file.listFiles();
            if(f != null && f.length > 0)
            {
                Arrays.sort(f, LMFileUtils.fileComparator);
                for(int i = 0; i < f.length; i++)
                {
                    //FIXME: loadFromFiles(this, f[i]);
                }
            }
        }

        file = e.world.getMode().getFile("guide_intro.txt");
        if(file.exists() && file.isFile())
        {
            try
            {
                String text = LMFileUtils.loadAsText(file);
                if(text != null && !text.isEmpty())
                {
                    printlnText(text.replace("\r", ""));
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        cleanup();
        clientGuideGui = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void refreshGui(GuiInfo gui)
    {
        clientGuideGui = gui;
    }
}