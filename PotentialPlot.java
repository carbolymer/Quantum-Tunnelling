import java.util.Observable;
import java.util.Observer;
import org.jfree.data.xy.XYSeries;


/*
 * Klasa odpowiedzialna za rysowanie wykresow potencjalu. Uruchamia dalsza logike aplikacji.
 */
public class PotentialPlot extends Observable implements Function, Observer, Runnable
{
	public double domainMinimum = -20;
	public double domainMaximum = 20;
	Interface frontend;
	
	PotentialPlot(Interface ui)
	{
		frontend = ui;
	}
	
	// glowna metoda, odpowiedzialna za generowanie serii danych
	public XYSeries calculate()
	{
		int index = frontend.potentialFunctionCombo.getSelectedIndex();
		switch(index)
		{
			case 0:
				return calculateRect();
			case 1:
				return calculateQ();
			case 2:
				return calculateAtan();
			case 3:
				return calculateNormal();
			case 4:
				return calculateSin();
			case 5:
				return calculatePolynomial();
			case 6:
			default:
				return calculateSinc();

		}
	}
	// wielomian
	public XYSeries calculatePolynomial()
	{
		double a = frontend.getAlpha();
		double b = frontend.getBeta();
		double c = frontend.getGamma();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			result.add(x, Math.pow(x,6)/100000-a*Math.pow(x, 5)/100-b*Math.pow(x, 3)/10+c*x);
		}
		return result;
	}
	
	// funkcja prostokatna, uogolniona na ograniczona prosta
	public XYSeries calculateRect()
	{
		double a = frontend.getAlpha();
		double b = frontend.getBeta();
		double c = frontend.getGamma();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			if(x < -c/2 || x > c/2)
				result.add(x, 0);
			else
				result.add(x, a*x+b*10);
		}
		return result;
	}
	
	// sincus
	public XYSeries calculateSinc()
	{
		double a = frontend.getAlpha();
		double b = frontend.getBeta();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			if(x == 0)
				result.add(0, a);
			else
				result.add(x, a*Math.sin(b*x)/(b*x));
		}
		return result;
	}
	
	// sinus
	public XYSeries calculateSin()
	{
		double a = frontend.getAlpha();
		double b = frontend.getBeta();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			result.add(x, a*Math.sin(b*x));
		}
		return result;
	}
	
	// arctan
	public XYSeries calculateAtan()
	{
		double a = frontend.getAlpha();
		double b = frontend.getBeta();
		double c = frontend.getGamma();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			result.add(x, a*(Math.atan(-x*x*b)+Math.PI/2));
		}
		return result;
	}
	
	// funkcja wymierna
	public XYSeries calculateQ()
	{
		double a = frontend.getAlpha();
		double b = frontend.getBeta();
		double c = frontend.getGamma();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			if(b == 0 && c == 0)
				result.add(x, 0);
			else if((b == 0 || c == 0 ) && x == 0)
				continue;
			result.add(x, a/(x*x*b+c));
		}
		return result;
	}
	
	// rozklad normalny
	public XYSeries calculateNormal()
	{
		double a = frontend.getAlpha();
		double b = frontend.getBeta();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			result.add(x, a*Math.exp(-x*x/b/b));
		}
		return result;
	}
	
	/*
	 * Rysowanie prostej energii na wykresie potencjalu
	 */
	public void updateEnergy()
	{
		XYSeries energyLine = new XYSeries("");
		energyLine.add(domainMinimum, frontend.getEnergy());
		energyLine.add(domainMaximum, frontend.getEnergy());
		frontend.potentialChartSeriesCollection.addSeries(energyLine);
	}

	/*
	 * Aktualizacja wykresu
	 */
	public void update(Observable o, Object arg)
	{
		frontend.potentialChartSeriesCollection.removeAllSeries();
		frontend.potentialValues = calculate();
		updateEnergy();
		new Thread(this).run(); // uruchamiamy obliczenia numeryczne -> rysowanie wykresow + liczenie wspolczynnikow
		frontend.potentialChartSeriesCollection.addSeries(frontend.potentialValues);
	}

	// TODO mozliwa desynchronizacja z innymi watkami, sprawdzic + dorobic kontrole watkow
	/*
	 * Przekazanie dzialania do obserwatorow
	 */
	public void run()
	{
		setChanged();
		notifyObservers(frontend.potentialValues);
	}

}
