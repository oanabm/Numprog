package homework_1;

public class Gleitpunktzahl {
	
	/**
	 * Update by
	 * 
	 * @author Juergen Braeckle (braeckle@in.tum.de)
	 * @author Sebastian Rettenberger (rettenbs@in.tum.de)
	 * @since Oktober 22, 2014
	 * @version 1.2
	 * 
	 *          Diese Klasse beschreibt eine Form von Gleitpunktarithmetik
	 */
	
	/********************/
	/* Membervariablen: */
	/********************/
	
	/* Vorzeichen, Mantisse und Exponent der Gleitpunktzahl */
	public boolean vorzeichen; /* true = "-1" */
	public int exponent;
	public int mantisse;
	
	/*
	 * Anzahl der Bits fuer die Mantisse: einmal gesetzt, soll sie nicht mehr
	 * veraendert werden koennen
	 */
	private static int sizeMantisse = 32;
	private static boolean sizeMantisseFixed = false;
	
	/*
	 * Anzahl der Bits fuer dem Exponent: einmal gesetzt, soll sie nicht mehr
	 * veraendert werden koennen. Maximale Groesse: 32
	 */
	private static int sizeExponent = 8;
	private static boolean sizeExponentFixed = false;
	
	/*
	 * Aus der Anzahl an Bits fuer den Exponenten laesst sich der maximale
	 * Exponent und der Offset berechnen
	 */
	private static int maxExponent = (int) Math.pow(2,
		Gleitpunktzahl.sizeExponent) - 1;
	private static int expOffset = (int) Math.pow(2,
		Gleitpunktzahl.sizeExponent - 1) - 1;
	
	/**
	 * Falls die Anzahl der Bits der Mantisse noch nicht gesperrt ist, so wird sie auf abm gesetzt
	 * und gesperrt
	 */
	public static void setSizeMantisse(int abm) {
		/*
		 * Falls sizeMantisse noch nicht gesetzt und abm > 0 dann setze auf
		 * abm und sperre den Zugriff
		 */
		if (!Gleitpunktzahl.sizeMantisseFixed & abm > 0) {
			Gleitpunktzahl.sizeMantisse = abm;
			Gleitpunktzahl.sizeMantisseFixed = true;
		}
	}
	
	/**
	 * Falls die Anzahl der Bits des Exponenten noch nicht gesperrt ist, so wird sie auf abe gesetzt
	 * und gesperrt. maxExponent und expOffset werden festgelegt
	 */
	public static void setSizeExponent(int abe) {
		if (!Gleitpunktzahl.sizeExponentFixed & abe > 0) {
			Gleitpunktzahl.sizeExponent = abe;
			Gleitpunktzahl.sizeExponentFixed = true;
			Gleitpunktzahl.maxExponent = (int) Math.pow(2, abe) - 1;
			Gleitpunktzahl.expOffset = (int) Math.pow(2, abe - 1) - 1;
		}
	}
	
	/** Liefert die Anzahl der Bits der Mantisse */
	public static int getSizeMantisse() {
		return Gleitpunktzahl.sizeMantisse;
	}
	
	/** Liefert die Anzahl der Bits des Exponenten */
	public static int getSizeExponent() {
		return Gleitpunktzahl.sizeExponent;
	}
	
	/**
	 * erzeugt eine Gleitpunktzahl ohne Anfangswert. Die Bitfelder fuer Mantisse und Exponent werden
	 * angelegt. Ist die Anzahl der Bits noch nicht gesetzt, wird der Standardwert gesperrt
	 */
	Gleitpunktzahl() {
		Gleitpunktzahl.sizeMantisseFixed = true;
		Gleitpunktzahl.sizeExponentFixed = true;
	}
	
	/** erzeugt eine Kopie der reellen Zahl r */
	Gleitpunktzahl(Gleitpunktzahl r) {
		
		/* Vorzeichen kopieren */
		vorzeichen = r.vorzeichen;
		/*
		 * Kopiert den Inhalt der jeweiligen Felder aus r
		 */
		exponent = r.exponent;
		mantisse = r.mantisse;
	}
	
