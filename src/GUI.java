import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener
{
    public static int clickCounter = 0;
    private static final int xOffset = 100;
    private static final int yOffset = 50;
    private static final int yPadding = 40;
    private static final float xScaling = 0.7f;
    private static final float yScaling = 0.95f;
    public static boolean gameOver = false;
    public static int[] clicks = {0, 0, 0, 0};
    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 720;
    public static final int CIRCLE_SIZE = 40;
    ArrayList<Integer> moves;
    JButton b_easyButton = new JButton("Easy");
    JButton b_mediumButton = new JButton("Medium");
    JButton b_hardButton = new JButton("Hard");


    GUI()
    {
        this.setResizable(false);
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.getContentPane().setBackground(Color.DARK_GRAY);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setVisible(true);
        clickCounter = 0;
        b_easyButton.setBounds(580, 10, 100, 40);
        b_easyButton.setFont(new Font("Arial", Font.PLAIN, 15));
        b_easyButton.addActionListener(this);
        this.add(b_easyButton);

        b_mediumButton.setBounds(730, 10, 100, 40);
        b_mediumButton.addActionListener(this);
        this.add(b_mediumButton);

        b_hardButton.setBounds(880, 10, 100, 40);
        b_hardButton.addActionListener(this);
        b_hardButton.setFont(new Font("Arial", Font.PLAIN, 15));
        this.add(b_hardButton);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent mouseEvent)
            {
                if (gameOver) return;

                Point lastClick = new Point(mouseEvent.getX(), mouseEvent.getY());
                Point actualPoint = new Point(invMap(lastClick));
                if (Utilities.find(StateManager.curr.redMarbles, Graph.map(actualPoint.x, actualPoint.y)))
                {

                    clicks[0] = actualPoint.x;
                    clicks[1] = actualPoint.y;
                    clickCounter = 1;

                    ArrayList<StateManager.Move> allmoves = GameManager.getPlayerMove();
                    moves = new ArrayList<>();

                    for (StateManager.Move m : allmoves)
                    {
                        if (m.x1 == clicks[0] && m.y1 == clicks[1]) moves.add(Graph.map(m.x2, m.y2));
                    }

                    reDraw();
                } else if (find(moves, Graph.map(actualPoint.x, actualPoint.y)))
                {

                    clicks[2] = actualPoint.x;
                    clicks[3] = actualPoint.y;
                    clickCounter = 0;

                    StateManager.curr.doMove(clicks[0], clicks[1], clicks[2], clicks[3]);
                    moves = new ArrayList<>();
                    reDraw();
                    // AI plays
                    showWinText(GameManager.checkwinning());
                    GameManager.AIPlay();
                    reDraw();
                    showWinText(GameManager.checkwinning());
                }
            }
        });
    }

    public void showWinText(int status)
    {
        if (status == -1)
        {
            gameOver = true;
            JLabel l_uWin = new JLabel("You Win");
            l_uWin.setFont(new Font("Arial", Font.PLAIN, 24));
            l_uWin.setForeground(Color.PINK);
            l_uWin.setBounds(20, 20, 100, 30);
            this.add(l_uWin);
        } else if (status == 1)
        {
            gameOver = true;
            JLabel l_aiWins = new JLabel("AI Wins");
            l_aiWins.setFont(new Font("Arial", Font.PLAIN, 24));
            l_aiWins.setForeground(Color.CYAN);
            l_aiWins.setBounds(20, 20, 100, 30);
            this.add(l_aiWins);
        }
    }

    public void reDraw()
    {
        this.repaint();
    }

    public void paint(Graphics g)
    {
        super.paint(g);
    /*
        //Gradient background
        Graphics2D graphics2D = (Graphics2D) g;
        Color color1 = Color.BLACK;
        Color color2 = Color.DARK_GRAY;
        GradientPaint gp = new GradientPaint(0, SCREEN_HEIGHT, color1, SCREEN_WIDTH, 0, color2);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setPaint(gp);
        graphics2D.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
     */

        //Draw board
        for (Graph.Node node : Graph.nodes)
        {
            g.setColor(Color.WHITE);
            if (node == null) continue;
            Point position = new Point(map(Graph.getCol(node.num), Graph.getRow(node.num)));
            g.fillOval(position.x, position.y, CIRCLE_SIZE, CIRCLE_SIZE);
        }

        g.setColor(Color.RED);
        for (int i = 0; i < 10; i++)
        {
            Point position = new Point(map(Graph.getCol(StateManager.curr.redMarbles[i]), Graph.getRow(StateManager.curr.redMarbles[i])));
            g.fillOval(position.x, position.y, CIRCLE_SIZE, CIRCLE_SIZE);
        }

        g.setColor(Color.BLUE);
        for (int i = 0; i < 10; i++)
        {
            Point position = new Point(map(Graph.getCol(StateManager.curr.blueMarbles[i]), Graph.getRow(StateManager.curr.blueMarbles[i])));
            g.fillOval(position.x, position.y, CIRCLE_SIZE, CIRCLE_SIZE);
        }

        if (moves == null)
            return;

        g.setColor(Color.YELLOW);
        for (Integer m : moves)
        {
            Point position = new Point(map(Graph.getCol(m), Graph.getRow(m)));
            g.fillOval(position.x, position.y, CIRCLE_SIZE, CIRCLE_SIZE);
        }

    }

    boolean find(ArrayList<Integer> moves, int num)
    {
        if (moves == null)
            return false;
        for (Integer m : moves)
        {
            if (m == num)
                return true;
        }
        return false;
    }

    public Point invMap(Point point)
    {
        int actualHeight = SCREEN_HEIGHT - yPadding;
        int newX = (int) ((point.x - xOffset) / ((SCREEN_WIDTH * xScaling) / 25));
        int newY = (int) (((point.y - yOffset) / yScaling) * (17.0 / actualHeight));
        return new Point(newY, newX);
    }


    public Point map(int x, int y)
    {
        int actualHeight = SCREEN_HEIGHT - yPadding;
        int newX = (int) ((((SCREEN_WIDTH * xScaling / 25) * x))) + xOffset;
        int newY = (int) (((actualHeight / 17) * y) * (yScaling) + yOffset);
        return new Point(newX, newY);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == b_easyButton)
        {
            GameManager.depth = 1;
            System.out.println("Changed difficulty to easy");
        } else if (e.getSource() == b_mediumButton)
        {
            GameManager.depth = 3;
            System.out.println("Changed difficulty to medium");
        } else if (e.getSource() == b_hardButton)
        {
            GameManager.depth = 5;
            System.out.println("Changed difficulty to hard");
        }
    }
}
