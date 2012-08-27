package problem;

import main.Main;
import utilities.ProteinGraph;
import algorithm.ConcreteTomaAlgorithm;

public class Test {

	@org.junit.Test
	public void test() throws Exception {

		Main.prop.setProperty("SEED", "17124");
		
		int monomerIndex = 3;
		
		Protein protein = new Protein("HPPHHPPH");
		
		ConcreteTomaAlgorithm algorithm = new ConcreteTomaAlgorithm(protein);
		
		for (int i = 0; i < 3; i++){
			
			algorithm.performRandomlyMovement(monomerIndex);
			protein.saveResults();
			ProteinGraph.show(protein.getResults());
		}
	}
}
