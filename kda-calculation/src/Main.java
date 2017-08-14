import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.*;

public class Main {
	
	static String KEY = "RGAPI-4e7c07e3-8e0f-400d-b187-51d8b8e38647";
	static String MAIN_URL = "https://la1.api.riotgames.com";

	public static void main(String[] args) throws IOException {
		// For each participant in the current match.
		for(Object p : getCurrentListParticipantBySummonerId(getSummonerByName("Crow22").getLong("id"))) {
			
			// Participant object.
			JSONObject participant = (JSONObject) p;
			
			// Summoner object.
			JSONObject summoner = getSummonerBySummonerId(participant.getLong("summonerId"));
			
			System.out.println(summoner.get("name"));
			
			// Object about the 20 last matches for summoner "summoner".
			JSONObject matchesObject = getLast20MatchesByAccountId(summoner.getLong("accountId"));
			
			// For each match
			for(Object m : matchesObject.getJSONArray("matches")) {
				
				// Match obtained from matches.
				JSONObject matchReference = (JSONObject) m;
				
				// Match object (detailed information)
				JSONObject match = getMatchByMatchID(matchReference.getLong("gameId"));
				
				
				
				System.out.println(match);
			}
			
			System.out.println();
		}
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
	
	public static JSONObject getSummonerBySummonerId(long summonerId) throws IOException {
		String s = MAIN_URL + "/lol/summoner/v3/summoners/" + summonerId + "?api_key=" + KEY;
	    URL url = new URL(s);
	    
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONObject summoner = new JSONObject(str);
	    
	    return summoner;
	}
	
	public static JSONObject getSummonerByName(String name) throws IOException {
		String s = MAIN_URL + "/lol/summoner/v3/summoners/by-name/" + name + "?api_key=" + KEY;
	    URL url = new URL(s);
	    
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONObject summoner = new JSONObject(str);
	    
	    return summoner;
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
//		JSONArray list = getCurrentListParticipantBySummonerId(getSummonerIdbyName(name));
//	    
//	    for(Object ob : list) {
//	    	JSONObject p = (JSONObject) ob;
//	    	System.out.println("Champion: " + p.getLong("championId") + "\t" + "Summoner: " + p.getString("summonerName"));
//	    }
	}
	
	public static JSONObject getLast20MatchesByAccountId(long accountId) throws IOException {
		String s = MAIN_URL + "/lol/match/v3/matchlists/by-account/" + accountId + "/recent?api_key=" + KEY;
	    URL url = new URL(s);
	    
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONObject last20Matches = new JSONObject(str);
	    
	    return last20Matches;
	}

	public static JSONObject getMatchByMatchID(long matchId) throws IOException {
		String s = MAIN_URL + "/lol/match/v3/matches/" + matchId + "?api_key=" + KEY;
	    URL url = new URL(s);
	    
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONObject match = new JSONObject(str);
	    
	    return match;
	}
}