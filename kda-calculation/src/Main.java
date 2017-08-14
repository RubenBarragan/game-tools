import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Scanner;
import org.json.*;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

public class Main {
	
	static String KEY = "RGAPI-4e7c07e3-8e0f-400d-b187-51d8b8e38647";
	static String MAIN_URL = "https://la1.api.riotgames.com";

	public static void main(String[] args) throws IOException, InterruptedException {
		// Define the limit 20 times per 1 second
		// These lines are used to control rate limit.
		Bandwidth limit = Bandwidth.simple(100, Duration.ofMinutes(3));
		Bucket bucket = Bucket4j.builder().addLimit(0, limit).build();
		
		// Consume one API request (rate limit control system)
		bucket.consume(1, BlockingStrategy.PARKING);
		// For each participant in the current match.
		for(Object p : getCurrentListParticipantBySummonerId(getSummonerByName("MaikelRyuu").getLong("id"))) {
			
			
			// Participant object.
			JSONObject participant = (JSONObject) p;
			
			// Consume one API request (rate limit control system)
			bucket.consume(1, BlockingStrategy.PARKING);
			// Summoner object.
			JSONObject summoner = getSummonerBySummonerId(participant.getLong("summonerId"));
			
			System.out.println(summoner.get("name"));
			
			// Consume one API request (rate limit control system)
			bucket.consume(1, BlockingStrategy.PARKING);
			// Object about the 20 last matches for summoner "summoner".
			JSONObject matchesObject = getLast20MatchesByAccountId(summoner.getLong("accountId"));
			
			int count = 0;
			
			// For each match
			for(Object m : matchesObject.getJSONArray("matches")) {
				
				// Match obtained from matches.
				JSONObject matchReference = (JSONObject) m;
				
				// Consume one API request (rate limit control system)
				bucket.consume(1, BlockingStrategy.PARKING);
				// Match object (detailed information)
				JSONObject match = getMatchByMatchID(matchReference.getLong("gameId"));
				
				System.out.println(++count);
			}
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