import java.util.Observable;
import java.util.Observer;



public class UpdateCoefficients extends Observable implements  Observer, Runnable
{
	public double domainMinimum = -20;
	public double domainMaximum = 20;
	public double domainTick = 0.05;
	Interface frontend;

	UpdateCoefficients (Interface ui)
	{
		frontend = ui;
	}
	public void updateD()
	{
		double t=0;
		double c=0;
//		for(int z = 0 ; z < frontend.potentialValues.getItemCount(); ++z)
//		{
//			double z1=(Double) frontend.potentialValues.getY(z),
//					z2=frontend.getEnergy();
//			
//			if(z1>z2)
//			{
//				c=c+Math.sqrt(2*frontend.getMass()*(z1-z2)*9.1093e-31)*domainTick;
//			}
//		}
//		t=Math.exp(-c/(6.582e-16));
		t = Math.pow(WaveFunctionPlot.E.abs(),2);
		if (t > 1)
			t = 1;
		frontend.dCoef.setText(""+Interface.round(t,4));
		frontend.rCoef.setText(""+Interface.round(1-t,4));
	}
	public void update(Observable o, Object arg)
	{
		updateD();
		new Thread(this).run();
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