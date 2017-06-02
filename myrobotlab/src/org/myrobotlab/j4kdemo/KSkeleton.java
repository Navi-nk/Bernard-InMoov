package org.myrobotlab.j4kdemo;

// Class that wraps around the Skeleton class from Java for Kinect

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;

import edu.ufl.digitalworlds.j4k.Skeleton;
import edu.ufl.digitalworlds.math.Geom;

public class KSkeleton extends Skeleton {
	public Joint[] Joints;
	
	public KSkeleton(Skeleton sk) {
		Joints  = new Joint[JOINT_COUNT + 1];
		this.joint_position = sk.getJointPositions();
		this.joint_orientation = sk.getJointOrientations();
		this.joint_state = sk.getJointTrackingStates();
		for(int i = 0; i < JOINT_COUNT + 1; i++){
			//Joints[i] = new Joint(0,0,0, q, 0, null);
		}
	}
	
	public Joint[] getJoints(){
		return Joints;
	}
}
