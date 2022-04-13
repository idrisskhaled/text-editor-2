package Part2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Useri extends JFrame implements ActionListener {
    static String[] QUEUE_NAMES1;
    static String[] QUEUE_NAMES2;
    String bold = "N";
    JPanel[] panel;
    JPanel panel1;
    JLabel lb;
    String italic = "N";
    boolean accessible = false;
    JTextArea[] textAreas;
    JScrollPane[] scrollPanes;
    JLabel[] fontLabels;
    JSpinner[] fontSizeSpinners;
    JButton[] fontColorButtons;
    JButton[] boldButtons;
    JButton[] italicButtons;
    JComboBox[] fontBoxs;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem exitItem;
    JLabel label;
    JTextField field;
    JButton submit;
    JLabel result;
    private String message = "";
    private int nbUsers;
    private JSONObject message1;
    private JSONObject message2[];
    private int userId = -1;

    Useri() {
        System.out.println("constructeur");
        message1 = new JSONObject();
        message1.put("text", "");
        message1.put("free",1);

        try {
            getNbUsers();
        } catch (IOException | TimeoutException ex) {
            ex.printStackTrace();
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Task " + userId);
        this.setSize(950, 600);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);
        field = new JTextField();
        result = new JLabel();
        label = new JLabel("User id: ");
        field.setPreferredSize(new Dimension(100, 30));
        submit = new JButton("submit");
        submit.addActionListener(this);


        // ------ menubar ------

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(this);

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // ------ /menubar ------
        this.setJMenuBar(menuBar);
        panel1 = new JPanel();
        this.panel1.add(label);
        this.panel1.add(field);
        this.panel1.add(result);
        this.panel1.add(submit);
        this.add(panel1);
        panel1.setVisible(true);
        result.setVisible(false);
        this.setVisible(true);
    }

    public static void main(String[] args) {

        new Useri();

    }

    public void initiateComponents() {
        System.out.println("initiate "+nbUsers);
        QUEUE_NAMES1 = new String[nbUsers];
        QUEUE_NAMES2 = new String[nbUsers];
        textAreas = new JTextArea[nbUsers];
        scrollPanes = new JScrollPane[nbUsers];
        fontLabels = new JLabel[nbUsers];
        fontSizeSpinners = new JSpinner[nbUsers];
        fontColorButtons = new JButton[nbUsers];
        boldButtons = new JButton[nbUsers];
        italicButtons = new JButton[nbUsers];
        fontBoxs = new JComboBox[nbUsers];
        message2 = new JSONObject[nbUsers];

        for (int j = 0; j < nbUsers; j++) {
            message2[j]=new JSONObject();
            message2[j].put("fontSize", 20);
            message2[j].put("fontFamily", "Arial");
            message2[j].put("red", 0);
            message2[j].put("green", 0);
            message2[j].put("blue", 0);
            message2[j].put("bold", 0);
            message2[j].put("italic", 0);
            QUEUE_NAMES1[j] = "text" + j;
            QUEUE_NAMES2[j] = "format" + j;
            fontColorButtons[j] = new JButton("Color");
            fontColorButtons[j].addActionListener(this);
            textAreas[j] = new JTextArea();

            textAreas[j].setLineWrap(true);
            textAreas[j].setWrapStyleWord(true);
            textAreas[j].setFont(new Font("Arial", Font.PLAIN, 20));
            int finalJ2 = j;
            textAreas[j].addFocusListener(new FocusListener() {
                                              @Override
                                              public void focusGained(FocusEvent e) {
                                                  message1.put("free", 0);
                                                  message1.put("userId",userId);
                                                  message1.put("text",textAreas[finalJ2].getText());
                                                  try {
                                                      EmitLog(message1,"text"+finalJ2);
                                                  } catch (IOException ex) {
                                                      ex.printStackTrace();
                                                  }
                                              }
                                              @Override
                                              public void focusLost(FocusEvent e) {
                                                  message1.put("free", 1);
                                                  message1.put("userId",userId);
                                                  message1.put("text",textAreas[finalJ2].getText());
                                                  try {
                                                      EmitLog(message1,"text"+finalJ2);
                                                  } catch (IOException ex) {
                                                      ex.printStackTrace();
                                                  }
                                              }
            }
            );
            scrollPanes[j] = new JScrollPane(textAreas[j]);
            scrollPanes[j].setPreferredSize(new Dimension(450, 200));
            scrollPanes[j].setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            fontLabels[j] = new JLabel("Font: ");

            fontSizeSpinners[j] = new JSpinner();
            fontSizeSpinners[j].setPreferredSize(new Dimension(50, 25));
            fontSizeSpinners[j].setValue(20);
            int finalJ1 = j;
            fontSizeSpinners[j].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    textAreas[finalJ1].setFont(new Font(textAreas[finalJ1].getFont().getFamily(), textAreas[finalJ1].getFont().getStyle(), (int) fontSizeSpinners[finalJ1].getValue()));
                    try {
                        message2[finalJ1].put("fontSize",fontSizeSpinners[finalJ1].getValue());
                        EmitLog(message2[finalJ1], QUEUE_NAMES2[finalJ1]);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            });


            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

            fontBoxs[j] = new JComboBox<>(fonts);
            fontBoxs[j].addActionListener(this);
            fontBoxs[j].setSelectedItem("Arial");
            fontBoxs[j].setPreferredSize(new Dimension(160, 25));

            int finalJ = j;
            textAreas[j].addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent arg0) {

                }

                @Override
                public void keyReleased(KeyEvent arg0) {
                    if((int)message1.get("free")==1 || (int)message1.get("userId")==userId)
                        try {
                            message1.put("free",0);
                            message1.put("text", textAreas[finalJ].getText());
                            message1.put("userId",userId);
                            System.out.println("*** typed ***"+finalJ);

                            EmitLog(message1, QUEUE_NAMES1[finalJ]);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                }

                @Override
                public void keyPressed(KeyEvent arg0) {

                }
            });

            boldButtons[j] = new JButton("B");
            boldButtons[j].setFont(new Font("Dialog", Font.BOLD, 15));
            boldButtons[j].setOpaque(false);
            boldButtons[j].setContentAreaFilled(false);
            boldButtons[j].setBorderPainted(false);
            boldButtons[j].addActionListener(this);
            italicButtons[j] = new JButton("I");
            italicButtons[j].setFont(new Font("Dialog", Font.ITALIC, 18));
            italicButtons[j].setOpaque(false);
            italicButtons[j].setContentAreaFilled(false);
            italicButtons[j].setBorderPainted(false);
            italicButtons[j].addActionListener(this);
        }
        panel = new JPanel[this.nbUsers];
        for (int i = 0; i < this.nbUsers; i++) {
            panel[i] = new JPanel();
            this.panel[i].add(fontLabels[i]);
            this.panel[i].add(fontSizeSpinners[i]);
            this.panel[i].add(fontColorButtons[i]);
            this.panel[i].add(fontBoxs[i]);
            this.panel[i].add(boldButtons[i]);
            this.panel[i].add(italicButtons[i]);
            this.panel[i].add(scrollPanes[i]);
            this.add(this.panel[i]);
            this.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("override");
        for (int j = 0; j < nbUsers; j++) {
            if (e.getSource() == fontColorButtons[j]) {
                JColorChooser colorChooser = new JColorChooser();
                Color color = JColorChooser.showDialog(null, "Choose a color", Color.black);
                textAreas[j].setForeground(color);
                try {
                    message2[j].put("red", color.getRed());
                    message2[j].put("green", color.getGreen());
                    message2[j].put("blue", color.getBlue());
                    this.EmitLog(message2[j], QUEUE_NAMES2[j]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                textAreas[j].setForeground(color);
            }
            if (e.getSource() == boldButtons[j]) {
                if ((int)message2[j].get("bold") == 0) {
                    message2[j].put("bold", 1);
                    textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(), textAreas[j].getFont().getStyle() + 1, textAreas[j].getFont().getSize()));
                    boldButtons[j].setBackground(new Color(220,220,220));
                } else {
                    message2[j].put("bold", 0);
                    textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(), textAreas[j].getFont().getStyle() - 1, textAreas[j].getFont().getSize()));
                }
                try {
                    this.EmitLog(message2[j], QUEUE_NAMES2[j]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (e.getSource() == italicButtons[j]) {
                if ((int)message2[j].get("italic") == 0) {
                    message2[j].put("italic", 2);
                    textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(), textAreas[j].getFont().getStyle() + 2, textAreas[j].getFont().getSize()));
                } else {
                    message2[j].put("italic", 0);
                    textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(), textAreas[j].getFont().getStyle() - 2, textAreas[j].getFont().getSize()));
                }
                try {
                    this.EmitLog(message2[j], QUEUE_NAMES2[j]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (e.getSource() == fontBoxs[j]) {
                textAreas[j].setFont(new Font((String) fontBoxs[j].getSelectedItem(), textAreas[j].getFont().getStyle(), textAreas[j].getFont().getSize()));
                try {
                    message2[j].put("fontFamily", fontBoxs[j].getSelectedItem());
                    this.EmitLog(message2[j], QUEUE_NAMES2[j]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (e.getSource() == exitItem) {
            System.exit(0);
        }
        if (e.getSource() == submit) {
            userId = Integer.parseInt(this.field.getText());
            message1.put("userId", userId);
            for(int j=0;j<nbUsers;j++){
                message2[j].put("userId", userId);
            }
            try {
                startConnection();
            } catch (IOException | TimeoutException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void getNbUsers() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchange", "direct", true);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "exchange", "nbUsers");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            this.nbUsers = Integer.parseInt(message);
            initiateComponents();
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }


    public void EmitLog(JSONObject obj, String Queue_name) throws IOException {
        String message = obj.toString();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("exchange", "direct", true);
            channel.basicPublish("exchange", Queue_name, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }


    }

    public void startConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchange", "direct", true);
        String[] queueNames1 = new String[nbUsers];
        String[] queueNames2 = new String[nbUsers];
        for (int i = 0; i < nbUsers; i++) {
            queueNames1[i] = channel.queueDeclare().getQueue();
            channel.queueBind(queueNames1[i], "exchange", "text" + i);
            queueNames2[i] = channel.queueDeclare().getQueue();
            channel.queueBind(queueNames2[i], "exchange", "format" + i);
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(message);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (Math.toIntExact((Long)json.get("userId")) != userId) {
                    message1.put("userId",Math.toIntExact((Long)json.get("userId")));
                    message1.put("text", json.get("text"));
                    message1.put("free",Math.toIntExact((Long)json.get("free")));
                    textAreas[finalI].setText((String) json.get("text"));
                    if((int)message1.get("free")==1){
                        textAreas[finalI].setEditable(true);
                        textAreas[finalI].setFocusable(true);
                    }
                    else {
                        textAreas[finalI].setEditable(false);
                        textAreas[finalI].setFocusable(false);
                    }
                }
            //    this.EmitLog(json, queueNames1[finalI]);

            };
            channel.basicConsume(queueNames1[i], true, deliverCallback, consumerTag -> {
            });
            DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
                message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                JSONParser parser = new JSONParser();
                JSONObject json = null;
                try {
                    json = (JSONObject) parser.parse(message);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (Math.toIntExact((Long)json.get("userId")) != userId) {
                    System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                    textAreas[finalI].setForeground(new Color(Math.toIntExact((Long)json.get("red")), Math.toIntExact((Long)json.get("green")), Math.toIntExact((Long) json.get("blue"))));
                    textAreas[finalI].setFont(new Font((String) json.get("fontFamily"), Math.toIntExact((Long)json.get("bold"))+ Math.toIntExact((Long)json.get("italic")),Math.toIntExact((Long)json.get("fontSize"))));
                    message2[finalI].put("red", Math.toIntExact((Long)json.get("red")));
                    message2[finalI].put("green",Math.toIntExact((Long) json.get("green")));
                    message2[finalI].put("blue",Math.toIntExact((Long) json.get("blue")));
                    message2[finalI].put("italic", Math.toIntExact((Long)json.get("italic")));
                    message2[finalI].put("bold", Math.toIntExact((Long)json.get("bold")));
                    message2[finalI].put("fontFamily",json.get("fontFamily"));
                    message2[finalI].put("fontSize", Math.toIntExact((Long)json.get("fontSize")));

                }
            };
            channel.basicConsume(queueNames2[i], true, deliverCallback2, consumerTag -> {
            });
            //this.EmitLog(message2, QUEUE_NAMES2[i]);
        }
    }
}

