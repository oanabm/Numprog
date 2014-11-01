package homework_1;

import junit.framework.TestCase;

import org.junit.Assert;
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
		testGleitPunktZahl("1.9375", 1.9375, 0b1000, 1, false);
	}
	
	@Test
	public void testTooSmallValue() {
		testGleitPunktZahl("0.4", 0.4, 0b1000, -1, false);
	}
	
	@Test
	public void testNormalisieren() {
		Gleitpunktzahl z1 = new Gleitpunktzahl(1.34);
		Gleitpunktzahl z2 = new Gleitpunktzahl(1.34);
		z2.normalisiere();
		Assert.assertEquals(z1, z2);
	}
	
	@Test
	public void testDenormalisieren() {
		Gleitpunktzahl z1 = new Gleitpunktzahl(1.75);
		Gleitpunktzahl z2 = new Gleitpunktzahl(3.5);
		Gleitpunktzahl.denormalisiere(z1, z2);
		testGleitpunktzahl("3.5 deno", z2, z1.mantisse << 1,
			z1.exponent - 1, z1.vorzeichen);
	}
	
	@Test
	public void testAdd() {
		Gleitpunktzahl z1 = new Gleitpunktzahl(0.5);
		Gleitpunktzahl z2 = new Gleitpunktzahl(2.5);
		
		Gleitpunktzahl z4 = z1.add(z2);
		
		Gleitpunktzahl z3 = new Gleitpunktzahl(3.0);
		
		Assert.assertEquals(z3, z4);
	}
	
	@Test
	public void testSub() {
		Gleitpunktzahl z1 = new Gleitpunktzahl(1.5);
		Gleitpunktzahl z2 = new Gleitpunktzahl(1.0);
		
		Gleitpunktzahl z3 = z1.sub(z2);
		
		Gleitpunktzahl z4 = new Gleitpunktzahl(0.5);
		
		Assert.assertEquals(z4, z3);
	}
	
	private Gleitpunktzahl testGleitPunktZahl(String name,
		double zahl, int mantExp, int expExp, boolean vorzExp) {
		Gleitpunktzahl z = new Gleitpunktzahl(zahl);
		testGleitpunktzahl(name, z, mantExp, expExp, vorzExp);
		return z;
	}
	
	private void testGleitpunktzahl(String name, Gleitpunktzahl z,
		int mantExp, int expExp, boolean vorzExp) {
		TestCase.assertEquals(name + " mantisse", mantExp, z.mantisse);
		TestCase.assertEquals(name + " exponent", expExp + 1,
			z.exponent);
		TestCase.assertEquals(name + " vorzeichen", vorzExp,
			z.vorzeichen);
	}
	
	public void testSpecialValue() {
		//TODO: Test NaN, 0 and infinite
	}
}