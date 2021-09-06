import org.w3c.dom.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static DataInputStream din;
    static DataOutputStream dos;
    static JTextField inputMessage;
    static String message;
    static Socket socket;
    static JTextArea displayMessage;
    static JFileChooser fc;

    //File declare
    static byte b[];
    static InputStream is;
    static FileOutputStream fr;

    private static void readFile() {
                    try {
                        is.read(b, 0, b.length);
                        fr.write(b, 0, b.length);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
    }
    public static void chatWithServer(String msg) {
        try {
            //DataInputStream din = new DataInputStream(socket.getInputStream());
                dos.writeUTF("Client: " + msg + "\n");
                dos.flush();
                System.out.println("Ok");
                //Nhan du lieu tu server
                //String st = din.readUTF();
                //System.out.println(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readInServer() {
        Thread threadRead = new Thread() {
            public void run() {
                while (true) {
                    try {
                        String st = din.readUTF();
                        displayMessage.append(st);
                    } catch (IOException e) {
                        System.out.println("Server stop...");
                        displayMessage.append("Server stop...\n");
                        break;
                    }
                }
            }
        };
        threadRead.start();
    }
    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 7000);
            dos = new DataOutputStream(socket.getOutputStream());
            din = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //File declare
        b = new byte[2002];
        try {
            is = socket.getInputStream();
            fr = new FileOutputStream("/home/huygrogbro/ClientServer/FileClient/Server.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Chat with server");
        displayMessage = new JTextArea();

        //Listen to server
        //readInServer();

        //Lister send file
        readFile();
        JPanel contentOfFrame = new JPanel();
        contentOfFrame.setLayout(new GridLayout(2, 1));
        frame.add(contentOfFrame);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(new JPanel(), BorderLayout.PAGE_START);
        topPanel.add(new JPanel(), BorderLayout.LINE_START);
        topPanel.add(new JPanel(), BorderLayout.LINE_END);
        JPanel displayArea = new JPanel();
        displayArea.setLayout(new BorderLayout());
        displayMessage.setEditable(false);
        JScrollPane jp = new JScrollPane(displayMessage);
        displayArea.add(jp, BorderLayout.CENTER);

        JPanel inputFieldAndSendButton = new JPanel();
        inputFieldAndSendButton.setLayout(new BorderLayout());
        inputMessage = new JTextField(20);
        inputFieldAndSendButton.add(inputMessage, BorderLayout.CENTER);
        JButton sendMessage = new JButton("Send Message");
        sendMessage.setActionCommand("Send message");
        sendMessage.addActionListener(new ButtonChooseFile());
        inputFieldAndSendButton.add(sendMessage, BorderLayout.LINE_END);
        displayArea.add(inputFieldAndSendButton, BorderLayout.PAGE_END);
        topPanel.add(displayArea, BorderLayout.CENTER);
            /*
            TextArea displayMessage = new TextArea();
            topPanel.add(displayMessage, BorderLayout.CENTER);
             */
        contentOfFrame.add(topPanel);

        //Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(new JLabel(""), BorderLayout.CENTER);
        bottomPanel.add(new JLabel(""), BorderLayout.LINE_END);
        bottomPanel.add(new JLabel(""), BorderLayout.LINE_START);
        bottomPanel.add(new JLabel(""), BorderLayout.PAGE_END);

        JPanel containButtonLayout = new JPanel();
        containButtonLayout.setLayout(new GridLayout(0, 3));
        containButtonLayout.add(new JLabel(""));
        JButton sendFile = new JButton("Send file");
        sendFile.setActionCommand("Send file");
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        sendFile.addActionListener(new ButtonChooseFile());
        containButtonLayout.add(sendFile);
        containButtonLayout.add(new JLabel(""));
        containButtonLayout.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomPanel.add(containButtonLayout, BorderLayout.PAGE_START);

        //JPanel content add
        contentOfFrame.add(bottomPanel);

        frame.setSize(700, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public static class ButtonChooseFile extends Component implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            switch (actionEvent.getActionCommand()) {
                case "Send message":
                    if(socket != null) {
                        if (inputMessage.getText() != null && !inputMessage.getText().isEmpty()) {
                            message = inputMessage.getText();
                            displayMessage.append("Client: " + message + "\n");
                            chatWithServer(message);
                            message = "";
                            inputMessage.setText("");
                        } else {
                            System.out.println("Please input");
                        }
                    } else  {
                        JOptionPane.showMessageDialog(this, "Server is close..", "Server error", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case "Send file":
                    int returnVal = fc.showOpenDialog(this);
                    if(socket != null) {
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            sendFile(file);
                        } else {
                            JOptionPane.showMessageDialog(this, "File is approved here", "File error", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Server is close..", "Server error", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }

        }
        public void sendFile(File file) {
                    try {
                        FileInputStream fr = new FileInputStream(file);
                        byte b[] = new byte[2002];
                        fr.read(b, 0, b.length);
                        OutputStream os = socket.getOutputStream();
                        os.write(b, 0, b.length);
                    } catch (
                            FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        }
    }
}
