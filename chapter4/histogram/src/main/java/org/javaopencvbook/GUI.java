package org.javaopencvbook;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class GUI {
	private JLabel imageView;
	private JLabel originalImageLabel; 
	private String windowName;
	private Mat image, originalImage,grayImage;

	private final ImageProcessor imageProcessor = new ImageProcessor();



	public GUI(String windowName, Mat newImage) {
		super();
		this.windowName = windowName;
		this.image = newImage;
		this.originalImage = newImage.clone();
		this.grayImage = newImage.clone();
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


		setupImage(frame);


		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
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
		
		imagesPanel.add(originalImageLabel);
		imagesPanel.add(imageView);

		frame.add(imagesPanel,c);
		processOperation();
	}



	protected void processOperation() {
		Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_RGB2GRAY);
		Imgproc.equalizeHist(grayImage, image);

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
		
		Mat originalAnnotatedMat = grayImage;
		Image originalAnnotated = imageProcessor.toBufferedImage(originalAnnotatedMat);
		originalImageLabel.setIcon(new ImageIcon(originalAnnotated));
		
	}

}
