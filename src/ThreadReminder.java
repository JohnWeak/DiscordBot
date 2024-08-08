import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ThreadReminder extends Thread
{
	private final int tempo;
	private final String nome;
	private final MessageChannel channel;
	private final User utente;
	
	private final LocalDateTime start, end;
	private boolean active;
	
	public ThreadReminder(String nome, int tempo, MessageChannel channel, User utente)
	{
		this.nome = nome;
		this.tempo = tempo;
		this.channel = channel;
		this.utente = utente;
		
		start = LocalDateTime.now();
		end = start.plusSeconds(tempo/1000);
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
	
	/**Restituisce il tempo di partenza in cui è stato impostato promemoria
	 * @return la data in cui il promemoria è stato creato*/
	public LocalDateTime getStart()
	{
		return start;
	}
	
	/**Restituisce il tempo in cui il promemoria scadrà
	 * @return la data in cui il promemoria suonerà*/
	public LocalDateTime getEnd()
	{
		return end;
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
			final String footer;
			final EmbedBuilder eb;
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
			Thread.sleep(tempo);
			end = LocalDateTime.now();
			
			footer = String.format("⏰ Promemoria di %s\t%s", utente.getName(), end.format(formatter));
			
			eb = new EmbedBuilder();
			eb.setTitle("Promemoria scaduto!");
			eb.setColor(Color.RED);
			eb.setThumbnail(utente.getAvatarUrl());
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
