package org.myrobotlab.service;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

import edu.ufl.digitalworlds.j4k.Skeleton;
import org.myrobotlab.service.J4K;
import org.myrobotlab.service.InMoov;
import org.myrobotlab.service.VirtualArduino;
import org.python.modules.math;
import org.myrobotlab.service.ProgramAB;
import org.myrobotlab.service.NaturalReaderSpeech;

public class Bernard extends Service implements Observer {

	private static final long serialVersionUID = 1L;
	
	// MRL Services
	public J4K KinectViewer;
	public InMoov i01;
	public VirtualArduino v1;
	public VirtualArduino v2;
	public ProgramAB AliceBot;
	public NaturalReaderSpeech i01mouth;
	
	// Skeletal Mapping Variables
	public Skeleton kSkeleton;
	public boolean robotImitation = false;
	public boolean vinMoovStarted=false;
	//public mapping coordinates for virtual bernard
	public float robotCenterZ;
	public double bodyOrientation;
	public float[] jointOrientation;
	public double[] torsoOrientation;
	public double leftArmOrientation;
	public double leftForeArmOrientation;
	public double rightArmorientation;
	public double rightForeArmOrientation;
	public double[] shoulderLeftJoint;
	public double[] shoulderRightJoint;
	public double[] elbowLeftJoint;
	public double[] elbowRightJoint;
	public double[] wristLeftJoint;
	public double[] wristRightJoint;
	public double[] handLeftJoint;
	public double[] handRightJoint;
	public double shoulderDistance;
	public double shoulderElbowDistanceLeft;
	public double shoulderElbowDistanceRight;
	public double elbowWristDistanceLeft;
	public double elbowWristDistanceRight;
	
	
	
	// InMoov Variables Initialisation
	public String leftPort = "COM20";
	public String rightPort = "COM91";
	public String headPort = leftPort;
	public String voiceType = "Ryan";
	public String lang = "EN";
	public String ProgramABPath = "C:\\Users\\Joycob\\git\\myrobotlab\\ProgramAB";

	public final static Logger log = LoggerFactory.getLogger(Bernard.class);
	  
	public Bernard(String n) {
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

	    ServiceType meta = new ServiceType(Bernard.class.getCanonicalName());
	    meta.addDescription("Bernard");
	    meta.setAvailable(true); // false if you do not want it viewable in a gui
	    // add dependency if necessary
	    // meta.addDependency("org.coolproject", "1.0.0");
	    meta.addCategory("general");
	    meta.addPeer("KinectViewer", "J4K", "Kinect Viewer");
	    meta.addPeer("i01", "InMoov", "InMoov");
	    meta.addPeer("v1", "VirtualArduino", "Virtual Arduino 1: Left");
	    meta.addPeer("v2", "VirtualArduino", "Virtual Arduino 2: Right");
	    meta.addPeer("AliceBot", "ProgramAB", "Alice Chatbot");
	    meta.addPeer("i01.mouth", "NaturalReaderSpeech", "Speech Control");
	    return meta;
	}

	public static void main(String[] args) {
		try {
			LoggingFactory.init(Level.INFO);

			Bernard VirtualBernard = (Bernard) Runtime.start("Bernard", "Bernard");
			Runtime.start("gui", "SwingGui");
	      
	    } catch (Exception e) {
	    	Logging.logError(e);
	    }
	}

	public void startKinectViewer() {
		KinectViewer = (J4K) startPeer("KinectViewer");
	}
	
	public void startRobotImitation() {
		robotImitation = true; 
		startKinectViewer();
		//System.out.println("startRobotImitation");
		//if(method=="angular") {
			//offset for kinect is 0,0,220
		//}
	}
	
	public void addKinectObservers() {
		this.KinectViewer.sub.addObserver(this);
		System.out.println("Current Kinect Observers: "+String.valueOf(this.KinectViewer.sub.countObservers()));
	}
		
	public void startInMoov() {
		// Actual InMoov
	}
	
