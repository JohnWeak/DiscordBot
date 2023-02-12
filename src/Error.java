import java.util.Arrays;

public class Error <T>
{
	public void print(Object object, T t)
	{
		var type = t.getClass().toString();
		
		if (type.contains("Exception"))
		{
			var e = (Exception) t;
			String msg =
					"<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + e.getMessage() + "\n" + e.getStackTrace()[0];
			Commands.canaleBot.sendMessage(msg).queue();
			new PrivateMessage(Utente.getGion()).send("StackTrace: `" + Arrays.toString(e.getStackTrace()) + "`");
		}
		else if (type.equalsIgnoreCase("String"))
		{
			var s = (String) t;
			
			String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + s;
			Commands.canaleBot.sendMessage(msg).queue();
		}
	} // fine print
	
	
} // fine classe Error
