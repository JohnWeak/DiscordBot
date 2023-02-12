import java.util.Arrays;

public class Error <T>
{
	public void print(Object object, T t)
	{
		var type = t.getClass().toString();
		var gion = new PrivateMessage(Utente.getGion());
		if (type.contains("Exception"))
		{
			var e = (Exception) t;
			String msg =
					"<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + e.getMessage() + "\n" + e.getStackTrace()[0];
			Commands.canaleBot.sendMessage(msg).queue();
			
			if (e.getStackTrace().length > 2000)
			{
				gion.send("StackTrace: `" + Arrays.toString(e.getStackTrace()) + "`");
			}
			gion.send("StackTrace: `" + Arrays.toString(e.getStackTrace()) + "`");
		}
		else if (type.equalsIgnoreCase("String"))
		{
			var s = (String) t;
			
			String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + s;
			Commands.canaleBot.sendMessage(msg).queue();
		}
	} // fine print
	
	
} // fine classe Error
