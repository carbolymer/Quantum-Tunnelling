import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;

/*
 * Glowna klasa odpowiedzialna za rysowanie interfejsu
 */

public class Interface
{
	// okno apletu
	Window frame;
	// wykres potencjalu
	private ChartPanel potentialChart;
	// wszystkie dane do wykresu
	protected XYSeriesCollection potentialChartSeriesCollection = new XYSeriesCollection();
	// wartosci potencjalu
	protected XYSeries potentialValues = new XYSeries("");

	// wykres funkcji falowej
	private ChartPanel wavefunctionChart;
	// wszystkie dane do wykresu
	private XYSeriesCollection wavefunctionChartSeriesCollection = new XYSeriesCollection();
	// wartosci funkcji falowej
	protected XYSeries wavefunctionValues = new XYSeries("");	
	
	// combo wyboru potencjalu
	String[] functions =
	{
		"<html>prostokąt (y=&alpha;x+10&beta; ; x&#8714;[-&gamma;/2;&gamma/2])</html>",
		"<html>&alpha;/(&beta;x<sup>2</sup>+&gamma;)</html>",
		"<html>&alpha;(arctg(-&beta;x<sup>2</sup>)+&pi;/2)</html>",
		"<html>&alpha;e<sup>-(x/&beta;)^2</sup></html>",
		"<html>&alpha;sin(&beta;x/&pi;)</html>",
		"<html>wielomian</html>",  // TODO 
		"<html>&alpha;sinc(&beta;x)</html>"
	};
	protected JComboBox potentialFunctionCombo = new JComboBox(functions);
	
	// parametry do obliczen, kolejno alpha, beta, gamma, energia, masa
	protected JTextField
		aParam = new JTextField("1.0",5),
		bParam = new JTextField("1.0",5),
		cParam = new JTextField("1.0",5),
		eParam = new JTextField("0.0",5),
		mParam = new JTextField("0.0",5),
		dCoef = new JTextField("0.0",5),
		rCoef = new JTextField("0.0",5);
	
	// suwaki do parametrow
	protected JSlider
		aSlider = new JSlider(JSlider.HORIZONTAL, 0,20000,1000),
		bSlider = new JSlider(JSlider.HORIZONTAL, 0,5000,1000),
		cSlider = new JSlider(JSlider.HORIZONTAL, 0,50000,1000),
		eSlider = new JSlider(JSlider.HORIZONTAL, 0,100000,0),
		mSlider = new JSlider(JSlider.HORIZONTAL, 0,100000,0);
	
	// akcja wykonywana po zmianie parametru suwaka
	protected ChangeListener sliderChangeAction;
	
	// akcja wykonywana pozmianie parametru pola tekstowego
	protected ActionListener fieldChangeAction;
	
	// obserwator modyfikacji parametru potencjalu
	protected ModificationListener.Changeable potentialUpdate;
	// obserwator modyfikacji parametru czasteczki
	protected ModificationListener.Changeable particleUpdate;
	
	Interface(Window applicationFrame)
	{
		frame = applicationFrame;
		sliderChangeAction = new ModificationListener.SliderChange(this);
		fieldChangeAction = new ModificationListener.FieldChange(this);
		potentialUpdate = new ModificationListener.Changeable();
		particleUpdate = new ModificationListener.Changeable();
	
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
		
		aSlider.addChangeListener(potentialUpdate);
		bSlider.addChangeListener(potentialUpdate);
		cSlider.addChangeListener(potentialUpdate);
		eSlider.addChangeListener(particleUpdate);
		mSlider.addChangeListener(particleUpdate);
		
		aParam.addActionListener(fieldChangeAction);
		bParam.addActionListener(fieldChangeAction);
		cParam.addActionListener(fieldChangeAction);
		eParam.addActionListener(fieldChangeAction);
		mParam.addActionListener(fieldChangeAction);

		aParam.addActionListener(potentialUpdate);
		bParam.addActionListener(potentialUpdate);
		cParam.addActionListener(potentialUpdate);
		eParam.addActionListener(particleUpdate);
		mParam.addActionListener(particleUpdate);
		
		potentialFunctionCombo.addActionListener(fieldChangeAction);
		potentialFunctionCombo.addActionListener(potentialUpdate);
	}
		
	/*
	 * Metoda odpowiedzialna za rysowanie interfejsu, uruchamiana w oddzielnym watku
	 */
	public void createGUI()
	{
		frame.setSize(1000,450);
		// GridBagLayout imo najwygodniejszy do uzycia
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// ustawienia wykresu potencjalu
		potentialValues.add(1,2); // TODO wywalic
		potentialValues.add(5,7); // TODO wywalic
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
		frame.add(new JLabel("Wykres potencjału U(x):", JLabel.CENTER),c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		frame.add(potentialChart, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		frame.add(new JLabel("<html>Wybierz funkcję<br/> modelującą potencjał:</html>"),c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		frame.add(potentialFunctionCombo, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		frame.add(new JLabel("<html>Parametr &alpha;: </html>",JLabel.RIGHT),c);
		c.gridx = 1;
		frame.add(aParam, c);
		c.gridx = 2;
		frame.add(aSlider,c);
		c.gridx = 0;
		c.gridy = 4;
		frame.add(new JLabel("<html>Parametr &beta;:</html>",JLabel.RIGHT ),c);
		c.gridx = 1;
		frame.add(bParam, c);
		c.gridx = 2;
		frame.add(bSlider,c);
		c.gridx = 0;
		c.gridy = 5;
		frame.add(new JLabel("<html>Parametr &gamma;: </html>",JLabel.RIGHT),c);
		c.gridx = 1;
		frame.add(cParam, c);
		c.gridx = 2;
		frame.add(cSlider,c);
		
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
		frame.add(new JLabel("<html>Wykres funkcji falowej &Psi;(x):</html>", JLabel.CENTER),c);
		c.gridx = 4;
		c.gridy = 1;
		frame.add(wavefunctionChart, c);
		c.gridwidth = 1;
		c.gridx = 4;
		c.gridy = 2;
		frame.add(new JLabel("Energia E [eV]: ",JLabel.RIGHT),c);
		c.gridx = 5;
		frame.add(eParam, c);
		c.gridx = 6;
		frame.add(eSlider,c);
		c.gridx = 4;
		c.gridy = 3;
		frame.add(new JLabel("<html>Masa m [m<sub>e</sub>]: </html>",JLabel.RIGHT),c);
		c.gridx = 5;
		frame.add(mParam, c);
		c.gridx = 6;
		frame.add(mSlider,c);
		c.gridy = 4;
		c.gridx = 4;
		frame.add(new JLabel("Współczynnik przejścia D: ",JLabel.RIGHT),c);
		c.gridx = 5;
		frame.add(dCoef,c);
		c.gridy = 5;
		c.gridx = 4;
		frame.add(new JLabel("Współczynnik odbicia R: ",JLabel.RIGHT), c);
		c.gridx = 5;
		frame.add(rCoef, c);
	}
	
	// akcesory dostepu do parametrow
	public double getAlpha()
	{
		return Double.parseDouble(aParam.getText());
	}
	
	public double getBeta()
	{
		return Double.parseDouble(bParam.getText());
	}
	
	public double getGamma()
	{
		return Double.parseDouble(cParam.getText());
	}
	
	public double getEnergy()
	{
		return Double.parseDouble(eParam.getText());
	}
	
	public double getMass()
	{
		return Double.parseDouble(mParam.getText());
	}
}
