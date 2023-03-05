import java.util.HashMap;

/**Questa classe contiene una hashmap dei comandi del bot.
 * Per istanziarla, usa <code>Cmd.init()</code>*/
public abstract class Cmd
{
	private static final String[] listaComandi =
	{
		"!coinflip", "!poll", "!info", "!8ball", "!pokemon", "!carta", "!colpevole", "!massshooting", "!dado",
		"!f", "!timer"
	};
	private static final String[] listaDescrizioni =
	{
		"Il bot lancerà una moneta.",
		"Permette di creare sondaggi.",
		"Visualizza le informazioni. Proprio quelle che stai leggendo!",
		"Chiedi un responso all'Entità Superiore: la magica palla 8.",
		"Acchiappali tutti!",
		"Genera una carta da gioco.",
		"Lascia che RNGesus decida la percentuale di colpevolezza di un altro utente.",
		"Ottieni il resoconto delle sparatorie di massa negli USA. Avviso: sono dati reali.",
		"Lancia un dado. Puoi specificare il numero di facce del dado.",
		"Paga i tuoi omaggi al defunto in questione.",
		"Imposta un timer, al suo scadere sarai taggato dal bot."
	};
	
	
	/**Istanzia la hashmap.
	 * @return HashMap contenente i comandi del bot*/
	public static HashMap<String,String> init()
	{
		var commands = new HashMap<String,String>();
		var size = listaComandi.length;
		
		for (int i = 0; i < size; i++)
			commands.put(listaComandi[i], listaDescrizioni[i]);
		
		return commands;
	}
}