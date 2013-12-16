package edu.wm.mashpotato.web;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Potato implements Serializable {
	private String pId;
	private int multiplier;

	public Potato(String pId, int multiplier, long creationDate, String holder,
			int lifeSpan, String gameID, int temp, double[] loc, long holding) {
		super();
		this.pId = pId;
		this.multiplier = multiplier;
		this.creationDate = creationDate;
		this.holder = holder;
		this.lifeSpan = lifeSpan;
		this.gameID = gameID;
		this.temp = temp;
		this.loc = loc;
		this.holding = holding;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public int getLifeSpan() {
		return lifeSpan;
	}

	public void setLifeSpan(int lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public String getGameID() {
		return gameID;
	}

	public void setGameID(String gameID) {
		this.gameID = gameID;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public double[] getLoc() {
		return loc;
	}

	public void setLoc(double[] loc) {
		this.loc = loc;
	}

	private long creationDate;
	private String holder;
	private int lifeSpan;
	private String gameID;
	private int temp;
	private double[] loc;
	private long holding;

	public int changeTemp(int steps) {
		long d = new Date().getTime();
		Random r = new Random(d);
		long e = (d - creationDate) * 100 / lifeSpan;
		steps = steps / 10;
		temp = temp - steps % 10 + (int) e;
		if (temp < 0) {
			temp = 0;
		} else if (temp > 100) {
			temp = 100;
		}
		return temp;
	}

	public long getHolding() {
		return holding;
	}

	public void setHolding(long holding) {
		this.holding = holding;
	}

}
