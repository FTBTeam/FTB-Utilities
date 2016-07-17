package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideRepoList;
import com.feed_the_beast.ftbu.gui.guide.GuideType;

import java.util.List;
import java.util.Map;

/**
 * Created by PC on 17.07.2016.
 */
public class LocalGuideRepoList extends GuideRepoList
{
    public static final LocalGuideRepoList INSTANCE = new LocalGuideRepoList();

    @Override
    protected void onReload(Map<GuideType, List<Guide>> m) throws Exception
    {
    }
}