import net.dv8tion.jda.api.entities.MessageChannel;

public class GenericException extends Exception
{
	private final Class c;
	private final Exception e;
	private final MessageChannel mc = Commands.canaleBot;
	
	public GenericException(Class c, Exception e)
	{
		this.c = c;
		this.e = e;
	}
	
	
	public void esplodi()
	{
		mc.sendMessage("`"+c+"`\n"+e).queue();
	}
	
	
	
} // fine GenericException
