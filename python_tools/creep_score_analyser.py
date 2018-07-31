import urllib.request, json
import time


API_KEY = "?api_key=<INSERT_API_KEY_HERE>"
URL = "https://la1.api.riotgames.com/"
GET_SUMMONER_BY_NAME_QUERY = "lol/summoner/v3/summoners/by-name/"
GET_MATCHES_LIST_BY_ACCOUNT_ID_QUERY = "lol/match/v3/matchlists/by-account/"
GET_MATCH_TIMELINE_BY_MATCH_ID_QUERY = "lol/match/v3/timelines/by-match/"
GET_MATCH_BY_MATCH_ID_QUERY = "lol/match/v3/matches/"
GET_CHAMPION_BY_ID_QUERY = "lol/static-data/v3/champions/"


def get_participant_id(summoner_name, match_id):
  result = urllib.request.urlopen(URL + GET_MATCH_BY_MATCH_ID_QUERY + match_id + API_KEY)
  match = json.loads(result.read().decode())

  participant_identities = match['participantIdentities']
  for participant in participant_identities:
    if participant['player']['summonerName'] == summoner_name:
      return participant['participantId']
  return -1


def convert_milliseconds_to_minutes(milliseconds):
  minutes=(milliseconds/(1000*60))%60
  return int(minutes)


# match_index is a 0-based index, 0 meaning the most recent match/game.
def analyse_creep_score(summoner_name, match_index):
    result = urllib.request.urlopen(URL + GET_SUMMONER_BY_NAME_QUERY + format_spaces_to_url(summoner_name) + API_KEY)
    summoner = json.loads(result.read().decode())

    result = urllib.request.urlopen(URL + GET_MATCHES_LIST_BY_ACCOUNT_ID_QUERY + str(summoner['accountId']) + API_KEY)
    matches_list = json.loads(result.read().decode())
    # TODO: make these comments a function to list all recent matches.
    # Print all matches V
    # print("Total games: ", matches_list['totalGames'])
    # for match in matches_list['matches']:
    #   print(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(match['timestamp'] / 1000)), match['gameId'], match['lane'])

    match = matches_list['matches'][match_index]
    print("Summoner name:", summoner_name)
    print("Timestamp:", time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(match['timestamp'] / 1000)))
    print("Lane:", match['lane'])
    print("Champion:", get_champion_name(match['champion']))

    result = urllib.request.urlopen(URL + GET_MATCH_TIMELINE_BY_MATCH_ID_QUERY + str(match['gameId']) + API_KEY)
    timeline = json.loads(result.read().decode())
    summoner_participant = get_participant_id(summoner_name, str(match['gameId']))
    print("Min", "\t", "CS", '\t', "Perfect CS", '\t', '%')
    for frame in timeline['frames']:
        # TODO: make this robuster.
        # this solves temporarily completeness with the perfect cs table.
        if convert_milliseconds_to_minutes(frame['timestamp']) > len(MINUTE_TO_PERFECT_CS_MAP) - 1 or MINUTE_TO_PERFECT_CS_MAP[convert_milliseconds_to_minutes(frame['timestamp'])] == 0:
          continue
          
        participant_frames = frame['participantFrames'][str(summoner_participant)]
        print(
            convert_milliseconds_to_minutes(frame['timestamp']), "\t", 
            participant_frames['minionsKilled'], "\t",
            MINUTE_TO_PERFECT_CS_MAP[convert_milliseconds_to_minutes(frame['timestamp'])], "\t\t",
            participant_frames['minionsKilled'] / MINUTE_TO_PERFECT_CS_MAP[convert_milliseconds_to_minutes(frame['timestamp'])]
        )


def get_champion_name(champion_id):
    result = urllib.request.urlopen(URL + GET_CHAMPION_BY_ID_QUERY + str(champion_id) + API_KEY)
    champion = json.loads(result.read().decode())
    return champion['name']


def format_spaces_to_url(string):
    return string.replace(" ", "%20")


MINUTE_TO_PERFECT_CS_MAP = {
    0 : 0,
    1 : 0,
    2 : 6,
    3 : 19,
    4 : 31,
    5 : 44,
    6 : 57,
    7 : 69,
    8 : 82,
    9 : 95,
    10 : 107,
    11 : 120,
    12 : 132,
    13 : 145,
    14 : 158,
    15 : 171,
    16 : 183,
    17 : 196,
    18 : 209,
    19 : 221,
    20 : 234,
    21 : 247,
    22 : 260,
    23 : 273,
    24 : 286,
    25 : 299,
    26 : 312,
    27 : 325,
    28 : 338,
    29 : 351,
    30 : 364,
}


analyse_creep_score("Brasped", 0)
