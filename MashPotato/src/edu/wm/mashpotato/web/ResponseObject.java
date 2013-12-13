package edu.wm.mashpotato.web;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ResponseObject {
	public String status;
	public Game game;
	public Player me;
	public List<Game> lobbyList;
	public boolean success;
	private final static String TAG = "ResponseObject";
	public static ResponseObject createResponse(String resp, boolean lobby) throws JSONException{
		Log.v(TAG, resp);
		JSONObject obj = new JSONObject(resp);
		ResponseObject retVal = new ResponseObject();
		retVal.status = obj.getString(Constants.status);
		retVal.success = retVal.status.equals(Constants.success);
		boolean hasGame = obj.has(Constants.lobby);
		List<Game> gamesList = new ArrayList<Game>();
		if(hasGame){
			JSONArray objArray = obj.getJSONArray(Constants.lobby);
			ResponseObject.makeGameList(objArray);
		}
		if(!lobby && hasGame){
			Log.v(TAG, obj.toString());
			retVal.game = gamesList.get(0);
//			double lat = obj.get
		}else{
			retVal.lobbyList = gamesList;
			Log.v(TAG, obj.toString());
		}
		return retVal;
	}
	
	public static List<Player> makePlayerList(JSONArray array) throws JSONException{
		List<Player> playerList = new ArrayList<Player>();
		for (int i = 0; i < array.length(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
        	String id = obj.getString(Constants.id);
            boolean isOut = obj.getBoolean(Constants.isOut);
//            private double lat;
//            private double lng;
            String userId = obj.getString(Constants.id);
            boolean hasPotato = obj.getBoolean(Constants.hasPotato);
            Integer score = obj.getInt(Constants.score);
            String game = obj.getString(Constants.game);
            Player player = new Player(id, isOut, 0, 0, userId, hasPotato, (int)score, game, null, null);
            playerList.add(player);
		}
		return playerList;
	}
	
	public static List<Game> makeGameList(JSONArray array) throws JSONException{
		List<Game> gameList = new ArrayList<Game>();
		Game g = null;
		for (int i = 0; i < array.length(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
			String id = obj.getString(Constants.id);
			String owner = obj.getString(Constants.owner);
			long maxRoundTime = obj.getLong(Constants.maxRoundTime);
			long creationDate = obj.getLong(Constants.creationDate);
			int roundCount = obj.getInt(Constants.roundCount);
			int state = obj.getInt(Constants.state);
			JSONArray playerObj = obj.getJSONArray(Constants.players);
			List<Player> playerList = makePlayerList(playerObj);
			g = new Game(id, owner, maxRoundTime, creationDate, roundCount, state, null, state, playerList, null);
			gameList.add(g);
		}
		return gameList;
	}
}
