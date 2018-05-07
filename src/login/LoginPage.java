package login;

import Constant.Constant;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class LoginPage extends JFrame {

    private static final long serialVersionUID = 1L;

    private JFrame jf;
    private JPanel jp1, jp2, jpRegisterNorth, jpRegisterSouth;
    private JTextField jtfAccount, jtfSetAccount;
    private JPasswordField jtfPassword, jtfSetPassword, jtfSetPasswordConfirm;
    private JButton btnLogin, btnRegister, btnOfflineUse, btnFinishRegister;

    public LoginPage() {

        setStyle();
        initView();
    }

    public static void main(String[] args) {
        new LoginPage();

    }

    public void initView() {
        jf = new JFrame("英文阅读器");

        jp1 = new JPanel();
        jtfAccount = new JTextField();
        jtfPassword = new JPasswordField();

        // 登录首页包括两个JPanel
        // jp1:账号、密码的填写
        // jp2:登录、注册、离线使用 三个按钮
        jp1.setLayout(new GridLayout(2, 2));
        jp1.add(new JLabel("账号", SwingConstants.RIGHT));
        jp1.add(jtfAccount);
        jp1.add(new JLabel("密码", SwingConstants.RIGHT));
        jp1.add(jtfPassword);
        jp2 = new JPanel();
        btnLogin = new JButton("登录");
        btnRegister = new JButton("注册");
        btnOfflineUse = new JButton("离线使用");
        jp2.add(btnLogin);
        jp2.add(btnRegister);
        jp2.add(btnOfflineUse);
        jf.add(jp1, BorderLayout.NORTH);
        jf.add(jp2, BorderLayout.SOUTH);
        //登录按钮的监听
        btnLogin.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jtfAccount.getText().equals("") || new String(jtfPassword.getPassword()).equals("")) {
                    JOptionPane.showMessageDialog(jf, "账号或密码不能为空!", "错误提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        login();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        });
        //注册按钮的监听
        btnRegister.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                jp1.setVisible(false);
                jp2.setVisible(false);
                // 进入注册模块
                loadRegisterModule();

            }
        });
        //离线使用按钮的监听
        btnOfflineUse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
               // new MainPageee();
            }
        });
        // 设置JFrame的一些属性
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.pack();
        jf.setSize(600, 450);
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension screen = tool.getScreenSize();
        jf.setLocation(screen.width / 2 - jf.getWidth() / 2, screen.height / 2 - jf.getHeight() / 2);
        jf.setVisible(true);
        jf.setResizable(false);
    }

    public void register() throws IOException {

        String account = jtfSetAccount.getText();
        String password = new String(jtfSetPassword.getPassword());

        URL url = new URL(Constant.URL_Register + "account=" + account + "&" + "password=" + password);
        // 接收servlet返回值，是字节
        InputStream is = url.openStream();

        // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        //判断结果
        if (sb.toString().equals(Constant.FLAG_SUCCESS)) {
            int op = JOptionPane.showConfirmDialog(jf, "注册成功!\n点击确定进入", "提示", JOptionPane.CLOSED_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                jf.dispose();
               // new MainPageee();
            }
        } else if (sb.toString().equals(Constant.FLAG_YES)) {
            JOptionPane.showMessageDialog(jf, "该账号已存在!", "错误提示", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(jf, "注册失败!", "错误提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void loadRegisterModule() {
        jtfSetAccount = new JTextField();
        jtfSetPassword = new JPasswordField();
        jtfSetPasswordConfirm = new JPasswordField();
        btnFinishRegister = new JButton("完成注册");
        btnFinishRegister.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jtfSetAccount.getText().equals("") || new String(jtfSetPassword.getPassword()).equals("")
                        || new String(jtfSetPassword.getPassword()).equals("")) {
                    JOptionPane.showMessageDialog(jf, "账号或密码不能为空!", "错误提示", JOptionPane.WARNING_MESSAGE);
                } else if (!new String(jtfSetPassword.getPassword())
                        .equals(new String(jtfSetPasswordConfirm.getPassword()))) {
                    JOptionPane.showMessageDialog(jf, "两次输入的密码不正确!", "错误提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        register();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        jpRegisterNorth = new JPanel();
        jpRegisterNorth.setLayout(new GridLayout(3, 2));
        jpRegisterNorth.add(new JLabel("输入账号", SwingConstants.RIGHT));
        jpRegisterNorth.add(jtfSetAccount);
        jpRegisterNorth.add(new JLabel("输入密码", SwingConstants.RIGHT));
        jpRegisterNorth.add(jtfSetPassword);
        jpRegisterNorth.add(new JLabel("确认密码", SwingConstants.RIGHT));
        jpRegisterNorth.add(jtfSetPasswordConfirm);
        jpRegisterSouth = new JPanel();
        jpRegisterSouth.add(btnFinishRegister);
        jf.add(jpRegisterNorth, BorderLayout.NORTH);
        jf.add(jpRegisterSouth, BorderLayout.SOUTH);

    }

    public void login() throws IOException {
        // 获取账号和密码
        String account = jtfAccount.getText();
        String password = new String(jtfPassword.getPassword());

        URL url = new URL(Constant.URL_Login + "account=" + account + "&" + "password=" + password);
        // 接收servlet返回值，是字节
        InputStream is = url.openStream();

        // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        if (sb.toString().equals(Constant.FLAG_SUCCESS)) {
            //销毁登录页面，进入主页面
            jf.dispose();
          //  new MainPageee();
        } else {
            JOptionPane.showMessageDialog(jf, "账号或密码错误!", "错误提示", JOptionPane.WARNING_MESSAGE);
        }
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
