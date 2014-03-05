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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.javaopencvbook.utils.ImageProcessor;
import org.javaopencvbook.utils.ImageViewer;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class GUI {
	private static final String erodeString = "Erode";
	private static final String dilateString = "Dilate";
	private static final String openString = "Open";
	private static final String closeString = "Close";
	private static final String rectangleString = "Rectangle";
	private static final String ellipseString = "Ellipse";
	private static final String crossString = "Cross";
	private String currentOperation = erodeString;
	private JLabel imageView;
	private String windowName;
	private Mat image;
	private int kernelSize = 0;

	private final ImageProcessor imageProcessor = new ImageProcessor();
	private int currentShape = Imgproc.CV_SHAPE_RECT;

	public GUI(String windowName, Mat newImage) {
		super();
		this.windowName = windowName;
		this.image = newImage;
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

		setupOperationRadioButtons(frame);
		setupSizeSlider(frame);
		setupShapeRadioButtons(frame);
		setupImage(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}

	private void setupShapeRadioButtons(JFrame frame) {
		JRadioButton rectangleButton = new JRadioButton(rectangleString);
		rectangleButton.setMnemonic(KeyEvent.VK_R);
		rectangleButton.setActionCommand(rectangleString);
		rectangleButton.setSelected(true);

		JRadioButton ellipseButton = new JRadioButton(ellipseString);
		ellipseButton.setMnemonic(KeyEvent.VK_L);
		ellipseButton.setActionCommand(ellipseString);

		JRadioButton crossButton = new JRadioButton(crossString);
		crossButton.setMnemonic(KeyEvent.VK_S);
		crossButton.setActionCommand(crossString);

		ButtonGroup group = new ButtonGroup();
		group.add(rectangleButton);
		group.add(ellipseButton);
		group.add(crossButton);

		ActionListener shapeChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				String currentShapeString = event.getActionCommand();
				if(rectangleString.equals(currentShapeString)){
					currentShape = Imgproc.CV_SHAPE_RECT;
				}
				else if(ellipseString.equals(currentShapeString)){
					currentShape = Imgproc.CV_SHAPE_ELLIPSE;
				}
				else if(crossString.equals(currentShapeString)){
					currentShape = Imgproc.CV_SHAPE_CROSS;
				}
				processOperation();	
			}
		};

		rectangleButton.addActionListener(shapeChangeListener);
		ellipseButton.addActionListener(shapeChangeListener);
		crossButton.addActionListener(shapeChangeListener);

		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel shapeRadioPanel = new JPanel(gridRowLayout);

		JLabel shapeLabel = new JLabel("Shape:");

		shapeRadioPanel.add(rectangleButton);
		shapeRadioPanel.add(ellipseButton);
		shapeRadioPanel.add(crossButton);


		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 2;


		frame.add(shapeLabel,c);

		c.gridx = 1;
		c.gridy = 2;

		frame.add(shapeRadioPanel,c);
	}

	private void setupOperationRadioButtons(JFrame frame) {
		JRadioButton erodeButton = new JRadioButton(erodeString);
		erodeButton.setMnemonic(KeyEvent.VK_E);
		erodeButton.setActionCommand(erodeString);
		erodeButton.setSelected(true);

		JRadioButton dilateButton = new JRadioButton(dilateString);
		dilateButton.setMnemonic(KeyEvent.VK_D);
		dilateButton.setActionCommand(dilateString);

		JRadioButton openButton = new JRadioButton(openString);
		openButton.setMnemonic(KeyEvent.VK_O);
		openButton.setActionCommand(openString);

		JRadioButton closeButton = new JRadioButton(closeString);
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.setActionCommand(closeString);

		ButtonGroup group = new ButtonGroup();
		group.add(erodeButton);
		group.add(dilateButton);
		group.add(openButton);
		group.add(closeButton);

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				currentOperation = event.getActionCommand();
				processOperation();	
			}
		};

		erodeButton.addActionListener(operationChangeListener);
		dilateButton.addActionListener(operationChangeListener);
		openButton.addActionListener(operationChangeListener);
		closeButton.addActionListener(operationChangeListener);

		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel radioOperationPanel = new JPanel(gridRowLayout);

		JLabel operationLabel = new JLabel("Operation:");

		radioOperationPanel.add(erodeButton);
		radioOperationPanel.add(dilateButton);
		radioOperationPanel.add(openButton);
		radioOperationPanel.add(closeButton);



		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 0;


		frame.add(operationLabel,c);

		c.gridx = 1;
		c.gridy = 0;

		frame.add(radioOperationPanel,c);

	}

	private void setupImage(JFrame frame) {
		imageView = new JLabel();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;

		frame.add(imageView,c);
	}


	private void setupSizeSlider(JFrame frame) {
		JLabel sliderLabel = new JLabel("Kernel size:", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		int minimum = 0;
		int maximum = 20;
		int initial =0;

		JSlider levelSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		levelSlider.setMajorTickSpacing(2);
		levelSlider.setMinorTickSpacing(1);
		levelSlider.setPaintTicks(true);
		levelSlider.setPaintLabels(true);
		levelSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				kernelSize = (int)source.getValue();
				processOperation();			
			}
		});


		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 0;
		c.gridy = 1;


		frame.add(sliderLabel,c);

		c.gridx = 1;
		c.gridy = 1;

		frame.add(levelSlider,c);

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

	private void processOperation() {
		Mat output = image.clone();
		if (erodeString.equals(currentOperation)){

			output = imageProcessor.erode(image, kernelSize, currentShape );	
		}
		else if(dilateString.equals(currentOperation)){
			output = imageProcessor.dilate(image, kernelSize, currentShape );
		}
		else if(openString.equals(currentOperation)){
			output = imageProcessor.open(image, kernelSize, currentShape );
		}
		else if(closeString.equals(currentOperation)){
			output = imageProcessor.close(image, kernelSize, currentShape );

		}
		updateView(output);
	}

}
