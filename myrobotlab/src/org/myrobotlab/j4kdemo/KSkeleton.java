package org.myrobotlab.j4kdemo;

// Class that wraps around the Skeleton class from Java for Kinect

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector4f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;

import edu.ufl.digitalworlds.j4k.Skeleton;
import edu.ufl.digitalworlds.math.Geom;

public class KSkeleton extends Skeleton {
	public static final int[] end_joints = {HEAD, HAND_TIP_RIGHT, HAND_TIP_LEFT, THUMB_RIGHT, THUMB_LEFT, FOOT_RIGHT, FOOT_LEFT};
	private Joint[] Joints;
	public static int[] spineHierarchy = {SPINE_BASE, SPINE_MID, SPINE_SHOULDER, NECK, HEAD};
	public static int[] rightHandHierarchy = {SPINE_SHOULDER, SHOULDER_RIGHT, ELBOW_RIGHT, WRIST_RIGHT, HAND_RIGHT, HAND_TIP_RIGHT};
	public static int[] leftHandHierarchy = {SPINE_SHOULDER, SHOULDER_LEFT, ELBOW_LEFT, WRIST_LEFT, HAND_LEFT, HAND_TIP_LEFT};
	public static int[] rightFootHierarchy = {SPINE_BASE, HIP_RIGHT, KNEE_RIGHT, ANKLE_RIGHT, FOOT_RIGHT};
	public static int[] leftFootHierarchy = {SPINE_BASE, HIP_LEFT, KNEE_LEFT, ANKLE_LEFT, FOOT_LEFT};
	public static int[][] initOrder = {spineHierarchy, rightHandHierarchy, leftHandHierarchy, rightFootHierarchy, leftFootHierarchy};
	
	public Joint spineShoulderJoint;
	public Joint shoulderLeftJoint;
	public Float wristLeftNormalAngle;
	public Float wristLeftBinormalAngle;
	
	
	public KSkeleton(Skeleton sk) {
		
		Joints  = new Joint[JOINT_COUNT];
		joint_position = sk.getJointPositions();
		joint_orientation = sk.getJointOrientations();
		skeleton_tracked = sk.isTracked();
		Quaternion[] joint_orientation_list = new Quaternion[JOINT_COUNT];
		
		for(int i=0;i<JOINT_COUNT-1;i++) {
			joint_orientation_list[i] = new Quaternion(joint_orientation[i*4],joint_orientation[i*4+1],
												joint_orientation[i*4+2],joint_orientation[i*4+3]);
		}

		joint_state = sk.getJointTrackingStates();
		
		Joints[SPINE_BASE] = new Joint(joint_position[SPINE_BASE*3],joint_position[SPINE_BASE*3+1],joint_position[SPINE_BASE*3+2],
										joint_orientation_list[SPINE_BASE], joint_state[SPINE_BASE], null, SPINE_BASE);
		for(int i=0;i<initOrder.length;i++) {
			for(int j=1; j < initOrder[i].length; j++){
				int index = initOrder[i][j];
				Joints[index] = new Joint(joint_position[index*3],joint_position[index*3+1],joint_position[index*3+2],
										joint_orientation_list[index], joint_state[index], Joints[initOrder[i][j-1]], index);
			}
		}

		Joints[THUMB_LEFT] = new Joint(joint_position[THUMB_LEFT*3],joint_position[THUMB_LEFT*3+1],joint_position[THUMB_LEFT*3+2],
				joint_orientation_list[THUMB_LEFT], joint_state[THUMB_LEFT], this.getJoint(WRIST_LEFT), THUMB_LEFT);
		Joints[THUMB_RIGHT] = new Joint(joint_position[THUMB_RIGHT*3],joint_position[THUMB_RIGHT*3+1],joint_position[THUMB_RIGHT*3+2],
				joint_orientation_list[THUMB_RIGHT], joint_state[THUMB_RIGHT], this.getJoint(WRIST_RIGHT), THUMB_RIGHT);
	}
	
	public Joint[] getJoints(){
		return Joints;
	}
	
	public Joint getJoint(int joint_id) {
		return Joints[joint_id];
	}
	
	public static boolean isEndJoint(int joint_id) {
		for(int i=0;i<end_joints.length;i++) {
			if(end_joints[i] == joint_id) {
				return true;
			}
		}
		return false;
	}
	/*
	public Vector3f getBone(int a, int b) {
		//Vector3f boneA = ;
		Vector3f boneB = this.getJoint(b).getAbsPosition();
		boneB.subtract(boneA);
		
		return 
	}
	*/
}
