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
import org.opencv.core.Rect;
import org.opencv.core.Size;
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
		Mat gray = new Mat();
		Imgproc.cvtColor(originalImage, gray, Imgproc.COLOR_RGB2GRAY);
		Mat floatGray = new Mat();
		gray.convertTo(floatGray, CvType.CV_32FC1);
		
		
		List<Mat> matList = new ArrayList<Mat>();
		matList.add(floatGray);
		Mat zeroMat = Mat.zeros(floatGray.size(), CvType.CV_32F);
		matList.add(zeroMat);
		Mat complexImage = new Mat();
		Core.merge(matList, complexImage);
		
		Core.dft(complexImage,complexImage);
		
		List<Mat> splitted = new ArrayList<Mat>();
		Core.split(complexImage,splitted);
		Mat magnitude = new Mat();
		Core.magnitude(splitted.get(0),splitted.get(1), magnitude);
		Core.add(Mat.ones(magnitude.size(), CvType.CV_32F), magnitude, magnitude);
	
		Core.log(magnitude, magnitude);
		
		
		
		int cx = magnitude.cols()/2;
		int cy = magnitude.rows()/2;

		Mat q0 = new Mat(magnitude,new Rect(0, 0, cx, cy));   // Top-Left - Create a ROI per quadrant
		Mat q1 = new Mat(magnitude,new Rect(cx, 0, cx, cy));  // Top-Right
		Mat q2 = new Mat(magnitude,new Rect(0, cy, cx, cy));  // Bottom-Left
		Mat q3 = new Mat(magnitude ,new Rect(cx, cy, cx, cy)); // Bottom-Right

		Mat tmp = new Mat();                           // swap quadrants (Top-Left with Bottom-Right)
		q0.copyTo(tmp);
		q3.copyTo(q0);
		tmp.copyTo(q3);

		q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
		q2.copyTo(q1);
		tmp.copyTo(q2);
		
		magnitude.convertTo(magnitude, CvType.CV_8UC1);
		Core.normalize(magnitude, magnitude,0,255, Core.NORM_MINMAX, CvType.CV_8UC1);

		image = magnitude.clone();//destImage.clone();

		originalImageAnnotated = originalImage.clone();


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
