package org.javaopencvbook;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.ml.Boost;
import org.opencv.ml.Ml;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) throws Exception {

		Mat data = new Mat(5, 3, CvType.CV_32FC1, new Scalar(0));

		data.put(0, 0, new float[]{1.69f, 1, 0});
		data.put(1, 0, new float[]{1.76f, 0, 0});
		data.put(2, 0, new float[]{1.80f, 0, 0});
		data.put(3, 0, new float[]{1.77f, 0, 0});
		data.put(4, 0, new float[]{1.83f, 0, 1});

		Mat responses = new Mat(5, 1, CvType.CV_32SC1, new Scalar(0));
		
		responses.put(0,0, new int[]{0,1,1,0,1});
		
		Boost boost = Boost.create();
		
		boost.setBoostType(Boost.DISCRETE);
		boost.setWeakCount(3);
		boost.setMinSampleCount(4);
		
		boost.train(data, Ml.ROW_SAMPLE, responses);

		//This will simply show the input data is correctly classified
		for(int i=0;i<5;i++){
			System.out.println("Result = " + boost.predict(data.row(i)));
		}
		
		Mat newPerson = new Mat(1,3,CvType.CV_32FC1, new Scalar(0));
		newPerson.put(0, 0, new float[]{1.60f, 1,0});
		System.out.println(newPerson.dump());
		
		System.out.println("New (woman) = " + boost.predict(newPerson));
		
		newPerson.put(0, 0, new float[]{1.8f, 0,1});
		System.out.println(newPerson.dump());
		
		System.out.println("New (man) = " + boost.predict(newPerson));
		
		newPerson.put(0, 0, new float[]{1.7f, 1,0});
		System.out.println(newPerson.dump());
		System.out.println("New (?) = " + boost.predict(newPerson));
		
	}
}
