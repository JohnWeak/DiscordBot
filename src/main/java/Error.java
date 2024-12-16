import java.util.Arrays;

/**@param <T> Gli unici tipi ammessi sono Exception e String.*/
public class Error <T>
{
	/**Invia un messaggio privato a Gion contenente i dettagli dell'errore
	 * @param object la classe che ha lanciato l'eccezione
	 * @param exception l'eccezione (in formato Exception) o la stringa d'errore
	 * */
	public void print(Object object, T exception)
	{
		final String type = exception.getClass().toString();
		final PrivateMessage gion = new PrivateMessage(Utente.getGion());
		String lessThan2k;
		
		if (type.contains("Exception"))
		{
			final Exception e = (Exception) exception;
			final Class<?> erClass = object.getClass();
			final String eMsg = e.getMessage();
			final StackTraceElement[] stackTrace = e.getStackTrace();
			final String stackTraceString = Arrays.toString(stackTrace);
			String msgToSend = "`" +erClass + "\n" + eMsg + "`\n"+stackTraceString;
			
			msgToSend = lessThan2K(msgToSend);
			gion.send(msgToSend);
		}
		else if (type.equalsIgnoreCase("String"))
		{
			final String s = (String) exception;
			
			lessThan2k = lessThan2K(s);
			
			gion.send(lessThan2k);
		}
		
	} // fine print
	
	
	private String lessThan2K(String s)
	{
		return s.length() > 2000 ? s.substring(0, 2000) : s;
	}
	
} // fine classe Error
