import java.util.Arrays;

/**@param <T> Gli unici tipi ammessi sono Exception e String.*/
public class Error <T>
{
	/**Invia un messaggio privato a Gion contenente i dettagli dell'errore*/
	public void print(Object object, T t)
	{
		var type = t.getClass().toString();
		var gion = new PrivateMessage(Utente.getGion());
		String lessThan2k;
		if (type.contains("Exception"))
		{
			Exception e = (Exception) t;
			var erClass = object.getClass();
			var eMsg = e.getMessage();
			var stackTrace = e.getStackTrace();
			var stackTraceString = Arrays.toString(stackTrace);
			var msgToSend = "`" +erClass + "\n" + eMsg + "`\n"+stackTraceString;
			
			msgToSend = lessThan2K(msgToSend);
			gion.send(msgToSend);
		}
		else if (type.equalsIgnoreCase("String"))
		{
			String s = (String) t;
			
			lessThan2k = lessThan2K(s);
			
			gion.send(lessThan2k);
		}
		
	} // fine print
	
	
	private String lessThan2K(String s)
	{
		
		return s.length() > 2000 ? s.substring(0, 2000) : s;
	}
	
} // fine classe Error
