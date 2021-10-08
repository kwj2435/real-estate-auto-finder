package com.dabang.finder.service;

import com.dabang.finder.domain.MessageLog;
import com.dabang.finder.domain.RoomData;
import com.dabang.finder.repository.MessageLogRepository;
import com.dabang.finder.repository.RoomRepository;
import com.dabang.finder.utils.TelegramUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

@Component
public class ScanService {

    @Autowired
    private TelegramUtils telegramUtils;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MessageLogRepository messageLogRepository;
    private int count = 0;

//    @Scheduled(cron = "0 0/3 * * * ?")
    @Scheduled(cron = "0 0/1 * * * ?")
    private void crawler() {
        // 배열 인덱스 별 위치
        // 0 - 송파구, 1 - 강동구, 2 - 군자역, 3 - 모란역
        String[] urls = {
                "https://www.dabangapp.com/api/3/room/list/multi-room/bbox?api_version=3.0.1&call_type=web&filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C3%2C2%2C4%2C5%2C7%2C6%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&location=%5B%5B127.0671251%2C37.497619%5D%2C%5B127.1323564%2C37.5246143%5D%5D&page=1&version=1&zoom=15"
                // "https://www.dabangapp.com/search/map?filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C3%2C2%2C4%2C5%2C7%2C6%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&position=%7B%22location%22%3A%5B%5B127.0671251%2C37.497619%5D%2C%5B127.1323564%2C37.5246143%5D%5D%2C%22center%22%3A%5B127.0997408%2C37.5111179%5D%2C%22zoom%22%3A15%7D&search=%7B%22id%22%3A%22%22%2C%22type%22%3A%22%22%2C%22name%22%3A%22%22%7D&tab=all"
                , "https://www.dabangapp.com/api/3/room/list/multi-room/bbox?api_version=3.0.1&call_type=web&filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C3%2C2%2C4%2C5%2C7%2C6%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&location=%5B%5B127.1066287%2C37.5386527%5D%2C%5B127.1392443%2C37.5521442%5D%5D&page=1&version=1&zoom=16"
//                , "https://www.dabangapp.com/search/map?filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C3%2C2%2C4%2C5%2C7%2C6%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&position=%7B%22location%22%3A%5B%5B127.1065214%2C37.5386357%5D%2C%5B127.1391371%2C37.5521271%5D%5D%2C%22center%22%3A%5B127.1228292%2C37.5453817%5D%2C%22zoom%22%3A16%7D&search=%7B%22id%22%3A%22%22%2C%22type%22%3A%22%22%2C%22name%22%3A%22%22%7D&tab=all"
                , "https://www.dabangapp.com/api/3/room/list/multi-room/bbox?api_version=3.0.1&call_type=web&filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C3%2C2%2C4%2C5%2C7%2C6%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&location=%5B%5B127.0664385%2C37.5529522%5D%2C%5B127.0889476%2C37.5630988%5D%5D&page=1&version=1&zoom=17"
                // https://www.dabangapp.com/search/map?filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C3%2C2%2C4%2C5%2C7%2C6%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&position=%7B%22location%22%3A%5B%5B127.0664385%2C37.5529522%5D%2C%5B127.0889476%2C37.5630988%5D%5D%2C%22center%22%3A%5B127.077693%2C37.5580257%5D%2C%22zoom%22%3A17%7D&search=%7B%22id%22%3A%22%22%2C%22type%22%3A%22%22%2C%22name%22%3A%22%22%7D&tab=all
                , "https://www.dabangapp.com/api/3/room/list/multi-room/bbox?api_version=3.0.1&call_type=web&filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B2%2C3%2C5%2C4%2C6%2C7%2C1%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&location=%5B%5B127.1172771%2C37.4276429%5D%2C%5B127.1397862%2C37.4378066%5D%5D&page=1&version=1&zoom=17"
        };
        String[] zikbangUrls = {
                "https://apis.zigbang.com/v2/items/list"
        };

//        zikbangCrawling(zikbangUrls[0], 1);
        for(int i = 0 ; i<urls.length ; i++) {
            doCrawling(urls[i], i);
        }

        Long logCount = messageLogRepository.count();
        System.out.println("======== 전체 Message Log 갯수 : "+ logCount + ", Crawling Count : " + (++count) +" ==========");
    }

