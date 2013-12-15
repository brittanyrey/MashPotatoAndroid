package edu.wm.mashpotato.web;

import java.io.Serializable;
import java.util.List;

public class Game implements Serializable{
    public Game(String id, String owner, long creationDate, long maxRoundTime,
			int roundCount, int state, double[] originalLocation,
			int potatoCount, List<Player> players, List<Potato> potato) {
		super();
		this.id = id;
		this.owner = owner;
		this.creationDate = creationDate;
		this.maxRoundTime = maxRoundTime;
		this.roundCount = roundCount;
		this.state = state;
		this.originalLocation = originalLocation;
		this.potatoCount = potatoCount;
		this.players = players;
		this.potato = potato;
	}
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public long getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	public long getMaxRoundTime() {
		return maxRoundTime;
	}
	public void setMaxRoundTime(long maxRoundTime) {
		this.maxRoundTime = maxRoundTime;
	}
	public int getRoundCount() {
		return roundCount;
	}
	public void setRoundCount(int roundCount) {
		this.roundCount = roundCount;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public double[] getOriginalLocation() {
		return originalLocation;
	}
	public void setOriginalLocation(double[] originalLocation) {
		this.originalLocation = originalLocation;
	}
	public int getPotatoCount() {
		return potatoCount;
	}
	public void setPotatoCount(int potatoCount) {
		this.potatoCount = potatoCount;
	}
	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	public List<Potato> getPotato() {
		return potato;
	}
	public void setPotato(List<Potato> potato) {
		this.potato = potato;
	}

	private String id;
    private String owner;
    private long creationDate;
    private long maxRoundTime;
    private int roundCount;
    private int state;
    private double[] originalLocation;
    private int potatoCount;
    private List<Player>players;
    private List<Potato>potato; 
}
