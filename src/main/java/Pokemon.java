import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Pokemon
{
	// UNRELATED TO POKEMON
	private static final Object object = Pokemon.class;
	private static final Error<Exception> error = new Error<>();
	public static final String NAMES_FILE = "nomiPokemon.txt";
	public static final String JSON_FILES = "./json/";
	public static boolean debug = false;
	private final PrivateMessage pm = new PrivateMessage(Utente.getGion());
	
	// FILE
	private static final File nomiPokemon = new File(NAMES_FILE);
	private static final Random random = new Random();
	private final boolean pokedex;
	public static final int ALL = 898;
	private final JSONArray jsonArray = new JSONArray();
	private static final JSONParser jsonParser = new JSONParser();
	private EmbedBuilder embedBuilder = null;
	private ThreadPokemon t = null;
	
	// POKEMON INFO
	private String nome;
	private String nomeFile;
	private String img;
	private boolean shiny = false;
	private String descrizione;
	private String[] tipo = new String[]{" "," "};
	private String generazione;
	private String dexNumber;
	private int[] individualValues = new int[6];
	private boolean catturato = false;
	private boolean catturabile = false;
	private User owner;
	private JSONArray types;
	
	// private static int pokemon_id = 261; -> Poochyena
	// https://pokeapi.co/api/v2/pokemon/261/
	
	public Pokemon(int id, boolean pokedex)
	{
		this.pokedex = pokedex;
		final File jsonFile;
		owner = null;
		
		String line="";
		BufferedReader reader = null;
		final Path path;
		
		if (id > ALL)
			return;
		
		try
		{
			path = Paths.get(Pokemon.NAMES_FILE);
			reader = Files.newBufferedReader(path);
			for (int i = 0; i < id; i++)
				line = reader.readLine().toLowerCase();
			
			nomeFile = line;
			
			if (debug)
				pm.send("`\n\nnome="+nomeFile+"\n\n`");
			jsonFile = new File(JSON_FILES + nomeFile+ ".json");
			
		}
		catch (Exception e)
		{
			error.print(object,e);
			return;
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}catch (IOException e) { error.print(object,e); }
			}
		}
		// determina se il pokemon sarà shiny
		if (random.nextInt(8142) == 42)
			shiny = true;
		
		// Genera i valori individuali
		// [HP, ATK, DEF, SPA, SPD, SPE]
		
		for (int index : individualValues)
			individualValues[index] = random.nextInt(32); // IVs: 0-31
		
		if (id <= 0)
			id = random.nextInt(1, ALL+1);
		
		
		// prendere i dati dal .json
		JSONObject data = getJsonObject(jsonFile);
		
		dexNumber = (String) data.get("id");
		nome = Utilities.capitalize((String) data.get("name"));
		types = (JSONArray) data.get("types");
		descrizione = (String) data.get("flavor_text");
		generazione = (String) data.get("generation");
		
		tipo[0] = (String) types.get(0);
		tipo[0] = tipo[0].toUpperCase();
		
		if (types.size() > 1)
		{
			tipo[1] = (String) types.get(1);
			tipo[1] = tipo[1].toUpperCase();
		}
		
		final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
		final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/" + id + ".png";
		
		img = (shiny ? urlShinyImg : urlImg);
		
		if (debug)
		{
			var msg = "nome: "+nome+"\ndexNumber: "+dexNumber+"\ndescrizione: "+descrizione+
					"\ngenerazione: "+generazione+"\ntipo/i: "+Arrays.toString(tipo);
			
			pm.send(msg);
			
			// pm.send("nome: " + nome);
			// pm.send("dexNumber: " + dexNumber);
			// pm.send("descrizione: " + descrizione);
			// pm.send("generazione: " + generazione);
			// pm.send("tipo/i: " + Arrays.toString(tipo));
		}
		
	} // fine costruttore
	
	
	public void spawn()
	{
		if (embedBuilder == null)
			embedBuilder = buildEmbed(pokedex);
		
		if (pokedex)
		{
			Commands.canaleBotPokemon.sendMessageEmbeds(embedBuilder.build()).queue();
		}
		else
		{
			t = new ThreadPokemon(this, (TextChannelImpl) Commands.canaleBotPokemon, embedBuilder);
			var tout = random.nextInt(2, 15);
			t.setTimeoutTime(t.MINUTES, tout);
			t.start();
			
			if (debug)
				new PrivateMessage(Utente.getGion()).send("\nThread alive:" + t.isAlive() + "\ntout: " + tout + "\n");
		}
	} // fine startEncounter
	
	
	private static JSONObject getJsonObject(File f)
	{
		String line;
		StringBuilder sb = new StringBuilder();
		Scanner scanner;
		
		try
		{
			scanner = new Scanner(f);
			
			while (scanner.hasNext())
			{
				if ((line = scanner.nextLine()) != null)
					sb.append(line);
			}
		
		}
		catch (FileNotFoundException e)
		{
			error.print(object, e);
		}
		
		JSONObject rtrn = null;
		try
		{
			rtrn = (JSONObject) jsonParser.parse(String.valueOf(sb));
		}catch (Exception e)
		{
			error.print(object,e);
		}
		return rtrn;
	}
	
	/** Genera un embed con il Pokemon */
	private EmbedBuilder buildEmbed(boolean pokedex)
	{
		final EmbedBuilder embedBuilder = new EmbedBuilder();
		final StringBuilder stringBuilder = new StringBuilder();
		String types = "";
		
		if (pokedex) // se è una entry del pokedex, mostra le informazioni varie
		{
			stringBuilder.append(tipo[0]);
			if (!(tipo[1].isEmpty()))
			{
				stringBuilder.append(" / ").append(tipo[1]);
			}
			types = String.valueOf(stringBuilder);
			
			// final String iconURL = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F53%2FPok%25C3%25A9_Ball_icon.svg%2F1026px-Pok%25C3%25A9_Ball_icon.svg.png&f=1&nofb=1";
			
			try
			{
				embedBuilder.setTitle(nome.toUpperCase());
			}
			catch (Exception e) { error.print(object, e); }
			
			if (descrizione != null)
			{
				String type = "Type";
				if (!tipo[1].isEmpty())
					type += "s";
				
				embedBuilder.addField("**"+type+"**", types, true);
				embedBuilder.addField("Generation", generazione, true);
				embedBuilder.addField("National Dex", dexNumber, true);
				
				embedBuilder.addField("Pokedex Entry", "*"+descrizione+"*", false);
				embedBuilder.setThumbnail(img);
			}
			else
			{
				embedBuilder.setImage(img);
			}
			
			final int color = shiny ? 0xFFD020 : 0xFF0000;
			embedBuilder.setColor(color);
			
			if (shiny)
				embedBuilder.setFooter("✨ Shiny! ✨");
			
		}
		else // se non è una entry del pokedex mostra solo nome e immagine
		{
			embedBuilder
				.setTitle("A wild " + nome + " appears!")
				.setImage(img)
				.setColor(Color.red)
				.setFooter("Type !catch to capture it.")
			;
		}
		
		return embedBuilder;
	} // fine buildEmbed()
	
	public static int getId(String nome)
	{
		int x = 0;
		try
		{
			Scanner scanner = new Scanner(nomiPokemon);
			while (scanner.hasNext())
			{
				x++;
				if (scanner.nextLine().equalsIgnoreCase(nome))
					break;
			}
		}catch (Exception e) { error.print(object, e); }
		return x;
	}
	
	//GETTER
	public String getNome() { return nome; }
	public String getImg() { return img; }
	public boolean isShiny() { return shiny; }
	public String getDescrizione() { return descrizione; }
	public String[] getTipo() { return tipo; }
	public String getGenerazione() { return generazione; }
	public String getDexNumber() { return dexNumber; }
	public int[] getIndividualValues() { return individualValues; }
	public boolean isCatturato() { return catturato; }
	public boolean isCatturabile() { return catturato; }
	public User getOwner() { return owner; }
	public ThreadPokemon getThread() { return t; }
	
	//SETTER
	public void setNome(String nome) { this.nome = nome;}
	public void setPokemonId(String img) { this.img = img; }
	public void setShiny(boolean shiny) { this.shiny = shiny; }
	public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
	public void setTipo(String[] tipi) { this.tipo = tipi; }
	public void setGenerazione(String generazione) { this.generazione = generazione; }
	public void setDexNumber(String dexNumber) { this.dexNumber = dexNumber; }
	public void setIndividualValues(int[] individualValues) { this.individualValues = individualValues; }
	public void setCatturato(boolean catturato) { this.catturato = catturato; }
	public void setCatturabile(boolean catturabile) { this.catturabile = catturabile; }
	public void setOwner(User owner) { this.owner = owner; }
	public void setThread(ThreadPokemon t) { this.t = t; }
	
} // fine classe
