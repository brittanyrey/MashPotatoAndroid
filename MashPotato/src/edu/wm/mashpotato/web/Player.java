package edu.wm.mashpotato.web;

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable {
	private String id;
	private boolean isOut;
	public double lat;
	public double lng;
	private String userId;
	private boolean hasString;
	private int score;
	private String game;
	private List<String> itemList;
	private List<String> potatoList;

	public Player(String id, boolean isOut, double lat, double lng,
			String userId, boolean hasString, int score, String game,
			List<String> itemList, List<String> potatoList) {
		super();
		this.id = id;
		this.isOut = isOut;
		this.lat = lat;
		this.lng = lng;
		this.userId = userId;
		this.hasString = hasString;
		this.score = score;
		this.game = game;
		this.itemList = itemList;
		this.potatoList = potatoList;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isHasString() {
		return hasString;
	}

	public void setHasString(boolean hasString) {
		this.hasString = hasString;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public List<String> getItemList() {
		return itemList;
	}

	public void setItemList(List<String> itemList) {
		this.itemList = itemList;
	}

	public List<String> getPotatoList() {
		return potatoList;
	}

	public void setPotatoList(List<String> potatoList) {
		this.potatoList = potatoList;
	}
}
