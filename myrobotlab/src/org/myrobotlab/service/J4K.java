package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

import edu.ufl.digitalworlds.j4k.Skeleton;
import org.myrobotlab.j4kdemo.kinectviewerapp.KinectSubject;

public class J4K extends Service {

	private static final long serialVersionUID = 1L;
	public Skeleton sk;
	public KinectSubject sub;

	  public final static Logger log = LoggerFactory.getLogger(J4K.class);
	  
	  public J4K(String n) {
	    super(n);
	  }
	  /**
	   * This static method returns all the details of the class without it having
	   * to be constructed. It has description, categories, dependencies, and peer
	   * definitions.
	   * 
	   * @return ServiceType - returns all the data
	   * 
	   */
	  static public ServiceType getMetaData() {

	    ServiceType meta = new ServiceType(J4K.class.getCanonicalName());
	    meta.addDescription("Java 4 Kinect");
	    meta.setAvailable(true); // false if you do not want it viewable in a gui
	    // add dependency if necessary
	    // meta.addDependency("org.coolproject", "1.0.0");
	    meta.addCategory("general");
	    return meta;
	  }

	  public static void main(String[] args) {
	    try {
	      LoggingFactory.init(Level.INFO);

	      J4K kinectViewer = (J4K) Runtime.start("kinectViewer", "J4K");
	      Runtime.start("gui", "SwingGui");
	      
	      
	    } catch (Exception e) {
	      Logging.logError(e);
	    }
	  }

	public void getSkeleton(Skeleton sk) {
		this.sk = sk;
		//System.out.println(this.sk.toString());
	}
	
	public void updateSkeletonSubject(KinectSubject sub) {
		this.sub = sub;
		//System.out.println(this.sub.toString());
	}
	
}