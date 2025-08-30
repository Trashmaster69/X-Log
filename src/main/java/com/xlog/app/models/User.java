
package com.xlog.app.models;
public class User {
    private String username; private int coins; private double xp; private int level;
    private int intelligence=5, charisma=5, strength=5, chi=5;
    public User(String username,int coins,double xp,int level){ this.username=username; this.coins=coins; this.xp=xp; this.level=level; }
    public String getUsername(){ return username; } public int getCoins(){ return coins; } public double getXp(){ return xp; } public int getLevel(){ return level; }
    public int getIntelligence(){ return intelligence; } public int getCharisma(){ return charisma; } public int getStrength(){ return strength; } public int getChi(){ return chi; }
    public void addCoins(int d){ coins = Math.max(0, coins + d); }
}
