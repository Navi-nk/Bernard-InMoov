package org.myrobotlab.j4kdemo.kinectviewerapp;
import java.util.Observable;
import org.myrobotlab.j4kdemo.KSkeleton;

public class KinectSubject extends Observable {

	private KSkeleton mySkeleton;
	/*
	public KinectSubject(Skeleton k) {
		this.mySkeleton = k;
	}
	*/
	public KSkeleton getSkeleton() {
		return mySkeleton;
	}
	
	public void notify(KSkeleton sk) {
		this.mySkeleton = sk;
		setChanged();
		notifyObservers(sk);
	}

}
