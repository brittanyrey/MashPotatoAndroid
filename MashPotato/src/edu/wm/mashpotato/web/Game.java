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
