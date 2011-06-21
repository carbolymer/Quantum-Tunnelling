import java.util.Observable;
import java.util.Observer;
import org.jfree.data.xy.XYSeries;


/*
 * Klasa odpowiedzialna za rysowanie wykresow potencjalu. Uruchamia dalsza logike aplikacji.
 */
public class PotentialPlot extends Observable implements Function, Observer, Runnable
{
	private Interface frontend;
	
	PotentialPlot(Interface ui)
	{
		frontend = ui;
	}
	
	// generowanie serii danych
	public XYSeries calculate()
	{
		// potencjaly dla poszczegolnych obszarow
		double v1 = frontend.getV1()/Interface.eV;
		double v2 = frontend.getV2()/Interface.eV;
		double v3 = frontend.getV3()/Interface.eV;
		double width = frontend.getWidth();
		XYSeries result = new XYSeries("");
		for(double x = domainMinimum; x <= domainMaximum; x+=domainTick)
		{
			if(x < 0)
				result.add(x, v1);
			else if(x > 0 && x < width)
				result.add(x, v2);
			else
				result.add(x,v3);
		}
		return result;
	}

	/*
	 * Rysowanie prostej energii na wykresie potencjalu
	 */
	public void updateEnergy()
	{
		XYSeries energyLine = new XYSeries("");
		energyLine.add(domainMinimum, frontend.getEnergy()/Interface.eV);
		energyLine.add(domainMaximum, frontend.getEnergy()/Interface.eV);
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
		new Thread(this).run(); // uruchamiamy rysowanie wykresow + liczenie wspolczynnikow
		frontend.potentialChartSeriesCollection.addSeries(frontend.potentialValues);
	}

	/*
	 * Przekazanie dzialania do obserwatorow
	 */
	public void run()
	{
		setChanged();
		notifyObservers(frontend.potentialValues);
	}

}
