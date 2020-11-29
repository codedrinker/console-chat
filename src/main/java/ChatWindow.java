import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by codedrinker on 2018/11/24.
 */
public class ChatWindow {
    private JPanel contentJPanel;

    private ChatProxy chatProxy = new ChatProxy();
    private String chatId;
    private String nickName;
    private String server;

    public ChatWindow(ToolWindow toolWindow) {
        init();
    }

    private String getLocalMacAddress() {
        InetAddress ia = null;
        try {
            ia = InetAddress.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1) {
                    sb.append("0" + str);
                } else {
                    sb.append(str);
                }
            }
            return sb.toString().toUpperCase();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return UUID.randomUUID().toString();
    }


    private void show(JTextArea chatArea, String nick, String content) {
        chatArea.setCaretColor(JBColor.RED);
        chatArea.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())
                + " [" + nick + "]" + " " + content);
        chatArea.setCaretPosition(chatArea.getText().length());
    }

    private void init() {
        contentJPanel = new JPanel(new GridLayout(2, 1));
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        new Thread(new MessageReceiver(chatArea)).start();
        JTextArea inputArea = new JTextArea();
        inputArea.setEditable(true);
        inputArea.setLineWrap(true);
        inputArea.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    String trim = StringUtils.trim(StringUtils.replace(inputArea.getText(), "\n", ""));
                    if (StringUtils.isBlank(trim)) {
                        inputArea.setText("");
                        return;
                    }
                    if (StringUtils.startsWith(trim, "chatId:") && StringUtils.isNotBlank(StringUtils.replace(trim, "chatId:", ""))) {
                        chatId = StringUtils.replace(trim, "chatId:", "");
                        show(chatArea, "System", "unique chat update successfully.\n");
                        show(chatArea, "System", "chat id : " + chatId + "\n");
                        inputArea.setText("");
                        return;
                    }

                    if (StringUtils.startsWith(trim, "nick:") && StringUtils.isNotBlank(StringUtils.replace(trim, "nick:", ""))) {
                        nickName = StringUtils.replace(trim, "nick:", "");
                        show(chatArea, "System", "nick update successfully.\n");
                        show(chatArea, "System", "nick : " + nickName + "\n");
                        inputArea.setText("");
                        return;
                    }

                    if (StringUtils.startsWith(trim, "server:") && StringUtils.isNotBlank(StringUtils.replace(trim, "server:", ""))) {
                        server = StringUtils.replace(trim, "server:", "");
                        show(chatArea, "System", "server update successfully.\n");
                        show(chatArea, "System", "server : " + server + "\n");
                        inputArea.setText("");
                        return;
                    }

                    if (StringUtils.isBlank(chatId)) {
                        show(chatArea, "System", "Please enter unique chat id. chatId:[your chat id]\n");
                        inputArea.setText("");
                        return;
                    }
                    if (StringUtils.isBlank(nickName)) {
                        show(chatArea, "System", "Please enter nickName. nick:[your nick]\n");
                        inputArea.setText("");
                        return;
                    }

                    Message message = new Message();
                    message.setSendId(getLocalMacAddress());
                    message.setChatId(chatId);
                    message.setNickName(nickName);
                    message.setContent(trim);
                    chatProxy.send(server,message);
                    show(chatArea, "Me", inputArea.getText());
                    inputArea.setText("");
                }
            }
        });

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentJPanel.add(scroll);
        contentJPanel.add(inputArea);
        show(chatArea, "System", "Please set unique chat id and nick as following.\nchatId:019273816\nnick:Tom\nAlso you can using server:http://yourdomain.com/chat to update your server.\nMore information visit https://sourl.cn/9bUutT \n");
    }

    public JPanel getContent() {
        return contentJPanel;
    }


    class MessageReceiver implements Runnable {
        private JTextArea chatArea;

        public MessageReceiver(JTextArea chatArea) {
            this.chatArea = chatArea;
        }

        @Override
        public void run() {
            Long watermark = System.currentTimeMillis();
            while (true) {
                List<Message> messages = chatProxy.read(server, chatId, getLocalMacAddress(), watermark);
                if (messages != null && messages.size() != 0) {
                    for (Message message : messages) {
                        show(chatArea, message.getNickName(), message.getContent() + "\n");
                    }
                    watermark = System.currentTimeMillis();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
