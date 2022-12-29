package hotplate;

import java.util.ArrayList;
import java.util.List;

public class Element implements Runnable
{
    private List<Element> neighbours;
    private double temperature;
    public static double heatConstant = 0.05;
    private boolean stopRequested;
    private static final int SLEEP_MS = 50;

   
    public Element(double temperature)
    { 
        neighbours = new ArrayList<Element>();
        stopRequested = false;
        this.temperature = temperature;
    }
    public synchronized double getTemperature()
    {   return temperature;
    }
    public void run()
    {   while(!stopRequested)
        {
            double totalTemp = 0;
            for(Element neighbour:neighbours)
            {   totalTemp += neighbour.getTemperature();
            }
            //System.out.println("TOTAL TEMP FOR ELEMENT "+elementNumber+"  is "+getTemperature());
            double avg = totalTemp/((double)neighbours.size());
            //System.out.println("AVERAGE TEMP FOR ELEMENTS NEIGHBOURS FOR ELEMENT "+elementNumber+" is "+avg);
            applyTempurature(avg);
            try
            {   Thread.sleep(SLEEP_MS);
            }
            catch(InterruptedException ie){}
        }
    }
    public synchronized void applyTempurature(double appliedTemp)
    {   double t = (appliedTemp - temperature)*heatConstant;
        temperature = temperature + t;
    }
    public void addNeighbour(Element element)
    {   neighbours.add(element);
        //System.out.println("ELEMENT "+elementNumber+" HAS THIS MANY NEIGHBOURS NOW "+neighbours.size());
    }
//    private synchronized void updateTemperature(double avg)
//    {   if(neighbours.size() > 0)
//        {
//            double t = (avg - temperature)*heatConstant;
//            temperature = temperature + t;
//        }
//        //System.out.println("Element Number "+elementNumber+" is at temperature "+temperature);
//    }
    public void requestStop()
    {   stopRequested = true;
    }
    public void start()
    {   stopRequested = false;
        Thread thread = new Thread(this);
        thread.start();
    }
    public synchronized void resetHeatConstant(double heatConstant)
    {   this.heatConstant = heatConstant;
    }
    public static void main(String[] args)
    {
        Element[] elements = new Element[2];
        elements[0] = new Element(300.0);
        elements[1] = new Element(0.0);

        //add neighbours
       elements[0].addNeighbour(elements[1]);
       elements[1].addNeighbour(elements[0]);
        
       for(int i=0;i<elements.length;i++)
       {   elements[i].start();
       }
        
        boolean stop = false;
        while(!stop)
        {
            double temp1 = elements[0].getTemperature();
            double temp2 = elements[1].getTemperature();
            double eps = Math.abs(temp1 - temp2);
            System.out.println("Elements 0: is at "+temp1);
            System.out.println("Elements 1: is at "+temp2+"\n\n");
            if(eps < 0.1)
                stop = true;
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
        }
    }
}
