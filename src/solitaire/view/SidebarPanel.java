package solitaire.view;

import solitaire.model.board.BoardFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    private static final Color BG     = Color.WHITE;
    private static final Color ACCENT = new Color(0x3B5BDB);
    private static final Font  LABEL  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font  HEAD   = new Font("SansSerif", Font.BOLD, 14);

    private final JSpinner     sizeSpinner;
    private final ButtonGroup  typeGroup = new ButtonGroup();
    private final ButtonGroup  modeGroup = new ButtonGroup();
    private final JLabel       statusLabel;
    private final JButton      autoplayButton;
    private final JButton      randomizeButton;
    private final JButton      undoButton;

    public SidebarPanel(BiConsumer<String, Integer> onNewGame, 
                       Runnable onUndo,
                       Runnable onAutoplay,
                       Runnable onRandomize,
                       Consumer<String> onModeChange) {
        setBackground(BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0xE0E0E0)),
            new EmptyBorder(30, 20, 30, 20)
        ));
        setPreferredSize(new Dimension(200, 0));

        // Board Type section
        add(sectionLabel("Board Type"));
        add(Box.createVerticalStrut(10));

        for (String type : BoardFactory.TYPES) {
            JRadioButton btn = new JRadioButton(type);
            btn.setFont(LABEL);
            btn.setBackground(BG);
            btn.setActionCommand(type);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            if (type.equals("English")) btn.setSelected(true);
            typeGroup.add(btn);
            add(btn);
            add(Box.createVerticalStrut(5));
        }

        add(Box.createVerticalStrut(20));

        // Board Size section
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sizePanel.setBackground(BG);
        sizePanel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel sizeLabel = new JLabel("Board size ");
        sizeLabel.setFont(HEAD);
        sizeLabel.setForeground(Color.BLACK);
        
        sizeSpinner = new JSpinner(new SpinnerNumberModel(7, 5, 9, 2));
        sizeSpinner.setFont(LABEL);
        JComponent editor = sizeSpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setColumns(3);
        }
        
        sizePanel.add(sizeLabel);
        sizePanel.add(sizeSpinner);
        add(sizePanel);

        add(Box.createVerticalStrut(30));

        // Game Mode section
        add(sectionLabel("Game Mode"));
        add(Box.createVerticalStrut(10));

        JRadioButton manualBtn = new JRadioButton("Manual");
        manualBtn.setFont(LABEL);
        manualBtn.setBackground(BG);
        manualBtn.setActionCommand("Manual");
        manualBtn.setAlignmentX(LEFT_ALIGNMENT);
        manualBtn.setSelected(true);
        manualBtn.addActionListener(e -> onModeChange.accept("Manual"));
        modeGroup.add(manualBtn);
        add(manualBtn);
        add(Box.createVerticalStrut(5));

        JRadioButton autoBtn = new JRadioButton("Automated");
        autoBtn.setFont(LABEL);
        autoBtn.setBackground(BG);
        autoBtn.setActionCommand("Automated");
        autoBtn.setAlignmentX(LEFT_ALIGNMENT);
        autoBtn.addActionListener(e -> onModeChange.accept("Automated"));
        modeGroup.add(autoBtn);
        add(autoBtn);

        add(Box.createVerticalStrut(30));
        
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setFont(LABEL);
        newGameBtn.setAlignmentX(LEFT_ALIGNMENT);
        newGameBtn.setMaximumSize(new Dimension(160, 35));
        newGameBtn.addActionListener(e -> onNewGame.accept(selectedType(), selectedSize()));
        add(newGameBtn);

        add(Box.createVerticalStrut(10));

        randomizeButton = new JButton("Randomize");
        randomizeButton.setFont(LABEL);
        randomizeButton.setAlignmentX(LEFT_ALIGNMENT);
        randomizeButton.setMaximumSize(new Dimension(160, 35));
        randomizeButton.addActionListener(e -> onRandomize.run());
        randomizeButton.setVisible(true);
        add(randomizeButton);

        add(Box.createVerticalStrut(10));

        autoplayButton = new JButton("Autoplay");
        autoplayButton.setFont(LABEL);
        autoplayButton.setAlignmentX(LEFT_ALIGNMENT);
        autoplayButton.setMaximumSize(new Dimension(160, 35));
        autoplayButton.addActionListener(e -> onAutoplay.run());
        autoplayButton.setVisible(false);
        add(autoplayButton);

        add(Box.createVerticalStrut(10));
        
        undoButton = new JButton("Undo");
        undoButton.setFont(LABEL);
        undoButton.setAlignmentX(LEFT_ALIGNMENT);
        undoButton.setMaximumSize(new Dimension(160, 35));
        undoButton.addActionListener(e -> onUndo.run());
        add(undoButton);

        add(Box.createVerticalStrut(30));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(0x666666));
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(statusLabel);
        
        add(Box.createVerticalGlue());
    }

    public void setStatus(String text, Color color) {
        statusLabel.setText("<html><body style='width:130px'>" + text + "</body></html>");
        statusLabel.setForeground(color);
    }

    public void setMode(String mode) {
        boolean isManual = mode.equals("Manual");
        autoplayButton.setVisible(!isManual);
        randomizeButton.setVisible(isManual);
    }

    public void setButtonStates(boolean undoEnabled, boolean actionEnabled) {
        undoButton.setEnabled(undoEnabled);
        autoplayButton.setEnabled(actionEnabled);
        randomizeButton.setEnabled(actionEnabled);
    }

    private String selectedType() {
        return typeGroup.getSelection().getActionCommand();
    }

    private int selectedSize() {
        return (Integer) sizeSpinner.getValue();
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(HEAD);
        l.setForeground(Color.BLACK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}
