import java.util.Arrays;

public class Error <T>
{
	/*public static void print(Object object, Exception exception)
	{
	
		String msg = "<@"+Utente.ID_GION+">\n"+"`"+object+"`\n"+exception.getMessage()+"\n"+exception.getStackTrace()[0];
		Commands.canaleBot.sendMessage(msg).queue();
		new PrivateMessage(Utente.getGion()).send("StackTrace: `"+Arrays.toString(exception.getStackTrace())+"`");
		
	} // fine printError(Object, Exception)
	
	public static void print(Object object, String message)
	{
		String msg = "<@"+Utente.ID_GION+">\n"+"`"+object+"`\n"+message;
		Commands.canaleBot.sendMessage(msg).queue();
		
	} // fine printError(Object, String) */
	
	public void print(Object object, T t)
	{
		try
		{
			if (t.getClass().toString().contains("Exception"))
			{
				var e = (Exception) t;
				String msg =
						"<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + e.getMessage() + "\n" + e.getStackTrace()[0];
				Commands.canaleBot.sendMessage(msg).queue();
				new PrivateMessage(Utente.getGion()).send("StackTrace: `" + Arrays.toString(e.getStackTrace()) + "`");
			}
			else if (t.getClass().toString().equalsIgnoreCase("String"))
			{
				var s = (String) t;
				
				String msg = "<@" + Utente.ID_GION + ">\n" + "`" + object + "`\n" + s;
				Commands.canaleBot.sendMessage(msg).queue();
			}
		}catch (Exception e)
		{
			Commands.canaleBot.sendMessage(""+e+"\n"+e.getStackTrace()[0]).queue();
		}
	} // fine print
	
	
} // fine classe Error
