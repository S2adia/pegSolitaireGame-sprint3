package solitaire.controller;

import solitaire.model.AutomatedGameMode;
import solitaire.model.GameMode;
import solitaire.model.GameStatus;
import solitaire.model.ManualGameMode;
import solitaire.view.AppWindow;

import javax.swing.*;
import java.awt.*;

public class GameController {

    private GameMode currentMode = new ManualGameMode();
    private final AppWindow window;

    public GameController() {
        window = new AppWindow(
            this::onNewGame, 
            this::onCellClick, 
            this::onUndo,
            this::onAutoplay,
            this::onRandomize,
            this::onModeChange
        );
        window.setStatus("Press New Game to start", new Color(0x868E96));
        window.setVisible(true);
    }

    private void onNewGame(String boardType, int size) {
        currentMode.newGame(boardType, size);
        window.board.setBoardType(boardType);
        refresh();
    }

    private void onCellClick(int row, int col) {
        currentMode.handleCellClick(row, col);
        refresh();
    }

    private void onUndo() {
        if (currentMode.undo()) refresh();
    }

    private void onModeChange(String mode) {
        // Preserve current board and history if game exists
        solitaire.model.board.Board currentBoard = currentMode.hasBoard() ? currentMode.getBoard() : null;
        java.util.Deque<solitaire.model.Move> currentHistory = new java.util.ArrayDeque<>(currentMode.getHistory());
        GameStatus currentStatus = currentMode.getStatus();

        // Create new mode instance
        currentMode = mode.equals("Manual") ? new ManualGameMode() : new AutomatedGameMode();

        // Transfer board and history to new mode if available
        if (currentBoard != null) {
            currentMode.setState(currentBoard, currentHistory, currentStatus);
        }

        updateUI();
    }

    private void updateUI() {
        // Update mode visibility
        String modeName = currentMode instanceof ManualGameMode ? "Manual" : "Automated";
        window.sidebar.setMode(modeName);
        
        refresh();
    }

    private void onAutoplay() {
        if (currentMode instanceof AutomatedGameMode auto) {
            if (auto.autoplay()) {
                // Get the last move from history
                solitaire.model.Move lastMove = currentMode.getHistory().peek();
                
                // Animate the move
                if (lastMove != null) {
                    window.board.animateMove(lastMove);
                }
                
                // Create Timer with 100ms delay for smooth visual transition
                Timer timer = new Timer(100, e -> refresh());
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void onRandomize() {
        if (currentMode instanceof ManualGameMode manual) {
            manual.randomize();
            refresh();
        }
    }

    private void refresh() {
        if (!currentMode.hasBoard()) return;

        // Get selected and reachable only for ManualGameMode
        int[] selected = null;
        java.util.Set<String> reachable = java.util.Set.of();
        if (currentMode instanceof ManualGameMode manual) {
            selected = manual.getSelected();
            reachable = manual.reachableFromSelected();
        }

        window.board.render(
            currentMode.getBoard().getGrid(),
            selected,
            reachable
        );

        String text  = statusText();
        Color  color = statusColor();
        window.setStatus(text, color);

        // Update button states
        boolean undoEnabled = !currentMode.getHistory().isEmpty();
        boolean actionEnabled = currentMode.getStatus() == GameStatus.PLAYING && 
                               !currentMode.getBoard().validMoves().isEmpty();
        window.sidebar.setButtonStates(undoEnabled, actionEnabled);
    }

    private String statusText() {
        return switch (currentMode.getStatus()) {
            case IDLE    -> "Press New Game to start";
            case PLAYING -> "Pegs left: " + currentMode.getBoard().pegCount();
            case WON     -> "You won! One peg left \uD83C\uDF89";
            case LOST    -> "No moves left. Game over.";
        };
    }

    private Color statusColor() {
        return switch (currentMode.getStatus()) {
            case IDLE    -> new Color(0x868E96);
            case PLAYING -> new Color(0x495057);
            case WON     -> new Color(0x2F9E44);
            case LOST    -> new Color(0xE03131);
        };
    }
}
