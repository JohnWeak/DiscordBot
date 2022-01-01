import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Pokemon
{
	private final URL urlGetPokemon = new URL("https://pokeapi.co/api/v2/pokemon/"); //  dopo pokemon/ inserire numero_id oppure nome
	private static int pokemon_id = 0;
	// https://pokeapi.co/api/v2/pokemon/261/ -> Poochyena
	
	private String nome;
	private int puntiFerita;
	private boolean isShiny = false;
	
	public Pokemon() throws MalformedURLException
	{
	
	}
	
	
	public void requestName()
	{
		String risultato = "";
		try
		{
			URL url = new URL(urlGetPokemon + String.valueOf(pokemon_id));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			risultato = con.getResponseMessage();
			
			con.disconnect();
			
		} catch (Exception e) { e.printStackTrace(); }
		
		System.out.println(risultato);
	} // fine requestName()
	
	
} // fine classe Pokemon
