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
		testGleitPunktZahl("1.25", 1.25, 0b1010, 0, false);
		testGleitPunktZahl("3.25", 3.25, 0b1101, 1, false);
	}
	
	@Test
	public void testRoundedValue() {
		testGleitPunktZahl("3.0625", 3.0625, 0b1100, 1, false);
		testGleitPunktZahl("1.9375", 1.9375, 0b1000, 1, false);
		testGleitPunktZahl("1.3", 1.3, 0b1010, 0, false);
	}
	
	@Test
	public void testTooSmallValue() {
		testGleitPunktZahl("0.4", 0.4, 0b1000, -1, false);
	}
	
	@Test
	public void testTooBigValue() {
		testGleitPunktZahl("5", 5.0, 0b1111, 1, false);
	}
	
	@Test
	public void testNegativeValue() {
		testGleitPunktZahl("-1.3", -1.3, 0b1010, 0, true);
		testGleitPunktZahl("-0.4", -0.4, 0b1000, -1, true);
		testGleitPunktZahl("-5", -5.0, 0b1111, 1, true);
	}
	
	public void testSpecialValue() {
		Assert.assertTrue(new Gleitpunktzahl(Double.NaN).isNaN());
		Assert.assertTrue(new Gleitpunktzahl(0).isNull());
		Assert.assertTrue(new Gleitpunktzahl(Double.POSITIVE_INFINITY)
			.isInfinite());
		Assert.assertTrue(new Gleitpunktzahl(Double.NEGATIVE_INFINITY)
			.isInfinite());
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
		testAdd(0.5, 2.5);
		testAdd(-1, 2.5);
		testAdd(-1, -0.25);
		testAdd(5, 2);
		testAdd(-5, 10);
		testAdd(1.3, -0.8);
	}
	
	@Test
	public void testSpecialValueAdd() {
		testAdd(0, 1);
		testAdd(Double.NaN, 1);
		testAdd(Double.POSITIVE_INFINITY, 0);
		//in java this is NaN:
		testAdd(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
	}
	
	private void testAdd(double a, double b) {
		Gleitpunktzahl z1 = new Gleitpunktzahl(a);
		Gleitpunktzahl z2 = new Gleitpunktzahl(b);
		Gleitpunktzahl z3 = z1.add(z2);
		Gleitpunktzahl z4 = new Gleitpunktzahl(a + b);
		Assert.assertEquals(a + " + " + b, z4, z3);
	}

	@Test
	public void testSub() {
		testSub(1.5, 0.5);
		testSub(0.5, 2.5);
		testSub(-1, 2.5);
		testSub(-1, -0.25);
		testSub(5, 2);
		testSub(-5, 10);
		testSub(1.3, -0.8);
	}
	
	@Test
	public void testSpecialValueSub() {
		testSub(0, 1);
		testSub(Double.NaN, 1);
		testSub(Double.POSITIVE_INFINITY, 0);
	}
	
	private void testSub(double a, double b) {
		Gleitpunktzahl z1 = new Gleitpunktzahl(a);
		Gleitpunktzahl z2 = new Gleitpunktzahl(b);
		Gleitpunktzahl z3 = z1.sub(z2);
		Gleitpunktzahl z4 = new Gleitpunktzahl(a - b);
		Assert.assertEquals(a + " - " + b, z4, z3);
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
}