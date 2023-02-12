import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

public class Pokemon
{
	private static final Object object = Pokemon.class;
	
	private final int max = 898; // fino a gen 8
	private static final File nomiPokemon = new File("nomiPokemon.txt");
	private static final Random random = new Random();
	
	private String nome;
	private String img;
	private boolean shiny = false;
	private String descrizione;
	private String[] tipo = new String[]{" "," "};
	private String[] lineaEvolutiva = new String[]{"1","2","3"};
	private String generazione;
	private String dexNumber;
	private int[] individualValues = new int[6];
	private boolean catturato = false;
	private String numeroPokedex;
	private JSONObject jsonObject, family;
	private JSONArray types, evoLine;
	private URL url;
	private JSONArray jsonArray = new JSONArray();
	private JSONParser jsonParser = new JSONParser();
	private EmbedBuilder embedBuilder;
	
	// private static int pokemon_id = 261; -> Poochyena
	// https://pokeapi.co/api/v2/pokemon/261/
	
	public Pokemon(boolean pokedex)
	{
		// determina se il pokemon sarà shiny
		if (random.nextInt(8142) == 42)
			shiny = true;
		
		// Genera i valori individuali
		// [HP, ATK, DEF, SPA, SPD, SPE]
		for (int index : individualValues)
			individualValues[index] = random.nextInt(32); // IVs: 0-31
		
		// genera un id nazionale casuale
		int id = random.nextInt(1, max);
		
		Scanner scanner;
		
		try
		{
			// cerca il pokemon nel dex
			scanner = new Scanner(nomiPokemon);
			for (int i = 0; i < id; i++)
				nome = scanner.nextLine();
		
			final String urlImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+id+".png";
			final String urlShinyImg = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/"+id+".png";
		
			img = (shiny ? urlShinyImg : urlImg);
		
			url = new URL("https://pokeapi.glitch.me/v1/pokemon/" + nome);
			scanner = new Scanner(nomiPokemon);
			while (scanner.hasNext())
			{
				if (nome.equalsIgnoreCase(scanner.nextLine()))
				{
					var connection = (HttpURLConnection) url.openConnection();
					connection.setRequestProperty("Accept", "application/json");
					
					var in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					var response = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null)
						response.append(inputLine);
					
					jsonArray = (JSONArray) jsonParser.parse(String.valueOf(response));
				}
			}
			
			jsonObject = (JSONObject) jsonArray.get(0);
			descrizione = (String) jsonObject.get("description");
			types = (JSONArray) jsonObject.get("types");
			family = (JSONObject) jsonObject.get("family");
			evoLine = (JSONArray) family.get("evolutionLine");
			
			for (int i = 0; i < types.size(); i++)
				tipo[i] = types.get(i).toString();
			
			generazione = String.valueOf(jsonObject.get("gen"));
			numeroPokedex = (String) jsonObject.get("number");
			
			for (int i = 0; i < evoLine.size(); i++)
				lineaEvolutiva[i] = evoLine.get(i).toString();
			
			embedBuilder = buildEmbed(pokedex);
		} catch (Exception e) { var ex = new Error<Exception>(); ex.print(object, e); }
		
	} // fine costruttore
	
	
	public void spawn(Pokemon pokemon)
	{
		var t = new ThreadPokemon(pokemon, Commands.canaleBotPokemon, embedBuilder);
		var tout = random.nextInt(2, 5);
		t.setTimeoutTime(t.MINUTES, tout);
		t.start();
		new PrivateMessage(Utente.getGion()).send("\nThread alive:"+t.isAlive()+"\ntout: "+tout+"\n");
		
	} // fine startEncounter
	
	
	/** Genera un embed con il Pokemon */
	private EmbedBuilder buildEmbed(boolean pokedex)
	{
		var embedBuilder = new EmbedBuilder();
		var stringBuilder = new StringBuilder();
		var types = "";
		
		if (pokedex) // se è una entry del pokedex, mostra le informazioni varie
		{
			stringBuilder.append(tipo[0]);
			if (!(tipo[1].equals(" ")))
			{
				stringBuilder.append(" / ").append(tipo[1]);
			}
			types = String.valueOf(stringBuilder);
			stringBuilder.delete(0, stringBuilder.length()); // pulizia per riciclarlo per la linea evolutiva
			stringBuilder.append(lineaEvolutiva[0]); //esiste per forza
			if (!(lineaEvolutiva[1].equals("2")))
			{
				stringBuilder.append(" > ").append(lineaEvolutiva[1]);
				if (!(lineaEvolutiva[2]).equals("3"))
				{
					stringBuilder.append(" > ").append(lineaEvolutiva[2]);
				}
			}
			else
			{
				stringBuilder.append(" doesn't evolve.");
			}
			var evoline = String.valueOf(stringBuilder);
			final String iconURL = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F53%2FPok%25C3%25A9_Ball_icon.svg%2F1026px-Pok%25C3%25A9_Ball_icon.svg.png&f=1&nofb=1";
			embedBuilder.setFooter(""+evoline, ""+iconURL);
			
			try
			{
				embedBuilder.setTitle(nome.toUpperCase());
			}
			catch (Exception e) { new Error<Exception>().print(object, e); }
			
			if (descrizione != null)
			{
				String type = "Type";
				if (!tipo[1].equals(" "))
					type += "s";
				
				embedBuilder.addField("**"+type+"**", ""+types, true);
				embedBuilder.addField("Generation", ""+generazione, true);
				embedBuilder.addField("National Dex", ""+numeroPokedex, true);
				
				embedBuilder.addField("Pokedex Entry", "*"+descrizione+"*", false);
				embedBuilder.setThumbnail(img);
			}
			else
			{
				embedBuilder.setImage(img);
			}
			
			var color = shiny ? 0xFFD020 : 0xFF0000;
			embedBuilder.setColor(color);
			
			if (shiny)
				embedBuilder.setFooter("✨ Shiny! ✨");
			
		}
		else // se non è una entry del pokedex mostra solo nome e immagine
		{
			embedBuilder
				.setTitle("A wild " + nome + "appears!")
				.setImage(img)
				.setFooter("Type !catch to capture it.")
			;
		}
		
		return embedBuilder;
	} // fine buildEmbed()
	
	
	
	//GETTER
	public String getNome() { return nome; }
	public String getImg() { return img; }
	public boolean isShiny() { return shiny; }
	public String getDescrizione() { return descrizione; }
	public String[] getTipo() { return tipo; }
	public String getGenerazione() { return generazione; }
	public String getDexNumber() { return dexNumber; }
	public String[] getLineaEvolutiva() { return lineaEvolutiva; }
	public int[] getIndividualValues() { return individualValues; }
	public boolean isCatturato() { return catturato; }
	
	//SETTER
	public void setNome(String nome) { this.nome = nome;}
	public void setPokemonId(String img) { this.img = img; }
	public void setShiny(boolean shiny) { this.shiny = shiny; }
	public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
	public void setTipo(String[] tipi) { this.tipo = tipi; }
	public void setGenerazione(String generazione) { this.generazione = generazione; }
	public void setDexNumber(String dexNumber) { this.dexNumber = dexNumber; }
	public void setLineaEvolutiva(String[] lineaEvolutiva) { this.lineaEvolutiva = lineaEvolutiva; }
	public void setIndividualValues(int[] individualValues) { this.individualValues = individualValues; }
	public void setCatturato(boolean catturato) { this.catturato = catturato; }
	
	
} // fine classe
