package org.myrobotlab.j4kdemo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.jme3.math.FastMath;

import edu.ufl.digitalworlds.j4k.Skeleton;

public class JointFilter {
	
	// List containing skeletons across time
	public LinkedList<Skeleton> skeletonList = new LinkedList<Skeleton>();
	// List containing skeleton orientation quaternions
	public LinkedList<ArrayList<Float[]>> orientationQList = new LinkedList<ArrayList<Float[]>>();
	// List containing joint tracking states
	public LinkedList<byte[]> trackingList = new LinkedList<byte[]>();
	// Array of lists containing joint positions
	public ArrayList<LinkedList<Float>> jointXListArray = new ArrayList<LinkedList<Float>>();
	public ArrayList<LinkedList<Float>> jointYListArray = new ArrayList<LinkedList<Float>>();
	public ArrayList<LinkedList<Float>> jointZListArray = new ArrayList<LinkedList<Float>>();
	// Array of InMoov Joints
	public int jointPosInclusions[] = {0,1,2,3,4,5,6,7,8,9,10,11,20,21,22,23,24};
	public int jointQInclusions[] = {0,1,2,4,5,6,7,8,9,10,14,20};

	// Final filtered skeleton to return
	public Skeleton sk;
	// Final filtered joint positions and orientations of skeleton to return
	public float[] joint_positions;
	public float[] joint_orientations;
	// Maximum size of joint arrays, default is set to 5 frames. Controls amount of smoothing.
	public int maxSize; 
	// Filter Method, default "None"
	public String jointFilterType = "None";

	public double bodyOrientation;
	public double[] torsoOrientation;
	
	// Filter Attributes (default)
	public Float _smoothing = 0.9f; // data smoothing factor
	
	// Filter Attributes for Holt's Double Exponential Smoothing
	public Float _correction = 0.5f; // Speed for adjusting trend
	public Float _prediction = 0.5f;
	public Float _jitterRadius = 0.05f; // jitter control
	public Float _maxDeviationRadius = 0.04f;
	//public Float[] _level = new Float[7];
	//public Float[] _trend = new Float[7];
	public Integer elapsedFrames = 0;
	public ArrayList<Float[]> skoldPosLevel = new ArrayList<Float[]>();
	public ArrayList<Float[]> skoldQLevel = new ArrayList<Float[]>();
	public ArrayList<Float[]> skoldPosTrend = new ArrayList<Float[]>();
	public ArrayList<Float[]> skoldQTrend = new ArrayList<Float[]>();
	
	public JointFilter(int initLength) {

		maxSize = initLength;
		
		for (int j=0; j<jointPosInclusions.length; j++) {
			jointXListArray.add(new LinkedList<Float>());
			jointYListArray.add(new LinkedList<Float>());
			jointZListArray.add(new LinkedList<Float>());
		}
		
		//skeleton = kSkeleton;
		//bodyOrientation = kSkeleton.getBodyOrientation();
		//torsoOrientation = kSkeleton.getTorsoOrientation();
		
	}
	
	public void setFrameHistory(int length) {
		maxSize = length;
	}
	
	public void setFilterMethod(String method) {
		jointFilterType = method;
	}
	
	public Skeleton getSkeleton() {
		
		switch (jointFilterType) {
			case "None": break;
			case "ExponentialSmoothing1": ExponentialSmoothing(1); break;
			case "ExponentialSmoothing2": ExponentialSmoothing(2); break;
			case "HoltDoubleExponential": ExponentialSmoothing(3); break;
		}
		
		return sk;
	}
	
	// Add Skeleton to the Skeleton Array
	public void addSkeleton(Skeleton skeleton) throws IOException {
		if(skeleton == null) {
			throw new IOException("Null Skeleton");
		}

		if(skeletonList.size()<=maxSize) {

			// retrieve new skeletal values from kinect
			sk = skeleton;
			joint_positions = skeleton.getJointPositions();
			joint_orientations = skeleton.getJointOrientations();
			
			// add skeleton data to list
			skeletonList.add(skeleton);
			trackingList.add(skeleton.getJointTrackingStates());
			//System.out.println(trackingList.peekLast()[2]);
			//System.out.println(skeleton.getJointTrackingStates()[3]);
			for(int i=0;i<jointPosInclusions.length;i++) {
				Float fx = skeleton.get3DJointX(jointPosInclusions[i]);
				Float fy = skeleton.get3DJointY(jointPosInclusions[i]);
				Float fz = skeleton.get3DJointZ(jointPosInclusions[i]);
				jointXListArray.get(i).add(fx);
				jointYListArray.get(i).add(fy);
				jointZListArray.get(i).add(fz);
				
				if(elapsedFrames == 1) {
					skoldPosLevel.add(new Float[]{fx,fy,fz});
					skoldPosTrend.add(new Float[]{0f,0f,0f});
				}
			}
			
			ArrayList<Float[]> o2s = new ArrayList<Float[]>();
			for(int i:jointQInclusions) {
				Float[] q = new Float[4];
				for(int j=0;j<4;j++) {
					q[j] = joint_orientations[i*4+j];
				}
				o2s.add(q);
				if(elapsedFrames == 1) {
					skoldQLevel.add(q);
					skoldQTrend.add(new Float[]{0f,0f,0f,0f});
				}
			}
			
			orientationQList.add(o2s);
			
		} 
		if(skeletonList.size()>maxSize){
			removeSkeleton(1);
		}
		
	}
	
