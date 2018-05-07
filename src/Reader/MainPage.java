package Reader;

import Dictionary.Dictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainPage extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    JFrame jframe = new JFrame();
    JTextArea content;
    boolean flag = true;
    String str_filePath = null;

    public MainPage() {
        //设置成经典样式
        setStyle();

        //输入框
        content = new JTextArea(10, 50);
        content.setAutoscrolls(true);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int count = e.getClickCount();
                if (count == 2){
                    String srcWord = content.getSelectedText();
                    String result = search(srcWord);
                    System.out.println(result);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }


            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        JScrollPane contentScroll = new JScrollPane(content);
        content.setBorder(BorderFactory.createBevelBorder(1));
        JPanel upper = new JPanel(new BorderLayout());
        upper.add(contentScroll);
        //按钮  
        JButton filePath = new JButton("打开文件");
        filePath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser jfc = new JFileChooser();

                    if (jfc.showOpenDialog(jframe) == JFileChooser.APPROVE_OPTION) {
                        str_filePath = jfc.getSelectedFile().getAbsolutePath();
                    }
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(str_filePath));
                    String str_line;
                    while ((str_line = bufferedReader.readLine()) != null) {
                        if (flag) {
                            content.setText(str_line);
                            flag = false;
                        } else {
                            content.setText(content.getText() + "\n" + str_line);
                        }
                    }
                    bufferedReader.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        JPanel buttonp = new JPanel();
        buttonp.add(filePath);
        JPanel all = new JPanel(new GridLayout(1, 1));
        all.add(upper);
        jframe.add(buttonp, BorderLayout.SOUTH);
        jframe.add(all, BorderLayout.CENTER);
        jframe.pack();
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension screen = tool.getScreenSize();
        jframe.setLocation(screen.width / 2 - jframe.getWidth() / 2, screen.height / 2 - jframe.getHeight() / 2);
        jframe.setTitle("英文阅读器");
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private String search(String srcWord) {
        //调用httpRequest方法，获取html字符串
        String html = Dictionary.httpRequest("http://www.iciba.com/" + srcWord);
        //利用正则表达式，抓取单词翻译信息
        String result = Dictionary.GetResult(html);

        return result;
    }

    private void setStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}  