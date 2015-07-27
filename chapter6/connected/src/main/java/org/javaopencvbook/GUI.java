package org.javaopencvbook;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class GUI {

	private static final String onFillString = "On";
	private static final String offFillString = "Off";
	private static final String boundingBoxString = "Bounding box";
	private static final String circleString = "Circle";
	private static final String convexHullString = "Convex Hull";
	private JLabel imageView;
	private JLabel binaryView;
	private String windowName;
	private Mat originalImage;
	private Mat image;
	private Mat binary = new Mat();

	private final ImageProcessor imageProcessor = new ImageProcessor();
	
	private String fillFlag = onFillString;
	private String enclosingType = boundingBoxString;
	private int imageThreshold = 125;
	private int areaThreshold = 500;

	public GUI(String windowName, Mat newImage) {
		super();
		this.windowName = windowName;
		this.image = newImage;
		originalImage = newImage.clone();
		binary.create(new Size(image.cols(), image.rows()), CvType.CV_8UC1);
		binary.setTo(new Scalar(0));
	}

	public void init() {
		setSystemLookAndFeel();
		initGUI();
		resetImage();
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

		setupFillRadio(frame);
		setupShapeRadio(frame);
		
		setupLowerSlider(frame);
		setupUpperSlider(frame);
		
		setupResetButton(frame);
		setupImage(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	private void resetImage() {
		image = originalImage.clone();
		Mat temp = new Mat();
		Imgproc.cvtColor(image, temp,Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(temp, binary, imageThreshold, 255.0, Imgproc.THRESH_BINARY_INV);
		
		updateView();
	}

	private void setupResetButton(JFrame frame) {
		JButton resetButton = new JButton("Draw contours");
		resetButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				drawContours();
			}

			
		});
		resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		
		frame.add(resetButton,c);
	}

	protected void drawContours() {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat contourMat = binary.clone();
		
		int thickness = (fillFlag.equals(onFillString))?-1:2;
		
		Imgproc.findContours(contourMat, contours, new Mat(), Imgproc.CHAIN_APPROX_NONE,Imgproc.CHAIN_APPROX_SIMPLE);
		System.out.println("Number of found contours: "+contours.size());
		for(int i=0;i<contours.size();i++){
			MatOfPoint currentContour = contours.get(i);
			double currentArea = Imgproc.contourArea(currentContour);
			
			if(currentArea > areaThreshold){
				System.out.println("Contour points: " +contours.get(i).size().height);
				Imgproc.drawContours(image, contours, i, new Scalar(0,255,0), thickness);
				System.out.println("Area: "+currentArea);
				
				if(boundingBoxString.equals(enclosingType)){
					drawBoundingBox(currentContour);
				}
				else if (circleString.equals(enclosingType)){
					drawEnclosingCircle(currentContour);					
				}
				else if (convexHullString.equals(enclosingType)){
					drawConvexHull(currentContour);					
				}
				
			}
			else{
				Imgproc.drawContours(image, contours, i, new Scalar(0,0,255), thickness);	
			}
			
		}
		updateView();
	}

	private void drawBoundingBox(MatOfPoint currentContour) {
		Rect rectangle = Imgproc.boundingRect(currentContour);
		Imgproc.rectangle(image, rectangle.tl(), rectangle.br(), new Scalar(255,0,0),1);

	}

	private void drawEnclosingCircle(MatOfPoint currentContour) {
		float[] radius = new float[1];
		Point center = new Point();
		
		MatOfPoint2f currentContour2f = new MatOfPoint2f();
		currentContour.convertTo(currentContour2f, CvType.CV_32FC2);
		Imgproc.minEnclosingCircle(currentContour2f, center, radius);
		
		Imgproc.circle(image, center, (int) radius[0], new Scalar(255,0,0));
		
	}

	private void drawConvexHull(MatOfPoint currentContour) {
		MatOfInt hull = new MatOfInt();
		Imgproc.convexHull(currentContour, hull);	
		
		List<MatOfPoint> hullContours = new ArrayList<MatOfPoint>();
		MatOfPoint hullMat = new MatOfPoint();
		hullMat.create((int)hull.size().height,1,CvType.CV_32SC2);
		
		for(int j = 0; j < hull.size().height ; j++)
		{
		    int index = (int)hull.get(j, 0)[0];
		    double[] point = new double[] {
		        currentContour.get(index, 0)[0], currentContour.get(index, 0)[1]
		    };
		    hullMat.put(j, 0, point);
		} 
		hullContours.add(hullMat);
		Imgproc.drawContours(image, hullContours, 0, new Scalar(128,0,0), 2);
	}

	private void setupLowerSlider(JFrame frame) {
		
		JLabel sliderLabel = new JLabel("Image threshold:", JLabel.RIGHT);

		int minimum = 0;
		int maximum = 255;
		int initial =imageThreshold;

		JSlider thresholdSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		thresholdSlider.setMajorTickSpacing(25);
		thresholdSlider.setMinorTickSpacing(5);
		thresholdSlider.setPaintTicks(true);
		thresholdSlider.setPaintLabels(true);
		thresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				imageThreshold = (int)source.getValue();
				processOperation();	
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		frame.add(sliderLabel,c);
		c.gridx = 1;
		c.gridy = 2;
		frame.add(thresholdSlider,c);
	}
	
private void setupUpperSlider(JFrame frame) {
		
		JLabel sliderLabel = new JLabel("Area threshold:", JLabel.RIGHT);

		int minimum = 0;
		int maximum = 10000;
		int initial =500;

		JSlider areaSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);
		areaSlider.setMajorTickSpacing(1000);
		areaSlider.setMinorTickSpacing(250);
		areaSlider.setPaintTicks(true);
		areaSlider.setPaintLabels(true);
		areaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				areaThreshold = (int)source.getValue();
				processOperation();	
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 3;
		frame.add(sliderLabel,c);

		c.gridx = 1;
		c.gridy = 3;
		frame.add(areaSlider,c);
	}



	private void setupShapeRadio(JFrame frame) {
		JRadioButton boundingBoxButton = new JRadioButton(boundingBoxString);
		boundingBoxButton.setMnemonic(KeyEvent.VK_B);
		boundingBoxButton.setActionCommand(boundingBoxString);
		boundingBoxButton.setSelected(true);

		JRadioButton circleButton = new JRadioButton(circleString);
		circleButton.setMnemonic(KeyEvent.VK_C);
		circleButton.setActionCommand(circleString);
		circleButton.setSelected(false);
		
		
		JRadioButton convexHullButton = new JRadioButton(convexHullString);
		convexHullButton.setMnemonic(KeyEvent.VK_H);
		convexHullButton.setActionCommand(convexHullString);
		convexHullButton.setSelected(false);


		ButtonGroup group = new ButtonGroup();
		group.add(boundingBoxButton);
		group.add(circleButton);
		group.add(convexHullButton);

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				enclosingType  = event.getActionCommand();
				processOperation();	
			}
		};

		boundingBoxButton.addActionListener(operationChangeListener);
		circleButton.addActionListener(operationChangeListener);
		convexHullButton.addActionListener(operationChangeListener);

		
		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel radioOperationPanel = new JPanel(gridRowLayout);

		JLabel rangeLabel = new JLabel("Enclosing shape:", JLabel.RIGHT);

		radioOperationPanel.add(boundingBoxButton);
		radioOperationPanel.add(circleButton);
		radioOperationPanel.add(convexHullButton);


		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 1;

		frame.add(rangeLabel,c);

		c.gridx = 1;
		c.gridy = 1;
		frame.add(radioOperationPanel,c);
		
	}

	private void setupFillRadio(JFrame frame){
		
		JRadioButton onButton = new JRadioButton(onFillString);
		onButton.setMnemonic(KeyEvent.VK_O);
		onButton.setActionCommand(onFillString);
		onButton.setSelected(true);

		JRadioButton offButton = new JRadioButton(offFillString);
		offButton.setMnemonic(KeyEvent.VK_F);
		offButton.setActionCommand(offFillString);
		offButton.setSelected(false);


		ButtonGroup group = new ButtonGroup();
		group.add(onButton);
		group.add(offButton);

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				fillFlag = event.getActionCommand();
				processOperation();	
			}
		};

		onButton.addActionListener(operationChangeListener);
		offButton.addActionListener(operationChangeListener);

		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel radioOperationPanel = new JPanel(gridRowLayout);

		JLabel fillLabel = new JLabel("Fill contour:", JLabel.RIGHT);

		radioOperationPanel.add(onButton);
		radioOperationPanel.add(offButton);


		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 0;


		frame.add(fillLabel,c);

		c.gridx = 1;
		c.gridy = 0;

		frame.add(radioOperationPanel,c);
		
	}

	
	
	
	
	
	

	private void setupImage(JFrame frame) {
		imageView = new JLabel();
		binaryView = new JLabel();
		

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;

		frame.add(new JLabel("Image", JLabel.CENTER),c);
		
		c.gridx = 1;
		
		frame.add(new JLabel("Thresholded", JLabel.CENTER),c);
		
		c.gridy = 6;
		c.gridx = 0;
		c.gridwidth = 1;
		
		frame.add(imageView,c);
		
		c.gridx = 1;
		
		frame.add(binaryView,c);
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
		
		Image outputImage = imageProcessor.toBufferedImage(image);
		Image binaryImage = imageProcessor.toBufferedImage(binary);
		imageView.setIcon(new ImageIcon(outputImage));
		binaryView.setIcon(new ImageIcon(binaryImage));
	}

	private void processOperation() {
		resetImage();
		
	}

}
