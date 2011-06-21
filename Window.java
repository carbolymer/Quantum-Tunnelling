import java.awt.Container;
import javax.swing.*;

/*
 * 
 * Quantum Tunnelling
 * 
 */

/*
 * Glowna klasa rozdzielajaca zadania
 */
public class Window extends JApplet
{
	// obiekt interfejsu, tworzy wszystkie elementy GUI, osadza reakcje na zdarzenia
	private Interface frontend;
	
	public void init(Container window)
	{
		frontend = new Interface(window);
		// rysowanie wykresu potencjalu
		PotentialPlot potentialPlot = new PotentialPlot(frontend);
		// aktualizacja wspolczynnikow przejscia i odbicia
		UpdateCoefficients updateCoefficients = new UpdateCoefficients(frontend);
		// rysowanie funkcji falowej
		WaveFunctionPlot wavefunctionPlot = new WaveFunctionPlot(frontend);
		
		// lancuch eventow oparty na wzorcu Obserwator
		// modyfikacja parametru potencjalu/czastki -> rysowanie wykresu potencjalu -> rysowanie funkcji falowej + obliczanie wspolczynnikow
		frontend.potentialUpdate.addObserver(potentialPlot);
		frontend.particleUpdate.addObserver(potentialPlot);
		potentialPlot.addObserver(updateCoefficients);
		potentialPlot.addObserver(wavefunctionPlot);
		
	    try
	    {
	    	// aby zapobiec zawieszeniu aplikacji podczas tworzenia GUI, rysowanie interfejsu poprzez oddzielny watek
	    	SwingUtilities.invokeAndWait(new Runnable()
	        {
	            public void run()
	            {
	            	frontend.createGUI();
	            	frontend.potentialUpdate.stateChanged(null); // aktualizacja stanu aplikacji poprzez odpalenie lancucha eventow
	            }
	        });
	    }
	    catch (Exception e)
	    {
	        System.err.println("Nie można byłu narysować interfejsu.");
	        System.err.println(e.getMessage());
	    }
	}
	
	// konstruktor dla apletu
	public void init()
	{
		init(this);
	}

	// miejsce startu aplikacji
	public static void main(String[] args)
	{
		// nowe okno aplikacji, rysowanie interfejsu
		Window d = new Window();
		d.init(new JFrame());
	}

}
