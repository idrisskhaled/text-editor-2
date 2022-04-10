package Part2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import javax.swing.*;
import javax.swing.event.*;

    public class Useri extends JFrame implements ActionListener {
        String bold="N";
        JPanel[] panel;
        JPanel panel1;
        JLabel lb;
        String italic="N";
        static String QUEUE_NAME1;
        static String QUEUE_NAME2;
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
        private int nbUsers;
        JLabel label;
        JTextField field;
        JButton submit;
        JLabel result;
        private int userId;
        Useri(){
            try {
                this.getNbUsers();
            } catch (IOException | TimeoutException ex) {
                ex.printStackTrace();
            }
            QUEUE_NAME1="text "+userId;
            QUEUE_NAME2="format "+userId;
            System.out.println("aaaaa"+this.nbUsers);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Task "+userId);
            this.setSize(500, 330);
            this.setLayout(new FlowLayout());
            this.setLocationRelativeTo(null);

            field=new JTextField();
            result=new JLabel();
            label=new JLabel("User id: ");
            field.setPreferredSize(new Dimension(100,30));
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
            panel1=new JPanel();
            this.panel1.add(label);
            this.panel1.add(field);
            this.panel1.add(result);
            this.panel1.add(submit);
            this.add(panel1);
            panel1.setVisible(true);
            result.setVisible(false);
            this.setVisible(true);


    }
        public void initiateComponents(){
            System.out.println(nbUsers);
            textAreas=new JTextArea[nbUsers];
            scrollPanes=new JScrollPane[nbUsers];
            fontLabels=new JLabel[nbUsers];
            fontSizeSpinners= new JSpinner[nbUsers];
            fontColorButtons=new JButton[nbUsers];
             boldButtons=new JButton[nbUsers];
            italicButtons=new JButton[nbUsers];
             fontBoxs=new JComboBox[nbUsers];
            for(int j=0;j<nbUsers;j++){
                fontColorButtons[j] = new JButton("Color");
                fontColorButtons[j].addActionListener(this);
                textAreas[j] = new JTextArea();
                textAreas[j].setLineWrap(true);
                textAreas[j].setWrapStyleWord(true);
                textAreas[j].setFont(new Font("Arial",Font.PLAIN,20));
                scrollPanes[j] = new JScrollPane(textAreas[j]);
                scrollPanes[j].setPreferredSize(new Dimension(450,200));
                scrollPanes[j].setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                fontLabels[j] = new JLabel("Font: ");

                fontSizeSpinners[j] = new JSpinner();
                fontSizeSpinners[j].setPreferredSize(new Dimension(50,25));
                fontSizeSpinners[j].setValue(20);
                int finalJ1 = j;
                fontSizeSpinners[j].addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {

                        textAreas[userId].setFont(new Font(textAreas[finalJ1].getFont().getFamily(),Font.PLAIN,(int) fontSizeSpinners[finalJ1].getValue()));
                        try {
                            EmitLog(";;"+ Integer.toString((Integer) fontSizeSpinners[finalJ1].getValue()),QUEUE_NAME2);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                });


                String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

                fontBoxs[j] = new JComboBox(fonts);
                fontBoxs[j].addActionListener(this);
                fontBoxs[j].setSelectedItem("Arial");
                fontBoxs[j].setPreferredSize(new Dimension(160,25));

                int finalJ = j;
                textAreas[j].getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        try {

                            EmitLog(textAreas[finalJ].getText(),QUEUE_NAME1);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        try {
                            EmitLog(textAreas[finalJ].getText(),QUEUE_NAME1);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        try {
                            EmitLog(textAreas[finalJ].getText(),QUEUE_NAME1);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                boldButtons[j] = new JButton("B");
                boldButtons[j].setFont(new Font("Dialog",Font.BOLD,15));
                boldButtons[j].setOpaque(false);
                boldButtons[j].setContentAreaFilled(false);
                boldButtons[j].setBorderPainted(false);
                boldButtons[j].addActionListener(this);
                italicButtons[j] = new JButton("I");
                italicButtons[j].setFont(new Font("Dialog",Font.ITALIC,18));
                italicButtons[j].setOpaque(false);
                italicButtons[j].setContentAreaFilled(false);
                italicButtons[j].setBorderPainted(false);
                italicButtons[j].addActionListener(this);
            }
            panel=new JPanel[this.nbUsers];
            for(int i=0;i<this.nbUsers;i++){
                panel[i]=new JPanel();
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
            for(int j=0;j<nbUsers;j++){
                if(e.getSource()==fontColorButtons[j]) {
                    JColorChooser colorChooser = new JColorChooser();
                    Color color = JColorChooser.showDialog(null, "Choose a color", Color.black);
                    textAreas[j].setForeground(color);
                    try {
                        this.EmitLog(color.getRed()+","+color.getGreen()+","+color.getBlue()+";;"+";;",QUEUE_NAME2);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    textAreas[j].setForeground(color);
                }
                if(e.getSource()==boldButtons[j]) {
                    if (this.bold.equals("B")) {
                        this.bold = "N";
                        textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(), textAreas[j].getFont().getStyle() - 1, textAreas[j].getFont().getSize()));

                    } else {
                        this.bold = "B";
                        textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(), textAreas[j].getFont().getStyle() + 1, textAreas[j].getFont().getSize()));
                    }
                    ;
                    try {
                        this.EmitLog(";;;" + this.bold + ";", QUEUE_NAME2);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                if(e.getSource()==italicButtons[j]) {
                    if(this.italic.equals("I")){this.italic="N";
                        textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(),textAreas[j].getFont().getStyle()-2,textAreas[j].getFont().getSize()));

                    }else {this.italic="I";
                        textAreas[j].setFont(new Font(textAreas[j].getFont().getFamily(),textAreas[j].getFont().getStyle()+2,textAreas[j].getFont().getSize()));
                    };
                    try {
                        this.EmitLog(";;;;"+this.italic,QUEUE_NAME2);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                if(e.getSource()==fontBoxs[j]) {
                    textAreas[j].setFont(new Font((String)fontBoxs[j].getSelectedItem(),Font.PLAIN,textAreas[j].getFont().getSize()));
                    try {
                        this.EmitLog(";"+(String)fontBoxs[j].getSelectedItem()+";;;;",QUEUE_NAME2);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if(e.getSource()==exitItem) {
                System.exit(0);
            }
            if(e.getSource()==submit){
                userId=Integer.parseInt(this.field.getText());

            }
        }
        public void getNbUsers() throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("exchange", "direct",true);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "exchange", "nbUsers");
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
                this.nbUsers=Integer.parseInt(message);
                this.initiateComponents();

            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        }
        public void EmitLog(String message,String Queue_name) throws IOException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.exchangeDeclare("exchange","direct",true);
                channel.basicPublish("exchange", Queue_name, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + message + "'");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }


        }
        public static void main(String[] args) {

            Useri user=new Useri();

        }
    }

