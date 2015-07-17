package org.javaopencvbook;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
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
import org.opencv.imgproc.Imgproc;

public class GUI {
	private JLabel imageView;
	private String windowName;
	private Mat image, originalImage;
	
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
		setupImage(frame);
		setupButton(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}

	private void setupImage(JFrame frame) {
		JLabel mouseWarning = new JLabel("Try clicking on the image!", JLabel.CENTER);
		mouseWarning .setAlignmentX(Component.CENTER_ALIGNMENT);
		mouseWarning.setFont(new Font("Serif", Font.PLAIN, 18));
		frame.add(mouseWarning);

		imageView = new JLabel();
		
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(640, 480));
		
		imageView.addMouseListener(new MouseAdapter()  
		{  
		    public void mousePressed(MouseEvent e)  
		    {  
		    	Imgproc.circle(image,new Point(e.getX(),e.getY()),20, new Scalar(0,0,255), 4);
		    	updateView(image);
		    }  
		}); 
		
		frame.add(imageScrollPane);
	}

	private void setupButton(JFrame frame) {
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent event) {
				image = originalImage.clone();
				updateView(originalImage);
			}
		});
		clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		frame.add(clearButton);
	}

	private void setupSlider(JFrame frame) {
		JLabel sliderLabel = new JLabel("Blur level", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int minimum = 0;
		int maximum = 10;
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
				int level = (int)source.getValue();
				Mat output = imageProcessor.blur(image, level);
				updateView(output);			
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

	private void updateView(Mat newMat) {
		Image outputImage = imageProcessor.toBufferedImage(newMat);
		imageView.setIcon(new ImageIcon(outputImage));
	}

}
