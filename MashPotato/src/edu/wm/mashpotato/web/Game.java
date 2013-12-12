package edu.wm.mashpotato.web;

import java.util.List;

public class Game {
    public Game(String id, String owner, long creationDate, long maxRoundTime,
			int roundCount, int state, double[] originalLocation,
			int potatoCount, List<String> players, List<String> potato) {
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
    private List<String>players;
    private List<String>potato; 
}
