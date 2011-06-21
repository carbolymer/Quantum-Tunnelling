import org.jfree.data.xy.XYSeries;

/*
 * Interfejs ktory powinna implementowac kazda klasa rysujaca wykres
 * zawiera w sobie stale uzywane do rysowania wykresow i wymagana metode calculate()
 */
public interface Function
{
	// kwantowanie przy rysowaniu wykresu, w nm
	public static double domainTick = 0.05;
	public double domainMinimum = -20;
	public double domainMaximum = 35;
	// metoda zwracajaca serie danych
	public XYSeries calculate();
}