	/**
	 * erzeugt eine reelle Zahl mit der Repraesentation des Double-Wertes d. Ist die Anzahl der Bits
	 * fuer Mantisse und Exponent noch nicht gesetzt, wird der Standardwert gesperrt
	 */
	Gleitpunktzahl(double d) {
		
		this();
		setDouble(d);
	}
	
	/**
	 * setzt dieses Objekt mit der Repraesentation des Double-Wertes d.
	 */
	public void setDouble(double d) {
		
		/* Abfangen der Sonderfaelle */
		if (d == 0) {
			setNull();
			return;
		}
		if (Double.isInfinite(d)) {
			setInfinite(d < 0);
			return;
		}
		if (Double.isNaN(d)) {
			setNaN();
			return;
		}
		
		/* Falls d<0 -> Vorzeichen setzten, Vorzeichen von d wechseln */
		if (d < 0) {
			vorzeichen = true;
			d = -d;
		} else
			vorzeichen = false;
		
		/*
		 * Exponent exp von d zur Basis 2 finden d ist danach im Intervall [1,2)
		 */
		int exp = 0;
		while (d >= 2) {
			d = d / 2;
			exp++;
		}
		while (d < 1) {
			d = 2 * d;
			exp--;
		} /* d in [1,2) */
		
		exponent = exp + Gleitpunktzahl.expOffset;
		
		/*
		 * Mantisse finden; fuer Runden eine Stelle mehr als noetig berechnen
		 */
		double rest = d;
		mantisse = 0;
		for (int i = 0; i <= Gleitpunktzahl.sizeMantisse; i++) {
			mantisse <<= 1;
			if (rest >= 1) {
				rest = rest - 1;
				mantisse |= 1;
			}
			rest = 2 * rest;
		}
		exponent -= 1; /* Mantisse ist um eine Stelle groesser! */
		
		/*
		 * normalisiere uebernimmt die Aufgaben des Rundens
		 */
		normalisiere();
	}
	
	/** liefert eine String-Repraesentation des Objekts */
	@Override
	public String toString() {
		if (isNaN())
			return "NaN";
		if (isNull())
			return "0";
		
		StringBuffer s = new StringBuffer();
		if (vorzeichen)
			s.append('-');
		if (isInfinite())
			s.append("Inf");
		else {
			for (int i = 32 - Integer.numberOfLeadingZeros(mantisse) - 1; i >= 0; i--) {
				if (i == Gleitpunktzahl.sizeMantisse - 2)
					s.append(',');
				if ((mantisse >> i & 1) == 1)
					s.append('1');
				else
					s.append('0');
			}
			s.append(" * 2^(");
			s.append(exponent);
			s.append("-");
			s.append(Gleitpunktzahl.expOffset);
			s.append(")");
		}
		return s.toString();
	}
	
	/** berechnet den Double-Wert des Objekts */
	public double toDouble() {
		/*
		 * Wenn der Exponent maximal ist, nimmt die Gleitpunktzahl einen der
		 * speziellen Werte an
		 */
		if (exponent == Gleitpunktzahl.maxExponent) {
			/*
			 * Wenn die Mantisse Null ist, hat die Zahl den Wert Unendlich oder
			 * -Unendlich
			 */
			if (mantisse == 0) {
				if (vorzeichen)
					return -1.0 / 0.0;
				else
					return 1.0 / 0.0;
			}
			/* Ansonsten ist der Wert NaN */
			else
				return 0.0 / 0.0;
		}
		double m = mantisse;
		if (vorzeichen)
			m *= -1;
		return m
			* Math.pow(2, exponent - Gleitpunktzahl.expOffset
				- (Gleitpunktzahl.sizeMantisse - 1));
	}
	
	/**
	 * Sonderfaelle abfragen
	 */
	/** Liefert true, wenn die Gleitpunktzahl die Null repraesentiert */
	public boolean isNull() {
		return !vorzeichen && mantisse == 0 && exponent == 0;
	}
	
	/**
	 * Liefert true, wenn die Gleitpunktzahl der NotaNumber Darstellung entspricht
	 */
	public boolean isNaN() {
		return mantisse != 0 && exponent == Gleitpunktzahl.maxExponent;
	}
	
	/** Liefert true, wenn die Gleitpunktzahl betragsmaessig unendlich gross ist */
	public boolean isInfinite() {
		return mantisse == 0 && exponent == Gleitpunktzahl.maxExponent;
	}
	
