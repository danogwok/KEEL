/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet;

/**
 * <p>Represents a sigmoidal neuron of a neural net
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class SigmNeuron extends LinkedNeuron {
	
	/**
	 * <p>
	 * Represents a sigmoidal neuron of a neural net
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////
	
	/** Generated by Eclipse */
	
	private static final long serialVersionUID = 1649577257047517573L;

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	public SigmNeuron() {
        super();
    }
    
	/////////////////////////////////////////////////////////////////
	// -------------------------------- Implementing abstract methods
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Init the input of the neuron (0 or 1 depending on the kind of neuron)
	 * </p>
	 * @return double Initialized value of the input
	 */
    protected double initInput(){
        return 0.;
    }
    
	/**
	 * <p>
	 * Input function of the neuron. Update input for each input neuron
	 * </p>
	 * @param input Old input
	 * @param in Output of the input neuron
	 * @param weight Weight of the link to the input neuron
	 * @return double Partial input of the input neuron
	 */
    protected double inputFunction(double input, double in, double weight) {
    	return input + in*weight;
    }
    
	/**
	 * <p>
	 * Output function of the neuron
	 * </p>
	 * @param input Input of the neuron
	 * @return double Output of the neuron
	 */
    protected double outputFunction(double input) {
    	return 1/(1+Math.exp(-input));
    }
    
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
	/**
	 * <p>
	 * Returns a string representation of the SigmNeuron
	 * </p>
	 * @return String Representation of the SigmNeuron
	 */
	public String toString(){		
		StringBuffer sb = new StringBuffer();
        double weight;

        sb.append("1/(1+e^-(\n");
        for(int i=0; i<links.length; i++)
        {
            if(!links[i].isBroken())
            {
                weight = links[i].getWeight();
	            //If it is a bias node
	            if(biased && links[i].getOrigin() == null)
	                sb.append("\t+" + weight + " * (1)\n");
	            //Else
	            else
		            sb.append("\t+" + weight + " * ( " + 
		                    links[i].getOrigin().toString() + " )\n");
            }
        }
        
        String buffer = sb.toString();
        if(buffer.length()!=0){
	        // Remove the last "\n"
	        buffer = buffer.substring(0, buffer.length()-1);
        }
        // Add the last brackets
        buffer += " ))";
        
		return buffer;
	}
}

