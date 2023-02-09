import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main
{
	static final String token = System.getenv("TOKEN");
	private static JDA jda;
	
	public static void main(String[] args)
	{
		var jda = generateJDA();
		
		if (jda != null)
			jda.upsertCommand("pog", "questo è un comando slash. woah.").queue();
	} // fine metodo main()
	
	private static JDA generateJDA()
	{
		try
		{
			jda = JDABuilder.createDefault(token)
					        .setActivity(selectActivity())
					        .setStatus(OnlineStatus.ONLINE)
					        .addEventListeners(new Commands())
					        .build();
		}catch (LoginException e)
		{
			Error.print(Main.class, e);
			
		}
		return jda;
	} // fine generateJDA()
	
	public static JDA getJda()
	{
		return jda;
	}
	
	public static Activity selectActivity()
	{
		var random = new Random();
		String giocoScelto, showScelto, easterEggScelto;
		Activity activity;
		
		final String[] games =
		{
			"Minecraft", "Dead Space", "Hitman", "Hitman 2", "Hitman 3", "Rimworld", "Darkest Dungeon",
			"FTL: Faster Than Light", "Terraria", "Team Fortress 2", "Farming Simulator", "Portal",
			"A Plague Tale", "Subnautica", "OneShot", "Child of Light", "Ghost Trick: Phantom Detective",
			"XCOM 2", "Papers, Please", "Celeste", "The Stanley Parable", "To The Moon", "GTA: San Andreas",
			"Fallout: New Vegas", "Half-Life 2", "Divinity: Original Sin 2", "Dark Souls", "Hollow Knight",
			"Celeste", "Total War: Shogun 2", "Lawn Moving Simulator", "Tetris"
		};
		
		final String[] anime =
		{
			"Steins;Gate", "FullMetal Alchemist", "Gurren Lagann", "Demon Slayer",
			"Attack on Titan", "The Promised Neverland", "Kill La Kill", "Death Parade", "Death Note",
			"Cowboy Bebop", "Goblin Slayer", "ID: Invaded", "Jujutsu Kaisen", "ODDTAXI", "Noragami",
			"My Hero Academia", "One-Punch Man", "HunterxHunter", "Chainsaw Man", "Gintama", "Made in Abyss"
		};
		
		final String[] movies =
		{
			"WALL•E", "Avatar", "Big Hero 6", "Deadpool", "Dragon Trainer", "Freaks Out",
			"Caccia a Ottobre Rosso", "Ghost in the Shell", "Into the Spiderverse", "La Teoria del Tutto",
			"Kingsman: Secret Service", "Lupin III: The First", "Kubo e la spada magica", "Megamind",
			"Shawn of the dead", "Star Trek", "Soul", "your name.", "Bullet Train", "Il diritto di contare",
			"Up", "X-MEN", "Omicidio all'italiana"
		};
		
		final String[] series =
		{
			"Dr. House", "Lie to me", "Mr. Robot", "Sherlock", "The Mentalist", "Forever", "Elementary",
			"Breaking Bad", "Limitless", "Squid Game"
		};

		final String[] easterEgg =
		{
			"Òbito che perde soldi in borsa", "Enigmo che simpa per Yano",
			"Gion che mangia una pizza con ananas", "Lex che guida un'auto elettrica",
			"il mondo bruciare", "una partita di calcio", "gli americani spararsi a vicenda"
		};
		
		var percent = random.nextInt(100);
		
		if (percent <= 45) // watch: 0-45
		{
			showScelto = switch (random.nextInt(3))
			{
				case 0 -> anime[random.nextInt(anime.length)];
				case 1 -> movies[random.nextInt(movies.length)];
				case 2 -> series[random.nextInt(series.length)];
				default -> "il nulla cosmico";
			};
			activity = Activity.watching(showScelto);
		}
		else if (percent <= 95)// play: 46-95
		{
			giocoScelto = games[random.nextInt(games.length)];
			activity = Activity.playing(giocoScelto);
		}
		else // easter egg: 96-99
		{
			easterEggScelto = easterEgg[random.nextInt(easterEgg.length)];
			activity = Activity.watching(easterEggScelto);
		}
		return activity;
	} // fine selectActivity()
	
} // fine classe Main