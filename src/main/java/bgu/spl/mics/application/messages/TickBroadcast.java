package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private int tickTime;
    private int totalTicks;

    public TickBroadcast(int tickTime, int totalTicks) {
        this.tickTime = tickTime;
        this.totalTicks = totalTicks;
    }

    public int getTickTime() {
        return tickTime;
    }

    public int getTotalTicks(){
        return this.totalTicks;
    }

    @Override
    public String toString() {
        return "Tick No. " + tickTime;
    }
}
