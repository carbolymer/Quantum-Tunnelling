import java.util.Observable;
import java.util.Observer;

/*
 * Klasa odpowiedzialna za liczenie wspolczynnikow przejscia i odbicia
 */
public class UpdateCoefficients implements  Observer
{
	private Interface frontend;

	UpdateCoefficients (Interface ui)
	{
		frontend = ui;
	}
	
	// aktualizacja R i T
	public void update(Observable o, Object arg)
	{
		double r = 0;
		r = Math.pow(WaveFunctionPlot.B.abs(),2); // wspolczynnik odbicia = kwadrat amplitudy fali odbitej
		if (r > 1)
			r = 1;
		frontend.tCoef.setText(""+Interface.round(1-r,4)); // z warunku T + R =1
		frontend.rCoef.setText(""+Interface.round(r,4));
	}

}