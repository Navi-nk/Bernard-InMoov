package org.myrobotlab.service;

//import java.io.IOException;

import java.util.Observable;
import java.util.Observer;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.ServiceType;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;

import edu.ufl.digitalworlds.math.Geom;
import edu.ufl.digitalworlds.j4k.Skeleton;
import org.myrobotlab.service.J4K;
import org.myrobotlab.service.InMoov;
import org.myrobotlab.service.VirtualArduino;
//import org.python.modules.math;
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
	public boolean trackUserFace=false;
	//public mapping coordinates for virtual bernard
	public float robotCenterZ;
	
	public double bodyOrientation;
	
	public float[] jointOrientation;
	public float[] shoulderLeftAngle;
	public float[] elbowLeftAngle;
	public float[] wristLeftAngle;
	public float[] wristLeftRotateAngle;
	public float[] handLeftAngle;
	public float[] handTipLeftAngle;
	
	public float[] shoulderRightAngle;
	public float[] elbowRightAngle;
	public float[] wristRightAngle;
	public float[] wristRightRotateAngle;
	public float[] handRightAngle;
	public float[] handTipRightAngle;
	
	public double[] torsoOrientation;
	public double leftArmOrientation;
	public double leftForeArmOrientation;
	public double rightArmorientation;
	public double rightForeArmOrientation;
	public double[] spineShoulderJoint;
	public double[] shoulderLeftJoint;
	public double[] shoulderRightJoint;
	public double[] elbowLeftJoint;
	public double[] elbowRightJoint;
	public double[] wristLeftJoint;
	public double[] wristRightJoint;
	public double[] handLeftJoint;
	public double[] handRightJoint;
	public double[] handTipLeftJoint;
	public double[] handTipRightJoint;
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
		
		jointOrientation = kSkeleton.getJointOrientations();
		bodyOrientation = kSkeleton.getBodyOrientation();
		torsoOrientation = kSkeleton.getTorsoOrientation();
		//double t[] = Geom.identity4();
		//double inv_t[] = Geom.identity4();
		//System.out.println(t.equals(inv_t));
		//double torsoTransform = kSkeleton.getTorsoTransform(t, inv_t);
		//double headTransform = kSkeleton.getHeadTransform(t, inv_t);
		//double fixHeadTransform = kSkeleton.getFixHeadTransform(t, inv_t);
		//double shoulderTransform = kSkeleton.getShoulderTransform(t, inv_t);
		//double betweenHandsTransform = kSkeleton.getBetweenHandsTransform(t, inv_t);
		//double rightArmTransform = kSkeleton.getRightArmTransform(t, inv_t);
		//double rightForearmTransform = kSkeleton.getRightArmTransform(t, inv_t);
		//double leftArmTransform = kSkeleton.getLeftArmTransform(t, inv_t);
		//double leftForearmTransform = kSkeleton.getLeftArmTransform(t, inv_t);
		//System.out.println(leftArmTransform);
		
		// midpoint between SPINE_MID and SPINE_BASE (navel) as VinMoov starting center position
		robotCenterZ = (kSkeleton.get3DJointY(Skeleton.SPINE_MID)-kSkeleton.get3DJointY(Skeleton.SPINE_BASE))/2;	
		spineShoulderJoint = kSkeleton.get3DJoint(Skeleton.SPINE_SHOULDER);
		
		// Head
		if(trackUserFace != true) {
			//System.out.println(90+jointOrientation[4*Skeleton.HEAD+2]*240);
			//i01.head.rothead.moveTo(90-jointOrientation[4*Skeleton.NECK+3]*120);
			//i01.head.neck.moveTo(90+jointOrientation[12+2]*240);
			//i01.head.rollNeck.moveTo(90+);
		}
		
		// Left Arm
		shoulderLeftJoint = kSkeleton.get3DJoint(Skeleton.SHOULDER_LEFT);
		elbowLeftJoint = kSkeleton.get3DJoint(Skeleton.ELBOW_LEFT);
		wristLeftJoint = kSkeleton.get3DJoint(Skeleton.WRIST_LEFT);
		handLeftJoint = kSkeleton.get3DJoint(Skeleton.HAND_LEFT);
		handTipLeftJoint = kSkeleton.get3DJoint(Skeleton.HAND_TIP_LEFT);
		shoulderElbowDistanceLeft = Math.sqrt(Math.pow(shoulderLeftJoint[0]-elbowLeftJoint[0],2)+Math.pow(shoulderLeftJoint[1]-elbowLeftJoint[1],2)+Math.pow(shoulderLeftJoint[2]-elbowLeftJoint[2],2));
		elbowWristDistanceLeft = Math.sqrt(Math.pow(elbowLeftJoint[0]-wristLeftJoint[0],2)+Math.pow(elbowLeftJoint[1]-wristLeftJoint[1],2)+Math.pow(elbowLeftJoint[2]-wristLeftJoint[2],2));
		
		Quaternion shoulderLeftQ = new Quaternion(jointOrientation[4*Skeleton.SHOULDER_LEFT],jointOrientation[4*Skeleton.SHOULDER_LEFT+1],jointOrientation[4*Skeleton.SHOULDER_LEFT+2],jointOrientation[4*Skeleton.SHOULDER_LEFT+3]);
		shoulderLeftAngle = shoulderLeftQ.toAngles(shoulderLeftAngle);
		Quaternion elbowLeftQ = new Quaternion(jointOrientation[4*Skeleton.ELBOW_LEFT],jointOrientation[4*Skeleton.ELBOW_LEFT+1],jointOrientation[4*Skeleton.ELBOW_LEFT+2],jointOrientation[4*Skeleton.ELBOW_LEFT+3]);
		elbowLeftAngle = elbowLeftQ.toAngles(elbowLeftAngle);
		Quaternion wristLeftQ = new Quaternion(jointOrientation[4*Skeleton.WRIST_LEFT],jointOrientation[4*Skeleton.WRIST_LEFT+1],jointOrientation[4*Skeleton.WRIST_LEFT+2],jointOrientation[4*Skeleton.WRIST_LEFT+3]);
		wristLeftAngle = wristLeftQ.toAngles(wristLeftAngle);
		Quaternion wristLeftRelativeQ = elbowLeftQ.inverse().mult(wristLeftQ);
		wristLeftRotateAngle = wristLeftRelativeQ.toAngles(wristLeftRotateAngle);
		Quaternion handLeftQ = new Quaternion(jointOrientation[4*Skeleton.HAND_LEFT],jointOrientation[4*Skeleton.HAND_LEFT+1],jointOrientation[4*Skeleton.HAND_LEFT+2],jointOrientation[4*Skeleton.HAND_LEFT+3]);
		handLeftAngle = handLeftQ.toAngles(handLeftAngle);
		Quaternion handTipLeftQ = new Quaternion(jointOrientation[4*Skeleton.HAND_TIP_LEFT],jointOrientation[4*Skeleton.HAND_TIP_LEFT+1],jointOrientation[4*Skeleton.HAND_TIP_LEFT+2],jointOrientation[4*Skeleton.HAND_TIP_LEFT+3]);
		handTipLeftAngle = handTipLeftQ.toAngles(handTipLeftAngle);
		
		Vector3f spineShoulderVec = new Vector3f((float)spineShoulderJoint[0],(float)spineShoulderJoint[1],(float)spineShoulderJoint[2]);
		Vector3f shoulderLeftVec = new Vector3f((float)shoulderLeftJoint[0],(float)shoulderLeftJoint[1],(float)shoulderLeftJoint[2]);
		Vector3f elbowLeftVec = new Vector3f((float)elbowLeftJoint[0],(float)elbowLeftJoint[1],(float)elbowLeftJoint[2]);
		Vector3f wristLeftVec = new Vector3f((float)wristLeftJoint[0],(float)wristLeftJoint[1],(float)wristLeftJoint[2]);
		Vector3f handLeftVec = new Vector3f((float)handLeftJoint[0],(float)handLeftJoint[1],(float)handLeftJoint[2]);
		Vector3f handTipLeftVec = new Vector3f((float)handTipLeftJoint[0],(float)handTipLeftJoint[1],(float)handTipLeftJoint[2]);
		
		Vector3f spineShoulderLeftVec = shoulderLeftVec.subtract(spineShoulderVec);
		Vector3f shoulderElbowLeftVec = elbowLeftVec.subtract(shoulderLeftVec);
		//Vector3f spineElbowLeftVec = shoulderLeftVec.add(shoulderElbowLeftVec);
		Vector3f elbowWristLeftVec = wristLeftVec.subtract(elbowLeftVec);
		Vector3f handHandTipLeftVec = handTipLeftVec.subtract(handLeftVec);
		spineShoulderLeftVec = spineShoulderLeftVec.normalizeLocal();
		shoulderElbowLeftVec = shoulderElbowLeftVec.normalizeLocal();
		elbowWristLeftVec = elbowWristLeftVec.normalizeLocal();
		handHandTipLeftVec = handHandTipLeftVec.normalizeLocal();
		
		//float degrees = Math.round(Math.toDegrees(FastMath.atan2(shoulderElbowLeftVec.getY(),shoulderElbowLeftVec.getX())));
		//i01.leftArm.omoplate.moveTo(degrees>90?270-degrees:-degrees-90);
		
		//i01.leftArm.omoplate.moveTo(Math.toDegrees(Math.asin(Math.abs(shoulderLeftJoint[0]-elbowLeftJoint[0])/shoulderElbowDistanceLeft)));
		float degrees = Math.round(Math.toDegrees(FastMath.atan2(shoulderElbowLeftVec.getZ(),shoulderElbowLeftVec.getY())));
		//i01.leftArm.shoulder.moveTo(30+(degrees>0?degrees-180:degrees+180));
		degrees = (float) Math.toDegrees(elbowLeftAngle[1]);
		//i01.leftArm.rotate.moveTo(Math.round(((degrees<-90)?360+degrees:degrees)+torsoOrientation[0]*90));
		//i01.leftArm.bicep.moveTo(Math.round(Math.toDegrees(elbowWristLeftVec.angleBetween(shoulderElbowLeftVec))));
		
		// for wrist, need to use all three rotations
		
		degrees = (float) Math.toDegrees(wristLeftRotateAngle[2]);
		//i01.leftHand.wrist.moveTo(degrees);
		//System.out.println(String.valueOf(Math.round(Math.toDegrees(wristLeftRotateAngle[0])))+","+String.valueOf(Math.round(Math.toDegrees(wristLeftRotateAngle[1])))+","+String.valueOf(Math.round(Math.toDegrees(wristLeftRotateAngle[2]))));
		
				
		// Right Arm
		shoulderRightJoint = kSkeleton.get3DJoint(Skeleton.SHOULDER_RIGHT);
		elbowRightJoint = kSkeleton.get3DJoint(Skeleton.ELBOW_RIGHT);
		wristRightJoint = kSkeleton.get3DJoint(Skeleton.WRIST_RIGHT);
		handRightJoint = kSkeleton.get3DJoint(Skeleton.HAND_RIGHT);
		handTipRightJoint = kSkeleton.get3DJoint(Skeleton.HAND_TIP_LEFT);
		shoulderElbowDistanceRight = Math.sqrt(Math.pow(shoulderRightJoint[0]-elbowRightJoint[0],2)+Math.pow(shoulderRightJoint[1]-elbowRightJoint[1],2)+Math.pow(shoulderRightJoint[2]-elbowRightJoint[2],2));
		elbowWristDistanceRight = Math.sqrt(Math.pow(elbowRightJoint[0]-wristRightJoint[0],2)+Math.pow(elbowRightJoint[1]-wristRightJoint[1],2)+Math.pow(elbowRightJoint[2]-wristRightJoint[2],2));
		
		Quaternion shoulderRightQ = new Quaternion(jointOrientation[4*Skeleton.SHOULDER_RIGHT],jointOrientation[4*Skeleton.SHOULDER_RIGHT+1],jointOrientation[4*Skeleton.SHOULDER_RIGHT+2],jointOrientation[4*Skeleton.SHOULDER_RIGHT+3]);
		shoulderRightAngle = shoulderRightQ.toAngles(shoulderRightAngle);
		Quaternion elbowRightQ = new Quaternion(jointOrientation[4*Skeleton.ELBOW_RIGHT],jointOrientation[4*Skeleton.ELBOW_RIGHT+1],jointOrientation[4*Skeleton.ELBOW_RIGHT+2],jointOrientation[4*Skeleton.ELBOW_RIGHT+3]);
		elbowRightAngle = elbowRightQ.toAngles(elbowRightAngle);
		Quaternion wristRightQ = new Quaternion(jointOrientation[4*Skeleton.WRIST_RIGHT],jointOrientation[4*Skeleton.WRIST_RIGHT+1],jointOrientation[4*Skeleton.WRIST_RIGHT+2],jointOrientation[4*Skeleton.WRIST_RIGHT+3]);
		wristRightAngle = wristRightQ.toAngles(wristRightAngle);
		Quaternion wristRightRelativeQ = elbowRightQ.inverseLocal().mult(wristRightQ);
		wristRightRotateAngle = wristRightRelativeQ.toAngles(wristRightRotateAngle);
		Quaternion handRightQ = new Quaternion(jointOrientation[4*Skeleton.HAND_RIGHT],jointOrientation[4*Skeleton.HAND_RIGHT+1],jointOrientation[4*Skeleton.HAND_RIGHT+2],jointOrientation[4*Skeleton.HAND_RIGHT+3]);
		handRightAngle = handRightQ.toAngles(handRightAngle);
		
		
		Vector3f shoulderRightVec = new Vector3f((float)shoulderRightJoint[0],(float)shoulderRightJoint[1],(float)shoulderRightJoint[2]);
		Vector3f elbowRightVec = new Vector3f((float)elbowRightJoint[0],(float)elbowRightJoint[1],(float)elbowRightJoint[2]);
		Vector3f wristRightVec = new Vector3f((float)wristRightJoint[0],(float)wristRightJoint[1],(float)wristRightJoint[2]);
		Vector3f handRightVec = new Vector3f((float)handRightJoint[0],(float)handRightJoint[1],(float)handRightJoint[2]);
		Vector3f handTipRightVec = new Vector3f((float)handTipRightJoint[0],(float)handTipRightJoint[1],(float)handTipRightJoint[2]);
		
		Vector3f spineShoulderRightVec = shoulderRightVec.subtract(spineShoulderVec);
		Vector3f shoulderElbowRightVec = elbowRightVec.subtract(shoulderRightVec);
		//Vector3f spineElbowLeftVec = shoulderLeftVec.add(shoulderElbowLeftVec);
		Vector3f elbowWristRightVec = wristRightVec.subtract(elbowRightVec);
		Vector3f handHandTipRightVec = handTipRightVec.subtract(handRightVec);
		spineShoulderRightVec = spineShoulderRightVec.normalizeLocal();
		shoulderElbowRightVec = shoulderElbowRightVec.normalizeLocal();
		elbowWristRightVec = elbowWristRightVec.normalizeLocal();
		handHandTipRightVec = handHandTipRightVec.normalizeLocal();
		
		
		
		//i01.rightArm.omoplate.moveTo(10+Math.toDegrees(Math.asin(Math.abs(shoulderRightJoint[0]-elbowRightJoint[0])/shoulderElbowDistanceRight)));
		degrees = Math.round(Math.toDegrees(FastMath.atan2(shoulderElbowRightVec.getZ(),shoulderElbowRightVec.getY())));
		//i01.rightArm.shoulder.moveTo(30+(degrees>0?degrees-180:degrees+180));
		
		//System.out.println(degrees);
		
		degrees = (float) -Math.toDegrees(elbowRightAngle[1]);
		//i01.rightArm.rotate.moveTo(Math.round(((degrees<-90)?360+degrees:degrees)-torsoOrientation[0]*90));
		//i01.rightArm.bicep.moveTo(Math.round(Math.toDegrees(elbowWristRightVec.angleBetween(shoulderElbowRightVec))));
		degrees = (float) Math.toDegrees(wristRightAngle[1]);
		//i01.rightHand.wrist.moveTo(90-degrees);
		
		
		
		// Body
		
		// Torso Upper
		shoulderDistance = Math.sqrt(Math.pow(shoulderLeftJoint[0]-shoulderRightJoint[0],2)+Math.pow(shoulderLeftJoint[1]-shoulderRightJoint[1],2)+Math.pow(shoulderLeftJoint[2]-shoulderRightJoint[2],2));
		//i01.torso.topStom.moveTo(90+Math.toDegrees(Math.asin(Math.abs(shoulderLeftJoint[1]-shoulderRightJoint[1])/shoulderDistance)));
		// Torso Mid
		
		//i01.torso.midStom.moveTo(90+torsoOrientation[0]*90);
		// Torso Low
		//i01.torso.lowStom.moveTo(90+torsoOrientation[1]*90);
		//double leftForeArmOrientation = kSkeleton.getLeftForearmTransform(transf, inv_transf);
		//double rightForeArmOrientation = kSkeleton.getRightForearmTransform(transf, inv_transf);
		//double leftArmOrientation = kSkeleton.getLeftArmTransform(transf, inv_transf);
		//double rightArmOrientation = kSkeleton.getRightArmTransform(transf, inv_transf);
		
	}
	
	public void startFaceTracking() {
		System.out.println("Hello");
	}
	
	
	@Override
	public void update(Observable subject, Object arg) {
		if(arg instanceof Skeleton) {
			//if(robotImitation && vinMoovStarted) {
			if(robotImitation) {
				this.kSkeleton = ((Skeleton)arg);
				mapSkeletonToRobot();
			}
		}

	}
	
}