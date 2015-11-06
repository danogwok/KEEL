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

package keel.Algorithms.Neural_Networks.NNEP_Common.algorithm;



import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import keel.Algorithms.Neural_Networks.NNEP_Common.mutators.parametric.ParametricMutator;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IEvaluator;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IMutator;
import net.sf.jclec.IPopulation;
import net.sf.jclec.IProvider;
import net.sf.jclec.ISpecies;
import net.sf.jclec.algorithm.AbstractAlgorithm;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.random.IRandGenFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * <p>Base implementation for all neural net algorithms
 * @author Written by Pedro Antonio Gutierrez Penna (University of Cordoba) 17/07/2007
 * @author Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @param <I> Type of individuals in population
 * @since JDK1.5
 * </p>
 */

public class NeuralNetAlgorithm<I extends IIndividual> extends AbstractAlgorithm<I> implements IPopulation<I>, IConfigure
{

	/**
	 * <p>
	 * Base implementation for all neural net algorithms
	 * </p>
	 */

	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */
	private static final long serialVersionUID = 7315921806325890102L;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------- Configuration properties
	/////////////////////////////////////////////////////////////////
	
	/** Random generators factory */
	
	protected IRandGenFactory randGenFactory;
	
	/** Individual species */
	
	protected ISpecies<I> species;
	
	/** Individuals evaluator */
	
	protected IEvaluator<I> evaluator;

	/** Individuals provider */
	
	protected IProvider<I> provider;
	
	/** Population size */
	
	protected int populationSize;
	
	/** Maximum of generations (stop criterium) */
	
	protected int maxOfGenerations;
	
	/** Individual mutator1 */
	
	protected IMutator<I> mutator1;
	
	/** Individual mutator2 */
	
	protected IMutator<I> mutator2;
	
	/** Ratio "elements created"/"elements remaining"  */
	
	protected double cratio;
	
	/** Percentage of parents mutated with second mutator  */
	
	protected int percentageSecondMutator;

	/** Number of parents mutated with second mutator  */
	
	protected int nofselSecondMutator;
	
	/** 
	 * Maximum number of generations allowed without improving
	 * the fitness mean of best individuals
	 */
	
	protected int mogmean;
	
	/** 
	 * Maximum number of generations allowed without improving
	 * the best fitness
	 */
	
	protected int mogbest;
	
	/** 
	 * Difference between two fitness that we consider
     * enoung to say that the fitness has improved
     */
	
	protected double fitDif;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- System state
	/////////////////////////////////////////////////////////////////
	
	/** Current generation */
	
	protected int generation;

	/** Current individuals set */
	
	protected List<I> bset;
	
	/** Individuals selected as parents for first mutator */
	
	protected List<I> pset1;
	
	/** Individuals selected as parents for second mutator */
	
	protected List<I> pset2;
	
	/** Individuals generated */

	protected List<I> cset;
	
	/** Individuals to replace */
	
	protected List<I> rset;
	
	/** Current best fitness */
	
	protected double currentBest = 0.;
	
	/** Current mean fitness */
	
	protected double currentMean = 0.;
	
	/** Previous best fitness */
	
	protected double previousBest = 0.;
	
	/** Previous mean fitness */
	
	protected double previousMean = 0.;
	
	/** Number of generations without improving best fitness */
	
	protected int nogbest = 0;
	
	/** Number of generations without improving mean fitness */
	
	protected int nogmean = 0;
	
	/** Best individual of last generation */
	
	protected I bestIndividual;
		
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Internal variables
	/////////////////////////////////////////////////////////////////
	
	/** Comparator of individuals */
	
