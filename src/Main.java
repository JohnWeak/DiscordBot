
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Random;

public class Main
{
	public static void main(String[] args) throws LoginException
	{
		final String token = "ODM2NTg2ODYyMjEzNzI2MjI4.YIgKOg.zFvGCTzAF1ffIUB_M5OnN_U29HI";

		var jda = JDABuilder.createDefault(token)
			.setActivity(selectActivity())
			.setStatus(OnlineStatus.ONLINE)
			.addEventListeners(new Commands())
			.build();
		
		jda.upsertCommand("pog", "questo è un comando slash. woah.").queue();
	} // fine metodo main()
	
	
	private static Activity selectActivity()
	{
		var random = new Random();
		String giocoScelto, showScelto = null, easterEggScelto;
		Activity activity;
		
		final String[] games =
		{
			"Minecraft", "Dead Space", "Hitman 2", "Rimworld", "Darkest Dungeon", "FTL: Faster Than Light",
			"Terraria", "Team Fortress 2", "Farming Simulator", "Portal", "A Plague Tale",
			"Subnautica", "OneShot", "Child of Light", "Ghost Trick: Phantom Detective", "XCOM 2",
			"Papers, Please", "Celeste", "The Stanley Parable", "To The Moon", "GTA: San Andreas",
			"Fallout: New Vegas", "Half-Life 2", "Divinity: Original Sin 2", "Dark Souls", "Hollow Knight",
			"Celeste", "Total War: Shogun 2", "Lawn Moving Simulator"
		};
		
		final String[] anime =
		{
			"Steins;Gate", "FullMetal Alchemist", "Gurren Lagann", "One Piece", "Naruto", "Demon Slayer",
			"Attack on Titan", "The Promised Neverland", "Kill La Kill", "Death Parade", "Death Note",
			"Cowboy Bebop", "Goblin Slayer", "ID: Invaded", "Jujutsu Kaisen", "ODDTAXI", "Noragami",
			"My Hero Academia", "One-Punch Man", "HunterxHunter"
		};
		
		final String[] movies =
		{
			"WALL•E", "Addio Fottuti Musi Verdi", "Big Hero 6", "Deadpool", "Dragon Trainer", "Freaks Out",
			"Caccia a Ottobre Rosso", "Ghost in the Shell", "Into the Spiderverse", "La Teoria del Tutto",
			"Kingsman: Secret Service", "Lupin III: The First", "Kubo e la spada magica", "Megamind",
			"Shawn of the dead", "Star Trek", "Soul", "your name."
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
			"il mondo bruciare", "una partita di calcio", "gli americani che si sparano fra loro"
		};
		
		int percent = random.nextInt(100);
		
		if (percent <= 45) // watch: 0-45
		{
			switch (random.nextInt(3))
			{
				case 0 -> showScelto = anime[random.nextInt(anime.length)];
				case 1 -> showScelto = movies[random.nextInt(movies.length)];
				case 2 -> showScelto = series[random.nextInt(series.length)];
			}
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