
/**
 * Created by codedrinker on 2018/11/24.
 */
public class Message {
    private String chatId;
    private String sendId;
    private String nickName;
    private String content;
    private Long time;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{" +
                "chatId='" + chatId + '\'' +
                ", sendId='" + sendId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                '}';
    }
}
