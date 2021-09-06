import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static ServerSocket server;
    private static Socket socket;
    private static DataInputStream din;
    private static DataOutputStream dos;
    private static JTextArea jt;
    private static JTextField tf;
    private static JFileChooser fc;
    private static byte b[];
    private static InputStream is;
    private static FileOutputStream fr;
    private static void readMessage() {
        //Running thread to listen client
        Thread threadRead = new Thread() {
            public void run() {
                while (true) {
                    try {
                        String st = din.readUTF();
                        jt.append(st);
                    } catch (IOException e) {
                        e.printStackTrace();
                        jt.append("Client logout...\n");
                        break;
                    }
                }
            }
        };
        threadRead.start();
        jt.append("Thread read message running...\n");
    }

    private static void sendMessage(String message) {
        Thread threadSend = new Thread() {
            public void run() {
                    try {
                        dos.writeUTF("Server: " + message+ "\n");
                        jt.append("Server: " + message + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        };
        threadSend.start();
    }
    private static void readFile() {

                        try {
                            is.read(b, 0, b.length);
                            fr.write(b, 0, b.length);
                        } catch (
                                Exception ex) {
                            ex.printStackTrace();
                        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat with client");
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        Border paneEdge = BorderFactory.createEmptyBorder(10,10,10,10);
        Border blackLine = BorderFactory.createTitledBorder("Message");
        contentPanel.setBorder(paneEdge);
        JPanel containtJTextArea = new JPanel();
        containtJTextArea.setLayout(new BorderLayout());
        containtJTextArea.setBorder(blackLine);
        jt = new JTextArea();
        jt.setEditable(false);
        JScrollPane jp = new JScrollPane(jt);
        containtJTextArea.add(jp);
        contentPanel.add(containtJTextArea, BorderLayout.CENTER);

        JPanel pageEnd = new JPanel();
        pageEnd.setBorder(new EmptyBorder(10,0,0,0));
        pageEnd.setLayout(new GridLayout(0,2));
            //Left
            tf = new JTextField(20);
            pageEnd.add(tf);

            //Right
            JPanel containBtn = new JPanel();
            containBtn.setLayout(new GridLayout(0, 2));
            JButton btnSend = new JButton("Send");
            btnSend.setActionCommand("Send message");
            btnSend.addActionListener(new ButtonAction());
            containBtn.add(btnSend);
            JButton btnSendFile = new JButton("Send file");
            btnSendFile.setActionCommand("Send file");
            btnSendFile.addActionListener(new ButtonAction());
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            containBtn.add(btnSendFile);
            pageEnd.add(containBtn);
        contentPanel.add(pageEnd, BorderLayout.PAGE_END);
        frame.setContentPane(contentPanel);

        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        try {
            server = new ServerSocket(7000);
            jt.append("Server run on " +  server.getInetAddress() +", on port: " +server.getLocalPort() +"....\n");
            socket = server.accept();
            if(socket != null) {
                jt.append("Client[ip: " + socket.getInetAddress() + ", port: " + socket.getLocalPort() + "] " + "connected...\n");
            }
            din = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            //File declare
            b = new byte[2002];
            try {
                is = socket.getInputStream();
                fr = new FileOutputStream("/home/huygrogbro/ClientServer/FileServer/Client.txt");
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
            //Running thread read message
            //readMessage();

            //Running thread listen file send
            readFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static class ButtonAction extends Component implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            switch (actionEvent.getActionCommand()) {
                case "Send message":
                    if(!tf.getText().isEmpty() && tf.getText() != null) {
                        sendMessage(tf.getText());
                        tf.setText("");
                        System.out.println("ok");
                    } else {
                        System.out.println("Empty text field");
                    }
                    break;
                case "Send file":
                    int returnVal = fc.showOpenDialog(this);
                    if(socket == null) {
                        JOptionPane.showMessageDialog(this, "Wait for client connect to your server!", "Client error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            sendFile(file);
                        } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                            System.out.println("Cancel");
                        } else {
                            JOptionPane.showMessageDialog(this, "File is approved here!", "File error", JOptionPane.WARNING_MESSAGE);
                        }
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