	protected Comparator<I> individualsComparator =  new Comparator<I> () 
	{
		/**
		 * {@inheritDoc} 
		 */
		
		public int compare(I arg0, I arg1) {
			return evaluator.getComparator().compare(arg0.getFitness(), arg1.getFitness());
		}
	};
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	
	public NeuralNetAlgorithm() 
	{
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------- IPopulation interface
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Access to system species
	 * </p>
	 * @return System species
	 */
	public final ISpecies<I> getSpecies() 
	{
		return species;
	}
	
	/**
	 * <p>
	 * Access to system evaluator
	 * </p>
	 * @return System evaluator
	 */
	public final IEvaluator<I> getEvaluator() 
	{
		return evaluator;
	}
	
	/**
	 * <p>
	 * Access to current generation
	 * </p>
	 * @return Current generation
	 */
	public final int getGeneration() 
	{
		return generation;
	}
	
	/**
	 * <p>
	 * Access to population inhabitants
	 * </p>
	 * @return Population inhabitants
	 */
	public List<I> getInhabitants()
	{
		return bset;
	}
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------ Getting system state properties
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Returns best individual fitness of the population
	 * </p>
	 * @return double Best individual fitness
	 */
	public double getCurrentBest() {
		return currentBest;
	}

	/**
	 * <p>
	 * Returns fitness mean of the percentageFirstMutator of population
	 * </p>
	 * @return double Fitness mean
	 */
	public double getCurrentMean() {
		return currentMean;
	}

	/**
	 * <p>
	 * Returns current number of generations without 
	 * improving best fitness
	 * </p>
	 * @return int Number of generations
	 */
	public int getNogbest() {
		return nogbest;
	}

	/**
	 * <p>
	 * Returns current number of generations without 
	 * improving mean fitness
	 * </p>
	 * @return int Number of generations
	 */
	public int getNogmean() {
		return nogmean;
	}
	
	/**
	 * <p>
	 * Returns previous best fitness
	 * </p>
	 * @return double Previous best fitness
	 */
	public double getPreviousBest() {
		return previousBest;
	}

	/**
	 * <p>
	 * Returns previous mean fitness
	 * </p>
	 * @return double Previous mean fitness
	 */
	public double getPreviousMean() {
		return previousMean;
	}
	
	/**
	 * <p>
	 * Returns the best individual of the population
	 * </p>
	 * @return I Best individual of the population  
	 */
	public I getBestIndividual() {
		return bestIndividual;
	}

	/////////////////////////////////////////////////////////////////
	// ------------------------------ Setting system state properties
	/////////////////////////////////////////////////////////////////

    /**
	 * <p>
	 * Sets the system species
	 * </p>
	 * @param species New system species
	 */	
	public final void setSpecies(ISpecies<I> species)
	{
		this.species = species;
	}

    /**
	 * <p>
	 * Sets the system evaluator
	 * </p>
	 * @param evaluator New system evaluator
	 */	
	public final void setEvaluator(IEvaluator<I> evaluator) 
	{
		this.evaluator = evaluator;		
	}
	
    /**
	 * <p>
	 * Sets the current generation
	 * </p>
	 * @param generation New current generation
	 */
	public final void setGeneration(int generation)
	{
		this.generation = generation;
	}

    /**
	 * <p>
	 * Sets the population inhabitants
	 * </p>
	 * @param inhabitants new population inhabitants
	 */	
	public final void setInhabitants(List<I> inhabitants)
	{
		this.bset = inhabitants;
	}
	
	/////////////////////////////////////////////////////////////////
	// --------------------------- Access to configuration properties
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Returns randgen factory
	 * </p>
	 * @return Current randgen factory
	 */	
	public IRandGenFactory getRandGenFactory() 
	{
		return randGenFactory;
	}
	
	/**
	 * <p>
	 * Sets the randgen factory
	 * </p>
	 * @param randGenFactory New randgen factory
	 */	
	public void setRandGenFactory(IRandGenFactory randGenFactory) 
	{
		this.randGenFactory = randGenFactory;
	}
	
	/**
	 * <p>
	 * Returns individual provider
	 * </p>
	 * @return Current individuals provider
	 */	
	public final IProvider<I> getProvider() 
	{
		return provider;
	}

	/**
	 * <p>
	 * Sets the individuals provider used in the init task
	 * </p>
	 * @param provider New individuals provider
	 */	
	public final void setProvider(IProvider<I> provider) 
	{	
		// Set provider
		this.provider = provider;
		// Contextualize provider
		this.provider.contextualize(this);
	}
	
	/**
	 * <p>
	 * Returns population size of the algorithm
	 * </p>
	 * @return int Population size
	 */	
	public final int getPopulationSize() 
	{
		return populationSize;
	}

	/**
	 * <p>
	 * Sets population size of the algorithm
	 * </p>
	 * @param populationSize New population size to set
	 */	
	public final void setPopulationSize(int populationSize) 
	{
		this.populationSize = populationSize;
	}
	
	/**
	 * <p>
	 * Get the maximum number of iterations for this algorithm
	 * </p>
	 * @return Max of generations
	 */
	public final int getMaxOfGenerations() 
	{
		return maxOfGenerations;
	}	

	/**
	 * <p>
	 * Set the maximum number of iterations for this algorithm
	 * </p>
	 * @param maxOfGenerations Max of generations
	 */
	public final void setMaxOfGenerations(int maxOfGenerations) 
	{
		this.maxOfGenerations = maxOfGenerations;
	}
	
	/**
	 * <p>
	 * Returns first individual mutator
	 * </p>
	 * @return IMutator First individual mutator
	 */
	public IMutator<I> getMutator1() 
	{
		return mutator1;
	}

	/**
	 * <p>
	 * Sets first individual mutator
	 * </p>
	 * @param mutator1 First individual mutator
	 */
	public void setMutator1(IMutator<I> mutator1) 
	{
		// Sets mutator1
		this.mutator1 = mutator1;
		// Contextualize mutator1
		this.mutator1.contextualize(this);
	}
	
	/**
	 * <p>
	 * Returns first second mutator
	 * </p>
	 * @return IMutator Second individual mutator
	 */
	public IMutator<I> getMutator2() 
	{
		return mutator2;
	}

	/**
	 * <p>
	 * Sets second individual mutator
	 * </p>
	 * @param mutator2 Second individual mutator
	 */
	public void setMutator2(IMutator<I> mutator2) 
	{
		// Sets mutator2
		this.mutator2 = mutator2;
		// Contextualize mutator2
		this.mutator2.contextualize(this);
	}

	/**
	 * <p>
	 * Returns creation ratio of the algorithm
	 * </p>
	 * @return double Creation ratio of the algorithm
	 */
	public double getCratio() {
		return cratio;
	}
	
	/**
	 * <p>
	 * Sets creation ratio of the algorithm
	 * </p>
	 * @param cratio New creation ratio to set
	 */
	public void setCratio(double cratio) {
		this.cratio = cratio;
	}
	
	/**
	 * <p>
	 * Returns percentage of census selected to be mutated 
	 * with second mutator
	 * </p>
	 * @return int Percentage of census
	 */	
	public int getPercentageSecondMutator() {
		return percentageSecondMutator;
	}

	/**
	 * <p>
	 * Sets percentage of census selected to be mutated 
	 * with second mutator
	 * </p>
	 * @param percentageSecondMutator New percentage of census
	 */
	public void setPercentageSecondMutator(int percentageSecondMutator) {
		this.percentageSecondMutator = percentageSecondMutator;
		nofselSecondMutator = (int)((percentageSecondMutator/100.)*populationSize);
	}
	
	/**
	 * <p>
	 * Returns significative fitness difference
	 * </p>
	 * @return double Significative fitness difference
	 */	
	public double getFitDif() {
		return fitDif;
	}

	/**
	 * <p>
	 * Sets significative fitness difference
	 * </p>
	 * @param fitDif New significative fitness difference
	 */	
	public void setFitDif(double fitDif) {
		this.fitDif = fitDif;
	}

	/**
	 * <p>
	 * Returns maximum number of generations allowed without 
	 * improving best fitness
	 * </p>
	 * @return int Maximum number of generations
	 */	
	public int getMogbest() {
		return mogbest;
	}
	
	/**
	 * <p>
	 * Sets maximum number of generations allowed without 
	 * improving best fitness
	 * </p>
	 * @param mogbest Maximum number of generations
	 */
	public void setMogbest(int mogbest) {
		this.mogbest = mogbest;
	}

	/**
	 * <p>
	 * Returns maximum number of generations allowed without 
	 * improving mean fitness
	 * </p>
	 * @return int Maximum number of generations
	 */	
	public int getMogmean() {
		return mogmean;
	}
	
	/**
	 * <p>
	 * Sets maximum number of generations allowed without 
	 * improving mean fitness
	 * </p>
	 * @param mogmean Maximum number of generations
	 */
	public void setMogmean(int mogmean) {
		this.mogmean = mogmean;
	}
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------- java.lang.Object methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Compare this algorithm to another
	 * </p>
         * @param other Object to compare with.
	 * @return a boolean indicating if the algorithms are equal
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof NeuralNetAlgorithm) {
			NeuralNetAlgorithm cother = (NeuralNetAlgorithm) other;
			EqualsBuilder eb = new EqualsBuilder();
			
			// Random generators factory
			eb.append(randGenFactory, cother.randGenFactory);
			// Individual species
			eb.append(species, cother.species);
			// Individuals evaluator
			eb.append(evaluator, cother.evaluator);
			// Individuals provider
			eb.append(provider, cother.provider);
			// Population size
			eb.append(populationSize, cother.populationSize);
			// Max of generations
			eb.append(maxOfGenerations, cother.maxOfGenerations);			
			// Individual mutator1
			eb.append(mutator1, cother.mutator1);			
			// Individual mutator2
			eb.append(mutator2, cother.mutator2);
			// Ratio "elements created"/"elements remaining"			
			eb.append(cratio, cother.cratio);
			// Percentage of parents mutated with second mutator			
			eb.append(percentageSecondMutator, cother.percentageSecondMutator);
			// Number of parents mutated with second mutator			
			eb.append(nofselSecondMutator, cother.nofselSecondMutator);			
			// Maximum number of generations allowed without improving
			// the fitness mean of best individuals			
			eb.append(mogmean, cother.mogmean);			
			// Maximum number of generations allowed without improving
			// the best fitness			
			eb.append(mogbest, cother.mogbest);			
			// Difference between two fitness that we consider
		    // enoung to say that the fitness has improved			
			eb.append(fitDif, cother.fitDif);
			// Return test result
			return eb.isEquals();
		}
		else {
			return false;
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------- Implementing IAlgorithm interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Create individuals in population, evaluating before start rest
	 * of evolution
	 * </p>
	 */	
	@SuppressWarnings("unchecked")
	public void doInit()
	{
	    // Init state variables
	    currentBest=0.;
	    previousBest=0.;
	    currentMean=0.;
	    previousMean=0.;
	    finished=false;
	    generation=0;
	    
	    // Init counters
	    nogbest=0;
	    nogmean=0;

	    //long t1 = System.currentTimeMillis();
		// Provide initial (cratio*populationSize) individuals
		bset = provider.provide((int) cratio*populationSize);
		
		// Evaluate individuals
		evaluator.evaluate(bset);
		
		//long t2 = System.currentTimeMillis();
		//System.out.println("InicializaciÃ³n " + (t2-t1));

		// Select (populationSize) best individuals
		doSort();

		// Best individual of the set
		bestIndividual = (I) bset.get(0).copy();
		
		//ParametricMutator of NeuralNetAlgorithm
		if(mutator1 instanceof ParametricMutator)
			((ParametricMutator<? extends IIndividual>) mutator1).alphaInit();
		if(mutator2 instanceof ParametricMutator)
			((ParametricMutator<? extends IIndividual>) mutator2).alphaInit();
	}
	
	/**
	 * <p>
	 * Iteration of the algorithm
	 * </p>
	 */	
	public void doIterate() 
	{
		// Increments generation counter
		generation++;
		// Do selection
		doSelection();
		// Do generation
		doGeneration();
		// Do replacement
		doReplacement();
		// Do update
		doUpdate();
		// Do control
		doControl();
	}
	
	/**
	 * <p>
	 * Check if evolution is finished. Default implementation of this
	 * method performs the operations:
	 * 
	 * <ul>
	 * <li>
	 * If number of generations exceeds the maximum permited, set the
	 * finished flag to true. Else, the flag remains false
	 * </li>
	 * <li>
	 * If number of generations without improving best fitness exceeds 
	 * the maximum permited and number of generations without improving
	 * mean fitness exceeds the maximum permited, set the finished flag
	 * to true. Else, the flag remains false
	 * </li>
	 * </ul>
	 * </p>
	 */	
	protected void doControl(){
		// If max of generations reached ...
		if (generation >= maxOfGenerations){
			finished = true;
			return;
		}
		
		// If max of generations without improving best fitness 
		// and max of generations without improving mean fitness 
		// reached ...
		if(nogbest >= mogbest && nogmean >= mogmean){
			finished = true;
			return;
		}
	}	
		
	/**
	 * <p>
	 * Implementation for the Selection task
	 * </p>
	 */	
	protected void doSelection() 
	{				
		// First mutator individual selection
		pset1 = bset.subList(nofselSecondMutator, populationSize);
		
		// Second mutator individual selection
		pset2 = bset.subList(0, nofselSecondMutator);
	}
	
	/**
	 * <p>
	 * Implementation for the Generation task
	 * 
	 * IMPORTANT NOTE: Parametric and structural mutators work directly with 
	 *                 the individuals instead of returning a mutated copy of them. This is
	 *                 for performance reasons. If you want to use another mutator
	 *                 you have to consider that individuals will be changed when
	 *                 you use these mutators.
	 * </p>
	 */	
	protected void doGeneration() 
	{
		// Mutate individuals using mutator 1
		//long t1 = System.currentTimeMillis();
		cset = mutator1.mutate(pset1);
		//long t2 = System.currentTimeMillis();
		//System.out.println("Mutacion Estructural => " + (t2-t1));
		
		// Evaluate individuals
		//t1 = System.currentTimeMillis();
		evaluator.evaluate(cset);
		//t2 = System.currentTimeMillis();
		//System.out.println("Evaluacion Estructural => " + (t2-t1));
		
		// First mutated individual must be mutated
		// with both mutators
		// => Extract first mutated individual
		I firstNnindPset1 = pset1.get(0);
		cset.remove(firstNnindPset1);
		// => ... and add it to the second mutation
		pset2.add(firstNnindPset1);
		
		// Mutate individuals using mutator 2
		//t1 = System.currentTimeMillis();
		cset.addAll(mutator2.mutate(pset2));
		//t2 = System.currentTimeMillis();
		//System.out.println("Mutacion Parametrica => " + (t2-t1));
		
		// Evaluate individuals
		//t1 = System.currentTimeMillis();
		evaluator.evaluate(cset);
		//t2 = System.currentTimeMillis();
		//System.out.println("Evaluacion Parametrica => " + (t2-t1));
	}
	
	/**
	 * <p>
	 * Implementation for the Replacement task
	 * </p>
	 */	
	protected void doReplacement() 
	{
		// Do nothing
	}

	/**
	 * <p>
	 * Implementation for the Update task
	 * </p>
	 */	
	@SuppressWarnings("unchecked")
	protected void doUpdate()
	{
		// Include previous best individual (Elitist algorithm)
		cset.add(bestIndividual);
		
		// Update bset
		bset = cset;
		
		// Sort individuals
		doSort();
		
		// Clear pset1, pset2, rset & cset
		pset1 = null;
		pset2 = null;
		rset = null;
		cset = null;
		
		// Best individual of the set
		bestIndividual = (I) bset.get(0).copy();
		
		//Obtain best fitness
		previousBest = currentBest;
		currentBest = ((SimpleValueFitness)bestIndividual.getFitness()).getValue();
		
		//Obtain mean fitness
		previousMean = currentMean;
		currentMean = 0;
		for(int i=0; i<nofselSecondMutator; i++)
			currentMean += ((SimpleValueFitness)bset.get(i).getFitness()).getValue();
		currentMean/=nofselSecondMutator;
		
		// Update variables
		if(previousBest+fitDif < currentBest)
			nogbest=0;
		else
			nogbest++;		
		if(previousMean+fitDif < currentMean)
			nogmean=0;
		else
			nogmean++;
	}
	
	/**
	 * <p>
	 * Sort individuals in bset
	 * </p>
	 */	
	@SuppressWarnings("unchecked")
	protected void doSort(){
		// => Sort an array copy
		I[] tmp = (I[]) bset.toArray(new IIndividual[bset.size()]);
		Arrays.sort(tmp,individualsComparator);
		// => Clear bset
		bset.clear();
		// => Add (census - nofselSecondMutator) last individuals (bests) to bset
		for (int i=tmp.length-1, j=populationSize; j>nofselSecondMutator; j--,i--)
			bset.add(tmp[i]);
		// => Duplicate nofselSecondMutator last individuals
		for (int i=tmp.length-1, j=populationSize; j>(populationSize-nofselSecondMutator); j--,i--)
			bset.add((I)tmp[i].copy());
	}

	/**
	 * <p>
	 * Factory method.
	 * 
	 * @return A new instance of a random generator
	 * </p>
	 */	
	public IRandGen createRandGen() {
		return randGenFactory.createRandGen();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------- Implementing IConfigure interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Configuration parameters for NeuralNetAlgorithm class are:
         * 
	 * <ul>
	 * <li>
	 * <code>species: ISpecies (complex)</code>
	 * Individual species
	 * </li> 
         * <li>
	 * <code>evaluator IEvaluator (complex)</code>
	 * Individuals evaluator
	 * </li><li>
	 * <code>population-size (int)</code>
	 * Population size
	 * </li><li>
	 * <code>max-of-generations (int)</code>
	 * Maximum number of generations
	 * </li>
	 * <li>
	 * <code>provider: IProvider (complex)</code>
	 * Individuals provider
	 * </li>
	 * <li>
	 * <code>mutator1: IMutator (complex)</code>
	 * Individuals mutator1
	 * </li>
	 * <li>
	 * <code>mutator2: IMutator (complex)</code>
	 * Individuals mutator2
	 * </li>
	 * <li>
	 * <code>creation-ratio (double)</code>
	 * Ratio "elements created"/"elements remaining"
	 * </li>
	 * <li>
	 * <code>percentage-second-mutator (int)</code>
	 * Percentage of individuals mutated with second mutator
	 * </li>
	 * <li>
	 * <code>max-generations-without-improving-mean (int)</code>
	 * Maximum number of generations without improving mean fitness
	 * </li>
	 * <li>
	 * <code>max-generations-without-improving-best (int)</code>
	 * Maximum number of generations without improving best fitness
	 * </li>
	 * <li>
	 * <code>fitness-difference (double)</code>
	 * Difference between two fitness that we consider
     * enough to say that the fitness has improved
	 * </li>
	 * </ul>
	 * </p>
	 * @param configuration Configuration object to obtain the parameters
	 */	
	@SuppressWarnings("unchecked")
	public void configure(Configuration configuration){
		
		// Random generators factory
		try {
			// Species classname
			String randGenFactoryClassname = 
				configuration.getString("rand-gen-factory[@type]");
			// Species class
			Class<? extends IRandGenFactory> randGenFactoryClass = 
				(Class<? extends IRandGenFactory>) Class.forName(randGenFactoryClassname);
			// Species instance
			IRandGenFactory randGenFactory = randGenFactoryClass.newInstance();
			// Configure species
			if (randGenFactory instanceof IConfigure) {
				((IConfigure) randGenFactory).configure
					(configuration.subset("rand-gen-factory"));
			}
			// Set species
			setRandGenFactory(randGenFactory);
		} 
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException
				("Illegal random generators factory classname");
		} 
		catch (InstantiationException e) {
			throw new ConfigurationRuntimeException
				("Problems creating an instance of random generators factory", e);
		} 
		catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException
				("Problems creating an instance of random generators factory", e);
		}	
		// Individual species
		try {
			// Species classname
			String speciesClassname = 
				configuration.getString("species[@type]");
			// Species class
			Class<ISpecies<I>> speciesClass = 
				(Class<ISpecies<I>>) Class.forName(speciesClassname);
			// Species instance
			ISpecies<I> species = speciesClass.newInstance();
			// Configure species if neccesary
			if(species instanceof IConfigure){
				// Extract species configuration
				Configuration speciesConfiguration = configuration.subset("species");
				// Configure species
				((IConfigure)species).configure(speciesConfiguration);
			}
			// Set species
			setSpecies(species);
		} 
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal species classname");
		} 
		catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of species", e);
		} 
		catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of species", e);
		}
		// Individuals evaluator
		try {
			// Evaluator classname
			String evaluatorClassname = 
				configuration.getString("evaluator[@type]");
			// Evaluator class
			Class<IEvaluator<I>> evaluatorClass = 
				(Class<IEvaluator<I>>) Class.forName(evaluatorClassname);
			// Evaluator instance
			IEvaluator<I> evaluator = evaluatorClass.newInstance();
			// Configure evaluator if neccesary
			if(evaluator instanceof IConfigure){
				// Extract species configuration
				Configuration evaluatorConfiguration = configuration.subset("evaluator");
				// Configure evaluator
				((IConfigure)evaluator).configure(evaluatorConfiguration);
			}
			// Set species
			setEvaluator(evaluator);
		} 
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal evaluator classname");
		} 
		catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of evaluator", e);
		} 
		catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of evaluator", e);
		}
		// Population size
		int populationSize = configuration.getInt("population-size");
		setPopulationSize(populationSize);
		// Maximum of generations
		int maxOfGenerations = configuration.getInt("max-of-generations"); 
		setMaxOfGenerations(maxOfGenerations);
		// Individuals provider
		try {
			// Provider classname
			String providerClassname = 
				configuration.getString("provider[@type]");
			// Provider class
			Class<IProvider<I>> providerClass = 
				(Class<IProvider<I>>) Class.forName(providerClassname);
			// Provider instance
			IProvider<I> provider = providerClass.newInstance();
			// Configure provider if neccesary
			if(provider instanceof IConfigure){
				// Extract provider configuration
				Configuration providerConfiguration = configuration.subset("provider");
				// Configure provider
				((IConfigure)provider).configure(providerConfiguration);
			}
			// Set provider
			setProvider(provider);
		} 
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal provider classname");
		} 
		catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of provider", e);
		} 
		catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of provider", e);
		}		
		// Individuals mutator1
		try {
			// Mutator1 classname
			String mutator1Classname = 
				configuration.getString("mutator1[@type]");
			// Mutator1 classe
			Class<IMutator<I>> mutator1Class = 
				(Class<IMutator<I>>) Class.forName(mutator1Classname);
			// Mutator1 instance
			IMutator<I> mutator1 = mutator1Class.newInstance();
			// Configure mutator1 if neccesary
			if(mutator1 instanceof IConfigure){
				// Extract mutator1 configuration
				Configuration mutator1Configuration = configuration.subset("mutator1");
				// Configure mutator1
				((IConfigure)mutator1).configure(mutator1Configuration);				
			}
			// Set mutator1
			setMutator1(mutator1);
		} 
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal mutator1 classname");
		} 
		catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator1", e);
		} 
		catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator1", e);
		}
		// Individuals mutator2
		try {
			// Mutator2 classname
			String mutator2Classname = 
				configuration.getString("mutator2[@type]");
			// Mutator2 class
			Class<IMutator<I>> mutator2Class = 
				(Class<IMutator<I>>) Class.forName(mutator2Classname);
			// Mutator2 instance
			IMutator<I> mutator2 = mutator2Class.newInstance();
			// Configure mutator2 if neccesary
			if(mutator2 instanceof IConfigure){
				// Extract mutator2 configuration
				Configuration mutator2Configuration = configuration.subset("mutator2");
				// Configure mutator2 
				((IConfigure)mutator2).configure(mutator2Configuration);
			}
			// Set mutator2
			setMutator2(mutator2);
		} 
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal mutator2 classname");
		} 
		catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator2", e);
		} 
		catch (IllegalAccessException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator2", e);
		}
		// Creation ratio
		double cratio = configuration.getDouble("creation-ratio");
		setCratio(cratio);
		// Percentage of individuals mutated with second mutator
		int percenageMutator2 = configuration.getInt("percentage-second-mutator"); 
		setPercentageSecondMutator(percenageMutator2);
		// Maximum of generations without improving mean fitness
		int mogmean = configuration.getInt("max-generations-without-improving-mean");
		setMogmean(mogmean);
		// Maximum of generations without improving best fitness
		int mogbest = configuration.getInt("max-generations-without-improving-best");
		setMogbest(mogbest);
		// Significative fitness difference
		double fitDif = configuration.getDouble("fitness-difference");
		setFitDif(fitDif);		
	}
}

