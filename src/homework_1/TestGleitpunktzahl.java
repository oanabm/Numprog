package homework_1;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestGleitpunktzahl {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("set up");
		Gleitpunktzahl.setSizeMantisse(4);
		Gleitpunktzahl.setSizeExponent(2);
	}
	
	@Test
	public void testExactValue() {
		testGleitPunktZahl("3.25", 3.25, 0b1101, 1, false);
	}
	
	@Test
	public void testRoundedValue() {
		testGleitPunktZahl("3.0625", 3.0625, 0b1100, 1, false);
		testGleitPunktZahl("3.875", 3.875, 0b1000, 2, false);
	}
	
	@Test
	public void testTooSmallValue() {
		testGleitPunktZahl("0.4", 0.4, 0b1000, -1, false);
	}
	
	private Gleitpunktzahl testGleitPunktZahl(String name,
		double zahl, int mantExp, int expExp, boolean vorzExp) {
		Gleitpunktzahl z1 = new Gleitpunktzahl(zahl);
		TestCase.assertEquals(name + " mantisse", mantExp, z1.mantisse);
		TestCase.assertEquals(name + " exponent", expExp + 1,
			z1.exponent);
		TestCase.assertEquals(name + " vorzeichen", vorzExp,
			z1.vorzeichen);
		return z1;
	}
	
	public void testSpecialValue() {
		//TODO: Test NaN, 0 and infinite
	}
}