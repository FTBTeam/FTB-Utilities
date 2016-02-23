package ftb.utils.ranks;

import ftb.lib.api.permissions.ForgePermission;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CommandPermission extends ForgePermission
{
	public CommandPermission(String id)
	{
		super(id, false, true);
	}
}
