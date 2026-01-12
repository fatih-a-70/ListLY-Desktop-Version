package org.example.listly;

public class FocusSession {
    public int id;
    public String mode; // "STOPWATCH", "COUNTDOWN_FORWARD", "COUNTDOWN_BACKWARD"
    public long durationPlanned;
    public long durationDone;
    public long dateMs;

    public FocusSession() {
    }

    public FocusSession(String mode, long durationPlanned, long durationDone, long dateMs) {
        this.mode = mode;
        this.durationPlanned = durationPlanned;
        this.durationDone = durationDone;
        this.dateMs = dateMs;
    }

    public boolean isCompleted() {
        if ("STOPWATCH".equals(mode)) {
            return true;
        }
        return durationPlanned > 0 && durationDone >= durationPlanned;
    }

    public long getRemaining() {
        if (durationPlanned > 0 && durationDone < durationPlanned) {
            return durationPlanned - durationDone;
        }
        return 0;
    }
}
