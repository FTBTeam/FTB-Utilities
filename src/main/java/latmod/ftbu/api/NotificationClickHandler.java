package latmod.ftbu.api;

import latmod.ftbu.util.*;
import latmod.ftbu.world.LMPlayerClient;

public interface NotificationClickHandler
{
	public void onClicked(NotificationClick c, Notification n, LMPlayerClient p);
}