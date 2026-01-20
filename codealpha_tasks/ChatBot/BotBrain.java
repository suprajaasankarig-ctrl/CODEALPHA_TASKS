public class BotBrain {
    public String getResponse(String input) {
        input = input.toLowerCase();

        if (input.contains("hi") || input.contains("hello")) {
            return "Heyyy 👋 What’s up?";
        }
        else if (input.contains("how are you")) {
            return "I’m just code, but I’m vibing 😌";
        }
        else if (input.contains("your name")) {
            return "I’m your friendly Java chatbot 🤖";
        }
        else if (input.contains("ai")) {
            return "AI is about making machines think smart—kinda like me 😎";
        }
        else if (input.contains("java")) {
            return "Java is powerful, portable, and slightly dramatic ☕";
        }
        else if (input.contains("bye")) {
            return "Bye bestie 👋 Come back soon!";
        }
        else {
            return "Hmm… I’m still learning 🌱 Try asking something else!";
        }
    }
}
