package org.javaopencvbook;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

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
	private static final String binaryInvString = "Binary inverted";
	private static final String binaryString = "Binary";
	private static final String truncateString = "Truncate";
	private static final String thresholdToZeroString = "Threshold to zero";
	private static final String invThresholdToZeroString = "Inverted threshold to zero";
	private static final String adaptiveMeanString = "Adaptive Mean Binary";
	private static final String noneString = "None";
	
	private Map<String, Integer> modeMap = new HashMap<String, Integer>();
	{		
		modeMap.put(binaryString, Imgproc.THRESH_BINARY);
		modeMap.put(binaryInvString, Imgproc.THRESH_BINARY_INV);
		modeMap.put(truncateString,	 Imgproc.THRESH_TRUNC);
		modeMap.put(thresholdToZeroString, Imgproc.THRESH_TOZERO);
		modeMap.put(invThresholdToZeroString, Imgproc.THRESH_TOZERO_INV);
	}
	
	
	private String thresholdMode = noneString;
	
	private JLabel imageView;
	private String windowName;
	private Mat image, originalImage;
	
	private final ImageProcessor imageProcessor = new ImageProcessor();
	
	private int level = 110;
	private double maxval = 255;
	private int blockSize = 3;
	protected int constantC = 5;
	private JSlider levelSlider;
	private JSlider maxSlider;
	private JSlider blockSlider;
	private JSlider constantSlider;
	
	

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
		setupThresholdSlider(frame);
		setupMaxSlider(frame);
		setupBlockSlider(frame);
		setupCSlider(frame);
		setupImage(frame);
		
		enableDisableSliders();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	private void setupTypeRadio(JFrame frame) {
		JRadioButton noneButton = new JRadioButton(noneString);
		noneButton.setMnemonic(KeyEvent.VK_O);
		noneButton.setActionCommand(noneString);
		noneButton.setSelected(true);
		
		
		JRadioButton binaryButton = new JRadioButton(binaryString);
		binaryButton.setMnemonic(KeyEvent.VK_B);
		binaryButton.setActionCommand(binaryString);
		
		JRadioButton binaryInvButton = new JRadioButton(binaryInvString);
		binaryInvButton.setMnemonic(KeyEvent.VK_I);
		binaryInvButton.setActionCommand(binaryInvString);
		
		JRadioButton truncateButton = new JRadioButton(truncateString);
		truncateButton.setMnemonic(KeyEvent.VK_T);
		truncateButton.setActionCommand(truncateString);
		
		JRadioButton thresholdToZeroButton = new JRadioButton(thresholdToZeroString);
		thresholdToZeroButton.setMnemonic(KeyEvent.VK_Z);
		thresholdToZeroButton.setActionCommand(thresholdToZeroString);
		
		JRadioButton invThresholdToZeroButton = new JRadioButton(invThresholdToZeroString);
		invThresholdToZeroButton.setMnemonic(KeyEvent.VK_N);
		invThresholdToZeroButton.setActionCommand(invThresholdToZeroString);
		
		JRadioButton adaptiveMeanButton = new JRadioButton(adaptiveMeanString);
		adaptiveMeanButton.setMnemonic(KeyEvent.VK_A);
		adaptiveMeanButton.setActionCommand(adaptiveMeanString);
		

		

		ButtonGroup group = new ButtonGroup();
		group.add(noneButton);
		group.add(binaryButton);
		group.add(binaryInvButton);
		group.add(truncateButton);
		group.add(thresholdToZeroButton);
		group.add(invThresholdToZeroButton);
		group.add(adaptiveMeanButton);
		
		

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				thresholdMode = event.getActionCommand();
				enableDisableSliders();
				processOperation();
			}
		};
		
		noneButton.addActionListener(operationChangeListener);
		binaryButton.addActionListener(operationChangeListener);
		binaryInvButton.addActionListener(operationChangeListener);
		truncateButton.addActionListener(operationChangeListener);			
		thresholdToZeroButton.addActionListener(operationChangeListener);
        invThresholdToZeroButton.addActionListener(operationChangeListener);
        adaptiveMeanButton.addActionListener(operationChangeListener);
        
		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel radioOperationPanel = new JPanel(gridRowLayout);

		JLabel modeLabel = new JLabel("Mode:", JLabel.RIGHT);

		radioOperationPanel.add(noneButton);
		radioOperationPanel.add(binaryButton);
		radioOperationPanel.add(binaryInvButton);
		radioOperationPanel.add(truncateButton);
		radioOperationPanel.add(thresholdToZeroButton);
		radioOperationPanel.add(invThresholdToZeroButton);
		radioOperationPanel.add(adaptiveMeanButton);
		


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
		if(noneString.equals(thresholdMode)){
			levelSlider   .setEnabled(false);
			maxSlider     .setEnabled(false);
			blockSlider   .setEnabled(false);
			constantSlider.setEnabled(false);
		}
		else if(adaptiveMeanString.equals(thresholdMode)){
			levelSlider   .setEnabled(false);
			maxSlider     .setEnabled(true);
			blockSlider   .setEnabled(true);
			constantSlider.setEnabled(true);
		}
		else{
			if(binaryString.equals(thresholdMode)|| 
					binaryInvString.equals(thresholdMode)){
				levelSlider   .setEnabled(true);
				maxSlider     .setEnabled(true);
				blockSlider   .setEnabled(false);
				constantSlider.setEnabled(false);
				
			}
			else{
				levelSlider   .setEnabled(true);
				maxSlider     .setEnabled(false);
				blockSlider   .setEnabled(false);
				constantSlider.setEnabled(false);
			}
		}
		
	}

	private void setupThresholdSlider(JFrame frame) {
		JLabel sliderLabel = new JLabel("Threshold:", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int minimum = 0;
		int maximum = 255;
		int initial =110;

		levelSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		levelSlider.setMajorTickSpacing(20);
		levelSlider.setMinorTickSpacing(5);
		levelSlider.setPaintTicks(true);
		levelSlider.setPaintLabels(true);
		levelSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				level = (int)source.getValue();
				processOperation();
				
				updateView(image);
				
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
	

	private void setupMaxSlider(JFrame frame) {
		JLabel sliderLabel = new JLabel("Max value:", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int minimum = 0;
		int maximum = 255;
		int initial =255;

		maxSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		maxSlider.setMajorTickSpacing(20);
		maxSlider.setMinorTickSpacing(5);
		maxSlider.setPaintTicks(true);
		maxSlider.setPaintLabels(true);
		maxSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				maxval = (int)source.getValue();
				processOperation();
				
				updateView(image);
				
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;

		frame.add(sliderLabel,c);
		c.gridx = 1;
		c.gridy = 2;
		frame.add(maxSlider,c);
		
	}
	
	private void setupBlockSlider(JFrame frame) {
		JLabel blockLabel = new JLabel("Block size:", JLabel.CENTER);
		blockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int minimum = 3;
		int maximum = 255;
		int initial =3;

		blockSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		blockSlider.setMajorTickSpacing(20);
		blockSlider.setMinorTickSpacing(2);
		blockSlider.setPaintTicks(true);
		blockSlider.setPaintLabels(true);
		blockSlider.setSnapToTicks(true);
		blockSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				
				if(blockSlider.getValueIsAdjusting()){
					int maybeEven = (int)source.getValue();					
					blockSize  = ((maybeEven%2)==0)? (maybeEven+1) :maybeEven;
				}
				else{
					blockSize = (int)source.getValue();
				}
				
				processOperation();
				
				updateView(image);
				
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;

		frame.add(blockLabel,c);
		c.gridx = 1;
		c.gridy = 3;
		frame.add(blockSlider,c);
		
	}
	
	private void setupCSlider(JFrame frame) {
		JLabel constantLabel = new JLabel("C constant:", JLabel.CENTER);
		constantLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int minimum = -100;
		int maximum = 100;
		int initial =5;

		constantSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		constantSlider.setMajorTickSpacing(20);
		constantSlider.setMinorTickSpacing(2);
		constantSlider.setPaintTicks(true);
		constantSlider.setPaintLabels(true);
		constantSlider.setSnapToTicks(true);
		constantSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				
				constantC  = (int)source.getValue();
				
				processOperation();
				
				updateView(image);
				
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;

		frame.add(constantLabel,c);
		c.gridx = 1;
		c.gridy = 4;
		frame.add(constantSlider,c);
		
	}
	
	
	
	

	private void setupImage(JFrame frame) {
		JPanel imagesPanel = new JPanel();
		imageView = new JLabel();
		imageView.setHorizontalAlignment(SwingConstants.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		
		JLabel originalImageLabel = new JLabel();
		Image originalAWTImage = imageProcessor.toBufferedImage(originalImage);
		originalImageLabel.setIcon(new ImageIcon(originalAWTImage));
		
		imagesPanel.add(originalImageLabel);
		imagesPanel.add(imageView);
		
		frame.add(imagesPanel,c);
	}

	
	
	


	protected void processOperation() {

		if(adaptiveMeanString.equals(thresholdMode)){
			Imgproc.adaptiveThreshold(originalImage, image, maxval, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, blockSize, constantC);	
		}
		else if(noneString.equals(thresholdMode)){
			image = originalImage.clone();
		}
		else{
			Imgproc.threshold(originalImage, image, level, maxval, modeMap.get(thresholdMode));
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