	// Remove Skeleton to the Skeleton Array
	public void removeSkeleton(int num) {
		for(int n=0;n<num;n++) {
			if(skeletonList.size() > 0) {
				skeletonList.poll();
				orientationQList.poll();
				for(int i=0;i<jointPosInclusions.length;i++) {
					jointXListArray.get(i).poll();
					jointYListArray.get(i).poll();
					jointZListArray.get(i).poll();
					trackingList.poll();
				}
			}
		}
	}
	
	public void addElapsedFrames() {
		elapsedFrames += 1;
	}
	
	public void resetElapsedFrames() {
		elapsedFrames = 0;
	}
	
	// Reset the filter and remove all existing skeletons
	public void reset() {
		skeletonList.clear();
	}
	
	//Implements exponential weighted moving average
	public void ExponentialSmoothing(int method) {
		
		if(method == 2 || method == 3) { 
			setFrameHistory(1);
			if(skeletonList.size() > maxSize) {
				removeSkeleton(skeletonList.size()-maxSize);
			}
		}
		Boolean tracked = true;
		for(int i=0;i<jointPosInclusions.length;i++) {
			Float[] fx = jointXListArray.get(i).toArray(new Float[jointXListArray.get(i).size()]);
			Float[] fy = jointYListArray.get(i).toArray(new Float[jointYListArray.get(i).size()]);
			Float[] fz = jointZListArray.get(i).toArray(new Float[jointZListArray.get(i).size()]);
			Float[][] fxyz = {fx, fy, fz};
			
			//if (trackingList.peekLast()[jointPosInclusions[i]] != Skeleton.TRACKED) tracked = false;
			if(method == 1) {
				for(int j=0;j<3;j++) {
					joint_positions[j+jointPosInclusions[i]*3] = EWMA1(fxyz[j]);
				}
			} else if(method == 2) {
				Float[] posLevel = new Float[3];
				for(int j=0;j<3;j++) {
					Float[] result = new Float[2];
					result = EWMA2(fxyz[j],skoldPosLevel.get(i)[j]);
					joint_positions[j+jointPosInclusions[i]*3] = result[0];
					posLevel[j] = result[1];
				}
				skoldPosLevel.set(i,posLevel);
				
			} else if(method == 3) {
				Float[] posLevel = new Float[3];
				Float[] posTrend = new Float[3];
				for(int j=0;j<3;j++) {
					Float[] result = new Float[3];
					result = HDE(fxyz[j],skoldPosLevel.get(i)[j],skoldPosTrend.get(i)[j],tracked);
					joint_positions[j+jointPosInclusions[i]*3] = result[0];
					posLevel[j] = result[1];
					posTrend[j] = result[2];
				}
				skoldPosLevel.set(i,posLevel);
				skoldPosTrend.set(i,posTrend);
			}
		}
		
		Float[][] q = new Float[jointQInclusions.length*4][orientationQList.size()];
		
		for(int i=0;i<jointQInclusions.length;i++) {
			if(method == 1) {
				for(int j=0;j<4;j++) {
					for(int k=0;k<orientationQList.size();k++) {
						q[i*4+j][k] = orientationQList.get(k).get(i)[j];
					}
					joint_orientations[jointQInclusions[i]*4+j] = EWMA1(q[i*4+j]);
				}
			} else if (method == 2){
				Float[] qLevel = new Float[4];
				for(int j=0;j<4;j++) {
					for(int k=0;k<orientationQList.size();k++) {
						q[i*4+j][k] = orientationQList.get(k).get(i)[j];
					}
					 Float[] result = EWMA2(q[i*4+j], skoldQLevel.get(i)[j]);
					 joint_orientations[jointQInclusions[i]*4+j] = result[0];
					 qLevel[j] = result[1];
				}
				skoldQLevel.set(i,qLevel);
			} else if (method == 3) {
				Float[] qLevel = new Float[4];
				Float[] qTrend = new Float[4];
				for(int j=0;j<4;j++) {
					for(int k=0;k<orientationQList.size();k++) {
						q[i*4+j][k] = orientationQList.get(k).get(i)[j];
					}
					Float[] result = HDE(q[i*4+j], skoldQLevel.get(i)[j], skoldQTrend.get(i)[j], tracked);
					joint_orientations[jointQInclusions[i]*4+j] = result[0];
					qLevel[j] = result[1];
					qTrend[j] = result[2];
				}
				skoldQLevel.set(i,qLevel);
				skoldQTrend.set(i,qTrend);
			}
		}
		
		sk.setJointPositions(joint_positions);
		sk.setJointOrientations(joint_orientations);

	}
	
