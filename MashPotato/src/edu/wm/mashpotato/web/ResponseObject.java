package edu.wm.mashpotato.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ResponseObject implements Serializable {
	public String status;
	public Game game;
	public Player me;
	public List<Game> lobbyList;
	public boolean success;
	private final static String TAG = "ResponseObject";

	public static ResponseObject createResponse(String resp, boolean lobby, String username)
			throws JSONException {
		Log.v(TAG, resp);
		JSONObject obj = new JSONObject(resp);
		ResponseObject retVal = new ResponseObject();
		retVal.status = obj.getString(Constants.status);
		retVal.success = retVal.status.equals(Constants.success);
		boolean hasGame = !obj.isNull(Constants.lobby);
		System.out.println(hasGame);
		List<Game> gamesList = new ArrayList<Game>();
		if (hasGame) {
			JSONArray objArray = obj.getJSONArray(Constants.lobby);
			gamesList = ResponseObject.makeGameList(objArray, retVal, username);
		}
		if (!lobby && hasGame) {
			Log.v(TAG, obj.toString());
			retVal.game = gamesList.get(0);
			// double lat = obj.get
		} else {
			retVal.lobbyList = gamesList;
			Log.v(TAG, obj.toString());
		}
		return retVal;
	}

	public static List<Player> makePlayerList(JSONArray array, ResponseObject resp, String username)
			throws JSONException {
		List<Player> playerList = new ArrayList<Player>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String id = obj.getString(Constants.id);
			boolean isOut = obj.getBoolean(Constants.isOut);
			// private double lat;
			// private double lng;
			String userId = obj.getString(Constants.id);
			boolean hasPotato = obj.getBoolean(Constants.hasPotato);
			Integer score = obj.getInt(Constants.score);
			String game = obj.getString(Constants.game);
			Player player = new Player(id, isOut, 0, 0, userId, hasPotato,
					(int) score, game, null, null);
			if(userId.equals(username)){
				resp.me = player;
			}
			playerList.add(player);
		}
		return playerList;
	}

	public static List<Game> makeGameList(JSONArray array, ResponseObject resp, String username) throws JSONException {
		List<Game> gameList = new ArrayList<Game>();
		Game g = null;
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String id = obj.getString(Constants.id);
			String owner = obj.getString(Constants.owner);
			long maxRoundTime = obj.getLong(Constants.maxRoundTime);
			long creationDate = obj.getLong(Constants.creationDate);
			int roundCount = obj.getInt(Constants.roundCount);
			List<Potato> potatoList = makePotatoList(obj.getJSONArray("potato"), resp,username);
			int state = obj.getInt(Constants.state);
			JSONArray playerObj = obj.getJSONArray(Constants.players);
			List<Player> playerList = makePlayerList(playerObj, resp, username);
			g = new Game(id, owner, maxRoundTime, creationDate, roundCount,
					state, null, state, playerList, potatoList);
			gameList.add(g);
		}
		return gameList;
	}

	private static List<Potato> makePotatoList(JSONArray array,
			ResponseObject resp, String username) throws JSONException {
		List<Potato> pList = new ArrayList<Potato>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.get(i);
			String id = obj.getString("pId");
			long creationDate = obj.getLong("creationDate");
			// private double lat;
			// private double lng;
			String holder = obj.getString("holder");
//			boolean hasPotato = obj.getBoolean(Constants.hasPotato);
			int score = 0;
//			Integer score = obj.getInt(Constants.score);
			long lifeSpan = obj.getLong("lifeSpan");
			String game = obj.getString("gameID");
			Potato p = new Potato(id, 1, creationDate, holder, lifeSpan, game, score, null, 0);
			pList.add(p);
		}
		
		return pList;
	}
}
