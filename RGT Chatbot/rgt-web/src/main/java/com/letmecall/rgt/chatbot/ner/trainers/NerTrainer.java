package com.letmecall.rgt.chatbot.ner.trainers;

/**
 * Interface for training NER models.
 * 
 * @author Florian Quirin
 *
 */
public interface NerTrainer {
	
	/**
	 * Train model with info given in constructor and store it.
	 */
	public void train();
}
