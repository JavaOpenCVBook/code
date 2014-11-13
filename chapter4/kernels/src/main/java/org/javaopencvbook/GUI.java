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
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class GUI {
	private static final String laplaceString = "Laplace";
	private static final String sobelString = "Sobel";
	private static final String cannyString = "Canny";
	private static final String noneString = "None";


	private String operation = noneString;

	private JLabel imageView;
	private String windowName;
	private Mat image, originalImage;

	private final ImageProcessor imageProcessor = new ImageProcessor();

	private int aperture = 3;
	private int xOrder = 1;
	private int yOrder = 1;
	protected int lowThreshold = 10;
	protected int highThreshold = 50;
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


		JRadioButton binaryButton = new JRadioButton(sobelString);
		binaryButton.setMnemonic(KeyEvent.VK_S);
		binaryButton.setActionCommand(sobelString);

		JRadioButton binaryInvButton = new JRadioButton(laplaceString);
		binaryInvButton.setMnemonic(KeyEvent.VK_L);
		binaryInvButton.setActionCommand(laplaceString);

		JRadioButton truncateButton = new JRadioButton(cannyString);
		truncateButton.setMnemonic(KeyEvent.VK_C);
		truncateButton.setActionCommand(cannyString);



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
		else if(sobelString.equals(operation)){
			apertureSliderLabel.setEnabled(true);
			apertureSlider   .setEnabled(true);
			xOrderSliderLabel.setEnabled(true);
			xorderSlider     .setEnabled(true);
			yOrderSliderLabel.setEnabled(true);
			yOrderSlider   .setEnabled(true);
			lowThresholdSliderLabel.setEnabled(false);
			lowThresholdSlider.setEnabled(false);
			highThresholdLabel.setEnabled(false);
			highThresholdSlider.setEnabled(false);

		}

		else if(laplaceString.equals(operation)){
			apertureSliderLabel.setEnabled(true);
			apertureSlider   .setEnabled(true);
			xOrderSliderLabel.setEnabled(false);
			xorderSlider     .setEnabled(false);
			yOrderSliderLabel.setEnabled(false);
			yOrderSlider   .setEnabled(false);
			lowThresholdSliderLabel.setEnabled(false);
			lowThresholdSlider.setEnabled(false);
			highThresholdLabel.setEnabled(false);
			highThresholdSlider.setEnabled(false);
		}
		else if(cannyString.equals(operation)){
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
		int initial = 10;

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

		if(sobelString.equals(operation)){
			Imgproc.Sobel(originalImage, image, -1, xOrder,yOrder,aperture,1.0, 0.0);
		}
		else if(laplaceString.equals(operation)){
			Imgproc.Laplacian(originalImage, image, -1, aperture, 1.0, 0.0);
		}
		else if(cannyString.equals(operation)){
			Imgproc.Canny(originalImage, image, lowThreshold, highThreshold, aperture, false);
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
