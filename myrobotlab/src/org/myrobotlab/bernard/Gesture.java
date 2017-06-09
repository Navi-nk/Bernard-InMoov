package org.myrobotlab.bernard;

import org.myrobotlab.service.InMoov;

import java.io.Serializable;
import java.util.LinkedList;

import org.myrobotlab.j4kdemo.KSkeleton;
//import org.myrobotlab.j4kdemo.Joint;


public class Gesture implements Serializable {
	public int duration;
	// List containing skeletons across time
	public LinkedList<KSkeleton> skeletonList = new LinkedList<KSkeleton>();
	
	public void Gesture() {
		
	}
	
	public void run() {
		
	}
	
	public void stop() {
		
	}
	
	public void record(KSkeleton sk) {
		
	}
	
	public void load() {
		
	}
	
	public void store() {
		
	}
}
