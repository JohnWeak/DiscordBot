import net.dv8tion.jda.api.JDA;

import java.util.TimerTask;

public class ChangeActivityTask extends TimerTask
{
	private final JDA jda;
	public ChangeActivityTask()
	{
		jda = Main.getJda();
	}
	
	@Override
	public void run()
	{
		if (jda != null)
		{
			jda.getPresence().setActivity(Main.selectActivity());
			
			Commands.canaleBot.sendMessage("Activity cambiata!").queue(l ->
			{
				try
				{
					Thread.sleep(1000*60*10); // 10 minuti
					l.delete().queue();
				}catch (InterruptedException e) { new Errore<Exception>().print(this,e); }
				
			});
		}
		else
		{
			new Errore<String>().print(this,"jda is null");
		}
	}
}
