
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main
{
	public static void main(String[] args) throws LoginException
	{
		JDABuilder jda = JDABuilder.createDefault("ODM2NTg2ODYyMjEzNzI2MjI4.YIgKOg.zFvGCTzAF1ffIUB_M5OnN_U29HI");
		jda.setActivity(Activity.playing("4D Chess"));
		jda.setStatus(OnlineStatus.IDLE);
		jda.addEventListeners(new Commands());
		jda.build();
	} // fine metodo main()
	
} // fine classe Main