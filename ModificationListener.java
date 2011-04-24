import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * Klasa obslugujaca wszystkie zdarzenia w oknie aplikacji.
 */
public class ModificationListener
{
	/*
	 * Klasa obslugujaca modyfikacje pola
	 */
	public static class FieldChange implements ActionListener
	{
		// glowne okno programu
		private Interface frontend;
	
		public FieldChange(Interface fe)
		{
			frontend = fe;
		}
	
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getSource().getClass().getName() == "javax.swing.JTextField")
			{
				JTextField field = (JTextField) evt.getSource();
				JSlider slider;
				int value;
				if(field == frontend.aParam)
					slider = frontend.aSlider;
				else if(field == frontend.bParam)
					slider = frontend.bSlider;
				else if(field == frontend.cParam)
					slider = frontend.cSlider;
				else if(field == frontend.eParam)
					slider = frontend.eSlider;
				else if(field == frontend.mParam)
					slider = frontend.mSlider;
				else 
					return;
				value = (int)(Float.parseFloat(field.getText().replace(",", ".").replace(" ",""))*1000.0f);
				if (value > slider.getMaximum()) // TODO zbadac sens ograniczania wartosci do maximum suwakow
					value = slider.getMaximum();
				else if(value < slider.getMinimum())
					value = slider.getMinimum();
				slider.setValue(value);
				field.setText(Float.toString(value/1000.0f));
				frontend.potentialUpdate.stateChanged(null);
			}
		}
	};
	
	/*
	 * Obsluga reakcji na uzycie slidera
	 */
	public static class SliderChange implements ChangeListener
	{
		// glowne okno programu
		private Interface frontend;

		public SliderChange(Interface fe)
		{
			frontend = fe;
		}
		public void stateChanged(ChangeEvent evt)
		{
			JTextField field;
			JSlider slider = (JSlider) evt.getSource();
			if(slider == frontend.aSlider)
				field = frontend.aParam;
			else if(slider == frontend.bSlider)
				field = frontend.bParam;
			else if(slider == frontend.cSlider)
				field = frontend.cParam;
			else if(slider == frontend.eSlider)
				field = frontend.eParam;
			else if(slider == frontend.mSlider)
				field = frontend.mParam;
			else
				return;
			field.setText(Float.toString(slider.getValue()/1000.0f));
		}
	};
	
	/*
	 * Implementacja obserwatora
	 */
	public static class Changeable extends Observable implements ActionListener, ChangeListener
	{
		public void stateChanged(ChangeEvent arg0)
		{
			setChanged();
			notifyObservers();
		}

		public void actionPerformed(ActionEvent e)
		{
			setChanged();
			notifyObservers();
		}
	};
}