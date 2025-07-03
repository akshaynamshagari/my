package com.letmecall.rgt.chatbot.intent;

import java.io.IOException;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;

public class IntentDocumentSampleStream implements ObjectStream<DocumentSample> {
	
	private static final Logger logger = LoggerFactory.getLogger(IntentDocumentSampleStream.class);

	private String category;
	private ObjectStream<String> stream;

	public IntentDocumentSampleStream(String category, ObjectStream<String> stream) {
		this.category = category;
		this.stream = stream;
	}

	public DocumentSample read() throws IOException {
		logger.debug("read:INVOKED");
		String sampleString = stream.read();

		if (sampleString != null) {

			String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(sampleString);

			Vector<String> vector = new Vector<String>(tokens.length);
			for (String token : tokens) {
				if (!token.startsWith("<")) {
					vector.add(token);
				}
			}

			tokens = new String[vector.size()];
			vector.copyInto(tokens);

			DocumentSample sample;

			if (tokens.length > 0) {
				sample = new DocumentSample(category, tokens);
			} else {
				throw new IOException("Empty lines are not allowed!");
			}

			return sample;
		} else {
			return null;
		}
	}

	public void reset() throws IOException, UnsupportedOperationException {
		stream.reset();
	}

	public void close() throws IOException {
		stream.close();
	}
}