package learning;
import java.util.Random;
public class learningKernel {
	public static final double LearningRate = 0.3;   // alpha
	public static final double DiscountRate = 0.9;   // gamma
	public static double explorationRate = 0.2; 
	private int currentState;   
	private int currentAction;
	private boolean isFirstRound = true;
	private LUT lut; 
	
	public learningKernel (LUT table) {
		lut = table;
	}
	
	//Off-policy learning
	public void QLearn (int nextState, int nextAction, double reward) {
		double lastQVal;
		double newQVal;
		if(isFirstRound) {
			isFirstRound = false;			
		}
		else {
			lastQVal = lut.getQValue(currentState, currentAction);
			newQVal  = lastQVal + LearningRate*(reward + DiscountRate * lut.getMaxQvalue(nextState)-lastQVal);
			lut.setQvalue(currentState, currentAction, newQVal);
		}
		
		currentState = nextState;
		currentAction = nextAction;
	}
	
	//On-policy Learning
	public void SARSLearn(int nextState, int nextAction, double reward) {
		double lastQVal;
		double newQVal;
		if(isFirstRound) {
			isFirstRound = false;			
		}
		else {
			lastQVal = lut.getQValue(currentState, currentAction);
			newQVal = lastQVal + LearningRate*(reward + DiscountRate * lut.getQValue(nextState, nextAction) - lastQVal);
			lut.setQvalue(currentState, currentAction, newQVal);
		}
		
		currentState = nextState;
		currentAction = nextAction;
	}
	
	//Episilon-Greedy
	public int selectAction(int state) {
		double epsl = Math.random();
		int action =0;
		if(epsl < explorationRate) {
			Random rand = new Random();
			action = rand.nextInt(actions.NumRobotActions);//Exploration Move
		}else {
			//Greedy Move
			action = lut.getMaxQAction(state);
		}
		return action;
	}
	
}
