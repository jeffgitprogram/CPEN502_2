package learning;

public class States {
	public static final int NumHeading = 4;  //Four states, up, right, down, left
	public static final int NumTargetDistance = 10;  //Ten levels of distance
	public static final int NumTargetBearing = 4;  
	public static final int NumHitWall = 2;  
	public static final int NumHitByBullet = 2;  
	public static final int NumStates;  
	private static final int Mapping[][][][][];
	
	static  {  
		Mapping = new int[NumHeading][NumTargetDistance][NumTargetBearing][NumHitWall][NumHitByBullet];  
		int count = 0;  
		for (int a = 0; a < NumHeading; a++)  
		  for (int b = 0; b < NumTargetDistance; b++)  
		    for (int c = 0; c < NumTargetBearing; c++)  
		      for (int d = 0; d < NumHitWall; d++)  
		        for (int e = 0; e < NumHitByBullet; e++)  
		      Mapping[a][b][c][d][e] = count++;  
		  
		NumStates = count;  
	}
	
	public static int getHeading(double heading)  {  
		double unit = 360 / NumHeading;  
		double newHeading = heading + unit / 2;  
		if (newHeading > 360.0)  
		  newHeading -= 360.0;  
		return (int)(newHeading / unit);  
	} 
	
	public static int getTargetDistance(double value)  {  
	    int distance = (int)(value / 30.0);  
	    if (distance > NumTargetDistance - 1)  
	      distance = NumTargetDistance - 1;  
	    return distance;  
    }
	
	public static int getTargetBearing(double bearing)  {  
		double pi_2 = Math.PI * 2;  
		if (bearing < 0)  
			bearing = pi_2 + bearing;  
		double unit = pi_2 / NumTargetBearing;  
		double newBearing = bearing + unit / 2;  
		if (newBearing > pi_2)  
			newBearing -= pi_2;  
		return (int)(newBearing / unit);  
	} 
	
	
	public static int getStateIndex(int heading, int distance, int bearing, int hitwall, int hitbybullet) {
		return Mapping[heading][distance][bearing][hitwall][hitbybullet];
	}
}
