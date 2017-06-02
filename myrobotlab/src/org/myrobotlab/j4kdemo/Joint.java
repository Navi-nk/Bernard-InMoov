package org.myrobotlab.j4kdemo;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;

public class Joint {
	
	public Vector3f absolutePos;
	public Vector3f relativePos;
	public int trackingState;
	public Quaternion absoluteOrientation;
	public Quaternion relativeOrientation;
	public Joint jointParent;
	public Joint jointChild;
	boolean _root = false;
	
	public Joint(float x, float y, float z, Quaternion o, int state, Joint parent){
		absolutePos = new Vector3f(x, y, z);
		absoluteOrientation = o;
		trackingState = state;
		if(parent != null) {
			jointParent = parent;
		} else {
			_root = true;
		}
	}
	
	public void addChild(float x, float y, float z, Quaternion o, int state, Joint parent) {
		jointChild = new Joint(x, y, z, o, state, parent);
	}
	
	public Quaternion getAbsOrientation() {
		return absoluteOrientation;
	}
	
	public Quaternion getRelativeOrientation() {
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
	
}
