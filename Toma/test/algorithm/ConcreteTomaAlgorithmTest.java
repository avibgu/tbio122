package algorithm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import problem.Protein;

public class ConcreteTomaAlgorithmTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		Protein protein = new Protein("HPPH");
		
		ConcreteTomaAlgorithm algorithm = new ConcreteTomaAlgorithm(protein);
		
		algorithm.performRandomlyMovement(1);
		
		
	}

}
