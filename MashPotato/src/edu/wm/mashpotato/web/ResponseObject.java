package edu.wm.mashpotato.web;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ResponseObject {
	public String status;
	public Game game;
	public boolean success;
	private final static String TAG = "Response";
	public static ResponseObject createResponse(String resp, boolean lobby) throws JSONException{
		JSONObject obj = new JSONObject(resp);
		ResponseObject retVal = new ResponseObject();
		retVal.status = obj.getString(Constants.status);
		retVal.success = retVal.status.equals(Constants.success);
		if(!lobby){
			obj = obj.getJSONObject(Constants.game);
			Log.v(TAG, obj.toString());
//			double lat = obj.get
//			Game g = new Game(obj.getString(Constants.id), obj.getString(Constants.owner),
//					obj.getLong(Constants.creationDate), obj.getLong(Constants.maxRoundTime),
//					obj.getInt(Constants.roundCount), obj.getInt(Constants.state), , 0, null, null);
		}else{
			Log.v(TAG, obj.toString());
		}
		return retVal;
	}
}
