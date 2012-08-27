package problem;

import main.Main;
import utilities.ProteinGraph;
import algorithm.ConcreteTomaAlgorithm;

public class Test {

	@org.junit.Test
	public void test() throws Exception {

		Main.prop.setProperty("SEED", "17124");
		
		int monomerIndex = 1;
		
		Protein protein = new Protein("HPPHHPPH");
		
		ConcreteTomaAlgorithm algorithm = new ConcreteTomaAlgorithm(protein);
		
		for (int i = 0; i < 3; i++){
			
			monomerIndex++;

			algorithm.performRandomlyMovement(monomerIndex);
			algorithm.calcPositionsStartingFromThisMonomer(monomerIndex);
			
			protein.saveResults();
			ProteinGraph.show(protein.getResults());
			Thread.sleep(2000);
		}
		
		monomerIndex--;
		
		algorithm.restoreStructure(monomerIndex);
		
		protein.saveResults();
		ProteinGraph.show(protein.getResults());
		Thread.sleep(2000);
	}
}
