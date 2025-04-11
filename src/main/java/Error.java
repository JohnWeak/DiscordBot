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
		
		if (type.contains("Exception"))
		{
			final Exception e = (Exception) exception;
			final Class<?> erClass = object.getClass();
			final String eMsg = e.getMessage();
			final StackTraceElement[] stackTrace = e.getStackTrace();
			final String stackTraceString = Arrays.toString(stackTrace);
			final String msgToSend = lessThan2K(String.format("`%s\n%s`\n%s", erClass, eMsg, stackTraceString));
			
			gion.send(msgToSend);
		}
		else if (type.equalsIgnoreCase("String"))
		{
			final String s = lessThan2K((String) exception);
			gion.send(s);
		}
		
	} // fine print
	
	
	private String lessThan2K(String s)
	{
		return s.length() > 2000 ? s.substring(0, 2000) : s;
	}
	
} // fine classe Error
