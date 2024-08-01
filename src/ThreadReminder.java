import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.time.LocalDateTime;

public class ThreadReminder extends Thread
{
	private static int id = 0;
	private final int tempo;
	private final String nome;
	private final MessageChannel channel;
	
	private final LocalDateTime start;
	private boolean active;
	
	public ThreadReminder(String nome, int tempo, MessageChannel channel)
	{
		id += 1;
		
		this.nome = nome;
		this.tempo = tempo;
		this.channel = channel;
		
		start = LocalDateTime.now();
		active = true;
	}
	
	public int getTempo()
	{
		return tempo;
	}
	
	public String getNome()
	{
		return nome;
	}
	
	public static int getThreadId()
	{
		return id;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	@Override
	public void run()
	{
		//final LocalDateTime end;
		try
		{
			Thread.sleep(tempo);
			//end = LocalDateTime.now();
			
			final EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Promemoria scaduto!");
			eb.setColor(Color.RED);
			eb.addField(nome,"",false);
			channel.sendMessageEmbeds(eb.build()).queue();
			
			active = false;
			
		}catch (InterruptedException e)
		{
			new Error<>().print(this,e);
		}
		
		
	} // run()
	
} // Reminder