	/**
	 * vergleicht betragsmaessig den Wert des aktuellen Objekts mit der reellen Zahl r
	 */
	public int compareAbsTo(Gleitpunktzahl r) {
		/*
		 * liefert groesser gleich 1, falls |this| > |r|
		 * 0, falls |this| = |r|
		 * kleiner gleich -1, falls |this| < |r|
		 */
		
		/* Exponenten vergleichen */
		int expVergleich = exponent - r.exponent;
		
		if (expVergleich != 0)
			return expVergleich;
		
		/* Bei gleichen Exponenten: Bitweisses Vergleichen der Mantissen */
		return mantisse - r.mantisse;
	}
	
	/**
	 * normalisiert und rundet das aktuelle Objekt auf die Darstellung r = (-1)^vorzeichen * 1,r_t-1
	 * r_t-2 ... r_1 r_0 * 2^exponent. Die 0 wird zu (-1)^0 * 0,00...00 * 2^0 normalisiert WICHTIG:
	 * Es kann sein, dass die Anzahl der Bits nicht mit sizeMantisse uebereinstimmt. Das Ergebnis
	 * soll aber eine Mantisse mit sizeMantisse Bits haben. Deshalb muss evtl. mit Bits aufgefuellt
	 * oder Bits abgeschnitten werden. Dabei muss das Ergebnis nach Definition gerundet werden.
	 * 
	 * Beispiel: Bei 3 Mantissenbits wird die Zahl 10.11 * 2^-1 zu 1.10 * 2^0
	 */
	public void normalisiere() {
		if (isNull() || isInfinite() || isNaN())
			return;//nichts zu tun
			
		//Zahl ist in Reichweite. Mantisse ueberpruefen
		int sollIstDiff = 32 - Integer.numberOfLeadingZeros(mantisse)
			- Gleitpunktzahl.sizeMantisse;
		
		if (sollIstDiff > 0) {
			mantisse >>= sollIstDiff - 1;
			//muss letzte Stelle runden
			if ((mantisse & 1) == 1) {
				//letzte Stelle ist 1 -> Aufrunden
				mantisse >>= 1;//um eine Stelle verschieben
				if (mantisse == Math.pow(2, Gleitpunktzahl.sizeMantisse) - 1) {
					//alle stellen sind 1. Exponent erhoehen
					mantisse = (int) Math.pow(2,
						Gleitpunktzahl.sizeMantisse - 1);
					exponent++;
				} else {
					mantisse += 1;//uebernimmt aufrunden
				}
			} else {
				//letzte Stelle ist 0 -> einfach verschieben
				mantisse >>= 1;
			}
			exponent += sollIstDiff;
		} else if (sollIstDiff < 0) {
			if (mantisse == 0) {
				throw new NumberFormatException(
					"Mantisse darf nicht 0 sein");
			} else {
				mantisse <<= -sollIstDiff;
				exponent += sollIstDiff;
			}
		}
		
		//noch ueberpruefen ob exponent in Reichweite. 
		if (exponent < -1) {
			//Zahl ist kleiner als die haelfte des kleinsten darstellbaren
			setNull();//auf null setzen
			return;
		} else if (exponent == -1) {
			//Zahl ist kleiner als darstellbar, aber naeher am kleinsten darstellbaren
			//als an null
			exponent = 0;
			mantisse = (int) Math
				.pow(2, Gleitpunktzahl.sizeMantisse - 1);
			return;
		} else if (exponent >= Gleitpunktzahl.maxExponent) {
			//exponent ist zu groß
			exponent = Gleitpunktzahl.maxExponent - 1;
			mantisse = (int) Math.pow(2, Gleitpunktzahl.sizeMantisse) - 1;//alle mantissen bits auf 1 sowie fuehrende 1
			return;
		}
	}
	
	/**
	 * denormalisiert die betragsmaessig goessere Zahl, so dass die Exponenten von a und b gleich
	 * sind. Die Mantissen beider Zahlen werden entsprechend erweitert. Denormalisieren wird fuer add
	 * und sub benoetigt.
	 */
	public static void denormalisiere(Gleitpunktzahl a,
		Gleitpunktzahl b) {
		int anzPos = 0;
		
		if (a.compareAbsTo(b) >= 1) {
			anzPos = a.exponent - b.exponent;
			a.exponent = b.exponent;
			a.mantisse <<= anzPos;
		}
		
		else if (a.compareAbsTo(b) <= -1) {
			anzPos = b.exponent - a.exponent;
			b.exponent = a.exponent;
			b.mantisse <<= anzPos;
		} else {
			return;
		}
		
	}
	
