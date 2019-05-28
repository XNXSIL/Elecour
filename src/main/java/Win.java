import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Win extends JFrame implements ActionListener{
    private JPanel Login;
    private JTextField name;
    private JPasswordField pwd;
    private JButton Log;
    private JButton Close;
    private JTextArea ReslutArea;
    private JPanel nameArea;
    private JPanel pwdArea;
    private JPanel button;
    public Win(){
        Login = new JPanel();
        nameArea = new JPanel();
        nameArea.add(new JLabel("学号："));
        name = new JTextField(20);
        nameArea.add(name);
        pwdArea = new JPanel();
        pwdArea.add(new JLabel("密码："));
        pwd = new JPasswordField(20);
        pwdArea.add(pwd);
        Close = new JButton("退出");
        Log = new JButton("登陆");
        button = new JPanel();
        button.add(Log);
        button.add(Close);
        ReslutArea = new JTextArea(20,10);
        setLayout(new GridLayout(3,1));
        setTitle("深大抢课系统");
        setSize(300,200);
        setLocation(400,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(nameArea);
        this.add(pwdArea);
        this.add(button);
        setVisible(true);
        setResizable(true);
        Log.addActionListener(this);
        Close.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand() == "登陆"){
            String nameStr = name.getText().trim();
            String pwdStr = pwd.getText().trim();
            System.out.println(pwdStr);
            ImageSB start = new ImageSB(nameStr,pwdStr,ReslutArea);
            this.remove(nameArea);
            this.remove(pwdArea);
            this.remove(button);
            this.setLayout(new BorderLayout());
            this.add(ReslutArea,BorderLayout.CENTER);
            this.repaint();
            Circle circle = new Circle(start);
            circle.start();
        }
    }


}
