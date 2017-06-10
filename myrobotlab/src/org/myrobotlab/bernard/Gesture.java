package org.myrobotlab.bernard;

import org.myrobotlab.service.InMoov;

//import org.json.*;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.myrobotlab.j4kdemo.KSkeleton;
//import org.myrobotlab.j4kdemo.Joint;


public class Gesture implements Serializable {
	public int duration=60;
	// List containing skeletons across time
	public LinkedList<KSkeleton> skeletonList = new LinkedList<KSkeleton>();
	//public JSONObject storage = new JSONObject();
	public int recordedFrames;
	public boolean finished;
	public String GestureName;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;
	
	public Gesture() {

	}
	
	public void setLength(int len) {
		duration = len;
	}
	
	public void record(KSkeleton sk) {
		if(recordedFrames < duration) {
			skeletonList.add(sk);
			recordedFrames++;
			System.out.println(recordedFrames);
		} else if(recordedFrames == duration) {	
			store(GestureName);
			finished = true;
		}
	}
	
	public void load(String name) throws Exception {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("gestures/" + name + ".dat"));
			skeletonList = (LinkedList<KSkeleton>) ois.readObject();
			ois.close();
			//oos.close();
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
		      oos.close();
		      System.out.println("stored");
		    } catch (Exception e) {
		      System.out.println("Problem serializing: " + e);
		    }
	}
	
	public Boolean findGesture(String name) {
		
		File dir = new File("gestures");
		File[] matches = dir.listFiles(new FilenameFilter()
		{
		  public boolean accept(File dir, String filename)
		  {
		     return filename.equals(name+".dat");
		  }
		});
		
		return matches.length == 1 ;
	}
}
