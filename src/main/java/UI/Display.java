package UI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author n
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;



    public class Display extends JPanel {

        private JTextField findText;
        private JTextArea ta;

        public Display(String text) {
        setLayout(new BorderLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        findText = new JTextField(20);


        ta = new JTextArea(20, 40);
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setEditable(false);
        add(new JScrollPane(ta));

        loadFile(text);
    }

    public void color(Color color, String Highlight)
    {
        String find = Highlight;//findText.getText();
                Document document = ta.getDocument();
                try {
                    for (int index = 0; index + find.length() < document.getLength(); index++) {
                        String match = document.getText(index, find.length());
                        if (find.equals(match)) {
                                javax.swing.text.DefaultHighlighter.DefaultHighlightPainter highlightPainter =
                                    new     javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(color);
                            ta.getHighlighter().addHighlight(index , index +     find.length(), highlightPainter);
                        }
                    }             
                } catch (BadLocationException exp) {
                    exp.printStackTrace();
                }
    }
    protected void loadFile(String text) {
        String searchText = findText.getText();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(text)))) {
            ta.read(reader, "Text");
        } catch (IOException exp) {
            exp.printStackTrace();
            JOptionPane.showMessageDialog(Display.this, "Could not create file", "Error", JOptionPane.ERROR_MESSAGE);
        }
        ta.setCaretPosition(0);
    }
}

