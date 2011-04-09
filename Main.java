import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;

/*
 * Quantum Tunnelling
 * Swing > AWT
 * Tworzenie apletów w Swing: http://download.oracle.com/javase/tutorial/uiswing/components/applet.html
 * Informacja dotycząca wątków w Swingu: http://download.oracle.com/javase/tutorial/uiswing/concurrency/index.html
 */


public class Main extends JApplet
{
	// wykres potencjalu
	private ChartPanel potentialChart;
	// wszystkie dane do wykresu
	private XYSeriesCollection potentialChartSeriesCollection = new XYSeriesCollection();
	// wartosci potencjalu
	private XYSeries potentialValues = new XYSeries("");

	// wykres funkcji falowej
	private ChartPanel wavefunctionChart;
	// wszystkie dane do wykresu
	private XYSeriesCollection wavefunctionChartSeriesCollection = new XYSeriesCollection();
	// wartosci funkcji falowej
	private XYSeries wavefunctionValues = new XYSeries("");	
	
	// combo wyboru potencjalu
	String[] functions =
	{
		"prostokątna bariera",
		"<html>&alpha;/(&beta;x<sup>2</sup>+&gamma;)</html>",
		"<html>&alpha;(arctg(-&beta;x<sup>2</sup>)+&pi;/2)</html>",
		"<html>&alpha;e<sup>-&beta;x^2</sup></html>",
		"<html>&alpha;sin(&beta;x)</html>"
	};
	private JComboBox potentialFunctionCombo = new JComboBox(functions);
	
	// parametry do obliczen, kolejno alpha, beta, gamma, energia, masa
	private JTextField
		aParam = new JTextField("0.0",5),
		bParam = new JTextField("0.0",5),
		cParam = new JTextField("0.0",5),
		eParam = new JTextField("0.0",5),
		mParam = new JTextField("0.0",5),
		dCoef = new JTextField("0.0",5),
		rCoef = new JTextField("0.0",5);
	
	// suwaki do parametrow
	private JSlider
		aSlider = new JSlider(JSlider.HORIZONTAL, 0,100000,0),
		bSlider = new JSlider(JSlider.HORIZONTAL, 0,100000,0),
		cSlider = new JSlider(JSlider.HORIZONTAL, 0,100000,0),
		eSlider = new JSlider(JSlider.HORIZONTAL, 0,100000,0),
		mSlider = new JSlider(JSlider.HORIZONTAL, 0,100000,0);
	
	// akcja wykonywana po zmianie parametru suwaka
	private ChangeListener sliderChangeAction = new ChangeListener()
	{
		public void stateChanged(ChangeEvent evt)
		{
			JTextField field;
			JSlider slider = (JSlider) evt.getSource();
			if(slider == aSlider)
				field = aParam;
			else if(slider == bSlider)
				field = bParam;
			else if(slider == cSlider)
				field = cParam;
			else if(slider == eSlider)
				field = eParam;
			else if(slider == mSlider)
				field = mParam;
			else
				return;
			field.setText(Float.toString(slider.getValue()/1000.0f));
			// TODO odpalanie stąd obliczeń numerycznych
		}
		
	};
	
	// akcja wykonywana pozmianie parametru pola tekstowego
	private ActionListener fieldChangeAction = new ActionListener()
	{
		public void actionPerformed(ActionEvent evt)
		{
			JTextField field = (JTextField) evt.getSource();
			JSlider slider;
			int value;
			if(field == aParam)
				slider = aSlider;
			else if(field == bParam)
				slider = bSlider;
			else if(field == cParam)
				slider = cSlider;
			else if(field == eParam)
				slider = eSlider;
			else if(field == mParam)
				slider = mSlider;
			else 
				return;
			value = (int)(Float.parseFloat(field.getText().replace(",", ".").replace(" ",""))*1000.0f);
			if (value > slider.getMaximum()) // TODO zbadac sens ograniczania wartosci do maximum suwakow
				value = slider.getMaximum();
			else if(value < slider.getMinimum())
				value = slider.getMinimum();
			slider.setValue(value);
			field.setText(Float.toString(value/1000.0f));
			// TODO odpalanie stąd obliczeń numerycznych
		}
	};

	public void init()
	{
	    try
	    {
			// odwoływanie się do komponentów swinga tylko poprzez wątek rozdzielający zadania
	        javax.swing.SwingUtilities.invokeAndWait(new Runnable()
	        {
	            public void run()
	            {
	                createGUI();
	            }
	        });
	    }
	    catch (Exception e)
	    {
	        System.err.println("Nie można byłu narysować interfejsu.");
	    }
	}
	
