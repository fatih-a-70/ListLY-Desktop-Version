package org.example.listly;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StopwatchController {

    @FXML private Label headerText;
    @FXML private Label timeText;
    @FXML private Label selectedModeLabel;
    @FXML private Label modeLabel;

    @FXML private Button focusModeBtn;
    @FXML private Button startBtn;
    @FXML private Button stopBtn;
    @FXML private Button resetBtn;
    @FXML private Button tenMinBtn;
    @FXML private Button thirtyMinBtn;
    @FXML private Button oneHourBtn;
    @FXML private Button threeHourBtn;
    @FXML private Button historyBtn;
    @FXML private Button customBtn;

    private enum FocusMode {
        STOPWATCH,
        COUNTDOWN_FORWARD,
        COUNTDOWN_BACKWARD
    }

    private FocusMode mode = FocusMode.STOPWATCH;
    private long presetMs = 0;
    private long baseTimeMs = 0;
    private long displayMs = 0;
    private boolean running = false;
    private long sessionStartMs = 0;

    private static final List<FocusSession> sessions = new ArrayList<>();

    private static class FocusSession {
        long durationPlanned;
        long durationDone;
        long dateMs;
        FocusMode mode;
    }

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (!running) return;
            long elapsed = System.currentTimeMillis() - baseTimeMs;
            if (mode == FocusMode.STOPWATCH) {
                displayMs = elapsed;
            } else if (mode == FocusMode.COUNTDOWN_FORWARD) {
                displayMs = elapsed;
                if (presetMs > 0 && elapsed >= presetMs) {
                    displayMs = presetMs;
                    timeText.setText(formatTime(displayMs));
                    stopRunning(true);
                    return;
                }
            } else if (mode == FocusMode.COUNTDOWN_BACKWARD) {
                long remain = presetMs - elapsed;
                if (remain <= 0) {
                    displayMs = 0;
                    timeText.setText(formatTime(displayMs));
                    stopRunning(true);
                    return;
                } else {
                    displayMs = remain;
                }
            }
            timeText.setText(formatTime(displayMs));
        }
    };

    @FXML
    private void initialize() {
        timeText.setText("00:00:00");
        modeLabel.setText("(not selected)");

        startBtn.setOnAction(e -> {
            if (!running) startSession();
        });
        stopBtn.setOnAction(e -> {
            if (running) stopRunning(false);
        });
        resetBtn.setOnAction(e -> resetAll());
        focusModeBtn.setOnAction(e -> showModeDialog());
        historyBtn.setOnAction(e -> showHistoryDialog());

        tenMinBtn.setOnAction(e -> applyPresetMinutes(10));
        thirtyMinBtn.setOnAction(e -> applyPresetMinutes(30));
        oneHourBtn.setOnAction(e -> applyPresetMinutes(60));
        threeHourBtn.setOnAction(e -> applyPresetMinutes(180));
        customBtn.setOnAction(e -> showCustomDialog());
    }

    private void startSession() {
        running = true;
        baseTimeMs = System.currentTimeMillis();
        sessionStartMs = baseTimeMs;
        timer.start();
    }

    private void stopRunning(boolean auto) {
        running = false;
        timer.stop();
        FocusSession s = new FocusSession();
        s.durationPlanned = presetMs;
        s.durationDone = displayMs;
        s.dateMs = sessionStartMs;
        s.mode = mode;
        sessions.add(s);
    }

    private void resetAll() {
        running = false;
        timer.stop();
        baseTimeMs = 0;
        displayMs = 0;
        presetMs = 0;
        mode = FocusMode.STOPWATCH;
        modeLabel.setText("(not selected)");
        timeText.setText("00:00:00");
    }

    private void applyPresetMinutes(int minutes) {
        presetMs = minutes * 60L * 1000L;
        if (mode == FocusMode.COUNTDOWN_BACKWARD) displayMs = presetMs;
        else displayMs = 0;
        timeText.setText(formatTime(displayMs));
    }

    private void showModeDialog() {
        String[] options = {"Stopwatch", "Countdown Forward", "Countdown Backward"};
        int i = Dialogs.choice("Focus Mode", java.util.Arrays.asList(options));
        if (i < 0) return;
        if (i == 0) mode = FocusMode.STOPWATCH;
        if (i == 1) mode = FocusMode.COUNTDOWN_FORWARD;
        if (i == 2) mode = FocusMode.COUNTDOWN_BACKWARD;
        modeLabel.setText(options[i]);
    }

    private void showCustomDialog() {
        String s = Dialogs.input("Custom Minutes", "Minutes", "");
        if (s == null || s.isEmpty()) return;
        int m;
        try { m = Integer.parseInt(s); } catch (Exception e) { m = 0; }
        presetMs = m * 60L * 1000L;
        if (mode == FocusMode.COUNTDOWN_BACKWARD) displayMs = presetMs;
        else displayMs = 0;
        timeText.setText(formatTime(displayMs));
        startSession();
    }

    private void showHistoryDialog() {
        if (sessions.isEmpty()) {
            Dialogs.info("Focus Sessions History", "No sessions yet");
            return;
        }
        StringBuilder sb = new StringBuilder();
        int i = sessions.size();
        for (FocusSession s : sessions) {
            String modeStr;
            if (s.mode == FocusMode.COUNTDOWN_FORWARD) modeStr = "countdown forward";
            else if (s.mode == FocusMode.COUNTDOWN_BACKWARD) modeStr = "countdown backward";
            else modeStr = "stopwatch";

            String dateStr = DateFormat.getDateInstance().format(new Date(s.dateMs));
            String timeStr = DateFormat.getTimeInstance().format(new Date(s.dateMs));

            sb.append("Focus session ").append(i).append("\n");
            long durationToShow = (s.mode == FocusMode.STOPWATCH)
                    ? s.durationDone
                    : (s.durationPlanned > 0 ? s.durationPlanned : s.durationDone);
            sb.append("Duration : ").append(formatTime(durationToShow)).append("\n");
            sb.append("Date : ").append(dateStr).append("\n");
            sb.append("Time : ").append(timeStr).append("\n");

            String status;
            if (s.mode == FocusMode.STOPWATCH) {
                status = "completed(" + formatTime(s.durationDone) + ")";
            } else if (s.durationPlanned > 0 && s.durationDone >= s.durationPlanned) {
                status = "completed(" + formatTime(s.durationPlanned) + ")";
            } else if (s.durationPlanned > 0) {
                long remaining = s.durationPlanned - s.durationDone;
                if (remaining < 0) remaining = 0;
                status = "not completed(" + formatTime(remaining) + " remained)";
            } else {
                status = "completed(" + formatTime(s.durationDone) + ")";
            }
            sb.append("Status : ").append(status).append("\n");
            sb.append("Focus mode : ").append(modeStr).append("\n\n");
            i--;
        }
        Dialogs.info("Focus Sessions History", sb.toString());
    }

    private String formatTime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
