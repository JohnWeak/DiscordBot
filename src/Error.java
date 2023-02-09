public abstract class Error
{
	public static void print(Object object, Exception exception)
	{
	
		String msg = "<@"+Utente.ID_GION+">\n"+"`"+object.getClass()+"`\n"+exception.getMessage()+"\n"+exception.getStackTrace()[0];
		Commands.canaleBot.sendMessage(msg).queue();
	
	} // fine printError()
	
} // fine classe Error
