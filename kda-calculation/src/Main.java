import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.*;

public class Main {
	
	static String KEY = "RGAPI-806b9f47-d5f5-44aa-bfaf-f9afc583cf1f";
	static String MAIN_URL = "https://la1.api.riotgames.com";

	public static void main(String[] args) throws IOException {
		printCurrentParticipantsByName("Brasped");
	}
	
	public static long getAccountIDbyName(String name) throws IOException {
		String s = MAIN_URL + "/lol/summoner/v3/summoners/by-name/" + name + "?api_key=" + KEY;
	    URL url = new URL(s);
	    
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONObject obj = new JSONObject(str);
	    
	    return obj.getLong("accountId");
	}
	
	public static long getSummonerIdbyName(String name) throws IOException {
		String s = MAIN_URL + "/lol/summoner/v3/summoners/by-name/" + name + "?api_key=" + KEY;
	    URL url = new URL(s);
	    
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONObject obj = new JSONObject(str);
	    
	    return obj.getLong("id");
	}
	
	public static JSONArray getCurrentListParticipantBySummonerId(long id) throws IOException {
		String s = MAIN_URL + "/lol/spectator/v3/active-games/by-summoner/" + id + "?api_key=" + KEY;
	    URL url = new URL(s);
	    
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONObject obj = new JSONObject(str);
	    
	    return obj.getJSONArray("participants");
	}
	
	public static void printCurrentParticipantsByName(String name) throws IOException {
		JSONArray list = getCurrentListParticipantBySummonerId(getSummonerIdbyName("Brasped"));
	    
	    for(Object ob : list) {
	    	JSONObject p = (JSONObject) ob;
	    	System.out.println("Champion: " + p.getLong("championId") + "\t" + "Summoner: " + p.getString("summonerName"));
	    }
	}
}