	public void startVinMoov() {
		try {
			i01 = (InMoov) startPeer("i01");
			//vinMoov = i01;
			v1 = (VirtualArduino) startPeer("v1");
			//VirtualArduino va1 = v1;
			v2 = (VirtualArduino) startPeer("v2");
			//VirtualArduino va2 = v2;
			v1.connect(leftPort);
			v2.connect(rightPort);
			i01.startAll(leftPort, rightPort);
			
			AliceBot = (ProgramAB) startPeer("AliceBot");
			//ProgramAB alice = AliceBot;
			AliceBot.setPath(ProgramABPath);
			i01mouth = (NaturalReaderSpeech) startPeer("i01.mouth");
			i01mouth.setVoice(voiceType);
			i01mouth.setLanguage(lang);
			
			i01.startVinMoov();
			//vinMoov.startIntegratedMovement();
			vinMoovStarted = true;
			
		} catch (Exception e) {
		      Logging.logError(e);
		}
	}
	/*
	public void getSkeleton(Skeleton sk) {
		this.kSkeleton = sk;
		if(this.robotImitation) {
			mapSkeletonToRobot();
		}
	}
	*/
	public void mapSkeletonToRobot() {
		// midpoint between SPINE_MID and SPINE_BASE (navel) as VinMoov starting center position
		robotCenterZ = (kSkeleton.get3DJointY(Skeleton.SPINE_MID)-kSkeleton.get3DJointY(Skeleton.SPINE_BASE))/2;
		// Head
		
		jointOrientation = kSkeleton.getJointOrientations();
		//System.out.println(90+jointOrientation[4*Skeleton.HEAD+2]*240);
		i01.head.rothead.moveTo(90-jointOrientation[4*Skeleton.NECK+3]*120);
		i01.head.neck.moveTo(90+jointOrientation[12+2]*240);
		//i01.head.rollNeck.moveTo(90+);
		// Left Arm
		shoulderLeftJoint = kSkeleton.get3DJoint(Skeleton.SHOULDER_LEFT);
		elbowLeftJoint = kSkeleton.get3DJoint(Skeleton.ELBOW_LEFT);
		wristLeftJoint = kSkeleton.get3DJoint(Skeleton.WRIST_LEFT);
		handLeftJoint = kSkeleton.get3DJoint(Skeleton.HAND_LEFT);
		shoulderElbowDistanceLeft = Math.sqrt(Math.pow(shoulderLeftJoint[0]-elbowLeftJoint[0],2)+Math.pow(shoulderLeftJoint[1]-elbowLeftJoint[1],2)+Math.pow(shoulderLeftJoint[2]-elbowLeftJoint[2],2));
		elbowWristDistanceLeft = Math.sqrt(Math.pow(elbowLeftJoint[0]-wristLeftJoint[0],2)+Math.pow(elbowLeftJoint[1]-wristLeftJoint[1],2)+Math.pow(elbowLeftJoint[2]-wristLeftJoint[2],2));
		//System.out.println(Math.toDegrees(Math.asin((elbowLeftJoint[0]-shoulderLeftJoint[0])/shoulderElbowDistanceLeft)));
		i01.leftArm.shoulder.moveTo(30+Math.toDegrees(Math.asin((shoulderLeftJoint[2]-elbowLeftJoint[2])/shoulderElbowDistanceLeft)));
		//i01.leftArm.rotate.moveTo();
		i01.leftArm.omoplate.moveTo(10+Math.toDegrees(Math.asin(Math.abs(shoulderLeftJoint[0]-elbowLeftJoint[0])/shoulderElbowDistanceLeft)));
		i01.leftArm.bicep.moveTo(-Math.toDegrees(Math.acos(Math.abs(elbowLeftJoint[2]-wristLeftJoint[2])/elbowWristDistanceLeft)));
		//i01.leftHand.wrist.moveTo(90+jointOrientation[4*Skeleton.HAND_LEFT+1]*180);
		
		// Right Arm
		shoulderRightJoint = kSkeleton.get3DJoint(Skeleton.SHOULDER_RIGHT);
		elbowRightJoint = kSkeleton.get3DJoint(Skeleton.ELBOW_RIGHT);
		wristRightJoint = kSkeleton.get3DJoint(Skeleton.WRIST_RIGHT);
		handRightJoint = kSkeleton.get3DJoint(Skeleton.HAND_RIGHT);
		shoulderElbowDistanceRight = Math.sqrt(Math.pow(shoulderRightJoint[0]-elbowRightJoint[0],2)+Math.pow(shoulderRightJoint[1]-elbowRightJoint[1],2)+Math.pow(shoulderRightJoint[2]-elbowRightJoint[2],2));
		elbowWristDistanceRight = Math.sqrt(Math.pow(elbowRightJoint[0]-wristRightJoint[0],2)+Math.pow(elbowRightJoint[1]-wristRightJoint[1],2)+Math.pow(elbowRightJoint[2]-wristRightJoint[2],2));
		//System.out.println(Math.toDegrees(Math.acos(Math.abs(elbowRightJoint[1]-wristRightJoint[1])/elbowWristDistanceRight)));
		//adjacent = 
		i01.rightArm.shoulder.moveTo(30+Math.toDegrees(Math.asin((shoulderRightJoint[2]-elbowRightJoint[2])/shoulderElbowDistanceRight)));
		i01.rightArm.omoplate.moveTo(10+Math.toDegrees(Math.asin(Math.abs(shoulderRightJoint[0]-elbowRightJoint[0])/shoulderElbowDistanceRight)));
		i01.rightArm.bicep.moveTo(Math.toDegrees(Math.acos(Math.abs(elbowRightJoint[1]-wristRightJoint[1])/elbowWristDistanceRight)));
		//i01.rightHand.wrist.moveTo(90-jointOrientation[4*Skeleton.HAND_RIGHT+1]*180);
		
		// Body
		bodyOrientation = kSkeleton.getBodyOrientation();
		// Torso Upper
		shoulderDistance = Math.sqrt(Math.pow(shoulderLeftJoint[0]-shoulderRightJoint[0],2)+Math.pow(shoulderLeftJoint[1]-shoulderRightJoint[1],2)+Math.pow(shoulderLeftJoint[2]-shoulderRightJoint[2],2));
		i01.torso.topStom.moveTo(90+Math.toDegrees(Math.asin(Math.abs(shoulderLeftJoint[1]-shoulderRightJoint[1])/shoulderDistance)));
		// Torso Mid
		torsoOrientation = kSkeleton.getTorsoOrientation();
		i01.torso.midStom.moveTo(90+torsoOrientation[0]*90);
		// Torso Low
		//i01.torso.lowStom.moveTo(90+torsoOrientation[1]*90);
		//double leftForeArmOrientation = kSkeleton.getLeftForearmTransform(transf, inv_transf);
		//double rightForeArmOrientation = kSkeleton.getRightForearmTransform(transf, inv_transf);
		//double leftArmOrientation = kSkeleton.getLeftArmTransform(transf, inv_transf);
		//double rightArmOrientation = kSkeleton.getRightArmTransform(transf, inv_transf);
	}
	@Override
	public void update(Observable subject, Object arg) {
		if(arg instanceof Skeleton) {
			this.kSkeleton = ((Skeleton)arg);
			if(robotImitation && vinMoovStarted) {
				mapSkeletonToRobot();
			}
		}

	}
	
}