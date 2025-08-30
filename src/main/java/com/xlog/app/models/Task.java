
package com.xlog.app.models;
public class Task {
    private final long id; private final String name; private final StatType stat;
    public Task(long id, String name, StatType stat){ this.id=id; this.name=name; this.stat=stat; }
    public long getId(){ return id; } public String getName(){ return name; } public StatType getStat(){ return stat; }
}
