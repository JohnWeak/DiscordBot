import java.util.Arrays;

public class Error <T>
{
	public void print(Object object, T t)
	{
		var type = t.getClass().toString();
		var gion = new PrivateMessage(Utente.getGion());
		if (type.contains("Exception"))
		{
			String lessThan2k;
			Exception e = (Exception) t;
			var eMsg = e.getMessage();
			var stackTrace = e.getStackTrace();
			var stackTraceString = Arrays.toString(stackTrace);
			
			lessThan2k = (stackTraceString.length() > 2000 ? stackTraceString.substring(0, 1999) : stackTraceString);
			
			String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + eMsg;
			//Commands.canaleBot.sendMessage(msg).queue();
			gion.send("`" + eMsg + "`\n"+lessThan2k);
		}
		else if (type.equalsIgnoreCase("String"))
		{
			String s = (String) t;
			
			String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + s;
			//Commands.canaleBot.sendMessage(msg).queue();
		}
	} // fine print
	
	
} // fine classe Error
