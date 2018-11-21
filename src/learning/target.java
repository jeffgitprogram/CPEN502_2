package learning;

public class Target {
	  private String name;   
	  private double bearing;   
	  private double head;   
	  private long ctime;   
	  private double speed;   
	  private double x, y;   
	  private double distance;   
	  private double changehead;   
	  private double energy;
	  
	  public Target() {
		  this.name = null;
		  this.bearing = 0.0d;
		  this.head = 0.0d;
		  this.ctime = 0L;
		  this.speed = 0.0d;
		  this.x = 0.0d;
		  this.y = 0.0d;
		  this.distance = 0.0d;
		  this.changehead = 0.0d;
		  this.energy = 0.0d;
	  }
	  public Target(String name, double bearing, double head, 
			  		long ctime, double speed, double x, double y,
			  		double distance, double changehead, double energy) {
		  this.name = name;
		  this.bearing = bearing;
		  this.head = head;
		  this.ctime = ctime;
		  this.speed = speed;
		  this.x = x;
		  this.y = y;
		  this.distance = distance;
		  this.changehead = changehead;
		  this.energy = energy;
	  }
	  
	  public String getName() {
		  return name;
	  }
	  
	  public void setName(String name) {
		  this.name = name;
	  }
	  
	  public double getBearing() {
		  return bearing;
	  }
	  
	  public void setBearing(double val) {
		  bearing = val;
	  }
	  
	  public double getHead() {
		  return head;
	  }
	  
	  public void setHead(double val) {
		  head = val;
	  }
	  
	  public long getCtime() {
		  return ctime;
	  }
	  
	  public void setCtime(long val)
	  {
		  ctime = val;
	  }
	  
	  public double getSpeed() {
		  return speed;
	  }
	  
	  public void setSpeed(double val) {
		  speed = val;
	  }
	  
	  public double getPositionX() {
		  return x;
	  }
	  
	  public void setPositionX(double val) {
		  x = val;
	  }
	  
	  public double getPositionY() {
		  return y;
	  }
	  
	  public void setPositionY(double val) {
		  y = val;
	  }
	  
	  public double getDistance() {
		  return distance;
	  }
	  
	  public void setDistance(double val) {
		  distance = val;
	  }
	  
	  public double getChangeHead() {
		  return changehead;
	  }
	  
	  public void setChangeHead(double val) {
		  changehead = val;
	  }
	  
	  public double getEnergy() {
		  return energy;
	  }
	  
	  public void setEnergy(double val) {
		  energy = val;
	  }
}
