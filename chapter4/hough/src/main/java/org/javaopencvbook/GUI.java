package org.javaopencvbook;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class GUI {
	private static final String houghString = "Hough Lines";
	private static final String circularHoughString = "Circular Hough Lines";
	private static final String pHoughString = "Probabilistic Hough Lines";
	private static final String noneString = "None";


	private String operation = noneString;

	private JLabel imageView;
	private String windowName;
	private Mat image, originalImage;

	private final ImageProcessor imageProcessor = new ImageProcessor();

	private int aperture = 3;
	private int xOrder = 1;
	private int yOrder = 1;
	protected int lowThreshold = 230;
	protected int highThreshold = 240;
	private JSlider apertureSlider;
	private JSlider xorderSlider;
	private JSlider yOrderSlider;
	private JSlider lowThresholdSlider;
	private JSlider highThresholdSlider;
	private JLabel highThresholdLabel;
	private JLabel apertureSliderLabel;
	private JLabel xOrderSliderLabel;
	private JLabel yOrderSliderLabel;
	private JLabel lowThresholdSliderLabel;



	public GUI(String windowName, Mat newImage) {
		super();
		this.windowName = windowName;
		this.image = newImage;
		this.originalImage = newImage.clone();
	}

	public void init() {
		setSystemLookAndFeel();
		initGUI();
	}

	private void initGUI() {
		JFrame frame = createJFrame(windowName);

		updateView(image);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}




	private JFrame createJFrame(String windowName) {
		JFrame frame = new JFrame(windowName);
		frame.setLayout(new GridBagLayout());

		setupTypeRadio(frame);
		setupApertureSlider(frame);
		setupXOrderSlider(frame);
		setupYOrderSlider(frame);
		setupLowThresholdSlider(frame);
		setupHighThresholdSlider(frame);

		setupImage(frame);

		enableDisableSliders();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}

	private void setupHighThresholdSlider(JFrame frame) {
		highThresholdLabel = new JLabel("High threshold:", JLabel.CENTER);
		highThresholdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		int minimum = 0;
		int maximum = 255;
		int initial = 50;

		highThresholdSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		highThresholdSlider.setMajorTickSpacing(20);
		highThresholdSlider.setMinorTickSpacing(2);
		highThresholdSlider.setPaintTicks(true);
		highThresholdSlider.setPaintLabels(true);
		highThresholdSlider.setSnapToTicks(true);
		highThresholdSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();

				highThreshold  = (int)source.getValue();

				processOperation();

				updateView(image);

			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;

		frame.add(highThresholdLabel,c);
		c.gridx = 1;
		c.gridy = 5;
		frame.add(highThresholdSlider,c);


	}

	private void setupTypeRadio(JFrame frame) {
		JRadioButton noneButton = new JRadioButton(noneString);
		noneButton.setMnemonic(KeyEvent.VK_N);
		noneButton.setActionCommand(noneString);
		noneButton.setSelected(true);


		JRadioButton binaryButton = new JRadioButton(circularHoughString);
		binaryButton.setMnemonic(KeyEvent.VK_S);
		binaryButton.setActionCommand(circularHoughString);

		JRadioButton binaryInvButton = new JRadioButton(houghString);
		binaryInvButton.setMnemonic(KeyEvent.VK_L);
		binaryInvButton.setActionCommand(houghString);

		JRadioButton truncateButton = new JRadioButton(pHoughString);
		truncateButton.setMnemonic(KeyEvent.VK_C);
		truncateButton.setActionCommand(pHoughString);



		ButtonGroup group = new ButtonGroup();
		group.add(noneButton);
		group.add(binaryButton);
		group.add(binaryInvButton);
		group.add(truncateButton);

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				operation = event.getActionCommand();
				enableDisableSliders();
				processOperation();
			}
		};

		noneButton.addActionListener(operationChangeListener);
		binaryButton.addActionListener(operationChangeListener);
		binaryInvButton.addActionListener(operationChangeListener);
		truncateButton.addActionListener(operationChangeListener);			

		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel radioOperationPanel = new JPanel(gridRowLayout);

		JLabel modeLabel = new JLabel("Mode:", JLabel.RIGHT);

		radioOperationPanel.add(noneButton);
		radioOperationPanel.add(binaryButton);
		radioOperationPanel.add(binaryInvButton);
		radioOperationPanel.add(truncateButton);



		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;

		frame.add(modeLabel,c);
		c.gridx = 1;
		c.gridy = 0;
		frame.add(radioOperationPanel,c);

	}


	protected void enableDisableSliders() {
		apertureSlider.setMinimum(1);
		apertureSlider.setMaximum(15);

		if(noneString.equals(operation)){
			apertureSliderLabel.setEnabled(false);
			apertureSlider   .setEnabled(false);
			xOrderSliderLabel.setEnabled(false);
			xorderSlider     .setEnabled(false);
			yOrderSliderLabel.setEnabled(false);
			yOrderSlider   .setEnabled(false);
			lowThresholdSliderLabel.setEnabled(false);
			lowThresholdSlider.setEnabled(false);
			highThresholdLabel.setEnabled(false);
			highThresholdSlider.setEnabled(false);
		}
		else if(circularHoughString.equals(operation)){
			apertureSliderLabel.setEnabled(true);
			apertureSlider   .setEnabled(true);
			xOrderSliderLabel.setEnabled(true);
			xorderSlider     .setEnabled(true);
			yOrderSliderLabel.setEnabled(true);
			yOrderSlider   .setEnabled(true);
			lowThresholdSliderLabel.setEnabled(true);
			lowThresholdSlider.setEnabled(true);
			highThresholdLabel.setEnabled(false);
			highThresholdSlider.setEnabled(false);

		}

		else if(houghString.equals(operation)){
			apertureSliderLabel.setEnabled(true);
			apertureSlider   .setEnabled(true);
			xOrderSliderLabel.setEnabled(false);
			xorderSlider     .setEnabled(false);
			yOrderSliderLabel.setEnabled(false);
			yOrderSlider   .setEnabled(false);
			lowThresholdSliderLabel.setEnabled(true);
			lowThresholdSlider.setEnabled(true);
			highThresholdLabel.setEnabled(false);
			highThresholdSlider.setEnabled(false);
		}
		else if(pHoughString.equals(operation)){
			if(aperture<3){
				aperture = 3;
			}
			else if(aperture>7){
				aperture = 7;
			}
			
			apertureSlider.setValue(aperture);
			apertureSliderLabel.setEnabled(true);
			apertureSlider   .setEnabled(true);

			apertureSlider.setMinimum(3);
			apertureSlider.setMaximum(7);



			xOrderSliderLabel.setEnabled(false);
			xorderSlider     .setEnabled(false);
			yOrderSliderLabel.setEnabled(false);
			yOrderSlider   .setEnabled(false);
			lowThresholdSliderLabel.setEnabled(true);
			lowThresholdSlider.setEnabled(true);
			highThresholdLabel.setEnabled(true);
			highThresholdSlider.setEnabled(true);
		}
	}


	private void setupApertureSlider(JFrame frame) {
		apertureSliderLabel = new JLabel("Aperture size:", JLabel.CENTER);
		apertureSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		int minimum = 1;
		int maximum = 15;
		int initial =3;

		apertureSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		apertureSlider.setSnapToTicks(true);
		apertureSlider.setMinorTickSpacing(2);

		apertureSlider.setMajorTickSpacing(2);

		apertureSlider.setPaintTicks(true);
		apertureSlider.setPaintLabels(true);
		apertureSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if(apertureSlider.getValueIsAdjusting())
					return;

				JSlider source = (JSlider)e.getSource();
				aperture = (int)source.getValue();


				processOperation();

				updateView(image);

			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;

		frame.add(apertureSliderLabel,c);
		c.gridx = 1;
		c.gridy = 1;
		frame.add(apertureSlider,c);
	}


	private void setupXOrderSlider(JFrame frame) {
		xOrderSliderLabel = new JLabel("Sobel X Order:", JLabel.CENTER);
		xOrderSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		int minimum = 0;
		int maximum = 2;
		int initial =1;

		xorderSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		xorderSlider.setMajorTickSpacing(1);
		xorderSlider.setMinorTickSpacing(1);
		xorderSlider.setPaintTicks(true);
		xorderSlider.setPaintLabels(true);
		xorderSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				xOrder = (int)source.getValue();
				processOperation();

				updateView(image);

			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;

		frame.add(xOrderSliderLabel,c);
		c.gridx = 1;
		c.gridy = 2;
		frame.add(xorderSlider,c);

	}

	private void setupYOrderSlider(JFrame frame) {
		yOrderSliderLabel = new JLabel("Sobel Y order:", JLabel.CENTER);
		yOrderSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		int minimum = 0;
		int maximum = 2;
		int initial =1;

		yOrderSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		yOrderSlider.setMajorTickSpacing(1);
		yOrderSlider.setMinorTickSpacing(1);
		yOrderSlider.setPaintTicks(true);
		yOrderSlider.setPaintLabels(true);
		yOrderSlider.setSnapToTicks(true);
		yOrderSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				yOrder = (int)source.getValue();
				processOperation();
				updateView(image);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;

		frame.add(yOrderSliderLabel,c);
		c.gridx = 1;
		c.gridy = 3;
		frame.add(yOrderSlider,c);

	}

	private void setupLowThresholdSlider(JFrame frame) {
		lowThresholdSliderLabel = new JLabel("Low threshold:", JLabel.CENTER);
		lowThresholdSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		int minimum = 0;
		int maximum = 255;
		int initial = 240;

		lowThresholdSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		lowThresholdSlider.setMajorTickSpacing(20);
		lowThresholdSlider.setMinorTickSpacing(2);
		lowThresholdSlider.setPaintTicks(true);
		lowThresholdSlider.setPaintLabels(true);
		lowThresholdSlider.setSnapToTicks(true);
		lowThresholdSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();

				lowThreshold  = (int)source.getValue();

				processOperation();

				updateView(image);

			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;

		frame.add(lowThresholdSliderLabel,c);
		c.gridx = 1;
		c.gridy = 4;
		frame.add(lowThresholdSlider,c);

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

		JLabel originalImageLabel = new JLabel();
		Image originalAWTImage = imageProcessor.toBufferedImage(originalImage);
		originalImageLabel.setIcon(new ImageIcon(originalAWTImage));

		imagesPanel.add(originalImageLabel);
		imagesPanel.add(imageView);

		frame.add(imagesPanel,c);
	}






	protected void processOperation() {

		if(circularHoughString.equals(operation)){
			Mat circles = new Mat();
			Mat canny = new Mat();
			//Imgproc.Canny(originalImage, canny,10 , 50, aperture, false);
			Imgproc.cvtColor( originalImage, canny, Imgproc.COLOR_BGR2GRAY);
			Imgproc.blur(canny, canny, new Size(3,3));
			
			image = originalImage.clone();
			
			//Imgproc.HoughCircles(canny, circles,Imgproc.CV_HOUGH_GRADIENT, 1, lowThreshold);
			Imgproc.HoughCircles(canny, circles,Imgproc.CV_HOUGH_GRADIENT, 1, canny.rows()/8, 200, lowThreshold, 0, 0 );
			System.out.println(circles.size());
			for( int i=0;i<circles.cols();i++){
				Point center = new Point( circles.get(0,i)[0], circles.get(0, i)[1]);
				int radius = (int) Math.round(circles.get(0, i)[2]);
				
				Imgproc.circle( image, center, radius, new Scalar(0,255,0),3);//radius, color)
			}
			
			
		}
		else if(houghString.equals(operation)){
			Mat canny = new Mat();
			Imgproc.Canny(originalImage, canny,10 , 50, aperture, false);
			image = originalImage.clone();
			Mat lines = new Mat();
			
			Imgproc.HoughLines(canny, lines, 1, Math.PI/180, lowThreshold);
			
			for( int i = 0; i < lines.cols(); i++ )
			{
			  double rho = lines.get(0, i)[0];
			  double theta = lines.get(0, i)[1];
			  Point pt1 = new Point(), pt2= new Point();
			  double a = Math.cos(theta), b = Math.sin(theta);
			  double x0 = a*rho, y0 = b*rho;
			  pt1.x = Math.round(x0 + 1000*(-b));
			  pt1.y = Math.round(y0 + 1000*(a));
			  pt2.x = Math.round(x0 - 1000*(-b));
			  pt2.y = Math.round(y0 - 1000*(a));
			  
			  Imgproc.line( image, pt1, pt2, new Scalar(255,0,0), 2, Core.LINE_AA,0);
			}
			
		}
		else if(pHoughString.equals(operation)){
			Mat canny = new Mat();
			Imgproc.Canny(originalImage, canny,10 , 50, aperture, false);
			//canny = originalImage.clone();
			image = originalImage.clone();
			
			//Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2BGR);
		
		Mat lines = new Mat();

		Imgproc.HoughLinesP(canny, lines, 1, Math.PI/180,  lowThreshold, 50, 5 );
		for( int i = 0; i < lines.cols(); i++ )
		{
			double a = lines.get(0, i)[0];
			double b = lines.get(0, i)[1];
			double c = lines.get(0, i)[2];
			double d = lines.get(0, i)[3];
			
			Imgproc.line( image, new Point(a, b), new Point(c, d), new Scalar(0,0,255), 1, Core.LINE_AA,0);
			
		}
		
		
//			lines = cvHoughLines2( Edges,
//			        storage,
//			        CV_HOUGH_PROBABILISTIC,
//			        1,
//			        Math.PI/180,           
//			        44,// threshold
//			        2,
//			        1 );
			
			
		}
		else if(noneString.equals(operation)){
			image = originalImage.clone();
		}

		updateView(image);
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

	private void updateView(Mat newMat) {
		Image outputImage = imageProcessor.toBufferedImage(newMat);
		imageView.setIcon(new ImageIcon(outputImage));
	}

}
