import java.util.Arrays;

public class Error <T>
{
	public void print(Object object, T t)
	{
		var type = t.getClass().toString();
		var gion = new PrivateMessage(Utente.getGion());
		String lessThan2k;
		if (type.contains("Exception"))
		{
			Exception e = (Exception) t;
			var eMsg = e.getMessage();
			var stackTrace = e.getStackTrace();
			var stackTraceString = Arrays.toString(stackTrace);
			
			lessThan2k = (stackTraceString.length() > 2000 ? stackTraceString.substring(0, 2000) : stackTraceString);
			
			gion.send("`" + eMsg + "`\n"+lessThan2k);
		}
		else if (type.equalsIgnoreCase("String"))
		{
			String s = (String) t;
			var l = s.length();
			
			lessThan2k = l > 2000 ? s.substring(0, 2000) : s ;
			
			gion.send(lessThan2k);
		}
	} // fine print
	
	
} // fine classe Error
