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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class GUI {


	private static final String blurString = "Blur";
	private static final String gaussianString = "Gaussian";
	private static final String medianString = "Median";
	private static final String bilateralString = "Bilateral";
	private static final String noneString = "None";
	private JLabel imageView = new JLabel();;
	private String windowName;

	private Mat originalImage;
	private Mat image;
	private Mat output;

	private final ImageProcessor imageProcessor = new ImageProcessor();
	protected String filterMode = blurString;
	

	public GUI(String windowName, Mat newImage) {
		super();
		this.windowName = windowName;
		this.image = newImage;
		originalImage = newImage.clone();
		processOperation();
		updateView();
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

		setupFilterRadio(frame);
		setupButtons(frame);
		
		setupImage(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	private void resetImage() {
		image = originalImage.clone();
		processOperation();
		updateView();
	}
	
	private void addNoise() {
		image = originalImage.clone();
		Mat grayRnd = new Mat(image.rows(), image.cols(), image.type());
		double noise = 128;
		grayRnd.setTo(new Scalar(noise/2, noise/2, noise/2));
		Core.subtract(image, grayRnd, image);
        Core.randu(grayRnd, 0, noise);
        Core.add(image, grayRnd, image);
		processOperation();
		updateView();
	}

	private void setupButtons(JFrame frame) {
		JButton noiseButton = new JButton("Add noise");
		noiseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addNoise();
			}
		});
		noiseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				resetImage();
			}

		});
		resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel buttonsPanel = new JPanel(gridRowLayout);

		buttonsPanel.add(resetButton);
		buttonsPanel.add(noiseButton);	
		
		
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 2;
		frame.add(buttonsPanel,c);
		
	}

	private void setupFilterRadio(JFrame frame) {
		JRadioButton noneButton = new JRadioButton(noneString);
		noneButton.setMnemonic(KeyEvent.VK_N);
		noneButton.setActionCommand(noneString);
		
		
		JRadioButton blurButton = new JRadioButton(blurString);
		blurButton.setMnemonic(KeyEvent.VK_B);
		blurButton.setActionCommand(blurString);
		blurButton.setSelected(true);

		JRadioButton gaussianButton = new JRadioButton(gaussianString);
		gaussianButton.setMnemonic(KeyEvent.VK_G);
		gaussianButton.setActionCommand(gaussianString);
		
		JRadioButton medianButton = new JRadioButton(medianString);
		medianButton.setMnemonic(KeyEvent.VK_M);
		medianButton.setActionCommand(medianString);
		
		JRadioButton bilateralButton = new JRadioButton(bilateralString);
		bilateralButton.setMnemonic(KeyEvent.VK_L);
		bilateralButton.setActionCommand(bilateralString);
		
		
		ButtonGroup group = new ButtonGroup();
		group.add(noneButton);
		group.add(blurButton);
		group.add(gaussianButton);
		group.add(medianButton);
		group.add(bilateralButton);

		ActionListener operationChangeListener = new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				filterMode  = event.getActionCommand();
				processOperation();
				updateView();
			}
		};
		noneButton.addActionListener(operationChangeListener);
		blurButton.addActionListener(operationChangeListener);
		gaussianButton.addActionListener(operationChangeListener);
		medianButton.addActionListener(operationChangeListener);
		bilateralButton.addActionListener(operationChangeListener);

		GridLayout gridRowLayout = new GridLayout(1,0);
		JPanel radioOperationPanel = new JPanel(gridRowLayout);

		JLabel modeLabel = new JLabel("Filter:", JLabel.RIGHT);

		radioOperationPanel.add(modeLabel);
		radioOperationPanel.add(noneButton);	
		radioOperationPanel.add(blurButton);	
		radioOperationPanel.add(bilateralButton);
		radioOperationPanel.add(gaussianButton);
		radioOperationPanel.add(medianButton);


		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		c.gridy = 0;

		frame.add(radioOperationPanel,c);

	}
	
	

	private void setupImage(JFrame frame) {
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.CENTER;
		
		c.gridy = 3;
		c.gridx = 0;
		
		frame.add(imageView,c);
		
		
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
		Image outputImage = imageProcessor.toBufferedImage(output);
		imageView.setIcon(new ImageIcon(outputImage));
	}

	private void processOperation() {
		if(noneString.equals(filterMode)){
			output = image.clone();
		}
		else {
			output = new Mat(image.rows(), image.cols(), image.type());
			Size size = new Size(3.0, 3.0);
			if(blurString.equals(filterMode)){
				Imgproc.blur(image, output, size);
			}
			else if(gaussianString.equals(filterMode)){
				Imgproc.GaussianBlur(image, output, size, 0);
			}
			else if(medianString.equals(filterMode)){
				Imgproc.medianBlur(image, output, 3);
			}
			else if(bilateralString.equals(filterMode)){
				Imgproc.bilateralFilter(image, output, 9, 100, 100);
			}
			
		}
		
	}

}
