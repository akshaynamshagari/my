package com.letmecall.rgt.chatbot.model;

import java.util.List;

public class IntentRequest {
        private String intentName;
        private List<String> patterns;
        private List<Response> responses;
        private String language;

        public String getIntentName() {
            return intentName;
        }

        public void setIntentName(String intentName) {
            this.intentName = intentName;
        }

        public List<String> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }

        public List<Response> getResponses() {
            return responses;
        }

        public void setResponses(List<Response> responses) {
            this.responses = responses;
        }

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}
    }