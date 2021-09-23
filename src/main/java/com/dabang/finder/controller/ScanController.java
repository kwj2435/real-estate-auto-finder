package com.dabang.finder.controller;

import com.dabang.finder.utils.TelegramUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScanController {

    @Autowired
    private TelegramUtils telegramUtils;

    @Scheduled(cron = "0/2 * * * * *")
    public void test() throws IOException {
        crawler();
    }

    private void crawler() {
        JSONParser parser = new JSONParser();

        String[] urls = {
                "https://www.dabangapp.com/api/3/room/list/multi-room/bbox?api_version=3.0.1&call_type=web&filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C2%2C3%2C4%2C5%2C6%2C7%2C-1%2C0%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&location=%5B%5B127.0791414%2C37.4979765%5D%2C%5B127.1278932%2C37.5176536%5D%5D&page=1&version=1&zoom=15"
        };
        try {
            String url = urls[0];
            Document conn = Jsoup.connect(url).get();
            String data = conn.getElementsByTag("body").text();
            JSONObject object = (JSONObject) parser.parse(data);

            JSONArray array = (JSONArray) object.get("rooms");
            for(Object room : array) {
                String roomDesc = ((JSONObject)room).get("room_desc2").toString();
                String roomPrice = ((JSONObject)room).get("price_title").toString();
                System.out.println(roomDesc + " 가격: " + roomPrice);
            }
            System.out.println("test");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
