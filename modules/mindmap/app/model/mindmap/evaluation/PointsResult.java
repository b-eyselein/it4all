package model.mindmap.evaluation;

public class PointsResult {

	private double maxPoints = 0.0;
	private double realPoints = 0.0;
	
	public double getMaxPoints() {
		return maxPoints;
	}
	
	public void addToMaxPoints(double points) {
		maxPoints += points;
	}
	
	public double getRealPoints() {
		return realPoints;
	}
	
	public void addToRealPoints(double points) {
		realPoints += points;
	}
	
	public void resetAllPoints() {
		maxPoints = 0.0;
		realPoints = 0.0;
	}
}
