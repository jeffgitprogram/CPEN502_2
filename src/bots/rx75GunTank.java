package bots;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintStream;

import robocode.*;

import learning.*;

//API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html


public class rx75GunTank extends AdvancedRobot{
	
	public static final double PI = Math.PI;
	private Target target;
	private LUT lut;
	private LearningKernel agent;
	private double reward = 0.0;
	private int isHitWall = 0;
	private int isHitByBullet = 0;
	
	private double targetDist, targetBearing;
	
	private boolean isFound = false;
	private int state, action;
	
	private double rewardForWin = 100;
	private double rewardForDeath = -20;
	private double accumuReward = 0.0;
	
	private boolean interRewards = true;
	private boolean isSARSA = false; //Switch between on policy and off policy, true = on-policy, false = off-policy
	
	public void run() {
		lut = new LUT();
		loadData();
		agent = new LearningKernel(lut);
		target = new Target();
		target.setDistance(100000);
		
		setAllColors(Color.red);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		execute();
		
		while(true) {
			turnRadarRightRadians(2*PI);
			//Get Last State
			state = getState();
			action = agent.selectAction(state);
			
			switch(action) {
			case Actions.RobotAhead:
				setAhead(Actions.RobotMoveDistance);
				break;
			case Actions.RobotBack:
				setBack(Actions.RobotMoveDistance);
				break;
			case Actions.RobotAheadTurnLeft:
				setAhead(Actions.RobotMoveDistance);
				setTurnLeft(Actions.RobotTurnDegree);
				break;
			case Actions.RobotAheadTurnRight:
				setAhead(Actions.RobotMoveDistance);
				setTurnRight(Actions.RobotTurnDegree);
				break;
			case Actions.RobotBackTurnLeft:
				setBack(Actions.RobotMoveDistance);
				setTurnLeft(Actions.RobotTurnDegree);
				break;
			case Actions.RobotBackTurnRight:
				setBack(Actions.RobotMoveDistance);
				setTurnRight(Actions.RobotTurnDegree);
				break;
			case Actions.RobotFire:
				ahead(0);
				turnLeft(0);
				scanAndFire();
				break;
			default:
				System.out.println("Action Not Found");
				break;
			
			}
			
			execute();
			
			turnRadarRightRadians(2*PI);
			//Update states
			state = getState();
			if(isSARSA) {
				agent.SARSLearn(state, action, reward);
			}
			else {
				agent.QLearn(state, action, reward);
			}
			
			accumuReward += reward;
			
			//Reset Values
			reward = 0.0d;
			isHitWall = 0;
			isHitByBullet = 0;
		}
		
	}
	
	////=====Supportive Functions-------////////
	private void scanAndFire() {
		isFound = false;
		while(!isFound) {
			setTurnRadarLeft(360);
			execute();
		}
		
		turnGunLeft(getGunHeading() - (getHeading() + targetBearing)); //All values in degree
		double currentTargetDist = targetDist;
		if(currentTargetDist < 101) fire(6); //Super bullet
		else if(currentTargetDist < 201) fire(4);//Big Bullet
		else if(currentTargetDist < 301) fire(2);// Small Bullet
		else fire(1); //Tiny bullet
	}
	
	private int getState() {
		int heading = States.getHeading(getHeading()); //get heading in degrees
		int targetDistance = States.getTargetDistance(target.getDistance());
		int targetBearing = States.getTargetBearing(target.getBearing());
		int state = States.getStateIndex(heading, targetDistance, targetBearing, isHitWall, isHitByBullet);
		return state;
		
	}
	// This function transform the range of bearing from 0-2pi to -pi-pi
	private double NormalizeBearing(double bearing) {
		while(bearing > PI) {
			bearing -= 2*PI;
		}
		while(bearing < -PI) {
			bearing += 2*PI;
		}
		return bearing;
	}
	
