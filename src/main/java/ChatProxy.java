import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codedrinker on 2018/11/24.
 */
public class ChatProxy {
    private String END_POINT = "http://console-chat.getharbours.com/chat";

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public List<Message> read(String server, String chatId, String readerId, Long watermark) {
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url((StringUtils.isNotBlank(server) ? server : END_POINT) + "?chatId=" + chatId + "&readerId=" + readerId + "&watermark=" + watermark)
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            List<Message> messages = JSON.parseArray(execute.body().string(), Message.class);
            if (execute.isSuccessful()) {
                return messages;
            }
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public void send(String server, Message message) {
        String content = JSON.toJSONString(message);
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(StringUtils.isNotBlank(server) ? server : END_POINT)
                .post(RequestBody.create(MediaType.parse("application/json"), content))
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            System.out.println(execute.body());
        } catch (Exception e) {
        }
    }
}
