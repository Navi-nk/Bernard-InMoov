package org.myrobotlab.bernard;

import org.myrobotlab.service.InMoov;

//import org.json.*;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	public int recordedFrames;
	public boolean finished;
	public String GestureName = "swing";
	
	public Gesture() {

	}
	
	public void setLength(int len) {
		duration = len;
	}
	
	public void record(KSkeleton sk) {
		if(recordedFrames < duration) {
			skeletonList.add(sk);
			recordedFrames++;
		}
		if(recordedFrames == duration) {
			store(GestureName);
		}
	}
	
	public void load(String name) throws Exception {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("gestures/" + name + ".dat"));
			skeletonList = (LinkedList<KSkeleton>) ois.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void store(String name) {
		try {
		      FileOutputStream output = new FileOutputStream("gestures/" + name + ".dat");
		      ObjectOutputStream oos = new ObjectOutputStream(output);
		      oos.writeObject(skeletonList);
		      oos.flush();
		    } catch (Exception e) {
		      System.out.println("Problem serializing: " + e);
		    }
	}
}