	//=======Robot Events=======////
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		isFound = true;
		targetDist = e.getDistance();
		targetBearing = e.getBearing();
		if ((e.getDistance() < target.getDistance())||(target.getName() == e.getName()))   
        {   
          //the next line gets the target's heading in radians  relative to your position
          double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*PI);   
          //this section sets all the information about our target   
          target.setName(e.getName());   
          double h = NormalizeBearing(e.getHeadingRadians() - target.getHead());// Determine which direction to turn   
          h = h/(getTime() - target.getCtime());   
          target.setChangeHead(h);   
          target.setPositionX(getX()+Math.sin(absbearing_rad)*e.getDistance()); //Determine the x coordinate of where the target is   
          target.setPositionY(getY()+Math.cos(absbearing_rad)*e.getDistance()); //Determine the y coordinate of where the target is   
          target.setBearing(e.getBearingRadians());   
          target.setHead(e.getHeadingRadians());  
          target.setCtime(getTime());             //Record the time at which this scan was produced   
          target.setSpeed(e.getVelocity());  
          target.setDistance(e.getDistance());   
          target.setEnergy(e.getEnergy());   
        }
	}

	
	/**
	 * onBulletHit: What to do when hit other robots
	 */
	public void onBulletHit(BulletHitEvent e)   
    {  
		if (target.getName() == e.getName()) {     
		    double change = e.getBullet().getPower() * 9;   
		    System.out.println("Bullet Hit: " + change);   
		    if (interRewards) reward += change;   
		}   
    }  
	
	
	/**
	 * onBulletMissed: What to do when miss other robots
	 */
	public void onBulletMissed(BulletMissedEvent e)   
    {   
		double change = -e.getBullet().getPower() * 7.5;   
		System.out.println("Bullet Missed: " + change);   
		if (interRewards) reward += change;   
    }
	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		if (target.getName()== e.getName())   {   
			double power = e.getBullet().getPower();   
			double change = -6 * power;
			System.out.println("Hit By Bullet: " + change);   
			if (interRewards) reward += change;  //Generate intermediate reward value 
		}
		isHitByBullet = 1;  
	}
	
	/**
	 * onHitByBullet: What to do when you're hit by a robot
	 */
	public void onHitRobot(HitRobotEvent e) {   
		if (target.getName() == e.getName()) {   
			double change = -6.0;   
			System.out.println("Hit Robot: " + change);   
			if (interRewards) reward += change;   
		}   
    }  
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		//double change = -(Math.abs(getVelocity()) * 0.5 - 1) * 10;   
		double change = -10.0;   
		System.out.println("Hit Wall: " + change);   
		if (interRewards) reward += change;   
        isHitWall = 1;
      
	}	
	
	/**
	 * onRobotDeath: What to do when other robot dead
	 */
	public void onRobotDeath(RobotDeathEvent e) {   
		if (e.getName() == target.getName()) {
			target.setDistance(10000); 
		}
		if (interRewards) reward += 20;
    }   
	
	/**
	 *  onWin: Robot win the game
	 */
	public void onWin(WinEvent event)   
    {   
		reward+=rewardForWin;
		//moveRobot();
		saveData();   
  		int winningTag=1;

  		PrintStream w = null; 
  		try { 
  			w = new PrintStream(new RobocodeFileOutputStream(getDataFile("battle_history.dat").getAbsolutePath(), true)); 
  			w.println(accumuReward+" \t"+getRoundNum()+" \t"+winningTag+" \t"+LearningKernel.explorationRate); 
  			if (w.checkError()) 
  				System.out.println("Could not save the data!");  //setTurnLeft(180 - (target.bearing + 90 - 30));
  				w.close(); 
  		} 
	    catch (IOException e) { 
	    	System.out.println("IOException trying to write: " + e); 
	    } 
	    finally { 
	    	try { 
	    		if (w != null) 
	    			w.close(); 
	    	} 
	    	catch (Exception e) { 
	    		System.out.println("Exception trying to close witer: " + e); 
	    	}
	    } 
    }   
     
	/**
	 *  onDeath: Robot lose the game
	 */
    public void onDeath(DeathEvent event)   
    {   
    	reward+=rewardForDeath;
    	//moveRobot();
		saveData();  
       
		int losingTag=0;
		PrintStream w = null; 
		try { 
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile("battle_history.dat").getAbsolutePath(), true)); 
			w.println(accumuReward+" \t"+getRoundNum()+" \t"+losingTag+" \t"+LearningKernel.explorationRate); 
			if (w.checkError()) 
				System.out.println("Could not save the data!"); 
			w.close(); 
		} 
		catch (IOException e) { 
			System.out.println("IOException trying to write: " + e); 
		} 
		finally { 
			try { 
				if (w != null) 
					w.close(); 
			} 
			catch (Exception e) { 
				System.out.println("Exception trying to close witer: " + e); 
			} 
		} 
    }	
    //======= Load and Save the Look Up Table =========	
	public void loadData()   {   
	      try   {   
	        lut.loadData(getDataFile("LUT.dat"));   
	      }   
	      catch (Exception e)   {
	      	out.println("Exception trying to write: " + e); 
	      }   
	    }   
	     
	public void saveData()   {   
	      try   {   
	        lut.saveData(getDataFile("LUT.dat"));   
	      }   
	      catch (Exception e)   {   
	        out.println("Exception trying to write: " + e);   
	      }   
	    }   
}
