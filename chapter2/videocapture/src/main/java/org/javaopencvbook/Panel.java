package org.javaopencvbook;
// Import the basic graphics classes.  
// The problem here is that we read the image with OpenCV into a Mat object.  
// But OpenCV for java doesn't have the method "imshow", so, we got to use  
// java for that (drawImage) that uses Image or BufferedImage.  
// So, how to go from one the other... Here is the way...  
import java.awt.*;  
import java.awt.image.BufferedImage;  
import java.awt.image.DataBufferByte;

import javax.swing.*;  

import org.opencv.core.Core;
import org.opencv.core.Mat;  
import org.opencv.highgui.VideoCapture;  
public class Panel extends JPanel{

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	private static final long serialVersionUID = 1L;  
	private BufferedImage image;  
	// Create a constructor method  
	public Panel(){  
		super();  
	}  
	private BufferedImage getimage(){  
		return image;  
	}  
	private void setimage(BufferedImage newimage){  
		image=newimage;  
		return;  
	}  
	/**  
	 * Converts/writes a Mat into a BufferedImage.  
	 *  
	 * @param matrix Mat of type CV_8UC3 or CV_8UC1  
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
	 */  
	public static BufferedImage matToBufferedImage(Mat m) {  
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			//Mat m2 = new Mat();
			//Imgproc.cvtColor(m,m2,Imgproc.COLOR_BGR2RGB);
			type = BufferedImage.TYPE_3BYTE_BGR;
			//m = m2;
		}
		byte [] b = new byte[m.channels()*m.cols()*m.rows()];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);

		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
		System.arraycopy(b, 0, targetPixels, 0, b.length);  


		//image.getRaster().setDataElements(0, 0, m.cols(),m.rows(), b);
		return image;

	}  
	public void paintComponent(Graphics g){  
		BufferedImage temp=getimage();  
		g.drawImage(temp,10,10,temp.getWidth(),temp.getHeight(), this);  
	}  
	public static void main(String arg[]){  

		JFrame frame = new JFrame("BasicPanel");  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setSize(400,400);  
		Panel panel = new Panel();  
		frame.setContentPane(panel);       
		frame.setVisible(true);       
		Mat webcam_image=new Mat();  
		BufferedImage temp;  
		VideoCapture capture =new VideoCapture(0);  
		if( capture.isOpened())  
		{  
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long start = System.currentTimeMillis();
			int count = 0;
			while( true )  
			{  
				capture.read(webcam_image);  
				if( !webcam_image.empty() )  
				{  
					frame.setSize(webcam_image.width()+40,webcam_image.height()+60);  
					temp=matToBufferedImage(webcam_image);  
					panel.setimage(temp);  
					panel.repaint();  
				}  
				else  
				{  
					System.out.println(" --(!) No captured frame -- Break!");  
					break;  
				}
				count++;
				long ellapsed = System.currentTimeMillis() - start;
				if(count%30==0){
					System.out.println("Fps: " + (count/(ellapsed/1000.0)) );
					count = 0;
					start = System.currentTimeMillis();
				}
			}  
		}
		return;  
	}  
}  