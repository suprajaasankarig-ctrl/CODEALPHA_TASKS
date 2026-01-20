import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatBot extends JFrame implements ActionListener {

    JTextArea chatArea;
    JTextField inputField;
    JButton sendButton;
    BotBrain brain;

    public ChatBot() {
        brain = new BotBrain();

        setTitle("AI Chatbot");
        setSize(400, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(this);
        inputField.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String userText = inputField.getText();
        chatArea.append("You: " + userText + "\n");

        String botReply = brain.getResponse(userText);
        chatArea.append("Bot: " + botReply + "\n\n");

        inputField.setText("");
    }

    public static void main(String[] args) {
        new ChatBot();
    }
}