	public Float[] HDE(Float[] data, Float _prevLevel, Float _prevTrend, Boolean tracked) {
		Float smoothing = _smoothing;
		Float correction = _correction;
		Float prediction = _prediction;
		Float jitterRadius = _jitterRadius;
		Float maxDeviationRadius = _maxDeviationRadius;
		Float diffVal = 0f;
		Float raw = data[0];
		Float level = 0f;
		Float trend = 0f;
		Float delta = 0f;
		Float prevLevel = _prevLevel;
		Float prevTrend = _prevTrend;
		Float predictedData = 0f;
		Float[] result = new Float[3];
		
		if (!tracked) {
			jitterRadius *= 2.0f;
            maxDeviationRadius *= 2.0f;
		}
		
		if(elapsedFrames == 1) {
			level = raw;
		} else if(elapsedFrames == 2) {
			level = (raw + prevLevel) * 0.5f;
			delta = level - prevLevel;
            trend = (delta * correction) + (prevTrend * (1.0f - correction));
		} else {
			// First apply jitter filter 
			delta = raw - prevLevel;
            diffVal = FastMath.abs(delta);
            if (diffVal <= jitterRadius) {
            	level = (raw * (diffVal / jitterRadius)) +
                                   (prevLevel * (1.0f - (diffVal / jitterRadius)));
            } else {
            	level = raw;
            }
            // Now the double exponential smoothing filter
            level = (level * (1.0f - smoothing)) +
                               ((prevLevel + prevTrend) * smoothing);

            delta = level - prevLevel;
            trend = (delta * correction) + (prevTrend * (1.0f - correction));
		}
		// Predict into the future to reduce latency
		predictedData = level + (trend * prediction);
		
		// Check that we are not too far away from raw data
        delta = predictedData - raw;
        diffVal = FastMath.abs(delta);

        if (diffVal > maxDeviationRadius) {
            predictedData = (predictedData * (maxDeviationRadius / diffVal)) +
                                (raw * (1.0f - (maxDeviationRadius / diffVal)));
        }
        
        result[0] = predictedData;
        result[1] = level;
        result[2] = trend;
        
		return result;
	}
	
	//Exponential weighted moving average method
	public float EWMA1(Float[] data) {
		
		Float numerator = 0f;
	    Float denominator = 0f;
	    Float average = 0f;
	    Float alpha = _smoothing;
	    
	    for(Float c:data) average = Float.sum(average, c);
	    average /= data.length;
	 
	    for ( int i = 0; i < data.length; ++i )
	    {	
	        numerator = Float.sum(numerator, data[i] * FastMath.pow( alpha, data.length - i - 1 ));
	        denominator = Float.sum(denominator, FastMath.pow( alpha, data.length - i - 1 ));
	    }
	 
	    numerator = Float.sum(numerator, (average * FastMath.pow( alpha, data.length )));
	    denominator = Float.sum(denominator, FastMath.pow( alpha, data.length ));
	    
	    return (numerator / denominator);
	
	}
	
	//Exponential weighted moving average method
	public Float[] EWMA2(Float[] data, Float _level) {
		
		/*  Null Checker
		if(data.length == 1 && data[0]==null) return 0.5f;

		List<Float> index = new ArrayList<Float>();
		for(int i=0;i<data.length;i++) {
			if(data[i] != null) {
				index.add(data[i]);
			}
		}
		Float[] data2 = index.toArray(new Float[index.size()]);
		*/
		Float raw = data[0];
		Float prevLevel;
		Float level;
		Float alpha = _smoothing;
		Float[] result = new Float[2];
		if(elapsedFrames == 1) {
			prevLevel = raw;
		} else {
			prevLevel = _level;
		}
		
		//c = alpha * a + (1.0f - alpha) * b;
		level = prevLevel + (alpha) * (raw - prevLevel);
		
		result[0] = level;
		result[1] = level;
		
		return result;
	}	
	
	public void writeToFile(File output_file) throws IOException {

        FileWriter fwt = new FileWriter(output_file);
        StringBuilder sb = new StringBuilder();
        /*
        for (int i = 0; i < joints.length; i++) {
            sb.append(joints[i].toString());
            sb.append("\n");
        }*/
        fwt.write(sb.toString());
        fwt.flush();
        fwt.close();

    }
}