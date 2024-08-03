import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.time.LocalDateTime;

public class ThreadReminder extends Thread
{
	private final int tempo;
	private final String nome;
	private final MessageChannel channel;
	private final String nomeUtente;
	
	private final LocalDateTime start;
	private boolean active;
	
	public ThreadReminder(String nome, int tempo, MessageChannel channel, String nomeUtente)
	{
		this.nome = nome;
		this.tempo = tempo;
		this.channel = channel;
		this.nomeUtente = nomeUtente;
		
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
	
	public boolean isActive()
	{
		return active;
	}
	
	
	@Override
	public String toString()
	{
		return "Nome: " + nome + "\nChannel: " + channel + "\n";
	}
	
	@Override
	public void run()
	{
		try
		{
			final LocalDateTime end;
			final String day, month, year, hour, minutes, footer;
			final EmbedBuilder eb;
			
			Thread.sleep(tempo);
			end = LocalDateTime.now();
			
			day = end.getDayOfMonth() < 10 ? "0"+end.getDayOfMonth() : ""+end.getDayOfMonth();
			month = end.getMonthValue() < 10 ? "0"+end.getMonthValue() : ""+end.getMonthValue();
			year = ""+end.getYear();
			hour = end.getHour() < 10 ? "0"+end.getHour() : ""+end.getHour();
			minutes = end.getMinute() < 10 ? "0"+end.getMinute() : ""+end.getMinute();
			
			footer = String.format("⏰ Promemoria di %s, `%s/%s/%s %s:%s`",nomeUtente, day, month, year, hour, minutes);
			
			eb = new EmbedBuilder();
			eb.setTitle("Promemoria scaduto!");
			eb.setColor(Color.RED);
			eb.addField(nome,"",false);
			eb.setFooter(footer);
			channel.sendMessageEmbeds(eb.build()).queue();
			
			active = false;
			
		}catch (InterruptedException e)
		{
			new Error<>().print(this,e);
		}
		
		
	} // run()
	
} // Reminder
