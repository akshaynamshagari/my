package com.letmecall.rgt.chatbot.ner.classifier;

/**
 * A class that holds a label for a token extracted by a model with extra info like certainty.
 */
public class TokenLabel {
	String token;
	String label;
	Double certainty = -1.0;
	
	public TokenLabel(String token, String label, Double certrainty){
		this.label = label;
		this.certainty = certrainty;
	}
	
	public String getToken(){
		return token;
	}
	
	public String getLabel(){
		return label;
	}
	
	public double getCertainty(){
		return certainty;
	}
	
	@Override
	public String toString(){
		return label;
	}
	public String toStringComplex(){
		return (label + " (" + certainty + ")");
	}
}