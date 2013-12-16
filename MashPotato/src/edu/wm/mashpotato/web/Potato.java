package edu.wm.mashpotato.web;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.lang.Math;

import android.util.Log;

public class Potato implements Serializable {
	private String pId;
	private int multiplier;
	private long creationDate;
    private String holder;
    private long lifeSpan;
    private String gameID;
    private int temp;
    private double[] loc;
    private long holding;
    private static String TAG = "Potato";

	public Potato(String pId, int multiplier, long creationDate, String holder,
			long lifeSpan, String gameID, int temp, double[] loc, long holding) {
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
	public long getLifeSpan() {
		return lifeSpan;
	}
	public void setLifeSpan(long lifeSpan) {
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

    public int changeTemp(int steps){
    	long d = new Date().getTime();
    	Random r = new Random(d);
    	long e = (d + r.nextInt(((int) (d - creationDate))) - creationDate) * 100 / lifeSpan;
    	if(e < 0){
    		e = d % lifeSpan / 1000 / 60;
    	}
    	Log.e(TAG, "Before temp " + temp+" steps "+steps + " e " + e );
//    	if(d - creationDate / lifeSpan > 1){
//    		temp = 100;
//    		return temp;
//    	}
    	steps = (int) Math.log(steps);
    	Log.e(TAG, "Before temp " + temp+" steps "+steps + " e " + e );
    	temp = temp - steps * 3 + (int)e;
    	Log.e(TAG, "After temp " + temp);
    	if(temp < 0){
    		temp = 0;
    	}else if(temp > 100){
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
