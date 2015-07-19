package org.javaopencvbook;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class GUI {
	private static final String firstString = 	"First";	
	private static final String secondString = 	"Second";
	private static final String thirdString = 	"Third";
	private static final String fourthString = 	"Fourth";
	
	private String operation = firstString;

	private JLabel imageView;
	private JLabel originalImageLabel; 
	private String windowName;
	private Mat image, originalImage,originalImageAnnotated;

	private final ImageProcessor imageProcessor = new ImageProcessor();

	private int mouseX[] = {0,200,0  ,200};
	private int mouseY[] = {0,50,200,200};



	public GUI(String windowName, Mat newImage) {
		super();
		this.windowName = windowName;
		this.image = newImage;
		this.originalImage = newImage.clone();
		this.originalImageAnnotated = newImage.clone();
	}

	public void init() {
		setSystemLookAndFeel();
		initGUI();
	}

	private void initGUI() {
		JFrame frame = createJFrame(windowName);

		updateView();

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}




	private JFrame createJFrame(String windowName) {
		JFrame frame = new JFrame(windowName);
		frame.setLayout(new GridBagLayout());

		setupTypeRadio(frame);

		setupImage(frame);


		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}


	private void setupTypeRadio(JFrame frame) {
		JRadioButton firstButton = new JRadioButton(firstString);
		firstButton.setMnemonic(KeyEvent.VK_F);
		firstButton.setActionCommand(firstString);
		firstButton.setSelected(true);


		JRadioButton secondButton = new JRadioButton(secondString);
		secondButton.setMnemonic(KeyEvent.VK_S);
		secondButton.setActionCommand(secondString);

		JRadioButton thirdButton = new JRadioButton(thirdString);
		thirdButton.setMnemonic(KeyEvent.VK_T);
		thirdButton.setActionCommand(thirdString);

		JRadioButton fourthButton = new JRadioButton(fourthString);
		fourthButton.setMnemonic(KeyEvent.VK_O);
		fourthButton.setActionCommand(fourthString);

		ButtonGroup group = new ButtonGroup();
		group.add(firstButton);
		group.add(secondButton);
		group.add(thirdButton);
		group.add(fourthButton);

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				operation = event.getActionCommand();
				processOperation();
			}
		};

		firstButton.addActionListener(operationChangeListener);
		secondButton.addActionListener(operationChangeListener);
		thirdButton.addActionListener(operationChangeListener);
		fourthButton.addActionListener(operationChangeListener);			

		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel radioOperationPanel = new JPanel(gridRowLayout);

		JLabel pointLabel = new JLabel("Point:", JLabel.RIGHT);

		radioOperationPanel.add(firstButton);
		radioOperationPanel.add(secondButton);
		radioOperationPanel.add(thirdButton);
		radioOperationPanel.add(fourthButton);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;

		frame.add(pointLabel,c);
		c.gridx = 1;
		c.gridy = 0;
		frame.add(radioOperationPanel,c);

	}


	private void updateWarpCoordinates(MouseEvent event) {
		if(operation.equals(firstString)){
			mouseX[0] = event.getX();
			mouseY[0] = event.getY();
		}
		else if(operation.equals(secondString)){
			mouseX[1] = event.getX();
			mouseY[1] = event.getY();
		}
		else if(operation.equals(thirdString)){
			mouseX[2] = event.getX();
			mouseY[2] = event.getY();
		}
		else if(operation.equals(fourthString)){
			mouseX[3] = event.getX();
			mouseY[3] = event.getY();
		}
	}


	private void setupImage(JFrame frame) {
		JPanel imagesPanel = new JPanel();
		imageView = new JLabel();
		imageView.setHorizontalAlignment(SwingConstants.CENTER);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;

		originalImageLabel = new JLabel();
		Image originalAWTImage = imageProcessor.toBufferedImage(originalImage);
		originalImageLabel.setIcon(new ImageIcon(originalAWTImage));
		
		originalImageLabel.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent arg0) {
				
			}
			
			public void mouseDragged(MouseEvent arg0) {
				updateWarpCoordinates(arg0);
				processOperation();
			}
			
		});
		originalImageLabel.addMouseListener( new MouseListener() {
			
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
			public void mousePressed(MouseEvent event) {
				updateWarpCoordinates(event);
				processOperation();				
			}
			
			public void mouseExited(MouseEvent arg0) {
				
			}
			
			public void mouseEntered(MouseEvent arg0) {
			}
			
			public void mouseClicked(MouseEvent event) {
			}
			
		});
		

		imagesPanel.add(originalImageLabel);
		imagesPanel.add(imageView);

		frame.add(imagesPanel,c);
		processOperation();
	}



	protected void processOperation() {
		Point[] srcPoints = new Point[4];
		for(int i=0;i<4;i++){
			srcPoints[i] =new Point(mouseX[i], mouseY[i]);
		}

		Point[] dstPoints = new Point[4];

		dstPoints[0] =  new Point(0, 0);
		dstPoints[1] =  new Point(originalImage.width()-1,0);
		dstPoints[2] =  new Point(0,originalImage.height()-1);
		dstPoints[3] =  new Point(originalImage.width()-1,originalImage.height()-1);

		MatOfPoint2f srcTri = new MatOfPoint2f(
				srcPoints[0],
				srcPoints[1],
				srcPoints[2],
				srcPoints[3]
				);

		MatOfPoint2f dstTri = new MatOfPoint2f(
				dstPoints[0],
				dstPoints[1],
				dstPoints[2],
				dstPoints[3]		
				);


		Mat warpMat = Imgproc.getPerspectiveTransform(srcTri,dstTri);
		Mat destImage = new Mat();

		Imgproc.warpPerspective(originalImage, destImage, warpMat, originalImage.size());

		image = destImage.clone();

		originalImageAnnotated = originalImage.clone();


		Imgproc.line(originalImageAnnotated,srcPoints[0], srcPoints[1], new Scalar( 255.0,0,0), 2);
		Imgproc.line(originalImageAnnotated,srcPoints[1], srcPoints[3], new Scalar( 255.0,0,0), 2);
		Imgproc.line(originalImageAnnotated,srcPoints[3], srcPoints[2], new Scalar( 255.0,0,0), 2);
		Imgproc.line(originalImageAnnotated,srcPoints[2], srcPoints[0], new Scalar( 255.0,0,0), 2);

		for(int i=0;i<4;i++){
			Imgproc.circle(originalImageAnnotated, srcPoints[i], 4, new Scalar (255.0,0,0),-1);
		}


		updateView();
	}

	private void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private void updateView() {
		Mat newMat = image;
		Image outputImage = imageProcessor.toBufferedImage(newMat);
		imageView.setIcon(new ImageIcon(outputImage));
		
		Mat originalAnnotatedMat = originalImageAnnotated;
		Image originalAnnotated = imageProcessor.toBufferedImage(originalAnnotatedMat);
		originalImageLabel.setIcon(new ImageIcon(originalAnnotated));
		
	}

}
