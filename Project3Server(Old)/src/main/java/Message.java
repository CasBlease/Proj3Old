import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private static final long serialVersionUID = 42L;

    private MessageType type;
    private String sender;
    private String content;
    private List<String> recipients;
    private String targetUser;
    private String group;

    public Message(MessageType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public Message(MessageType type, String sender, String content, List<String> recipients) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipients = recipients;
    }

    public Message(MessageType type, String sender, String content, String targetUser) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.targetUser = targetUser;
    }

    public Message(MessageType type, String sender, String content, String group, List<String> recipients) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.group = group;
        this.recipients = recipients;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public enum MessageType {
        CREATE_USER,
        SEND_TO_ALL,
        SEND_TO_INDIVIDUAL,
        GET_CONNECTED_USERS,
        GET_MESSAGES,
        CONNECTED,
        DISCONNECTED
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", recipients=" + recipients +
                ", targetUser='" + targetUser + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
