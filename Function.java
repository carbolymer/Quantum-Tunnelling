import org.jfree.data.xy.XYSeries;

/*
 * Interfejs ktory powinna implementowac kazda klasa rysujaca wykres
 */
public interface Function
{
	// kwantowanie przy rysowaniu wykresu
	public static double domainTick = 0.05;
	public double domainMinimum = -15;
	public double domainMaximum = 40;
	// metoda zwracajaca serie danych
	public XYSeries calculate();
}