	/*
	 * Metoda odpowiedzialna za rysowanie interfejsu, uruchamiana w oddzielnym watku
	 */
	public void createGUI()
	{
		setSize(1000,450);
		// GridBagLayout imo najwygodniejszy do uzycia
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		aParam.setHorizontalAlignment(JTextField.RIGHT);
		bParam.setHorizontalAlignment(JTextField.RIGHT);
		cParam.setHorizontalAlignment(JTextField.RIGHT);
		eParam.setHorizontalAlignment(JTextField.RIGHT);
		mParam.setHorizontalAlignment(JTextField.RIGHT);
		rCoef.setHorizontalAlignment(JTextField.RIGHT);
		dCoef.setHorizontalAlignment(JTextField.RIGHT);
		rCoef.setEditable(false);
		dCoef.setEditable(false);
		
		aSlider.addChangeListener(sliderChangeAction);
		bSlider.addChangeListener(sliderChangeAction);
		cSlider.addChangeListener(sliderChangeAction);
		eSlider.addChangeListener(sliderChangeAction);
		mSlider.addChangeListener(sliderChangeAction);
		
		aParam.addActionListener(fieldChangeAction);
		
		
		// ustawienia wykresu potencjalu, atm zapychanie miejsca
		potentialValues.add(1,2);
		potentialValues.add(5,7);
		potentialChartSeriesCollection.addSeries(potentialValues);
		potentialChart = new ChartPanel(ChartFactory.createXYLineChart(
				"", "x", "U(x)", potentialChartSeriesCollection,
				PlotOrientation.VERTICAL, false, false, false));
		potentialChart.setPreferredSize(new Dimension(450,300));
		
		// lewa kolumna, ustawienia potencjalu
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		add(new JLabel("Wykres potencjału U(x):", JLabel.CENTER),c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		add(potentialChart, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		add(new JLabel("<html>Wybierz funkcję<br/> modelującą potencjał:</html>"),c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		add(potentialFunctionCombo, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		add(new JLabel("<html>Parametr &alpha;: </html>",JLabel.RIGHT),c);
		c.gridx = 1;
		add(aParam, c);
		c.gridx = 2;
		add(aSlider,c);
		c.gridx = 0;
		c.gridy = 4;
		add(new JLabel("<html>Parametr &beta;:</html>",JLabel.RIGHT ),c);
		c.gridx = 1;
		add(bParam, c);
		c.gridx = 2;
		add(bSlider,c);
		c.gridx = 0;
		c.gridy = 5;
		add(new JLabel("<html>Parametr &gamma;: </html>",JLabel.RIGHT),c);
		c.gridx = 1;
		add(cParam, c);
		c.gridx = 2;
		add(cSlider,c);
		
		
		// zapychacz miejsca
		wavefunctionValues.add(1,2);
		wavefunctionValues.add(5,7);
		wavefunctionChartSeriesCollection.addSeries(potentialValues);
		wavefunctionChart = new ChartPanel(ChartFactory.createXYLineChart(
				"", "x", "Ψ(x)  /  |Ψ(x)|²", wavefunctionChartSeriesCollection,
				PlotOrientation.VERTICAL, false, false, false));
		wavefunctionChart.setPreferredSize(new Dimension(500,300));
		
		// prawa kolumna, funkcja falowa, energia, masa oraz wspolczynniki wyliczone
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 3;
		add(new JLabel("<html>Wykres funkcji falowej &Psi;(x):</html>", JLabel.CENTER),c);
		c.gridx = 4;
		c.gridy = 1;
		add(wavefunctionChart, c);
		c.gridwidth = 1;
		c.gridx = 4;
		c.gridy = 2;
		add(new JLabel("Energia E [eV]: ",JLabel.RIGHT),c);
		c.gridx = 5;
		add(eParam, c);
		c.gridx = 6;
		add(eSlider,c);
		c.gridx = 4;
		c.gridy = 3;
		add(new JLabel("<html>Masa m [m<sub>e</sub>]: </html>",JLabel.RIGHT),c);
		c.gridx = 5;
		add(mParam, c);
		c.gridx = 6;
		add(mSlider,c);
		c.gridy = 4;
		c.gridx = 4;
		add(new JLabel("Współczynnik przejścia D: ",JLabel.RIGHT),c);
		c.gridx = 5;
		add(dCoef,c);
		c.gridy = 5;
		c.gridx = 4;
		add(new JLabel("Współczynnik odbicia R: ",JLabel.RIGHT), c);
		c.gridx = 5;
		add(rCoef, c);
	}
}
