package learning;


import interfaces.LUTInterface;

import java.io.*;

import robocode.RobocodeFileOutputStream;

public class LUT implements LUTInterface{
	private int bestAction = 0;
	private double [][] table;
	public LUT() {
		table = new double [states.NumStates][actions.NumRobotActions];
		initializeLUT();
	}
	
	public void initializeLUT() {
		for (int stateN = 0; stateN < states.NumStates; stateN++)   
		      for (int actionN = 0; actionN < actions.NumRobotActions; actionN++)   
		          	  table[stateN][actionN] = 0.0;  
	}
	
	
	
	public double getQValue(int state, int action) {
		return this.table[state][action];
	}
	
	public void setQvalue(int state, int action, double value) {
		this.table[state][action] = value;
	}
	
	public double getMaxQvalue(int state) {
		double maxQVal = Double.NEGATIVE_INFINITY;
		for(int actionN = 0; actionN < this.table[state].length; actionN++) {
			if (table[state][actionN] > maxQVal)
				maxQVal = this.table[state][actionN];
				bestAction = actionN;
		}
		return maxQVal;
	}
	
	public int getMaxQAction(int state) {
		getMaxQvalue(state);
		return bestAction;
	}
	
	public void loadData(File file)   {   
		BufferedReader read = null;   
	    try   {   
	    	read = new BufferedReader(new FileReader(file));   
	    	for (int i = 0; i < states.NumStates; i++)   
	    		for (int j = 0; j < actions.NumRobotActions; j++)   
	    			table[i][j] = Double.parseDouble(read.readLine());   
	    }   
	    catch (IOException e)   {   
	    	System.out.println("IOException trying to open reader: " + e);   
	    	initializeLUT();   
	    }   
	    catch (NumberFormatException e)   {   
	    	initializeLUT();   
	    }  
	    finally {   
	    	try {   
		        if (read != null)   
		        	read.close();   
	        }   
	    	catch (IOException e) {   
	    		System.out.println("IOException trying to close reader: " + e);   
	    	}   
	    }   
    }   
	   
	public void saveData(File file)   {   
		PrintStream saveFile = null;   
	    try   {   
	    	saveFile = new PrintStream(new RobocodeFileOutputStream(file));   
  			for (int i = 0; i < states.NumStates; i++)   
  				for (int j = 0; j < actions.NumRobotActions; j++)   
  					saveFile.println(new Double(table[i][j]));  
  			
  			if (saveFile.checkError())   
  				System.out.println("Could not save the data!");   
  			
  			saveFile.close();   
	    }   
	    catch (IOException e)   {   
	    	System.out.println("IOException trying to write: " + e);   
	    }   
	    finally   {   
	    	try   {   
	    		if (saveFile != null)   
	    			saveFile.close();   
	    	}   
	    	catch (Exception e)   {   
	    		System.out.println("Exception trying to close witer: " + e);   
	    	}   
	    }   
	  }
	
	
	public void printTable(String fileName) throws IOException {
		System.out.println("good up till here");
		PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));		
		printWriter.printf("State, Action, QValue \n");
		for (int i = 0; i < states.NumStates; i++)   {
	        for (int j = 0; j < actions.NumRobotActions; j++) {
	        	printWriter.printf("%d, %d, %f, \n", i, j, this.table[i][j]);
	        }
		}
		printWriter.flush();
		printWriter.close();
	}




	

	
}
