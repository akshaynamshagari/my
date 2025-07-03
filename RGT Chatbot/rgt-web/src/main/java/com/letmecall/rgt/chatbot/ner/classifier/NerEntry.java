package com.letmecall.rgt.chatbot.ner.classifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NerEntry {
	
	private static final Logger logger = LoggerFactory.getLogger(NerEntry.class);
	
	private String token;
	private String originalToken;
	private String bestLabel;
	private Collection<TokenLabel> allLabels;
	private double certainty = -1.0;
	
	public NerEntry(String originalToken, String token, String bestLabel, Collection<TokenLabel> allLabels){
		this.originalToken = originalToken;
		this.token = token;
		this.bestLabel = bestLabel;
		this.allLabels = allLabels;
	}
	
	public String getToken(){
		return token;
	}
	
	public String getOriginalToken(){
		return originalToken;
	}
	
	public String getBestLabel(){
		return bestLabel;
	}
	
	public Collection<TokenLabel> getAllLabels(){
		return allLabels;
	}
	public void addAllLabels(Collection<TokenLabel> labels){
		if (allLabels == null) allLabels = new ArrayList<>();
		allLabels.addAll(labels);
	}
	
	public void addToken(String tokenToAdd){
		token += (" " + tokenToAdd);
	}
	public void addOriginalToken(String tokenToAdd){
		originalToken += (" " + tokenToAdd);
	}
	
	public void setCertainty(double c){
		certainty = c;
	}
	/**
	 * If this value is supported it will be between 0.0-1.0. Else it will be negative -1.0.
	 * @return
	 */
	public double getCertainty(){
		return certainty;
	}
	
	@Override
	public String toString(){
		return (originalToken + "=" + bestLabel);
	}
	
	//---------- helpers ----------
	
	/**
	 * Take a list of NER entries and convert the best labels to one string. Handy for comparing to test data.
	 * @param entryList
	 * @return
	 */
	public static String getLabelStringFromEntryList(List<NerEntry> entryList){
		logger.debug("getLabelStringFromEntryList:INVOKED");
		StringBuffer buffer = new StringBuffer();
		for (NerEntry ne : entryList){
			buffer.append(ne.bestLabel);
			buffer.append(" ");
		}
		return buffer.toString().trim();
	}
	/**
	 * Take a list of NER entries and convert the normalized tokens to one string.
	 * @param entryList
	 * @return
	 */
	public static String getTokenStringFromEntryList(List<NerEntry> entryList){
		logger.debug("getTokenStringFromEntryList:INVOKED");
		StringBuffer buffer = new StringBuffer();
		for (NerEntry ne : entryList){
			buffer.append(ne.token);
			buffer.append(" ");
		}
		return buffer.toString().trim();
	}

}
