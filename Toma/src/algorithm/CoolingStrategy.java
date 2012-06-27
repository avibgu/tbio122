package algorithm;

public class CoolingStrategy {

	public static double getInitialTemperature(int pNumOfH, int pNumOfP) {
		return (0.3 * pNumOfH) / (0.5 * (pNumOfH + pNumOfP));
	}

	public static double getNewTemperature(double pCurrentTemperature) {
		return pCurrentTemperature * 0.999998;
	}
}
