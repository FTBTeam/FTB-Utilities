package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideRepoList;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonElement;
import com.latmod.lib.util.LMJsonUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class LocalGuideRepoList extends GuideRepoList
{
    static final LocalGuideRepoList INSTANCE = new LocalGuideRepoList();

    private LocalGuideRepoList()
    {
    }

    @Override
    protected void onReload(InfoPage infoPage) throws Exception
    {
        List<Guide> guides = new ArrayList<>();

        File folder = new File(FTBLib.folderLocal, "guidepacks");

        if(folder.exists() && folder.isDirectory())
        {
            File[] guideTypes = folder.listFiles();

            if(guideTypes != null && guideTypes.length > 0)
            {
                for(File f : guideTypes)
                {
                    if(f.isDirectory())
                    {
                        GuideType type = GuideType.getFromString(f.getName());

                        File[] guideFolders = f.listFiles();

                        if(guideFolders != null && guideFolders.length > 0)
                        {
                            for(File f1 : guideFolders)
                            {
                                if(f1.isDirectory())
                                {
                                    try
                                    {
                                        JsonElement infoFile = LMJsonUtils.fromJson(new File(f1, "info.json"));

                                        if(infoFile.isJsonObject())
                                        {
                                            Guide g = new LocalGuide(f1.getName(), type, f1);
                                            g.fromJson(infoFile);
                                            guides.add(g);
                                        }
                                    }
                                    catch(Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }
}