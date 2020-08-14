import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;


public class PongPanel extends JPanel implements ActionListener, KeyListener
{
	private final static Color BACKGROUND_COLOUR = Color.WHITE;
	private final static int TIMER_DELAY = 5;
	private final static int BALL_MOVEMENT_SPEED = 2;
	private final static int PADDLE_SPEED = 2;
	private final static int POINTS_TO_WIN = 11;
	private final static int SCORE_TEXT_X =100;
	private final static int SCORE_TEXT_Y =100;
	private final int SCORE_FONT_SIZE = 50;
	private final static String SCORE_FONT_FAMILY = "OCR A Extended";
	int player1Score = 0, player2Score = 0;
	Player gameWinner;
	GameState gameState = GameState.Initialising;
	Ball ball;
	Paddle paddle1, paddle2;
	
	public PongPanel()					// SET UP GAME PANEL
	{
		setBackground(BACKGROUND_COLOUR);
		
		Timer timer = new Timer(TIMER_DELAY, this);
			timer.start();
		
			addKeyListener(this);
			setFocusable(true);
	}

	private void update()				// GAME RUNTIME
	{
		switch(gameState)
		{
			case Initialising :
			{
				createObjects();
				gameState = GameState.Playing;
				ball.setXVelocity(BALL_MOVEMENT_SPEED);
				ball.setYVelocity(BALL_MOVEMENT_SPEED);				
				break;
			}
			case Playing :
			{
				moveObject(paddle1);		// left paddle
				moveObject(paddle2);		// right paddle
				moveObject(ball);
				checkWallBounce();
				checkPaddleBounce();
				checkWin();
				break;
			}
			case GameOver :
			{
				break;
			}
		}
	}
	
	private void createObjects()
	{
		ball = new Ball(getWidth(), getHeight());
		paddle1 = new Paddle(Player.One, getWidth(), getHeight());
		paddle2 = new Paddle(Player.Two, getWidth(), getHeight());
	}
	
	private void moveObject(Sprite object)
	{
		object.setXPosition(object.getXPosition() + object.getXVelocity());
		object.setYPosition(object.getYPosition() + object.getYVelocity(), getHeight());
	}
	
	private void checkWallBounce()
	{
		// hit left side of screen
		if(ball.getXPosition() <= 0)
		{
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.Two);
			resetBall();
		}
		
		// hit right side of screen
		if(ball.getXPosition() >= getWidth() - ball.getWidth())
		{
			ball.setXVelocity(-ball.getXVelocity());
			addScore(Player.One);
			resetBall();
		}
		
		// hit top or bottom of screen
		if(ball.getYPosition() <= 0 || ball.getYPosition() >= getHeight() - ball.getHeight())
			ball.setYVelocity(-ball.getYVelocity());
	}
	
	private void addScore(Player player)
	{
		if(player == Player.Two)
			player2Score++;
		if(player == Player.One)
			player1Score++;
	}
	
	private void checkWin()
	{
		if(player1Score >= POINTS_TO_WIN)
		{
			gameWinner = Player.One;
			gameState= GameState.GameOver;
		}
		if(player2Score >= POINTS_TO_WIN)
		{
			gameWinner = Player.Two;
			gameState= GameState.GameOver;
		}
	}
	
	private void resetBall()
	{
		ball.resetToInitialPosition();
	}
	
	private void checkPaddleBounce()
	{
		if(ball.getXVelocity() < 0 && ball.getRectangle().intersects(paddle1.getRectangle()))
			ball.setXVelocity(BALL_MOVEMENT_SPEED);
		if(ball.getXVelocity() > 0 && ball.getRectangle().intersects(paddle2.getRectangle()))
			ball.setXVelocity(-BALL_MOVEMENT_SPEED);
	}
	
	private void paintDottedLine(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g.create();
		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {9}, 0);
		g2d.setStroke(dashed);
		g2d.setPaint(Color.WHITE);
		g2d.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
		g2d.dispose();
	}
	
	private void paintSprite(Graphics g, Sprite sprite)
	{
		g.setColor(sprite.getColour());
		g.fillRect(sprite.getXPosition(), sprite.getYPosition(), sprite.getWidth(), sprite.getHeight());
	}
	
	private void paintScores(Graphics g)
	{
		Font scoreFont = new Font(SCORE_FONT_FAMILY, Font.BOLD, SCORE_FONT_SIZE);
		String leftScore = Integer.toString(player1Score);
		String rightScore = Integer.toString(player2Score);
		g.setFont(scoreFont);
		g.drawString(leftScore, SCORE_TEXT_X, SCORE_TEXT_Y);
		g.drawString(rightScore, getWidth() - SCORE_TEXT_X, SCORE_TEXT_Y);
	}
	
	private void paintWin(Graphics g)
	{
		Font winFont = new Font(SCORE_FONT_FAMILY, Font.BOLD, SCORE_FONT_SIZE);
		String winString = "WIN!";
		g.setFont(winFont);
		if(player1Score > player2Score)
			g.drawString(winString, SCORE_TEXT_X, SCORE_TEXT_Y * 2);
		else
			g.drawString(winString, getWidth() - SCORE_TEXT_X * 2, SCORE_TEXT_Y * 2);
	
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		update();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		paintDottedLine(g);
		
		if(gameState != GameState.Initialising)
		{
			paintSprite(g, ball);
			paintSprite(g, paddle1);
			paintSprite(g, paddle2);
			paintScores(g);
		}
		if(gameState == GameState.GameOver)
		{
			paintWin(g);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent event)
	{
		
	}
	
	@Override
	public void keyPressed(KeyEvent event)
	{
		// Player 1 Controls
		if(event.getKeyCode() == KeyEvent.VK_W)
			paddle1.setYVelocity(-PADDLE_SPEED);
		else if(event.getKeyCode() == KeyEvent.VK_S)
			paddle1.setYVelocity(PADDLE_SPEED);
		
		// Player 2 Controls
		if(event.getKeyCode() == KeyEvent.VK_UP)
			paddle2.setYVelocity(-PADDLE_SPEED);
		else if(event.getKeyCode() == KeyEvent.VK_DOWN)
			paddle2.setYVelocity(PADDLE_SPEED);
	}
	
	@Override
	public void keyReleased(KeyEvent event)
	{
		// Player 1 Controls
		if(event.getKeyCode() == KeyEvent.VK_W|| event.getKeyCode() == KeyEvent.VK_S)
			paddle1.setYVelocity(0);
		
		// Player 2 Controls
		if(event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN)
			paddle2.setYVelocity(0);
	}
}
