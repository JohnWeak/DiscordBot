
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Random;

public class Main
{
	private static JDABuilder jda;
	
	public static void main(String[] args) throws LoginException
	{
		jda = JDABuilder.createDefault("ODM2NTg2ODYyMjEzNzI2MjI4.YIgKOg.zFvGCTzAF1ffIUB_M5OnN_U29HI");
		selectActivity();
		jda.setStatus(OnlineStatus.ONLINE);
		jda.addEventListeners(new Commands());
		jda.build();
	} // fine metodo main()
	
	private static void selectActivity()
	{
		final String[] games =
		{
			"Minecraft", "Dead Space", "Hitman 2", "Rimworld", "Darkest Dungeon", "FTL: Faster Than Light",
			"Terraria", "Team Fortress 2", "God of War", "Farming Simulator", "Portal", "A Plague Tale",
			"Subnautica", "OneShot", "Child of Light", "Ghost Trick: Phantom Detective", "XCOM 2",
			"Papers, Please", "Celeste", "The Stanley Parable", "To The Moon", "GTA: San Andreas",
			"Fallout: New Vegas", "Half-Life 2", "Divinity: Original Sin 2", "Dark Souls"
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
		
		var random = new Random();
		int bound = random.nextInt(4);
		switch (bound)
		{
			case 0 -> jda.setActivity(Activity.playing(games[random.nextInt(games.length)]));
			case 1 -> jda.setActivity(Activity.watching(anime[random.nextInt(anime.length)]));
			case 2 -> jda.setActivity(Activity.watching(movies[random.nextInt(movies.length)]));
			case 3 -> jda.setActivity(Activity.watching(series[random.nextInt(series.length)]));
		}
	} // fine selectActivity()
	
} // fine classe Main