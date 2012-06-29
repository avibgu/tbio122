package algorithm;

public class CoolingStrategy {

	public static double getInitialTemperature(int pNumOfHMonomers,
			int pNumOfPMonomers) {
		
		return (0.3 * pNumOfHMonomers)
				/ (0.5 * (pNumOfHMonomers + pNumOfPMonomers));
	}

	public static double getNewTemperature(double pCurrentTemperature) {
		return pCurrentTemperature * 0.999998;
	}
}
