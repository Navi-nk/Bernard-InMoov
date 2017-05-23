package org.myrobotlab.j4kdemo.kinectviewerapp;
import java.util.Observable;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class KinectSubject extends Observable {

	private Skeleton mySkeleton;
	/*
	public KinectSubject(Skeleton k) {
		this.mySkeleton = k;
	}
	*/
	public Skeleton getSkeleton() {
		return mySkeleton;
	}
	
	public void notify(Skeleton sk) {
		this.mySkeleton = sk;
		setChanged();
		notifyObservers(sk);
	}

}
