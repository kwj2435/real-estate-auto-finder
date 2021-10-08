package com.dabang.finder.utils;

import com.dabang.finder.model.MessageVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@Component
public class TelegramUtils {
    private static final String TOKEN = "2052523280:AAFkccBsCbHF9O6nBjMbG9xiWPV5wlFNvLM";
    private static final String CHAT_ID = "1966561554";

    public String sendMessage(String text) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient sender = new OkHttpClient();
        MessageVO messageVO = new MessageVO();
        messageVO.setChat_id(CHAT_ID);
        messageVO.setText(text);
        String url = "https://api.telegram.org/bot" + TOKEN + "/sendMessage";
        String jsonMessage = objectMapper.writeValueAsString(messageVO);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonMessage);

        Request request = new Request.Builder().url(url).post(requestBody).build();
        Response response = sender.newCall(request).execute();

        return "test";
    }
}
