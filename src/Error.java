import java.util.Arrays;

public abstract class Error
{
	public static void print(Object object, Exception exception)
	{
	
		String msg = "<@"+Utente.ID_GION+">\n"+"`"+object+"`\n"+exception.getMessage()+"\n"+exception.getStackTrace()[0];
		Commands.canaleBot.sendMessage(msg).queue();
		new PrivateMessage(Utente.getGion()).send("`"+Arrays.toString(exception.getStackTrace())+"`");
		
	} // fine printError()
	
} // fine classe Error
