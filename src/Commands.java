
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands extends ListenerAdapter
{
	// public static final String prefix = "!";
	private static final Random random = new Random();
	private static MessageChannel messageChannel;
	private static final String[] listaComandi = {"!vergognati", "!coinflip", "!poll", "!info", "!8ball"};
	private static final String[] listaParole = {"pigeon", "owo", "pog", "òbito", "vergogna", "no"};
	private static final String[] listaDescrizioni =
	{
		"Il bot risponderà usando la carta \"No u\"",
		"Il bot lancerà una moneta",
		"Permette di creare sondaggi",
		"Visualizza le informazioni. Proprio quelle che stai leggendo!",
		"Chiedi un responso all'Entità Superiore: la magica palla 8."
	};
	
	public void onMessageReceived(MessageReceivedEvent event)
	{
		messageChannel = event.getChannel();
		String[] args = event.getMessage().getContentRaw().split(" ");
		String comando = args[0];
		String message = event.getMessage().getContentRaw();
		String msgLowerCase = message.toLowerCase(Locale.ROOT);
		
		if (event.getAuthor().isBot()) return; // avoid loop with other bots
		
		if (event.getAuthor().getDiscriminator().equals("2804"))
			if (random.nextInt(6000) == 42) // 0,00016%
				messageChannel.sendMessage("Òbito vergognati").queue();
		
		if (event.getAuthor().getDiscriminator().equals("2241")) //2241 = Lex
			if ( random.nextInt(120) == 42) // 0,008%
				messageChannel.addReactionById(event.getMessageIdLong(), "U+1F1F7 U+1F1F4").queue(); //unicode della bandiera della romania
		
		
		switch (comando)
		{
			case "!vergognati" -> vergognati(event);
			case "!coinflip" -> coinflip(event);
			case "!poll" -> poll(event);
			case "!info" -> info();
			case "!8ball" -> eightBall();
		}
		
		
		if (msgLowerCase.contains("pigeon"))
			react("pigeon");
		
		if (msgLowerCase.contains("owo"))
			react("owo");
		
		if (msgLowerCase.contains("pog"))
			react("pog");
		
		if (msgLowerCase.contains("òbito") || msgLowerCase.contains("obito"))
			if (random.nextInt(4096) == 42) // 0,00024%
			{
				react("obito");
				react("vergogna");
			}
		
		if (msgLowerCase.contains("vergogna"))
			react("vergogna");
		
		if (msgLowerCase.contains("no"))
		{
			Pattern pattern = Pattern.compile("n+o+ *u+", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(msgLowerCase);
			
			if (matcher.matches())
				react("nou");
		}
		
		if (msgLowerCase.contains("sabaping"))
			react("sabaping");
		
		
	} // fine onMessageReceived()
	
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
			embedBuilder.addField("Esempio", "`!poll domanda / opzione 1 / opzione 2 / opzione 3 ...`\n`!poll Cosa preferite? Pizza / Pollo / Panino / Sushi`", false);
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
		
		final long id = messageChannel.getLatestMessageIdLong();
		String emoteDaUsare = switch (emote)
		{
			case "pigeon" -> emotePigeon;
			case "nou" -> emoteNou;
			case "owo" -> emoteOwO;
			case "pog" -> emotePog;
			case "vergogna" -> emoteVergognati[random.nextInt(2)];
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
	
	public void eightBall()
	{
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
		
		String ball = "🎱 ";
		messageChannel.sendTyping().queue();
		messageChannel.sendMessage(ball+risposte[random.nextInt(risposte.length)]).queue();
		
	} // fine eightBall()
	
	
} // fine classe Commands