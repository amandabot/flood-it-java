/**
 * @author Amanda
 * @version %I% %U%
 */
package flooditgame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FloodItGame {

    private final int NUM_OF_SQUARES = 14; //Number of squares on the board
    private final int SQUARE_SIZE = 30; //Size in pixels of each square
    private final int BOARD_SIZE = NUM_OF_SQUARES * SQUARE_SIZE;
    private final JPanel gameboard;
    private final JFrame frame;
    private final Random random = new Random(); //Used in generating new boards
    private final static List<Color> colors = new ArrayList<>(); //Used in generating new boards
    private final List<JButton> colorButtons = new ArrayList<>(); //References to game buttons. Used to change colors for new game
    private static final FloodItGame INSTANCE = new FloodItGame();
    /* Tracks the current color of each square */
    private int[][] colorBoard = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
    private JLabel turnsLabel;
    private int numOfTurns;
    private Color currentColor; //Current color of the square at (0, 0)
    /* Indicates that the win/lose dialog has been shown and prevents further 
     * action until a new game is started */
    private boolean shown = false;

    /**
     * Creates a unique and single instance of the FloodItGame.
     *
     * @return the {@code FloodItGame} instance
     */
    public static FloodItGame getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor for FloodItGame
     */
    private FloodItGame() {
        gameboard = new JPanel();
        gameboard.setBackground(Color.DARK_GRAY);
        gameboard.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE + SQUARE_SIZE + 10));
        gameboard.setLayout(new BorderLayout());
        gameboard.add(createButtonPanel(), BorderLayout.NORTH);
        resetBoard(); //Must precede createPaintBoard()
        gameboard.add(createPaintBoard(), BorderLayout.CENTER); //Must follow resetBoard

        frame = new JFrame("Flood It!");
        frame.getContentPane().add(gameboard);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Resets the board with new colors and displays it. Resets labels and
     * status flags
     */
    private void resetBoard() {
        colors.clear();
        for (int k = 0; k < 6; k++) {
            colors.add(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            colorButtons.get(k).setBackground(colors.get(k));
        }
        for (int i = 0; i < NUM_OF_SQUARES; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                colorBoard[i][j] = random.nextInt(colors.size());
            }
        }
        numOfTurns = 0;
        turnsLabel.setText("Turns 0/25");
        shown = false;
        gameboard.repaint();
    }

    /**
     * Fills the square at ({@code xIndex}, {@code yIndex}) with
     * {@code newColor}. Checks the vertical and horizontal neighbors of (
     * {@code xIndex},{@code yIndex}) and fills them with {@code newColor} if
     * they are {@code oldColor}.
     *
     * @param xIndex the x coordinate of the square to be filled
     * @param yIndex the y coordinate of the square to be filled
     * @param oldColor the target color of squares to be filled
     * @param newColor the new color which will fill the square
     */
    private void floodFill(int xIndex, int yIndex, Color oldColor, Color newColor) {
        if (xIndex > NUM_OF_SQUARES - 1 || xIndex < 0 || yIndex > NUM_OF_SQUARES - 1 || yIndex < 0) {
            return;
        }
        if (colors.get(colorBoard[xIndex][yIndex]) != oldColor) {
            return;
        }
        int newColorIndex = colors.indexOf(newColor);
        colorBoard[xIndex][yIndex] = newColorIndex;
        floodFill(xIndex + 1, yIndex, oldColor, newColor);
        floodFill(xIndex - 1, yIndex, oldColor, newColor);
        floodFill(xIndex, yIndex + 1, oldColor, newColor);
        floodFill(xIndex, yIndex - 1, oldColor, newColor);
    }

    /**
     * Determines if the game has been won or lost, or if it remains in play.
     * Displays a messages indicating a win or loss. Increments the turn counter
     * and updates the label displaying the number of turns consumed.
     */
    private void checkForWin() {
        numOfTurns += 1;
        turnsLabel.setText("Turns " + numOfTurns + "/25");

        //Checks for a win
        int temp = colorBoard[0][0];
        boolean flag = true;
        for (int i = 0; i < NUM_OF_SQUARES; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                if (colorBoard[i][j] != temp) {
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            JOptionPane.showMessageDialog(frame, "You Win!", "", JOptionPane.INFORMATION_MESSAGE);
            shown = true;
            return;
        }

        //Checks for a loss
        if (numOfTurns > 24) {
            JOptionPane.showMessageDialog(frame, "You Lose!", "", JOptionPane.INFORMATION_MESSAGE);
            shown = true;
        }
    }

    /**
     * Creates the panel that controls the game. Panel indicates the number of
     * turns taken, contains buttons that when clicked determine the current
     * fill color, and contains a button to start a new game.
     */
    private JPanel createButtonPanel() {
        JPanel jpanel = new JPanel();
        jpanel.setPreferredSize(new Dimension(BOARD_SIZE, SQUARE_SIZE + 10));
        jpanel.setBackground(Color.LIGHT_GRAY);
        turnsLabel = new JLabel("Turns 0/25");
        jpanel.add(turnsLabel);

        //Creates the buttons for changing the current fill color
        for (int i = 0; i < 6; i++) {
            colorButtons.add(createColorButtons(i));
            jpanel.add(colorButtons.get(i));
        }

        //Creates the new game button
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
            }
        });
        jpanel.add(newGameButton);
        return jpanel;
    }

    /**
     * Convenience method for creating the buttons which control the current
     * fill color.
     */
    private JButton createColorButtons(int index) {
        JButton jbutton = new JButton();
        //jbutton.setBackground(colors.get(index));
        jbutton.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
        jbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Indicates the game has been won or lost, and prevents further
                 * action from taking place */
                if (shown) {
                    return;
                }
                JButton jbutton = (JButton) e.getSource();
                currentColor = colors.get(colorBoard[0][0]);
                Color fillColor = jbutton.getBackground();
                //Checks that the clicked button is not the same color as (0,0)
                if (fillColor != currentColor) {
                    floodFill(0, 0, currentColor, fillColor);
                    gameboard.repaint();
                    checkForWin();
                }
            }
        });
        return jbutton;
    }

    /**
     * Creates the panel which displays the painted squares. Updates based on
     * the current state of colorBoard
     */
    private JPanel createPaintBoard() {
        JPanel jpanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                for (int i = 0; i < NUM_OF_SQUARES; i++) {
                    for (int j = 0; j < NUM_OF_SQUARES; j++) {
                        g2.setColor(colors.get(colorBoard[i][j]));
                        g2.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                    }
                }
            }
        };
        jpanel.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        return jpanel;
    }
}
