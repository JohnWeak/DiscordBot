/** Contiene la lista di tutte le emote del server privato "Server Discord" */
public abstract class Emotes
{
	public static final String OwO = "OwO:604351952205381659";
	public static final String NoU = "nou:671402740186087425";
	public static final String pigeon = "pigeon:647556750962065418";
	public static final String pogey = "pogey:733659301645910038";
	public static final String[] obito = {"obi:670007761760681995", "ito:670007761697898527"};
	public static final String[] vergognati = {"vergognati:670009511053885450", "vergognati2:880100281315098685"};
	public static final String[] sabaPing = {"leftPowerUp:785565275608842250", "sabaPing:785561662605885502", "rightPowerUp:785565774953709578"};
	public static final String getRekt = "getrekt:742330625347944504";
	public static final String smh = "smh:880423534365659176";
	public static final String giorno = "GiOrNo:618591225582321703";
	public static final String dansGame = "dansgame:848955120157720576";
	public static final String ingredibile = "ingredibile:593532244434485259";
	public static final String[] scarab = {"leftPowerUp:785565275608842250", "scarab:847008906994778122", "rightPowerUp:785565774953709578"};
	public static final String wtf = "WTF:670033776524656641";
	public static final int[] hitman = {7, 8, 19, 12, 0, 13}; // posizioni lettere nell'alfabeto
	public static final int[] XCOM = {23, 2, 14, 12}; // posizioni lettere nell'alfabeto
	public static final String bigBrain = "BigBrain:619059338883104771";
	public static final String birds = "birds:1081894118126518272";
	public static final String borisK = "BorisK:858638399426396161";
	public static final String boo2 = "Boo2:598597182123147275";
	public static final String comedyGenius = "ComedyGenius:774950569474916364";
	public static final String dshock = "Dshock:856664076615811093";
	public static final String gabeN = "GabeN:652573135077376021";
	public static final String hampter = "Hampter:798667312705568779";
	public static final String lul = "LUL:586333997160726538";
	public static final String pepeSad = "PepeSad:924409510620196904";
	public static final String ragey = "Ragey:618591740374417417";
	public static final String tonyakaradio105 = "Tonyakaradio105:624580446952357918";
	public static final String Tpose = "Tpose:609385915395342361";
	public static final String doubt = "doubt:607910262925688832";
	public static final String everyone = "everyone:713125848047157318";
	public static final String grrr = "grrr:585960136418394113";
	public static final String harry_fotter = "harry_fotter:585959442793889803";
	public static final String kappa = "kappa:596751965099393054";
	public static final String kappaPride = "kappaPride:596751965447389252";
	public static final String konoDio = "konoDio:593421650049892352";
	public static final String monkaS = "monkaS:601409193374646301";
	public static final String noIdontThinkIwill = "noidontthinkiwill:619058401242120192";
	public static final String o7 = "o7:928753025777037354";
	public static final String okSaitama = "oksaitama:606068895886999558";
	public static final String tf2spy = "tf2spy:610393246220288001";
	public static final String tf2spy2 = "tf2spy2:620707027638812673";
	public static final String thinkHang = "thinkHang:788174095354298428";
	public static final String thonking = "thonking:596753589666447377";
	public static final String what = "what:913701372996759593";
	public static final String monkaSTEER = "monkaSTEER:869346241794949140";
	public static final String lonk = "lonk:1095778061229768755";
	// public static final String emote = "";
	
	
	public static final String[] letters =
	{
		"\uD83C\uDDE6", // A
		"\uD83C\uDDE7", // B
		"\uD83C\uDDE8", // C
		"\uD83C\uDDE9", // D
		"\uD83C\uDDEA", // E
		"\uD83C\uDDEB", // F
		"\uD83C\uDDEC", // G
		"\uD83C\uDDED", // H
		"\uD83C\uDDEE", // I
		"\uD83C\uDDEF", // J
		"\uD83C\uDDF0", // K
		"\uD83C\uDDF1", // L
		"\uD83C\uDDF2", // M
		"\uD83C\uDDF3", // N
		"\uD83C\uDDF4", // O
		"\uD83C\uDDF5", // P
		"\uD83C\uDDF6", // Q
		"\uD83C\uDDF7", // R
		"\uD83C\uDDF8", // S
		"\uD83C\uDDF9", // T
		"\uD83C\uDDFA", // U
		"\uD83C\uDDFB", // V
		"\uD83C\uDDFC", // W
		"\uD83C\uDDFD", // X
		"\uD83C\uDDFE", // Y
		"\uD83C\uDDFF"  // Z
	};
	
	public static String emoteDaUsare(String s)
	{
		return switch (s.toLowerCase())
		{
			case "pigeon" -> pigeon;
			case "nou" -> NoU;
			case "owo" -> OwO;
			case "pogey" -> pogey;
			case "vergognati" -> vergognati[0];
			case "vergognati2" -> vergognati[1];
			case "getrekt" -> getRekt;
			case "smh" -> smh;
			case "birds" -> birds;
			case "giorno" -> giorno;
			case "obi" -> obito[0];
			case "ito" -> obito[1];
			case "leftpowerup" -> sabaPing[0];
			case "sabaping" -> sabaPing[1];
			case "rightpowerup" -> sabaPing[2];
			case "dansgame" -> dansGame;
			case "ingredibile" -> ingredibile;
			case "wtf" -> wtf;
			case "bigbrain" -> bigBrain;
			case "boo2" -> boo2;
			case "boris", "borisk" -> borisK;
			case "comedygenius" -> comedyGenius;
			case "dshock" -> dshock;
			case "gaben" -> gabeN;
			case "hampter" -> hampter;
			case "lul" -> lul;
			case "pepesad" -> pepeSad;
			case "ragey" -> ragey;
			case "tonyakaradio105" -> tonyakaradio105;
			case "tpose" -> Tpose;
			case "doubt" -> doubt;
			case "everyone" -> everyone;
			case "grrr" -> grrr;
			case "harry_fotter" -> harry_fotter;
			case "kappa" -> kappa;
			case "kappapride" -> kappaPride;
			case "konodio" -> konoDio;
			case "monkas" -> monkaS;
			case "noidontthinkiwill" -> noIdontThinkIwill;
			case "o7" -> o7;
			case "oksaitama" -> okSaitama;
			case "scarab" -> scarab[1];
			case "tf2spy" -> tf2spy;
			case "tf2spy2" -> tf2spy2;
			case "thinkhang" -> thinkHang;
			case "thonking" -> thonking;
			case "what" -> what;
			case "monkasteer" -> monkaSTEER;
			case "lonk" -> lonk;
			
			default -> s;
		};
		
	} // fine emoteDaUsare()
	
} // fine classe Bot.Emotes
