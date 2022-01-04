import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands extends ListenerAdapter
{
	// public static final String prefix = "!";
	private static final File file = new File("valori.txt");
	// private static final File nomiPkmn = new File("nomiPokemon.txt");
	private static final Random random = new Random();
	private static MessageChannel messageChannel;
	private static final String[] listaComandi = {"!vergognati", "!coinflip", "!poll", "!info", "!8ball", "!pokemon"};
	// private static final String[] listaParole = {"pigeon", "owo", "pog", "òbito", "vergogna", "no"};
	private static final String[] listaDescrizioni =
	{
		"Il bot risponderà usando la carta \"No u\"",
		"Il bot lancerà una moneta",
		"Permette di creare sondaggi",
		"Visualizza le informazioni. Proprio quelle che stai leggendo!",
		"Chiedi un responso all'Entità Superiore: la magica palla 8.",
		"Acchiappali tutti!"
	};
	private static int messaggiInviati = 0;
	private static int limite;
	
	
	/*
		Òbito: 2804
		Enigmo: 7166
		Lex: 2241
		Gion: 0935
	*/
	
	public void onMessageReceived(MessageReceivedEvent event)
	{
		messageChannel = event.getChannel();
		String[] args = event.getMessage().getContentRaw().split(" ");
		String comando = args[0];
		String message = event.getMessage().getContentRaw();
		String msgLowerCase = message.toLowerCase(Locale.ROOT);
		
		List<Emote> e = event.getMessage().getEmotes();

		if (event.getAuthor().isBot()) return; // Per evitare problemi con altri bot

		for (Emote emote : e)
			event.getMessage().addReaction(emote).queue();

		if (!msgLowerCase.contains("!pokemon")) // genera un pokemon casuale soltanto se non viene eseguito il comando
			spawnPokemon(event);
		
		
		if (event.getAuthor().getDiscriminator().equals("2804")) // 2804 -> Òbito
			if (random.nextInt(100) == 42) // 1%
				event.getMessage().reply("Òbito vergognati").queue((message1 ->
				{
					react("obito");
					react("vergogna");
				}));
		
		if (event.getAuthor().getDiscriminator().equals("2241")) //2241 = Lex
			if (random.nextInt(10) == 9) // 10%
				messageChannel.addReactionById(event.getMessageIdLong(), "U+1F1F7 U+1F1F4").queue(); //unicode della bandiera della romania
		
		
		switch (comando)
		{
			case "!vergognati" -> vergognati(event);
			case "!coinflip" -> coinflip(event);
			case "!poll" -> poll(event);
			case "!info" -> info();
			case "!8ball" -> eightBall(event);
			case "!pokemon" -> pokemon();
		}
		
		
		if (msgLowerCase.contains("pigeon"))
			react("pigeon");
		
		if (msgLowerCase.contains("owo"))
			react("owo");
		
		if (msgLowerCase.contains("pog"))
			react("pog");
		
		if (msgLowerCase.contains("òbito") || msgLowerCase.contains("obito"))
			if (random.nextInt(50) == 42) // 2%
			{
				react("obito");
				react("vergogna");
			}
		
		if (msgLowerCase.contains("vergogna"))
			react("vergogna");
		
		if (msgLowerCase.contains("no"))
		{
			Pattern pattern = Pattern.compile(".*n+o+ *u+.*", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(msgLowerCase);
			
			if (matcher.matches())
				react("nou");
		}
		
		if (msgLowerCase.contains("sabaping"))
			react("sabaping");
		
		if (msgLowerCase.contains("get"))
			if (msgLowerCase.contains("rekt"))
				react("getrekt");

		if (msgLowerCase.contains("smh"))
			react("smh");
		
		
	} // fine onMessageReceived()
	
	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{
		String c = event.getName();
		
		if (c.equals("test"))
			messageChannel.sendMessage("test eseguito con successo!").queue();
	
	//FIXME: come diavolo si fa?!
	} // fine onSlashCommand()
	
	public void vergognati(MessageReceivedEvent event)
	{
		final String emoteNou = "<:nou:671402740186087425>";
		final String emoteNouLess = "nou:671402740186087425";
		
		event.getChannel().sendTyping().queue();
		event.getMessage().reply(emoteNou).queue(message -> message.addReaction(emoteNouLess).queue());
	}
	
	public void coinflip(MessageReceivedEvent event)
	{
		final String testa = "<:pogey:733659301645910038>";
		final String croce = "<:pigeon:647556750962065418>";
		String autore = event.getAuthor().getName()+" lancia una moneta...";
		
		if (random.nextInt(2) == 1) // testa
			event.getChannel().sendMessage(autore+"\n**È uscito** " + testa + "**! (Testa)**").queue(message -> message.addReaction("pogey:733659301645910038").queue());
		else
			event.getChannel().sendMessage(autore+"\n**È uscito** " + croce + "**! (Croce)**").queue(message -> message.addReaction("pigeon:647556750962065418").queue());
		
	} // fine coinflip()
	
	public void poll(MessageReceivedEvent event)
	{
		// args[0] = "!poll"
		// args[1] = domanda
		// args[2, 3, ...] = risposte
		String msg = event.getMessage().getContentRaw();
		
		if (msg.length() <= 5)
		{
			sondaggio("Pog?", new String[]{"Pog sì", "Pog no", "Porgo Tensing"}, true);
			return;
		}
		
		String[] domandaERisposte = event.getMessage().getContentRaw().split("\\?");
		String domanda = domandaERisposte[0].substring("!poll".length());
		String[] risposte = msg.substring("!poll".length()+domanda.length()+1).split("/");
		
		System.out.printf("DomandaERisposte length: %d\nDomandaERisposte: %s\nDomanda length: %d\nDomanda: %s\nRisposte.length: %d\nRisposte: %s\n", domandaERisposte.length, Arrays.toString(domandaERisposte), domanda.length(), domanda, risposte.length, Arrays.toString(risposte));
		
		sondaggio(domanda, risposte, false);
	} // fine poll()
	
	public void sondaggio(String domanda, String[] risposte, boolean flag)
	{
		risposte[0] = risposte[0].substring(0, risposte[0].length()-1).trim();
		int sleepInterval = random.nextInt(500) + 1000;
		final int size = risposte.length;
		EmbedBuilder embedBuilder = new EmbedBuilder();
		final String[] letters = new String[]
		{
			"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9",
			"\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED",
			"\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1",
			"\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5",
			"\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9",
			"\uD83C\uDDFA", "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD",
			"\uD83C\uDDFE", "\uD83C\uDDFF"
		}; // array di lettere emoji A -> Z
		
		if (size < 2 || size > 20 || flag)
		{
			//embedBuilder.setAuthor("");
			embedBuilder.setTitle("`!poll` - Istruzioni per l'uso");
			embedBuilder.addField("Sondaggio", "Per creare un sondaggio devi usare il comando `!poll` + `domanda?` + `[risposte]`\nSepara le risposte con uno slash `/`.", false);
			embedBuilder.addField("Esempio", "`!poll domanda? opzione 1 / opzione 2 / opzione 3 ...`\n`!poll Cosa preferite? Pizza / Pollo / Panino / Sushi`", false);
			embedBuilder.addField("Votazione", "Per votare, usa le reazioni!", false);
			embedBuilder.setColor(0xFFFFFF);
			
			messageChannel.sendTyping().queue();
			try { Thread.sleep(sleepInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }
			messageChannel.sendMessageEmbeds(embedBuilder.build()).queue();
		}
		else
		{
			String descrizione = "";
			
			embedBuilder.setTitle(domanda+"?");
			for (int i = 0; i < risposte.length; i++)
				descrizione = descrizione.concat(letters[i] + "\t" + risposte[i]) + "\n";
			embedBuilder.setDescription(descrizione);
			embedBuilder.setColor(0xFF0000);
			
			messageChannel.sendTyping().queue();
			
			try { Thread.sleep(sleepInterval); }
			catch (InterruptedException e) { e.printStackTrace(); }
			
			messageChannel.sendMessageEmbeds(embedBuilder.build()).queue((message) ->
			{
				for (int i = 0; i < size; i++)
					message.addReaction(letters[i]).queue();
			});
			
		}
		
	} // fine sondaggio()
	
	public void react(String emote)
	{
		final String emoteOwO = "OwO:604351952205381659";
		final String emoteNou = "nou:671402740186087425";
		final String emotePigeon = "pigeon:647556750962065418";
		final String emotePog = "pogey:733659301645910038";
		final String[] emoteObito = {"obi:670007761760681995", "ito:670007761697898527"};
		final String[] emoteVergognati = {"vergognati:670009511053885450", "vergogna2:880100281315098685"};
		final String[] emoteSabaPing = {"leftPowerUp:785565275608842250", "sabaPing:785561662605885502", "rightPowerUp:785565774953709578"};
		final String emoteGetRekt = "getrekt:742330625347944504";
		final String emoteSmh = "smh:880423534365659176";
		
		final long id = messageChannel.getLatestMessageIdLong();
		String emoteDaUsare = switch (emote)
		{
			case "pigeon" -> emotePigeon;
			case "nou" -> emoteNou;
			case "owo" -> emoteOwO;
			case "pog" -> emotePog;
			case "vergogna" -> emoteVergognati[random.nextInt(2)];
			case "getrekt" -> emoteGetRekt;
			case "smh" -> emoteSmh;
			default -> "";
		};
		
		if (emote.equals("obito"))
			for (String s : emoteObito)
				messageChannel.addReactionById(id, s).queue();
		
		if (emote.equals("sabaping"))
			for (String s : emoteSabaPing)
				messageChannel.addReactionById(id, s).queue();
		
		if (!emoteDaUsare.equals(""))
		{
			try
			{
				messageChannel.addReactionById(id, emoteDaUsare).queue();
			}
			catch (Exception e) { e.printStackTrace(); }
		}
		
	} // fine react()
	
	public void info()
	{
		var embedBuilder = new EmbedBuilder();
		String urlOwO = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fres.cloudinary.com%2Fteepublic%2Fimage%2Fprivate%2Fs--amf4Rvt7--%2Ft_Preview%2Fb_rgb%3A191919%2Cc_limit%2Cf_jpg%2Ch_630%2Cq_90%2Cw_630%2Fv1518097892%2Fproduction%2Fdesigns%2F2348593_0.jpg&f=1&nofb=1";
		String urlTitle = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
		
		embedBuilder.setTitle("Informazioni", urlTitle);
		embedBuilder.setDescription("Questo bot permette di lanciare monete, creare sondaggi e, soprattutto, essere un rompiballe.");
		
		for (int i = 0; i < listaComandi.length; i++)
			embedBuilder.addField("`"+listaComandi[i]+"`", "*"+listaDescrizioni[i]+"*", false);
		
		embedBuilder.setThumbnail(urlOwO);
		//embedBuilder.addField();
		embedBuilder.setColor(0xFF0000);
		embedBuilder.addBlankField(false);
		embedBuilder.setFooter("Creato con ❤ da JohnWeak", urlOwO);
		
		MessageEmbed embed = embedBuilder.build();
		messageChannel.sendMessageEmbeds(embed).queue();
		
	} // fine info()
	
	public void eightBall(MessageReceivedEvent event)
	{
		final Message message = event.getMessage();
		final String ball = "🎱 ";
		final String risposta = ball+"says... ";
		final String[] risposte =
		{
			"Yes",
			"It is certain.",
			"It is decidedly so.",
			"Without a doubt.",
			"Yes definitely.",
			"You may rely on it.",
			"As I see it, yes.",
			"Most likely.",
			"Outlook good.",
			"Yes.",
			"Signs point to yes.",
			"Reply hazy, try again.",
			"Ask again later.",
			"Better not tell you now.",
			"Cannot predict now.",
			"Concentrate and ask again.",
			"Don't count on it.",
			"My reply is no.",
			"My sources say no.",
			"Outlook not so good.",
			"Very doubtful."
		};

		messageChannel.sendTyping().queue();

		pause(-1, -1);

		message.reply(risposta).queue(message1 ->
		{

			pause(-1,-1);

			message1.editMessage(risposta+"**"+risposte[random.nextInt(risposte.length)]+"**").queue();
		});
		
		
	} // fine eightBall()

	private void pause(int millis, int bound)
	{
		if (millis < 0)
			millis = 1500;

		if (bound < 0)
			bound = 500;

		try { Thread.sleep(millis+random.nextInt(bound)); }
		catch (InterruptedException e) { e.printStackTrace(); }
	} // fine pause()
	
	public void pokemon()
	{
		Pokemon pokemon = new Pokemon();
		EmbedBuilder embedBuilder;
		
		if (random.nextInt(10) == 9)
		{
			doubleEncounter(pokemon, new Pokemon());
		}
		else
		{
			embedBuilder = buildEmbed(pokemon);
			messageChannel.sendMessageEmbeds(embedBuilder.build()).queue();
		
		}
	} // fine metodo definitivo pokemon()

	private void doubleEncounter(Pokemon uno, Pokemon due)
	{
		EmbedBuilder embedBuilder;
		String[] titolo = {"Primo Pokemon!", "Secondo Pokemon!"};
		Pokemon[] pokemons = {uno, due};
		messageChannel.sendMessage("Doppio Incontro!").queue();
		
		for (int i = 0; i < 2; i++)
		{
			embedBuilder = buildEmbed(pokemons[i]);
			embedBuilder.setDescription(titolo[i]);
			//embedBuilder.setFooter("Catturalo con !catch","https://www.pngall.com/wp-content/uploads/4/Pokeball-PNG-Images.png");
			messageChannel.sendMessageEmbeds(embedBuilder.build()).queue();
			
		}
		
		System.out.printf("\nUno: %s, shiny: %s\nDue: %s, shiny: %s\n",uno.getNome(), uno.isShiny(), due.getNome(), due.isShiny());
	} // fine
	
	private EmbedBuilder buildEmbed(Pokemon pokemon)
	{
		EmbedBuilder embedBuilder = new EmbedBuilder();
		
		embedBuilder.setTitle(pokemon.getNome());
		embedBuilder.setImage(pokemon.getImg());
		if (pokemon.isShiny())
		{
			embedBuilder.setColor(Color.YELLOW);
			embedBuilder.setFooter("✨ Shiny! ✨");
		}
		else
		{
			embedBuilder.setColor(Color.RED);
		}
		
		return embedBuilder;
	} // fine buildEmbed()
	
	public void spawnPokemon(MessageReceivedEvent event)
	{
		int[] valori = new int[2];
		Scanner scanner;
		FileWriter fileWriter;

		try
		{
			scanner = new Scanner(file);
			for (int i = 0; i < 2; i++)
				valori[i] = scanner.nextInt();

			scanner.close();

			limite = valori[0];
			messaggiInviati = valori[1];
		}
		catch (FileNotFoundException e) { System.out.println("File non trovato!"); }

		//valori[0] : limite (max) messaggi
		//valori[1] : messaggiInviati
		
		if (messaggiInviati == valori[0])
		{
			pokemon(); // genera un incontro
			messaggiInviati = 0; // resetta il contatore
			limite = random.nextInt(10) + 5; // genera un nuovo max per i messaggi
		}
		else
		{
			messaggiInviati++;
		}
		valori[0] = limite;
		valori[1] = messaggiInviati;
		
		try
		{
			fileWriter = new FileWriter(file);
			fileWriter.write(valori[0]+"\n"+valori[1]);
			fileWriter.close();

		}catch (IOException e) { System.out.println("Errore nella scrittura del file!"); }

		System.out.println("Valori nel file: " + Arrays.toString(valori));
		System.out.printf("Valori nelle variabili: [limite: %d, messaggiInviati: %d]\n", limite, messaggiInviati);


	} // fine spawnPokemon
	
	
} // fine classe Commands