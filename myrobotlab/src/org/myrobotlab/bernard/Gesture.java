package org.myrobotlab.bernard;

import org.myrobotlab.service.InMoov;

//import org.json.*;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.myrobotlab.j4kdemo.KSkeleton;
//import org.myrobotlab.j4kdemo.Joint;


public class Gesture implements Serializable {
	public int duration;
	// List containing skeletons across time
	public LinkedList<KSkeleton> skeletonList = new LinkedList<KSkeleton>();
	//public JSONObject storage = new JSONObject();
	public int frames;
	public int recordedFrames;
	public boolean finished;
	
	public Gesture(int len) {
		setLength(len);
	}
	
	public void setLength(int len) {
		frames = len;
	}
	
	public void run() {
		
	}
	
	public void stop() {
		
	}
	
	public void record(KSkeleton sk) {
		if(recordedFrames < frames) {
			skeletonList.add(sk);
			recordedFrames++;
		}
		if(recordedFrames == frames) {
			store();
		}
	}
	
	public void load() {
		
	}
	
	public void store() {
		try {
		      FileOutputStream output = new FileOutputStream("skeleton.sk");
		      ObjectOutputStream oos = new ObjectOutputStream(output);
		      oos.writeObject();
		      oos.flush();
		    } catch (Exception e) {
		      System.out.println("Problem serializing: " + e);
		    }
	}
}
