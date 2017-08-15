import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.Scanner;
import org.json.*;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

public class Main {

	static String KEY = "RGAPI-c23c9494-b076-4711-9ec0-70a0a6b94508";
	static String MAIN_URL = "https://la1.api.riotgames.com";
	
	static Bandwidth limit;
	static Bucket bucket;
	static int requests = 25;
	static Duration time = Duration.ofSeconds(1);

	public static void main(String[] args) throws IOException, InterruptedException {
		// Define the limit 20 times per 1 second
		// These lines are used to control rate limit.
		limit = Bandwidth.simple(requests, time);
		bucket = Bucket4j.builder().addLimit(0, limit).build();

		calculatePerformanceSummoner("Ara Asura");
		calculatePerformanceSummoner("Ara Asura");
		calculatePerformanceSummoner("Ara Asura");
		calculatePerformanceSummoner("Ara Asura");
		calculatePerformanceSummoner("Ara Asura");
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

	public static JSONObject getSummonerByName(String name) throws IOException, InterruptedException {
		// Consume one API request (rate limit control system)
		bucket.consume(1, BlockingStrategy.PARKING);
		
		//URLEncoder.encode(name, "UTF-8")
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
		// JSONArray list =
		// getCurrentListParticipantBySummonerId(getSummonerIdbyName(name));
		//
		// for(Object ob : list) {
		// JSONObject p = (JSONObject) ob;
		// System.out.println("Champion: " + p.getLong("championId") + "\t" + "Summoner:
		// " + p.getString("summonerName"));
		// }
	}

	public static JSONObject getLast20MatchesByAccountId(long accountId) throws IOException, InterruptedException {
		// Consume one API request (rate limit control system)
		bucket.consume(1, BlockingStrategy.PARKING);
		
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

	public static JSONObject getMatchByMatchId(long matchId) throws IOException {
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

	public static JSONObject getMatchByMatchId(long matchId, long accountId) throws IOException, InterruptedException {
		// Consume one API request (rate limit control system)
		bucket.consume(1, BlockingStrategy.PARKING);
		
		String s = MAIN_URL + "/lol/match/v3/matches/" + matchId + "?forAccountId=" + accountId + "&api_key=" + KEY;
		URL url = new URL(s);

		Scanner scan = new Scanner(url.openStream());
		String str = new String();
		while (scan.hasNext())
			str += scan.nextLine();
		scan.close();

		JSONObject match = new JSONObject(str);

		return match;
	}

	/*
	 * Gets all the participants of a current match and then, for each participant
	 * it gets the last 20 matches and evaluates their performance.
	 */
	public static void allKDACalculation(String name) throws IOException, InterruptedException {
		// Consume one API request (rate limit control system)
		bucket.consume(1, BlockingStrategy.PARKING);
		// For each participant in the current match.
		for (Object p : getCurrentListParticipantBySummonerId(getSummonerByName("MaikelRyuu").getLong("id"))) {

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
			for (Object m : matchesObject.getJSONArray("matches")) {

				// Match obtained from matches.
				JSONObject matchReference = (JSONObject) m;

				// Consume one API request (rate limit control system)
				bucket.consume(1, BlockingStrategy.PARKING);
				// Match object (detailed information)
				JSONObject match = getMatchByMatchId(matchReference.getLong("gameId"));

				System.out.println(++count);
			}
		}
	}

	public static void calculatePerformanceSummoner(String name) throws IOException, InterruptedException {
		// Get the summoner object.
		JSONObject summoner = getSummonerByName(name);

		// Get the object to handle the last 20 matches.
		JSONObject last20Matches = getLast20MatchesByAccountId(summoner.getLong("accountId"));

		// Array with the last 20 matches.
		JSONArray matches = last20Matches.getJSONArray("matches");

		// Variables for stats.
		double kills = 0, deaths = 0, assists = 0;
		double visionScore = 0, wardsPlaced = 0, wardsKilled = 0;
		double wins = 0;
		String[] lanes = new String[last20Matches.getInt("totalGames")];
		double top = 0, jg = 0, mid = 0, bot = 0;

		for (int i=0; i<last20Matches.getInt("totalGames"); i++) {
			JSONObject matchReference = matches.getJSONObject(i);

			// Match object.
			JSONObject match = getMatchByMatchId(matchReference.getLong("gameId"), summoner.getLong("accountId"));

			// Array of the participants.
			JSONArray participantIdentities = match.getJSONArray("participantIdentities");

			// Get the participantId of the summoner in the match.
			int participantId = -1;
			for (Object p : participantIdentities) {
				JSONObject participant = (JSONObject) p;

				if (2 == participant.length()) {
					// This is left for post-checking.
					JSONObject player = participant.getJSONObject("player");

					if (player.getString("summonerName").equals(summoner.getString("name"))) {
						participantId = participant.getInt("participantId");
						break;
					}
				}
			}

			JSONArray participants = match.getJSONArray("participants");

			// Participant object whose information is about the match.
			JSONObject participant = (JSONObject) participants.get(participantId - 1);

			JSONObject stats = participant.getJSONObject("stats");
			
			JSONObject timeline = participant.getJSONObject("timeline");

			kills += stats.getInt("kills");
			deaths += stats.getInt("deaths");
			assists += stats.getInt("assists");
			
			if(stats.has("visionScore")) {
				visionScore += stats.getInt("visionScore");
			}
			if(stats.has("wardsPlaced")) {
				wardsPlaced += stats.getInt("wardsPlaced");
			}
			if(stats.has("wardsPlaced")) {
				wardsKilled += stats.getInt("wardsKilled");
			}
			
			if(stats.getBoolean("win")) {
				wins++;
			}
			
			lanes[i] = timeline.getString("lane");
			switch(lanes[i]) {
			case "TOP": top++;
				break;
			case "JUNGLE":
				if(participant.getInt("spell2Id") == 11 || participant.getInt("spell1Id") == 11) {
					jg++;
				}
				break;
			case "MIDDLE": mid++;
				break;
			case "BOTTOM": bot++;
				break;
			}
		}

		kills = kills / last20Matches.getInt("totalGames");
		deaths = deaths / last20Matches.getInt("totalGames");
		assists = assists / last20Matches.getInt("totalGames");
		
		wardsPlaced = wardsPlaced / last20Matches.getInt("totalGames");
		wardsKilled = wardsKilled / last20Matches.getInt("totalGames");
		visionScore = visionScore / last20Matches.getInt("totalGames");
		
		wins = wins / last20Matches.getInt("totalGames");
		
		top = top / last20Matches.getInt("totalGames");
		jg = jg / last20Matches.getInt("totalGames");
		mid = mid / last20Matches.getInt("totalGames");
		bot = bot / last20Matches.getInt("totalGames");

		System.out.println(summoner.getString("name"));
		System.out.println("Win %: " + wins);
		System.out.println("KDA: " + ((kills + assists) / deaths) + "\t" + "Kills: " + kills + "\t" + "Deaths: "
				+ deaths + "\t" + "Assists: " + assists);
		System.out.println("Vision Score: " + visionScore + "\t" + "Wards placed: " + wardsPlaced + "\t" + "Wards killed: " + wardsKilled);
		System.out.println("Lanes: " + StringArraytoString(lanes));
		System.out.println("Lane %: " + "Top: " + top + "\t" + "Jungle: " + jg + "\t" + "Mid: " + mid + "\t" + "Bottom: " + bot);
		System.out.println("Matches analyzed: " + last20Matches.getInt("totalGames"));
	}
	
	static String StringArraytoString(String[] array) {
		String res = "[";
		for(String s : array) {
			res += s + ", ";
		}
		return res += "]";
	}

	public static JSONObject getSummonerSpellList() throws IOException {
		String s = MAIN_URL + "/lol/static-data/v3/summoner-spells?api_key=" + KEY;
		URL url = new URL(s);

		Scanner scan = new Scanner(url.openStream());
		String str = new String();
		while (scan.hasNext())
			str += scan.nextLine();
		scan.close();

		JSONObject spellList = new JSONObject(str);

		return spellList;
	}
}