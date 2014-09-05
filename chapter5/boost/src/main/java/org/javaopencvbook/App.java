package org.javaopencvbook;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.ml.CvBoost;
import org.opencv.ml.CvBoostParams;
import org.opencv.ml.Ml;

public class App 
{
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) throws Exception {

		Mat data = new Mat(5, 3, CvType.CV_32FC1, new Scalar(0));

		data.put(0, 0, 1.69, 1, 0);
		data.put(1, 0, 1.76, 0, 0);
		data.put(2, 0, 1.80, 0, 0);
		data.put(3, 0, 1.77, 0, 0);
		data.put(4, 0, 1.83, 0, 1);

		Mat responses = new Mat(5, 1, CvType.CV_32SC1, new Scalar(0));
		
		responses.put(0,0, new int[]{0,1,1,0,1});
		
		
		
		CvBoost boost = new CvBoost();
		//boost.train(data,1, responses); // CV_ROW_SAMPLE = 1

		CvBoostParams params = new CvBoostParams();
		/*
	
		params.set_split_criteria(CvBoost.MISCLASS);
		params.set_weight_trim_rate(0.0f);*/

		params.set_boost_type(CvBoost.DISCRETE);
		params.set_weak_count(3);
		params.set_min_sample_count(4);
		boost.train(data,1,responses,new Mat(),new Mat(),new Mat(), new Mat(),params,false);
		System.out.println(params.get_min_sample_count());
		for(int i=0;i<5;i++){
			System.out.println("Result = " + boost.predict(data.row(i)));
		}
		
		Mat newPerson = new Mat(1,3,CvType.CV_32FC1, new Scalar(0));
		newPerson.put(0, 0, 1.60, 1,0);
		System.out.println(newPerson.dump());
		
		System.out.println("New (woman) = " + boost.predict(newPerson));
		
		newPerson.put(0, 0, 1.8, 0,1);
		
		System.out.println("New (man) = " + boost.predict(newPerson));
		
		newPerson.put(0, 0, 1.7, 1,0);
		
		System.out.println("New (?) = " + boost.predict(newPerson));
		
		boost.save("boost.xml");


	}
}