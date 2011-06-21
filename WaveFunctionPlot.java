import java.util.Observable;
import java.util.Observer;
import org.apache.commons.math.complex.*;

import org.jfree.data.xy.XYSeries;

/*
 * Rysowanie funkcji falowej: czesci rzeczywistej, urojonej i gestosci prawdopodobienstwa
 */
public class WaveFunctionPlot implements Observer, Function
{
	protected static double h = 1.05457168e-34; // stala plancka - h kreslone
	
	// wspolczynniki rownania falowego
	protected static Complex
		B = Complex.ZERO,
		C = Complex.ZERO,
		D = Complex.ZERO,
		E = Complex.ZERO,
		k1 = Complex.ZERO,
		k2 = Complex.ZERO,
		k3 = Complex.ZERO;
	// odpowiednie czesci funkcji falowej
	protected static XYSeries
		real = new XYSeries("Re Ψ(x) "),
		imaginary = new XYSeries("Im Ψ(x) "),
		probability = new XYSeries("|Ψ(x)|²");
	
	private Interface frontend;

	WaveFunctionPlot(Interface ui)
	{
		frontend = ui;
	}

	// osadzenie danych na wykresie
	public void update(Observable arg0, Object arg1)
	{
		frontend.wavefunctionChartSeriesCollection.removeAllSeries();
		frontend.wavefunctionValues = calculate();
		frontend.wavefunctionChartSeriesCollection.addSeries(frontend.wavefunctionValues);
		frontend.wavefunctionChartSeriesCollection.addSeries(imaginary);
		frontend.wavefunctionChartSeriesCollection.addSeries(probability);
	}

	public XYSeries calculate()
	{
		real.clear();
		imaginary.clear();
		probability.clear();
		// zmienne pomocnicze do obliczen
		Complex
			denominator, a, b, c, point;
		
		// wspolczynniki dla odpowiednich obszarow
		k1 = k(frontend.getV1());
		k2 = k(frontend.getV2());
		k3 = k(frontend.getV3());
		double
			x = 0,
			l = frontend.getWidth(); // szerokosc bariery
		int
			i = 0,
			max = frontend.potentialValues.getItemCount();
		// dla E < V1 czastka nie moze nadleciec z lewej strony, wiec funkcja falowa = 0
		// tak samo dla zerowej masy
		if(frontend.getEnergy() <= frontend.getV1() || frontend.getMass() == 0)
		{
			real.add(domainMinimum,0);
			imaginary.add(domainMinimum,0);
			probability.add(domainMinimum,0);
			real.add(domainMaximum,0);
			imaginary.add(domainMaximum,0);
			probability.add(domainMaximum,0);
			return real;
		}
		
		// poniewaz nie ma przeciazania operatorow w javie, algorytm na liczbach zespolonych zostal tak zakodowany
		c = Complex.I.multiply(k2).multiply(l*2).exp();
		a = k2.add(c.multiply(k2)).add(k3).subtract(c.multiply(k3));
		b = k2.subtract(c.multiply(k2)).add(k3).add(c.multiply(k3));
		denominator = k1.multiply(a).add(k2.multiply(b));
		
		// wspolczynniki amplitudowe
		B = k1.multiply(2).multiply(a).divide(denominator).subtract(Complex.ONE);
		
		C = k1.multiply(2).multiply(k2.add(k3)).divide(denominator);
		
		D = k1.multiply(2).multiply(k2.subtract(k3)).multiply(c).divide(denominator);
		
		E = k1.multiply(k2).multiply(4).multiply(Complex.I.multiply(k2.subtract(k3)).multiply(l).exp()).divide(denominator);
		
		// obliczenia wartosci funkcji falowej
		for(i = 0; i < max; ++i)
		{
			x = frontend.potentialValues.getX(i).doubleValue();
			if(x <= 0)
				point = Complex.I.multiply(k1).multiply(x).exp().add(
						B.multiply(Complex.I.multiply(k1).multiply(-x).exp())
				);
			else if (x < l)
				point = C.multiply(Complex.I.multiply(k2).multiply(x).exp()).add(
						D.multiply(Complex.I.multiply(k2).multiply(-x).exp())
				);
			else
				point = E.multiply(Complex.I.multiply(k3).multiply(x).exp());
			real.add(x,point.getReal());
			imaginary.add(x,point.getImaginary());
			probability.add(x,Math.pow(point.abs(),2));
		}
		return real;
	}
	
	// wartosc k dla danego V
	public Complex k(double V)
	{
		if(frontend.getEnergy()-V == 0)
			V = frontend.getEnergy()-1e-3*Interface.eV;
		// ostatnie .multiply(1e-9) bo skala rzednych jest w nm
		return Complex.ONE.multiply(2*frontend.getMass()*(frontend.getEnergy()-V)).sqrt().divide(Complex.ONE.multiply(h)).multiply(1e-9);
	}
}
