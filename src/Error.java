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
			var stackTrace = e.getStackTrace();
			var stackTraceString = Arrays.toString(stackTrace);
			
			lessThan2Thousand = (stackTraceString.length() > 2000 ? stackTraceString.substring(0, 1999) : stackTraceString);
			
			String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + e.getMessage();
			//Commands.canaleBot.sendMessage(msg).queue();
			gion.send("`"+e.getMessage()+"`\n"+lessThan2Thousand);
		}
		else if (type.equalsIgnoreCase("String"))
		{
			var s = (String) t;
			
			String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + s;
			//Commands.canaleBot.sendMessage(msg).queue();
		}
	} // fine print
	
	
} // fine classe Error
