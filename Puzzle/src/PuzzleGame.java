import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class PuzzleGame extends JFrame {

	/**
	 * Automatically generated default value
	 */
	private static final long serialVersionUID = 1L;
	protected JPanel gamePanel;
	private BufferedImage sourceImage;
	private BufferedImage resizedImage;
	private Image image;
	private Button lastButton;
	private int width, height;

	protected List<Button> buttons = new ArrayList<>();
	protected List<Point> solution = new ArrayList<>();
	private final int NUM_BUTTONS = 12;
	private final int DESIRED_WIDTH = 300;

	public PuzzleGame() {
		initialUI();
	}

	private void initialUI() {

		solution.add(new Point(0, 0));
		solution.add(new Point(0, 1));
		solution.add(new Point(0, 2));
		solution.add(new Point(1, 0));
		solution.add(new Point(1, 1));
		solution.add(new Point(1, 2));
		solution.add(new Point(2, 0));
		solution.add(new Point(2, 1));
		solution.add(new Point(2, 2));
		solution.add(new Point(3, 0));
		solution.add(new Point(3, 1));
		solution.add(new Point(3, 2));

		gamePanel = new JPanel();
		gamePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		gamePanel.setLayout(new GridLayout(4, 3, 0, 0));

		try {
			sourceImage = loadImage();
			int resizedHeight = getNewHeight(sourceImage.getWidth(), sourceImage.getHeight());
			resizedImage = resizeImage(sourceImage, DESIRED_WIDTH, resizedHeight, BufferedImage.TYPE_INT_ARGB);
		} catch (IOException e) {
			Logger.getLogger(PuzzleGame.class.getName()).log(Level.SEVERE, null, e);
		}

		width = resizedImage.getWidth(null);
		height = resizedImage.getHeight(null);

		add(gamePanel, BorderLayout.CENTER);
		// loop through each panel assigning portion of image until last point to assign
		// last button
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				image = createImage(new FilteredImageSource(resizedImage.getSource(),
						new CropImageFilter(j * width / 3, i * height / 4, (width / 3), height / 4)));
				Button button = new Button(image);
				button.putClientProperty("position", new Point(i, j));

				if (i == 3 && j == 2) {
					lastButton = new Button();
					lastButton.setBorderPainted(false);
					lastButton.setContentAreaFilled(false);
					lastButton.setLastButton();
					lastButton.putClientProperty("position", new Point(i, j));
				} else {
					buttons.add(button);
				}
			}
		}
		// shuffling image, then adding last button
		Collections.shuffle(buttons);
		buttons.add(lastButton);

		for (int i = 0; i < NUM_BUTTONS; i++) {
			Button button = buttons.get(i);
			gamePanel.add(button);
			button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			button.addActionListener(new ClickAction());
		}

		pack();
		setTitle("Puzzle Game");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D graphics = resizedImage.createGraphics();
		graphics.drawImage(originalImage, 0, 0, width, height, null);
		graphics.dispose();
		return resizedImage;
	}

	private int getNewHeight(int width, int height) {
		double ratio = DESIRED_WIDTH / (double) width;
		int newHeight = (int) (height * ratio);
		return newHeight;
	}

	private BufferedImage loadImage() throws IOException {
		BufferedImage image = ImageIO.read(new File("src/ricky.jpg"));
		return image;
	}

	//Class to allow use of abstract action by extending
	private class ClickAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			checkButton(e);
			checkSolution();

		}

		// buttons from puzzle
		private void checkButton(ActionEvent e) {
			// TODO Auto-generated method stub
			int index = 0;

			for (Button buttonClass : buttons) {
				if (buttonClass.getLastButton() == true) {
					index = buttons.indexOf(buttonClass);
				}
			}

			JButton button = (JButton) e.getSource();
			int btnIndex;
			btnIndex = buttons.indexOf(button);
			if (((btnIndex - 1) == index) || ((btnIndex + 1) == index) || ((btnIndex - 3) == index)
					|| ((btnIndex + 3) == index)) {
				Collections.swap(buttons, btnIndex, index);
				updateButtons();
			}
		}

		private void updateButtons() {
			// TODO Auto-generated method stub

			gamePanel.removeAll();

			for (JComponent button : buttons) {
				gamePanel.add(button);
			}
			gamePanel.validate();
		}

		private void checkSolution() {
			// TODO Auto-generated method stub
			List<Point> current = new ArrayList<>();

			// buttons from puzzle
			for (JComponent button : buttons) {
				current.add((Point) button.getClientProperty("position"));
			}
			if (compareList(solution, current)) {
				// gamepanel from puzzle
				JOptionPane.showMessageDialog(gamePanel, "Finished", "Congragulations",
						JOptionPane.INFORMATION_MESSAGE);
			}

		}

		public boolean compareList(List<Point> solution, List<Point> current) {
			// TODO Auto-generated method stub
			return solution.toString().contentEquals(current.toString());
		}
	}
	public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            PuzzleGame puzzle = new PuzzleGame();
            puzzle.setVisible(true);
        });
    }
}

class Button extends JButton {

	/**
	 * Automatically generated default value
	 */
	private static final long serialVersionUID = 1L;
	private boolean isLastButton;

	public Button() {
		super();
		initialUI();
	}

	public Button(Image image) {
		super(new ImageIcon(image));
		initialUI();
	}

	private void initialUI() {
		isLastButton = false;
		BorderFactory.createLineBorder(Color.gray);
		// Setting Border to yellow when hovered over and back to gray after.
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.yellow));
			}

			public void mouseExited(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.gray));
			}
		});
	}

	public void setLastButton() {
		isLastButton = true;
	}

	public boolean getLastButton() {
		return isLastButton;
	}
}
