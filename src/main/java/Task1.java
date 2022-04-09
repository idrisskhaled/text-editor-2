import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

public class Task1 extends JFrame implements ActionListener{
    String bold="N";
    String italic="N";
    static String QUEUE_NAME1="text1";
    static String QUEUE_NAME2="format1";
    Channel channel=null;
    JTextArea textArea;
    JScrollPane scrollPane;
    JLabel fontLabel;
    JSpinner fontSizeSpinner;
    JButton fontColorButton,boldButton,italicButton;
    JComboBox fontBox;

    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;

    Task1(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Task 1");
        this.setSize(500, 330);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial",Font.PLAIN,20));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450,200));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        fontLabel = new JLabel("Font: ");

        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50,25));
        fontSizeSpinner.setValue(20);
        fontSizeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                textArea.setFont(new Font(textArea.getFont().getFamily(),Font.PLAIN,(int) fontSizeSpinner.getValue()));
                try {
                    EmitLog(";;"+ Integer.toString((Integer) fontSizeSpinner.getValue()),QUEUE_NAME2);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });

        fontColorButton = new JButton("Color");
        fontColorButton.addActionListener(this);

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        fontBox = new JComboBox(fonts);
        fontBox.addActionListener(this);
        fontBox.setSelectedItem("Arial");
        fontBox.setPreferredSize(new Dimension(160,25));

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {

                    EmitLog(textArea.getText(),QUEUE_NAME1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    EmitLog(textArea.getText(),QUEUE_NAME1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    EmitLog(textArea.getText(),QUEUE_NAME1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        boldButton = new JButton("B");
        boldButton.setFont(new Font("Dialog",Font.BOLD,15));
        boldButton.setOpaque(false);
        boldButton.setContentAreaFilled(false);
        boldButton.setBorderPainted(false);
        boldButton.addActionListener(this);
        italicButton = new JButton("I");
        italicButton.setFont(new Font("Dialog",Font.ITALIC,18));
        italicButton.setOpaque(false);
        italicButton.setContentAreaFilled(false);
        italicButton.setBorderPainted(false);
        italicButton.addActionListener(this);
        // ------ menubar ------

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // ------ /menubar ------

        this.setJMenuBar(menuBar);
        this.add(fontLabel);
        this.add(fontSizeSpinner);
        this.add(fontColorButton);
        this.add(fontBox);
        this.add(boldButton);
        this.add(italicButton);
        this.add(scrollPane);


        this.setVisible(true);
        this.setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==fontColorButton) {
            JColorChooser colorChooser = new JColorChooser();

            Color color = colorChooser.showDialog(null, "Choose a color", Color.black);

            textArea.setForeground(color);
            try {
                this.EmitLog(color.getRed()+","+color.getGreen()+","+color.getBlue()+";;"+";;",QUEUE_NAME2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            textArea.setForeground(color);
        }
        if(e.getSource()==boldButton) {
            if (this.bold.equals("B")) {
                this.bold = "N";
                textArea.setFont(new Font(textArea.getFont().getFamily(), textArea.getFont().getStyle() - 1, textArea.getFont().getSize()));

            } else {
                this.bold = "B";
                textArea.setFont(new Font(textArea.getFont().getFamily(), textArea.getFont().getStyle() + 1, textArea.getFont().getSize()));
            }
            ;
            try {
                this.EmitLog(";;;" + this.bold + ";", QUEUE_NAME2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if(e.getSource()==italicButton) {
            if(this.italic.equals("I")){this.italic="N";
                textArea.setFont(new Font(textArea.getFont().getFamily(),textArea.getFont().getStyle()-2,textArea.getFont().getSize()));

            }else {this.italic="I";
                textArea.setFont(new Font(textArea.getFont().getFamily(),textArea.getFont().getStyle()+2,textArea.getFont().getSize()));
            };
            try {
                this.EmitLog(";;;;"+this.italic,QUEUE_NAME2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if(e.getSource()==fontBox) {
            textArea.setFont(new Font((String)fontBox.getSelectedItem(),Font.PLAIN,textArea.getFont().getSize()));
            try {
                this.EmitLog(";"+(String)fontBox.getSelectedItem()+";;;;",QUEUE_NAME2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if(e.getSource()==openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            fileChooser.setFileFilter(filter);

            int response = fileChooser.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                Scanner fileIn = null;

                try {
                    fileIn = new Scanner(file);
                    if(file.isFile()) {
                        while(fileIn.hasNextLine()) {
                            String line = fileIn.nextLine()+"\n";
                            textArea.append(line);
                        }
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                finally {
                    fileIn.close();
                }
            }
        }
        if(e.getSource()==saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int response = fileChooser.showSaveDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                File file;
                PrintWriter fileOut = null;

                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    fileOut = new PrintWriter(file);
                    fileOut.println(textArea.getText());
                }
                catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                finally {
                    fileOut.close();
                }
            }
        }
        if(e.getSource()==exitItem) {
            System.exit(0);
        }
    }
    public void startConnection(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("exchange","direct",true);
            this.channel=channel;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
    public void EmitLog(String message,String Queue_name) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("exchange","direct",true);
            channel.basicPublish("exchange", Queue_name, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }
    public static void main(String[] args) {

        new Task1();

    }
}
