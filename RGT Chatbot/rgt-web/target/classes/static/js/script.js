document.addEventListener("DOMContentLoaded", function() {
	const iframe = document.getElementById("chatbot-iframe");
	iframe.src = `http://localhost:8080/api/rgt/chatbot/ui?backendUrl=http://localhost:8080/api/rgt/chatbot/chat`;
});
