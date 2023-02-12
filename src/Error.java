import java.util.Arrays;

public class Error <T>
{
	public void print(Object object, T t)
	{
		var type = t.getClass().toString();
		var gion = new PrivateMessage(Utente.getGion());
		if (type.contains("Exception"))
		{
			String lessThan2Thousand;
			var e = (Exception) t;
			String msg =
					"<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + e.getMessage() + "\n" + e.getStackTrace()[0];
			Commands.canaleBot.sendMessage(msg).queue();
			
			if (e.getMessage().length() > 2000)
			{
				lessThan2Thousand = e.getMessage().substring(0,1999);
				gion.send("StackTrace: `" + lessThan2Thousand + "`");
			}
			else
				gion.send("StackTrace: `" + e.getMessage() + "`");
		}
		else if (type.equalsIgnoreCase("String"))
		{
			var s = (String) t;
			
			String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + s;
			Commands.canaleBot.sendMessage(msg).queue();
		}
	} // fine print
	
	
} // fine classe Error
