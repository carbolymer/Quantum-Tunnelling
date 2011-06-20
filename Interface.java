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
	public static double me = 9.10938e-31; // masa elektronu
	public static double eV = 1.60217653e-19; // elektronowolt
	
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
	protected XYSeriesCollection wavefunctionChartSeriesCollection = new XYSeriesCollection();
	// wartosci funkcji falowej
	protected XYSeries wavefunctionValues = new XYSeries("");	
	
	// parametry do obliczen, kolejno szerokosc bariery, V1,V2, V3, energia, masa
	protected JTextField
		widthParam = new JTextField("10.0",5),
		v1Param = new JTextField("0.0",5),
		v2Param = new JTextField("30.0",5),
		v3Param = new JTextField("0.0",5),
		eParam = new JTextField("10.0",5),
		mParam = new JTextField("0.5",5),
		dCoef = new JTextField("0.0",5), // wspolczynnik przenikania
		rCoef = new JTextField("0.0",5); // wspolczynnik odbicia
	
	// suwaki do parametrow
	protected JSlider
		v1Slider = new JSlider(JSlider.HORIZONTAL, 0,50000,0),
		v2Slider = new JSlider(JSlider.HORIZONTAL, 0,50000,30000),
		v3Slider = new JSlider(JSlider.HORIZONTAL, 0,50000,0),
		widthSlider = new JSlider(JSlider.HORIZONTAL, 0,35000,10000),
		eSlider = new JSlider(JSlider.HORIZONTAL, 0,50000,10000),
		mSlider = new JSlider(JSlider.HORIZONTAL, 0,4000,1000);
	
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
	
		v1Param.setHorizontalAlignment(JTextField.RIGHT);
		v2Param.setHorizontalAlignment(JTextField.RIGHT);
		v3Param.setHorizontalAlignment(JTextField.RIGHT);
		widthParam.setHorizontalAlignment(JTextField.RIGHT);
		eParam.setHorizontalAlignment(JTextField.RIGHT);
		mParam.setHorizontalAlignment(JTextField.RIGHT);
		rCoef.setHorizontalAlignment(JTextField.RIGHT);
		dCoef.setHorizontalAlignment(JTextField.RIGHT);
		rCoef.setEditable(false);
		dCoef.setEditable(false);
		
		v1Slider.addChangeListener(sliderChangeAction);
		v2Slider.addChangeListener(sliderChangeAction);
		v3Slider.addChangeListener(sliderChangeAction);
		widthSlider.addChangeListener(sliderChangeAction);
		eSlider.addChangeListener(sliderChangeAction);
		mSlider.addChangeListener(sliderChangeAction);
		
		v1Slider.addChangeListener(potentialUpdate);
		v2Slider.addChangeListener(potentialUpdate);
		v3Slider.addChangeListener(potentialUpdate);
		widthSlider.addChangeListener(potentialUpdate);
		eSlider.addChangeListener(particleUpdate);
		mSlider.addChangeListener(particleUpdate);
		
		v1Param.addActionListener(fieldChangeAction);
		v2Param.addActionListener(fieldChangeAction);
		v3Param.addActionListener(fieldChangeAction);
		widthParam.addActionListener(fieldChangeAction);
		eParam.addActionListener(fieldChangeAction);
		mParam.addActionListener(fieldChangeAction);

		v1Param.addActionListener(potentialUpdate);
		v2Param.addActionListener(potentialUpdate);
		v3Param.addActionListener(potentialUpdate);
		widthParam.addActionListener(potentialUpdate);
		eParam.addActionListener(particleUpdate);
		mParam.addActionListener(particleUpdate);
		
	}
		
	/*
	 * Metoda odpowiedzialna za rysowanie interfejsu, uruchamiana w oddzielnym watku
	 */
	public void createGUI()
	{
		frame.setSize(1080,440);
		// GridBagLayout imo najwygodniejszy do uzycia
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// ustawienia wykresu potencjalu
		potentialChart = new ChartPanel(ChartFactory.createXYLineChart(
				"", "x", "U(x) [eV]", potentialChartSeriesCollection,
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
		frame.add(new JLabel("<html>Szerokość bariery potencjału [eV]: </html>",JLabel.RIGHT),c);
		c.gridx = 1;
		c.gridy = 2;
		frame.add(widthParam, c);
		c.gridx = 2;
		c.gridy = 2;
		frame.add(widthSlider, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		frame.add(new JLabel("<html>Potencjał pierwszego przedziału [eV]: </html>",JLabel.RIGHT),c);
		c.gridx = 1;
		frame.add(v1Param, c);
		c.gridx = 2;
		frame.add(v1Slider,c);
		c.gridx = 0;
		c.gridy = 4;
		frame.add(new JLabel("<html>Potencjał w drugim przedziale [eV]:</html>",JLabel.RIGHT ),c);
		c.gridx = 1;
		frame.add(v2Param, c);
		c.gridx = 2;
		frame.add(v2Slider,c);
		c.gridx = 0;
		c.gridy = 5;
		frame.add(new JLabel("<html>Potencjał w trzecim przedziale [eV]: </html>",JLabel.RIGHT),c);
		c.gridx = 1;
		frame.add(v3Param, c);
		c.gridx = 2;
		frame.add(v3Slider,c);
		
		wavefunctionChart = new ChartPanel(ChartFactory.createXYLineChart(
				"", "x", "Ψ(x)  /  |Ψ(x)|²", wavefunctionChartSeriesCollection,
				PlotOrientation.VERTICAL, true, false, false));
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
	public double getV1()
	{
		return Double.parseDouble(v1Param.getText())*eV;
	}
	
	public double getV2()
	{
		return Double.parseDouble(v2Param.getText())*eV;
	}
	
	public double getV3()
	{
		return Double.parseDouble(v3Param.getText())*eV;
	}
	
	public double getWidth()
	{
		return Double.parseDouble(widthParam.getText());
	}
	
	public double getEnergy()
	{
		return Double.parseDouble(eParam.getText())*eV;
	}
	
	public double getMass()
	{
		return Double.parseDouble(mParam.getText())*me;
	}
	public static double round(double number, int decimalPlaces)
	{
		double modifier = Math.pow(10.0, decimalPlaces);
		return Math.round(number * modifier) / modifier;
	}
}
