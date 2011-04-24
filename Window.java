import java.util.Observable;
import java.util.Observer;

import javax.swing.*;


/*
 * 
 * Quantum Tunnelling
 * Tworzenie apletów w Swing: http://download.oracle.com/javase/tutorial/uiswing/components/applet.html
 * Informacja dotycząca wątków w Swingu: http://download.oracle.com/javase/tutorial/uiswing/concurrency/index.html
 * 
 */

/*
 * Glowna klasa rozdzielajaca zadania
 */
public class Window extends JApplet
{
	public void init()
	{
		// obiekt interfejsu, tworzy wszystkie elementy GUI, osadza reakcje na zdarzenia
		final Interface frontend = new Interface(this);
		// obiekt odpowiedzialny za rysowanie wykresu
		PotentialPlot potentialPlot = new PotentialPlot(frontend);
		
		// lancuch eventow
		// modyfikacja parametru potencjalu -> rysowanie wykresu potencjalu + obliczenia numeryczne -> rysowanie funkcji falowej + obliczanie wspolczynnikow
		// TODO obliczenia numeryczne, rysowanie wykresu f.f. i obliczenia wspolczynnikow
		//frontend.potentialUpdate.addObserver( <<obiekt klasy do obliczen numerycznych dziedziczacy po Observable i implementujacy Observer>> );
		frontend.potentialUpdate.addObserver(potentialPlot);
		frontend.particleUpdate.addObserver(potentialPlot);
		//frontend.particleUpdate.addObserver( <<j/w>> );
		
	    try
	    {
	    	// odwoływanie się do komponentów swinga tylko poprzez wątek rozdzielający zadania
	    	SwingUtilities.invokeAndWait(new Runnable()
	        {
	            public void run()
	            {
	            	frontend.createGUI();
	            	frontend.potentialUpdate.stateChanged(null); // aktualizacja stanu aplikacji
	            }
	        });
	    }
	    catch (Exception e)
	    {
	        System.err.println("Nie można byłu narysować interfejsu.");
	    }
	    
	    
	}

}
