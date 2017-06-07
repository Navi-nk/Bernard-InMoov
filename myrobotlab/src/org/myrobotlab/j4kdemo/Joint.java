package org.myrobotlab.j4kdemo;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;

public class Joint {
	
	public int jointID;
	public Vector3f absolutePos;
	public Vector3f relativePos;
	public int trackingState;
	public Quaternion absoluteOrientation;
	public Quaternion relativeOrientation;
	public Joint jointParent;
	public Joint jointChild;
	boolean _root = false;
	public Vector3f normal = new Vector3f(0,1,0);
	public Vector3f binormal = new Vector3f(1,0,0);
	
	public Joint(float x, float y, float z, Quaternion o, int state, Joint parent, int id){
		jointID = id;
		absolutePos = new Vector3f(x, y, z);
		if(KSkeleton.isEndJoint(id)) {
			absoluteOrientation = Quaternion.IDENTITY;
		} else {
			absoluteOrientation = o;
		}
		trackingState = state;
		if(parent != null) {
			jointParent = parent;
		} else {
			_root = true;
		}
	}
	
	public void addChild(float x, float y, float z, Quaternion o, int state, int id) {
		jointChild = new Joint(x, y, z, o, state, this, id);
	}
	
	public Quaternion getAbsOrientation() {
		return absoluteOrientation;
	}
	
	public Quaternion getRelativeOrientation() {
		relativeOrientation = jointParent.getAbsOrientation().inverse().multLocal(this.getAbsOrientation());
		return relativeOrientation;
	}

	public Vector3f getAbsPosition(){
		return absolutePos;
	}
	
	public Vector3f getRelativePosition(){
		
		return relativePos;
	}

	public float getAbsX(){
		return absolutePos.x;
	}

	public float getAbsY(){
		return absolutePos.y;
	}

	public float getAbsZ(){
		return absolutePos.z;
	}
	
	public float getRelativeX(){
		return relativePos.x;
	}

	public float getRelativeY(){
		return relativePos.y;
	}

	public float getRelativeZ(){
		return relativePos.z;
	}

	public int getState(){
		return trackingState;
	}
	
	public Vector3f getBinormal() {
		return this.getRelativeOrientation().mult(binormal);
	}
	
	public Vector3f getNormal() {
		return this.getRelativeOrientation().mult(normal);
	}
	
	public Float getBinormalAngle() {
		return this.getBinormal().angleBetween(binormal);
	}
	
	public Float getNormalAngle() {
		return this.getNormal().angleBetween(normal);
	}
	
}