	/**
	 * addiert das aktuelle Objekt und die Gleitpunktzahl r. Dabei wird zuerst die betragsmaessig
	 * groessere Zahl denormalisiert und die Mantissen beider zahlen entsprechend vergroessert. Das
	 * Ergebnis wird in einem neuen Objekt gespeichert, normiert, und dieses wird zurueckgegeben.
	 */
	public Gleitpunktzahl add(Gleitpunktzahl r) {
		/*
		 * TODO: hier ist die Operation add zu implementieren. Verwenden Sie die
		 * Funktionen normalisiere und denormalisiere.
		 * Achten Sie auf Sonderfaelle!
		 */
		
		Gleitpunktzahl.denormalisiere(this, r);
		
		if (this.isNull()) return new Gleitpunktzahl(r);
+    		if (r.isNull()) return new Gleitpunktzahl(this);
+		if (this.isInfinite()) return new Gleitpunktzahl(r);
+		if (r.isInfinite ()) return new Gleitpunktzahl(this);
+    		if (this.isNaN()) return new Gleitpunktzahl(r);
+		if (r.isNaN ()) return new Gleitpunktzahl(this);
		
		Gleitpunktzahl result = new Gleitpunktzahl();
		result.mantisse = mantisse + r.mantisse;
		result.exponent = r.exponent;
		result.normalisiere();
		
		return result;
	}
	
	/**
	 * subtrahiert vom aktuellen Objekt die Gleitpunktzahl r. Dabei wird zuerst die betragsmaessig
	 * groessere Zahl denormalisiert und die Mantissen beider zahlen entsprechend vergroessert. Das
	 * Ergebnis wird in einem neuen Objekt gespeichert, normiert, und dieses wird zurueckgegeben.
	 */
	public Gleitpunktzahl sub(Gleitpunktzahl r) {
		/*
		 * TODO: hier ist die Operation sub zu implementieren. Verwenden Sie die
		 * Funktionen normalisiere und denormalisiere.
		 * Achten Sie auf Sonderfaelle!
		 */
		
		Gleitpunktzahl.denormalisiere(this, r);
		
		if (this.isNull()) return new Gleitpunktzahl(r);
+    		if (r.isNull()) return new Gleitpunktzahl(this);
+		if (this.isInfinite()) return new Gleitpunktzahl(r);
+		if (r.isInfinite ()) return new Gleitpunktzahl(this);
+    		if (this.isNaN()) return new Gleitpunktzahl(r);
+		if (r.isNaN ()) return new Gleitpunktzahl(this);
		
		Gleitpunktzahl result = new Gleitpunktzahl();
		result.mantisse = mantisse - r.mantisse;
		result.exponent = r.exponent;
		result.normalisiere();
		
		return result;
	}
	
	/**
	 * Setzt die Zahl auf den Sonderfall 0
	 */
	public void setNull() {
		vorzeichen = false;
		exponent = 0;
		mantisse = 0;
	}
	
	/**
	 * Setzt die Zahl auf den Sonderfall +/- unendlich
	 */
	public void setInfinite(boolean vorzeichen) {
		this.vorzeichen = vorzeichen;
		exponent = Gleitpunktzahl.maxExponent;
		mantisse = 0;
	}
	
	/**
	 * Setzt die Zahl auf den Sonderfall NaN
	 */
	public void setNaN() {
		vorzeichen = false;
		exponent = Gleitpunktzahl.maxExponent;
		mantisse = 1;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Gleitpunktzahl) {
			Gleitpunktzahl z2 = (Gleitpunktzahl) arg0;
			if (isInfinite())
				return z2.isInfinite();
			if (isNull())
				return z2.isNull();
			if (isNaN())
				return z2.isNaN();
			return z2.mantisse == mantisse && z2.exponent == exponent
				&& z2.vorzeichen == vorzeichen;
		}
		return super.equals(arg0);
	}
}
