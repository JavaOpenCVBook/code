package org.javaopencvbook;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.javaopencvbook.utils.ImageProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
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

		setupButton(frame);
		setupImage(frame);


		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}

	private void setupImage(JFrame frame) {
		imageView = new JLabel();
		imageView.setHorizontalAlignment(SwingConstants.CENTER);

		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(640, 480));
		frame.add(imageScrollPane);
	}

	private void setupButton(JFrame frame) {
		final JPanel buttonsPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		buttonsPanel.setLayout(flowLayout);

		JButton restoreButton = new JButton("Restore");
		restoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				image = originalImage.clone();
				updateView(originalImage);
			}
		});
		


		
		JButton pyramidDown = new JButton("Pyramid Down");
		pyramidDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Imgproc.pyrDown(image, image);
				updateView(image);
			}
		});
		
		JButton pyramidUp = new JButton("Pyramid Up");
		pyramidUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Imgproc.pyrUp(image, image);
				updateView(image);
			}
		});
		
		JButton laplacian = new JButton("Laplacian");
		laplacian.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Mat gp1 = new Mat();
				Imgproc.pyrDown(image, gp1);
				Imgproc.pyrUp(gp1, gp1);
				Core.subtract(image, gp1, gp1);
				updateView(gp1);
			}
		});
		

		buttonsPanel.add(restoreButton);
		buttonsPanel.add(pyramidDown);
		buttonsPanel.add(pyramidUp);
		buttonsPanel.add(laplacian);
		frame.add(buttonsPanel);

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
