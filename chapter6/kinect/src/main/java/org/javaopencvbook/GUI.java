package org.javaopencvbook;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
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

public class GUI {
	public static final String DISPARITY_STRING = "Disparity Map";
	public static final String RGB_STRING = "RGB Image";
	public static final String BACKGROUND_STRING = "Background";
	public static final String DISPARITY_THRESHOLD_STRING = "Disparity Thresholded";
	public static final String COMBINED_STRING = "Combined";
	public static final String RGB_MASK_STRING = "Masked RGB";
	private JLabel imageView;
	private String windowName;
	private String outputMode = COMBINED_STRING;
	private Mat image, originalImage;
	private int level = 0;
	
	public int getLevel() {
		return level;
	}

	private final ImageProcessor imageProcessor = new ImageProcessor();
	

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
	
	public String getOutputMode() {
		return outputMode;
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
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

		setupSlider(frame);
		setupRadio(frame);
		setupImage(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}

	private void setupRadio(JFrame frame) {
		JRadioButton disparityMapButton = new JRadioButton(DISPARITY_STRING);
		disparityMapButton.setMnemonic(KeyEvent.VK_D);
		disparityMapButton.setActionCommand(DISPARITY_STRING);
		disparityMapButton.setSelected(false);
		
		JRadioButton disparityThresholdButton = new JRadioButton(DISPARITY_THRESHOLD_STRING);
		disparityThresholdButton.setMnemonic(KeyEvent.VK_T);
		disparityThresholdButton.setActionCommand(DISPARITY_THRESHOLD_STRING);
		disparityThresholdButton.setSelected(false);

		JRadioButton rgbButton = new JRadioButton(RGB_STRING);
		rgbButton.setMnemonic(KeyEvent.VK_R);
		rgbButton.setActionCommand(RGB_STRING);
		rgbButton.setSelected(false);
		
		
		JRadioButton backgroundButton = new JRadioButton(BACKGROUND_STRING);
		backgroundButton.setMnemonic(KeyEvent.VK_B);
		backgroundButton.setActionCommand(BACKGROUND_STRING);
		backgroundButton.setSelected(false);
		
		JRadioButton combinedButton = new JRadioButton(COMBINED_STRING);
		combinedButton.setMnemonic(KeyEvent.VK_S);
		combinedButton.setActionCommand(COMBINED_STRING);
		combinedButton.setSelected(true);
		
		
		JRadioButton rgbMaskButton = new JRadioButton(RGB_MASK_STRING);
		rgbMaskButton.setMnemonic(KeyEvent.VK_M);
		rgbMaskButton.setActionCommand(RGB_MASK_STRING);
		rgbMaskButton.setSelected(false);
		
		
		ButtonGroup group = new ButtonGroup();
		group.add(combinedButton);
		group.add(rgbMaskButton);
		group.add(disparityThresholdButton);
		group.add(disparityMapButton);
		group.add(rgbButton);
		group.add(backgroundButton);
		group.add(combinedButton);
		

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				outputMode  = event.getActionCommand();
			}
		};

		rgbMaskButton.addActionListener(operationChangeListener);
		rgbButton.addActionListener(operationChangeListener);
		combinedButton.addActionListener(operationChangeListener);
		disparityThresholdButton.addActionListener(operationChangeListener);
		backgroundButton.addActionListener(operationChangeListener);
		disparityMapButton.addActionListener(operationChangeListener);

		
		JPanel radioOperationPanel = new JPanel();

		JLabel outputLabel = new JLabel("Output:", JLabel.RIGHT);

		radioOperationPanel.add(outputLabel);
		radioOperationPanel.add(combinedButton);
		radioOperationPanel.add(rgbMaskButton);
		radioOperationPanel.add(disparityMapButton);
		radioOperationPanel.add(disparityThresholdButton);
		radioOperationPanel.add(rgbButton);
		radioOperationPanel.add(backgroundButton);
		
		

		frame.add(radioOperationPanel);
		

		
	}

	private void setupImage(JFrame frame) {
		imageView = new JLabel();
		imageView.setHorizontalAlignment(SwingConstants.CENTER);
		
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(680, 510));
		
		frame.add(imageScrollPane);
	}


	private void setupSlider(JFrame frame) {
		JLabel sliderLabel = new JLabel("Depth filter", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int minimum = 0;
		int maximum = 100;
		int initial =0;

		JSlider levelSlider = new JSlider(JSlider.HORIZONTAL,
				minimum, maximum, initial);

		levelSlider.setMajorTickSpacing(10);
		levelSlider.setMinorTickSpacing(1);
		levelSlider.setPaintTicks(true);
		levelSlider.setPaintLabels(true);
		levelSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				level = (int)source.getValue();
				//Mat output = imageProcessor.blur(image, level);
				//updateView(output);			
			}
		});

		frame.add(sliderLabel);
		frame.add(levelSlider);
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

	public void updateView(Mat newMat) {
		Image outputImage = imageProcessor.toBufferedImage(newMat);
		imageView.setIcon(new ImageIcon(outputImage));
	}

}
