import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.Random;


public class Main
{
	private static final String token = System.getenv("TOKEN");
	@Getter private static JDA jda;
	@Getter private static Activity activity;
	@Getter private static String tipo;
	private static final Object object = Main.class;
	
	public static void main(String[] args) throws InterruptedException
	{
		jda = generateJDA();
		cmds();
		
	} // fine metodo main()
	
	public static void cmds()
	{
		try
		{
			final ArrayList<CommandData> commands = new ArrayList<>();
			CommandData newCommand;
			newCommand = new CommandDataImpl("pog", "Risponde con l'emote \"pog\"");
			commands.add(newCommand);
			
			newCommand = new CommandDataImpl("dado","Lancia un dado")
				.addOptions(new OptionData(
					OptionType.INTEGER,
					"facce",
					"quante facce deve avere il dado",
					false
					)
					.setRequiredRange(2,100)
				);
			commands.add(newCommand);
			
			newCommand = new CommandDataImpl("cena", "Chiedi ad un utente di uscire a cena con te")
				.addOptions(new OptionData(
				OptionType.USER,
				"utente",
				"l'utente che vuoi invitare a cena",
				true
				)
			);
			commands.add(newCommand);
			
			newCommand = new CommandDataImpl("pokemon", "Genera un incontro con un pokemon!")
				.addOptions(new OptionData(
					OptionType.STRING,
					"nome",
					"Il nome del pokemon che vuoi cercare",
					true
					),
					new OptionData(
						OptionType.BOOLEAN,
						"shiny",
						"shiny",
						false
					)
				);
			
			commands.add(newCommand);
			
			jda.updateCommands().addCommands(commands).queue();
			
		}catch (Exception e)
		{
			new Error<Exception>().print(object,e);
		}
		
	}
	
	private static JDA generateJDA() throws InterruptedException
	{
		try
		{
			jda = JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.setActivity(selectActivity())
				.setStatus(OnlineStatus.ONLINE)
				.addEventListeners(new Commands())
				.build();
		} catch (Exception e)
		{
			new Error<>().print(object, e);
		}
		return jda.awaitReady();
	} // fine generateJDA()
	
	public static Activity selectActivity()
	{
		final Random random = new Random();
		final String giocoScelto, showScelto, easterEggScelto;
		
		final String[] games =
		{
			"Minecraft", "Dead Space", "Hitman", "Rimworld", "Darkest Dungeon",
			"FTL: Faster Than Light", "Terraria", "Team Fortress 2", "Farming Simulator", "Portal",
			"A Plague Tale", "Subnautica", "OneShot", "Child of Light", "Ghost Trick: Phantom Detective",
			"XCOM 2", "Papers, Please", "Celeste", "The Stanley Parable", "To The Moon", "GTA: San Andreas",
			"Fallout: New Vegas", "Half-Life 2", "Divinity: Original Sin 2", "Dark Souls", "Hollow Knight",
			"Celeste", "Total War: Shogun 2", "Lawn Moving Simulator", "Tetris", "Outer Wilds"
		};
		
		final String[] anime =
		{
			"Steins;Gate", "FullMetal Alchemist", "Gurren Lagann", "Demon Slayer",
			"Attack on Titan", "The Promised Neverland", "Kill La Kill", "Death Parade", "Death Note",
			"Cowboy Bebop", "Goblin Slayer", "ID: Invaded", "Jujutsu Kaisen", "ODDTAXI", "Noragami",
			"My Hero Academia", "One-Punch Man", "HunterxHunter", "Chainsaw Man", "Gintama", "Dan Dan Dan"
		};
		
		final String[] movies =
		{
			"WALL•E", "Big Hero 6", "Deadpool", "Dragon Trainer", "Freaks Out", "Il diritto di contare",
			"Caccia a Ottobre Rosso", "Ghost in the Shell", "Into the Spiderverse", "La Teoria del Tutto",
			"Kingsman: Secret Service", "Lupin III: The First", "Kubo e la spada magica", "Megamind",
			"Shawn of the dead", "Star Trek", "Soul", "your name.", "Bullet Train", "Il diritto di contare",
			"Up", "X-MEN", "Omicidio all'italiana", "Morbius", "L'era Glaciale", "Super Mario Bros. (2023)",
			"Dungeons&Dragons: l'onore dei ladri"
		};
		
		final String[] series =
		{
			"Dr. House", "Lie to me", "Mr. Robot", "Sherlock", "The Mentalist", "Forever", "Elementary",
			"Breaking Bad", "Limitless", "Squid Game", "LOST", "Arcane"
		};

		final String[] easterEgg =
		{
			"Òbito che perde soldi in borsa", "Enigmo che simpa per Yano",
			"Gion che mangia una pizza con ananas", "Lex che guida un'auto elettrica",
			"il mondo bruciare", "una partita di calcio", "gli americani spararsi a vicenda",
			"un gender reveal party causare un incendio", "le tipe nude attraverso lo spioncino della porta"
		};
		
		final int percent = random.nextInt(100);
		
		if (percent <= 45) // watch: 0-45
		{
			switch (random.nextInt(3))
			{
				case 0 ->
				{
					showScelto = anime[random.nextInt(anime.length)];
					tipo = "Anime";
				}
				case 1 ->
				{
					showScelto = movies[random.nextInt(movies.length)];
					tipo = "Film";
				}
				case 2 ->
				{
					showScelto = series[random.nextInt(series.length)];
					tipo = "Serie TV";
				}
				default ->
				{
					showScelto = "il nulla cosmico";
					tipo = null;
				}
			}
			activity = Activity.watching(showScelto);
		}
		else if (percent <= 95) // play: 46-95
		{
			giocoScelto = games[random.nextInt(games.length)];
			tipo = "Gioco";
			activity = Activity.playing(giocoScelto);
		}
		else // easter egg: 96-99
		{
			easterEggScelto = easterEgg[random.nextInt(easterEgg.length)];
			tipo = "Easter Egg";
			activity = Activity.watching(easterEggScelto);
		}
		
		return activity;
	} // fine selectActivity()
	
	/**@return il tipo di attività tradotta in italiano e formattata.*/
	public static String getActivityTradotta()
	{
		return getActivityType().toString().equals("WATCHING") ? "guardo " : "gioco a ";
	}
	
	/**@return il tipo di attività: guarda/gioca*/
	public static Activity.ActivityType getActivityType()
	{
		return activity.getType();
	}
	
	
} // fine classe Main