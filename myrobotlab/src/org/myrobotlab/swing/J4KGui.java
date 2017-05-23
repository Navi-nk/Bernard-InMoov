package org.myrobotlab.swing;

/* Adapted from:
 * 
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or research purposes, 
 * please cite the following article that introduced this Java library: 
 * 
 * A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
 * and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
 * October 2013, Vol. 43(5), Pages: 1347-1356. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.myrobotlab.j4kdemo.kinectviewerapp.Kinect;
import org.myrobotlab.j4kdemo.kinectviewerapp.ViewerPanel3D;
import edu.ufl.digitalworlds.j4k.Skeleton;

import edu.ufl.digitalworlds.j4k.J4K1;
import edu.ufl.digitalworlds.j4k.J4K2;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.service.SwingGui;
import org.myrobotlab.service.J4K;
import org.myrobotlab.service.Runtime;
import org.slf4j.Logger;


public class J4KGui extends ServiceGui implements ActionListener, ChangeListener, Observer {
	
	static final long serialVersionUID = 1L;
	public final static Logger log = LoggerFactory.getLogger(J4KGui.class);
	DecimalFormat df = new DecimalFormat("#.###");
	
	public Kinect myKinect = new Kinect();
	public ViewerPanel3D mainPanel = new ViewerPanel3D();
	JSlider elevation_angle = new JSlider();
	JCheckBox near_mode = new JCheckBox("Near Mode");
	JCheckBox seated_skeleton = new JCheckBox("Seated Skeleton");
	JCheckBox show_infrared = new JCheckBox("Infrared");
	JButton turn_off = new JButton("Turn Off");
	JComboBox depth_resolution = new JComboBox();
	JComboBox video_resolution = new JComboBox();
	JCheckBox show_video = new JCheckBox("Show Texture");
	JCheckBox mask_players = new JCheckBox("Mask Players");
	JLabel accelerometer = new JLabel("0,0,0");
	
	JPanel eastPanel = new JPanel();
	
	public Skeleton[] skeletonData;
	
	JCheckBox joint_orientation = new JCheckBox("Joint Orientation");
	JLabel spine_base = new JLabel("0,0,0");
	JLabel spine_mid = new JLabel("0,0,0");
	JLabel neck = new JLabel("0,0,0");
	JLabel head = new JLabel("0,0,0");
	JLabel shoulder_left = new JLabel("0,0,0");
	JLabel elbow_left = new JLabel("0,0,0");
	JLabel wrist_left = new JLabel("0,0,0");
	JLabel hand_left = new JLabel("0,0,0");
	JLabel shoulder_right = new JLabel("0,0,0");
	JLabel elbow_right = new JLabel("0,0,0");
	JLabel wrist_right = new JLabel("0,0,0");
	JLabel hand_right = new JLabel("0,0,0");
	JLabel hip_left = new JLabel("0,0,0");
	JLabel knee_left = new JLabel("0,0,0");
	JLabel ankle_left = new JLabel("0,0,0");
	JLabel foot_left = new JLabel("0,0,0");
	JLabel hip_right = new JLabel("0,0,0");
	JLabel knee_right = new JLabel("0,0,0");
	JLabel ankle_right = new JLabel("0,0,0");
	JLabel foot_right = new JLabel("0,0,0");
	JLabel spine_shoulder = new JLabel("0,0,0");
	JLabel hand_tip_left = new JLabel("0,0,0");
	JLabel thumb_left = new JLabel("0,0,0");
	JLabel hand_tip_right = new JLabel("0,0,0");
	JLabel thumb_right = new JLabel("0,0,0");
	JLabel body_orientation = new JLabel("0,0,0");
	JLabel torso_orientation = new JLabel("0,0,0");
	//JLabel relative_NUI_coords = new JLabel("0,0,0");
	
	JPanel controls = new JPanel();
	
	public J4KGui(final String boundServiceName, final SwingGui myService) {

	    super(boundServiceName, myService);
	    J4K boundService = (J4K) Runtime.getService(boundServiceName);
	    
	    if(!myKinect.start(Kinect.DEPTH| Kinect.COLOR |Kinect.SKELETON |Kinect.XYZ|Kinect.PLAYER_INDEX))
		{
	    	log.info("ERROR: The Kinect device could not be initialized. Check if the Microsoft's Kinect SDK was succesfully installed on this computer or check if the Kinect is plugged into a power outlet. Otherwise, check if the Kinect is connected to a USB port of this computer.");
		}
	   
	    near_mode.setSelected(false);
		near_mode.addActionListener(this);
		if(myKinect.getDeviceType()!=J4KSDK.MICROSOFT_KINECT_1) near_mode.setEnabled(false);
		
		seated_skeleton.addActionListener(this);
		if(myKinect.getDeviceType()!=J4KSDK.MICROSOFT_KINECT_1) seated_skeleton.setEnabled(false);
		
		// Elevation Angle
		elevation_angle.setMinimum(-27);
		elevation_angle.setMaximum(27);
		elevation_angle.setValue((int)myKinect.getElevationAngle());
		elevation_angle.setToolTipText("Elevation Angle ("+elevation_angle.getValue()+" degrees)");
		elevation_angle.addChangeListener(this);
		
		turn_off.addActionListener(this);
		
		// Add Depth Resolution
		if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_1) {
			depth_resolution.addItem("80x60");
			depth_resolution.addItem("320x240");
			depth_resolution.addItem("640x480");
			depth_resolution.setSelectedIndex(1);
		} else if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_2) {
			depth_resolution.addItem("512x424");
			depth_resolution.setSelectedIndex(0);
		}
		depth_resolution.addActionListener(this);
		
		// Add Video Resolution
		if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_1)
		{
			video_resolution.addItem("640x480");
			video_resolution.addItem("1280x960");
			video_resolution.setSelectedIndex(0);
		}
		else if(myKinect.getDeviceType()==J4KSDK.MICROSOFT_KINECT_2)
		{
			video_resolution.addItem("1920x1080"); 
			video_resolution.setSelectedIndex(0);
		}
		video_resolution.addActionListener(this);
		
		//Infrared
		show_infrared.setSelected(false);
		show_infrared.addActionListener(this);
		
		//Show Texture
		show_video.setSelected(false);
		show_video.addActionListener(this);
		
		//Mask Players
		mask_players.setSelected(false);
		mask_players.addActionListener(this);
		
		//Show Joint Orientation
		joint_orientation.setSelected(false);
		joint_orientation.addActionListener(this);
		
		controls.setLayout(new GridLayout(0, 6));
		controls.add(new JLabel("Depth Stream:"));
		controls.add(depth_resolution);
		controls.add(mask_players);
		controls.add(near_mode);
		controls.add(seated_skeleton);
		controls.add(accelerometer);
		controls.add(new JLabel("Texture Stream:"));
		controls.add(video_resolution);
		controls.add(show_infrared);
		controls.add(show_video);
		controls.add(elevation_angle);
		controls.add(turn_off);
		
		mainPanel.setShowVideo(false);
		myKinect.setViewer(mainPanel);
		myKinect.setLabel(accelerometer);
		
		myKinect.subject.addObserver(this);
		send("updateSkeletonSubject", myKinect.subject);
		
		eastPanel.setLayout(new GridLayout(30,1));
		eastPanel.add(new JLabel("Joint Positions X,Y,Z"));
		eastPanel.add(joint_orientation);
		eastPanel.add(new JLabel("Spine Base:"));
		
		eastPanel.add(spine_base);
		eastPanel.add(new JLabel("Spine Mid:"));
		eastPanel.add(spine_mid);
		eastPanel.add(new JLabel("Neck:"));
		eastPanel.add(neck);
		eastPanel.add(new JLabel("Head:"));
		eastPanel.add(head);
		eastPanel.add(new JLabel("Shoulder Left:"));
		eastPanel.add(shoulder_left);
		eastPanel.add(new JLabel("Elbow Left:"));
		eastPanel.add(elbow_left);
		eastPanel.add(new JLabel("Wrist Left:"));
		eastPanel.add(wrist_left);
		eastPanel.add(new JLabel("Hand Left:"));
		eastPanel.add(hand_left);
		eastPanel.add(new JLabel("Shoulder Right:"));
		eastPanel.add(shoulder_right);
		eastPanel.add(new JLabel("Elbow Right:"));
		eastPanel.add(elbow_right);
		eastPanel.add(new JLabel("Wrist Right:"));
		eastPanel.add(wrist_right);
		eastPanel.add(new JLabel("Hand Right:"));
		eastPanel.add(hand_right);
		eastPanel.add(new JLabel("Hip Left:"));
		eastPanel.add(hip_left);
		eastPanel.add(new JLabel("Knee Left:"));
		eastPanel.add(knee_left);
		eastPanel.add(new JLabel("Ankle Left:"));
		eastPanel.add(ankle_left);
		eastPanel.add(new JLabel("Foot Left:"));
		eastPanel.add(foot_left);
		eastPanel.add(new JLabel("Hip Right:"));
		eastPanel.add(hip_right);
		eastPanel.add(new JLabel("Knee Right:"));
		eastPanel.add(knee_right);
		eastPanel.add(new JLabel("Ankle Right:"));
		eastPanel.add(ankle_right);
		eastPanel.add(new JLabel("Foot Right:"));
		eastPanel.add(foot_right);
		eastPanel.add(new JLabel("Spine Shoulder:"));
		eastPanel.add(spine_shoulder);
		eastPanel.add(new JLabel("Hand Tip Left:"));
		eastPanel.add(hand_tip_left);
		eastPanel.add(new JLabel("Thumb Left:"));
		eastPanel.add(thumb_left);
		eastPanel.add(new JLabel("Hand Tip Right:"));
		eastPanel.add(hand_tip_right);
		eastPanel.add(new JLabel("Thumb Right:"));
		eastPanel.add(thumb_right);
		eastPanel.add(new JLabel("Body Orientation:"));
		eastPanel.add(body_orientation);
		eastPanel.add(new JLabel("Torso Orientation:"));
		eastPanel.add(torso_orientation);
		//eastPanel.add(new JLabel("RelativeNUICoords:"));
		//eastPanel.add(relative_NUI_coords);
		
		
		display.setLayout(new BorderLayout());		
		display.add(mainPanel, BorderLayout.CENTER);
		display.add(controls, BorderLayout.SOUTH);
		display.add(eastPanel, BorderLayout.EAST);
		
	}
	  
	@Override
	public void update(Observable subject, Object arg) {
		
		if(arg instanceof Skeleton) {
			send("getSkeleton",arg);
		}
		// Update Gui with Skeleton Joint Positions and Orientations
		if(arg instanceof Skeleton && !joint_orientation.isSelected()) {
			double spine_base_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.SPINE_BASE);
			spine_base.setText(String.valueOf(df.format(spine_base_joint[0]))+","+String.valueOf(df.format(spine_base_joint[1]))+","+String.valueOf(df.format(spine_base_joint[2])));
			double spine_mid_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.SPINE_MID);
			spine_mid.setText(String.valueOf(df.format(spine_mid_joint[0]))+","+String.valueOf(df.format(spine_mid_joint[1]))+","+String.valueOf(df.format(spine_mid_joint[2])));
			double neck_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.NECK);
			neck.setText(String.valueOf(df.format(neck_joint[0]))+","+String.valueOf(df.format(neck_joint[1]))+","+String.valueOf(df.format(neck_joint[2])));
			double head_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.HEAD);
			head.setText(String.valueOf(df.format(head_joint[0]))+","+String.valueOf(df.format(head_joint[1]))+","+String.valueOf(df.format(head_joint[2])));
			double shoulder_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.SHOULDER_LEFT);
			shoulder_left.setText(String.valueOf(df.format(shoulder_left_joint[0]))+","+String.valueOf(df.format(shoulder_left_joint[1]))+","+String.valueOf(df.format(shoulder_left_joint[2])));
			double elbow_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.ELBOW_LEFT);
			elbow_left.setText(String.valueOf(df.format(elbow_left_joint[0]))+","+String.valueOf(df.format(elbow_left_joint[1]))+","+String.valueOf(df.format(elbow_left_joint[2])));
			double wrist_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.WRIST_LEFT);
			wrist_left.setText(String.valueOf(df.format(wrist_left_joint[0]))+","+String.valueOf(df.format(wrist_left_joint[1]))+","+String.valueOf(df.format(wrist_left_joint[2])));
			double hand_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.HAND_LEFT);
			hand_left.setText(String.valueOf(df.format(hand_left_joint[0]))+","+String.valueOf(df.format(hand_left_joint[1]))+","+String.valueOf(df.format(hand_left_joint[2])));
			double shoulder_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.SHOULDER_RIGHT);
			shoulder_right.setText(String.valueOf(df.format(shoulder_right_joint[0]))+","+String.valueOf(df.format(shoulder_right_joint[1]))+","+String.valueOf(df.format(shoulder_right_joint[2])));
			double elbow_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.ELBOW_RIGHT);
			elbow_right.setText(String.valueOf(df.format(elbow_right_joint[0]))+","+String.valueOf(df.format(elbow_right_joint[1]))+","+String.valueOf(df.format(elbow_right_joint[2])));
			double wrist_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.WRIST_RIGHT);
			wrist_right.setText(String.valueOf(df.format(wrist_right_joint[0]))+","+String.valueOf(df.format(wrist_right_joint[1]))+","+String.valueOf(df.format(wrist_right_joint[2])));
			double hand_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.HAND_RIGHT);
			hand_right.setText(String.valueOf(df.format(hand_right_joint[0]))+","+String.valueOf(df.format(hand_right_joint[1]))+","+String.valueOf(df.format(hand_right_joint[2])));
			double hip_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.HIP_LEFT);
			hip_left.setText(String.valueOf(df.format(hip_left_joint[0]))+","+String.valueOf(df.format(hip_left_joint[1]))+","+String.valueOf(df.format(hip_left_joint[2])));
			double knee_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.KNEE_LEFT);
			knee_left.setText(String.valueOf(df.format(knee_left_joint[0]))+","+String.valueOf(df.format(knee_left_joint[1]))+","+String.valueOf(df.format(knee_left_joint[2])));
			double ankle_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.ANKLE_LEFT);
			ankle_left.setText(String.valueOf(df.format(ankle_left_joint[0]))+","+String.valueOf(df.format(ankle_left_joint[1]))+","+String.valueOf(df.format(ankle_left_joint[2])));
			double foot_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.FOOT_LEFT);
			foot_left.setText(String.valueOf(df.format(foot_left_joint[0]))+","+String.valueOf(df.format(foot_left_joint[1]))+","+String.valueOf(df.format(foot_left_joint[2])));
			double hip_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.HIP_RIGHT);
			hip_right.setText(String.valueOf(df.format(hip_right_joint[0]))+","+String.valueOf(df.format(hip_right_joint[1]))+","+String.valueOf(df.format(hip_right_joint[2])));
			double knee_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.KNEE_RIGHT);
			knee_right.setText(String.valueOf(df.format(knee_right_joint[0]))+","+String.valueOf(df.format(knee_right_joint[1]))+","+String.valueOf(df.format(knee_right_joint[2])));
			double ankle_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.ANKLE_RIGHT);
			ankle_right.setText(String.valueOf(df.format(ankle_right_joint[0]))+","+String.valueOf(df.format(ankle_right_joint[1]))+","+String.valueOf(df.format(ankle_right_joint[2])));
			double foot_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.FOOT_RIGHT);
			foot_right.setText(String.valueOf(df.format(foot_right_joint[0]))+","+String.valueOf(df.format(foot_right_joint[1]))+","+String.valueOf(df.format(foot_right_joint[2])));
			double spine_shoulder_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.SPINE_SHOULDER);
			spine_shoulder.setText(String.valueOf(df.format(spine_shoulder_joint[0]))+","+String.valueOf(df.format(spine_shoulder_joint[1]))+","+String.valueOf(df.format(spine_shoulder_joint[2])));
			double hand_tip_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.HAND_TIP_LEFT);
			hand_tip_left.setText(String.valueOf(df.format(hand_tip_left_joint[0]))+","+String.valueOf(df.format(hand_tip_left_joint[1]))+","+String.valueOf(df.format(hand_tip_left_joint[2])));
			double thumb_left_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.THUMB_LEFT);
			thumb_left.setText(String.valueOf(df.format(thumb_left_joint[0]))+","+String.valueOf(df.format(thumb_left_joint[1]))+","+String.valueOf(df.format(thumb_left_joint[2])));
			double hand_tip_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.HAND_TIP_RIGHT);
			hand_tip_right.setText(String.valueOf(df.format(hand_tip_right_joint[0]))+","+String.valueOf(df.format(hand_tip_right_joint[1]))+","+String.valueOf(df.format(hand_tip_right_joint[2])));
			double thumb_right_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.THUMB_RIGHT);
			thumb_right.setText(String.valueOf(df.format(thumb_right_joint[0]))+","+String.valueOf(df.format(thumb_right_joint[1]))+","+String.valueOf(df.format(thumb_right_joint[2])));
			//double joint_count_joint[] = ((Skeleton)arg).get3DJoint(Skeleton.JOINT_COUNT);
			//joint_count.setText(String.valueOf(df.format(joint_count_joint[0]))+","+String.valueOf(df.format(joint_count_joint[1]))+","+String.valueOf(df.format(joint_count_joint[2])));
		} else if(arg instanceof Skeleton && joint_orientation.isSelected()) {
			float jointOrientation[] = ((Skeleton)arg).getJointOrientations();
			double bodyOrientation = ((Skeleton)arg).getBodyOrientation();
			double torsoOrientation[] = ((Skeleton)arg).getTorsoOrientation();
			//System.out.println(jointOrientation[11]*120);
			spine_base.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_BASE]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_BASE+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_BASE+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_BASE+3])));
			spine_mid.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_MID]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_MID+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_MID+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_MID+3])));
			neck.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.NECK]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.NECK+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.NECK+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.NECK+3])));
			head.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.HEAD]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HEAD+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HEAD+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HEAD+3])));
			shoulder_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_LEFT+3])));
			elbow_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_LEFT+3])));
			wrist_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_LEFT+3])));
			hand_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_LEFT+3])));
			shoulder_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SHOULDER_RIGHT+3])));
			elbow_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ELBOW_RIGHT+3])));
			wrist_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.WRIST_RIGHT+3])));
			hand_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_RIGHT+3])));
			hip_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_LEFT+3])));
			knee_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_LEFT+3])));
			ankle_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_LEFT+3])));
			foot_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_LEFT+3])));
			hip_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HIP_RIGHT+3])));
			knee_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.KNEE_RIGHT+3])));
			ankle_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.ANKLE_RIGHT+3])));
			foot_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.FOOT_RIGHT+3])));
			spine_shoulder.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_SHOULDER]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_SHOULDER+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_SHOULDER+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.SPINE_SHOULDER+3])));
			hand_tip_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_LEFT+3])));
			thumb_left.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_LEFT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_LEFT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_LEFT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_LEFT+3])));
			hand_tip_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.HAND_TIP_RIGHT+3])));
			thumb_right.setText(String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_RIGHT]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_RIGHT+1]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_RIGHT+2]))+","+String.valueOf(df.format(jointOrientation[4*Skeleton.THUMB_RIGHT+3])));
			body_orientation.setText(String.valueOf(df.format(bodyOrientation)));
			torso_orientation.setText(String.valueOf(df.format(torsoOrientation[0]))+","+String.valueOf(df.format(torsoOrientation[1]))+","+String.valueOf(df.format(torsoOrientation[2])));
			//relative_NUI_coords.setText(String.valueOf(df.format(relativeNUICoords[0]))+","+String.valueOf(df.format(relativeNUICoords[1]))+","+String.valueOf(df.format(relativeNUICoords[2])));
		}
	}
	
	private void resetKinect() {
		if(turn_off.getText().compareTo("Turn on")==0) return;
		
		myKinect.stop();
		int depth_res=J4K1.NUI_IMAGE_RESOLUTION_INVALID;
		if(depth_resolution.getSelectedIndex()==0) myKinect.setDepthResolution(80, 60);//  depth_res=J4K1.NUI_IMAGE_RESOLUTION_80x60;
		else if(depth_resolution.getSelectedIndex()==1) myKinect.setDepthResolution(320, 240);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_320x240;
		else if(depth_resolution.getSelectedIndex()==2) myKinect.setDepthResolution(640, 480);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
		
		int video_res=J4K1.NUI_IMAGE_RESOLUTION_INVALID;
		if(video_resolution.getSelectedIndex()==0) myKinect.setColorResolution(640, 480);//video_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
		else if(video_resolution.getSelectedIndex()==1) myKinect.setDepthResolution(1280, 960);//video_res=J4K1.NUI_IMAGE_RESOLUTION_1280x960;
		
		int flags=Kinect.SKELETON;
		flags=flags|Kinect.COLOR;
		flags=flags|Kinect.DEPTH;
		flags=flags|Kinect.XYZ;
		if(show_infrared.isSelected()) {flags=flags|Kinect.INFRARED; myKinect.updateTextureUsingInfrared(true);}
		else myKinect.updateTextureUsingInfrared(false);
			
		myKinect.start(flags);
		if(show_video.isSelected())myKinect.computeUV(true);
		else myKinect.computeUV(false);
		if(seated_skeleton.isSelected())myKinect.setSeatedSkeletonTracking(true);
		if(near_mode.isSelected()) myKinect.setNearMode(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==near_mode)
		{
			if(near_mode.isSelected()) myKinect.setNearMode(true);
			else myKinect.setNearMode(false);
		}
		else if(e.getSource()==seated_skeleton)
		{
			if(seated_skeleton.isSelected()) myKinect.setSeatedSkeletonTracking(true);
			else myKinect.setSeatedSkeletonTracking(false);
		}
		else if(e.getSource()==show_infrared)
		{
			resetKinect();
		}
		else if(e.getSource()==turn_off)
		{
			if(turn_off.getText().compareTo("Turn Off")==0)
			{
				myKinect.stop();
				turn_off.setText("Turn On");
			}
			else
			{
				turn_off.setText("Turn Off");
				resetKinect();
			}
		}
		else if(e.getSource()==depth_resolution)
		{
			resetKinect();
		}
		else if(e.getSource()==video_resolution)
		{
			resetKinect();
		}
		else if(e.getSource()==show_video)
		{
			mainPanel.setShowVideo(show_video.isSelected());
			if(show_video.isSelected()) myKinect.computeUV(true);
			else myKinect.computeUV(false);
		}
		else if(e.getSource()==mask_players)
		{
			myKinect.maskPlayers(mask_players.isSelected());
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==elevation_angle)
		{
			if(!elevation_angle.getValueIsAdjusting())
			{
				myKinect.setElevationAngle(elevation_angle.getValue());
				elevation_angle.setToolTipText("Elevation Angle ("+elevation_angle.getValue()+" degrees)");
			}
		}
	}

	@Override
	public void subscribeGui() {
	// un-defined gui's

	// subscribe("someMethod");
	// send("someMethod");
	}

	@Override
	public void unsubscribeGui() {
	// commented out subscription due to this class being used for
	// un-defined gui's
	// unsubscribe("someMethod");
		myKinect.stop();
	}  
	  /**
	   * Service State change - this method will be called when a "broadcastState"
	   * method is called which triggers a publishState.  This event handler is typically
	   * used when data or state information in the service has changed, and the UI should
	   * update to reflect this changed state.
	   * @param template
	   */
	public void onState(J4KGui template) {
	  SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {

	    }
	  });
	}

}