    // 개발중
    private void zikbangCrawling(String url, int locationIdx) {
        String body = "{" +
                "\"domain\": \"zigbang\"," +
                "\"item_ids\": [" +
                "    27992764, 28667150" +
                "    ]," +
                "\"withCoalition\": true}";
        int[] test = {27992764, 28667150};
        try {
            Document data = Jsoup.connect(url)
                    .data("domain", "zigbang")
                    .data("item_ids", test.toString())
                    .post();
            System.out.println("test");
        }catch (IOException e) {
            System.out.println(" ERROR ");
        }
    }
    // roomList = 크롤링 리스트
    // storedRoomList = DB 저장 리스트
    private void doCrawling(String url, int locationIdx) {
        String locationLabel = getLocationLabel(locationIdx);
        // 크롤링
        JSONArray roomList = crawlingToDabang(url);

        // DB 저장된 Room List 가져옴
        List<RoomData> storedRoomList = roomRepository.findAllByLocationIdx(locationIdx);
        System.out.println("DB 저장 갯수-"+locationLabel +":"+ storedRoomList.size());
        System.out.println("Crawling 갯수-"+locationLabel +":"+ roomList.size());

        // DB 저장 Size 0일 경우 DB Data 초기화
        if(storedRoomList.size() < 1) {
            for(Object room : roomList) {
                RoomData roomData = new RoomData();
                roomData.setLocationIdx(locationIdx);
                roomData.setDataKey(((JSONObject)room).get("id").toString());
                storedRoomList.add(roomData);
            }
            roomRepository.saveAllAndFlush(storedRoomList);
        } else {    // DB 데이터 존재 할 경우 크롤링 데이터와 비교후 신규 매물 정보 알림 전송
            for(int i = 0; i<storedRoomList.size() ; i++) {
                for(int j = 0 ; j<roomList.size() ; j++) {
                    String crawledRoom = ((JSONObject)roomList.get(j)).get("id").toString();
                    String storedRoom = storedRoomList.get(i).getDataKey();

                    // 이미 확인한 매물 패스
                    if(storedRoom.equals(crawledRoom)) {
                        roomList.remove(j);
                        break;
                    }
                }
                if(roomList.size() == 0) {
                    roomRepository.delete(storedRoomList.get(i));
                }
            }
            // 신규 매물 텔레그램 알림 처리 및 등록
            try {
                for(Object room : roomList) {
                    RoomData roomData = new RoomData();
                    roomData.setLocationIdx(locationIdx);
                    roomData.setDataKey(((JSONObject)room).get("id").toString());
                    ((JSONObject)room).get("id").toString();
                    storedRoomList.add(roomData);

                    String roomDesc = ((JSONObject)room).get("room_desc2").toString();
                    String price = ((JSONObject)room).get("price_title").toString();
                    telegramUtils.sendMessage(locationLabel + " - " +roomDesc + " - " + price);

                    MessageLog messageLog = new MessageLog(roomDesc);
                    messageLogRepository.save(messageLog);
                }
                roomRepository.saveAllAndFlush(storedRoomList);
            } catch (IOException e) {
                System.out.println(" ERROR ");
            }
        }
    }
    private JSONArray crawlingToDabang(String url) {
        JSONParser parser = new JSONParser();
        JSONArray roomList = new JSONArray();
        try {
            Document conn = Jsoup.connect(url).get();
            String data = conn.getElementsByTag("body").text();
            JSONObject object = (JSONObject) parser.parse(data);
            roomList = (JSONArray) object.get("rooms");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.out.println(" ERROR ");
        }
        return roomList;
    }

    private String getLocationLabel(int locationIdx) {
        switch (locationIdx) {
            case 0:
                return "송파구";
            case 1:
                return "강동구";
            case 2:
                return "군자역주변";
            case 3:
                return "모란역";
            default:
                System.out.println(" ============== ERROR : 지명되지 않은 지역명 ==========");
                throw new RuntimeException();
        }

    }
}
