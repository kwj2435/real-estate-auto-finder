package com.dabang.finder.service;

import com.dabang.finder.domain.RoomData;
import com.dabang.finder.repository.RoomRepository;
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
import java.util.List;

@Component
public class ScanService {

    @Autowired
    private TelegramUtils telegramUtils;
    @Autowired
    private RoomRepository roomRepository;

    @Scheduled(cron = "0/2 * * * * *")
    public void test() throws IOException {
        crawler();
    }

    private void crawler() {
        // 배열 인덱스 별 위치
        // 0 - 송파구, 1 - 강동구, 2 - 군자역
        String[] urls = {
                "https://www.dabangapp.com/api/3/room/list/multi-room/bbox?api_version=3.0.1&call_type=web&filters=%7B%22multi_room_type%22%3A%5B0%2C1%2C2%5D%2C%22selling_type%22%3A%5B1%5D%2C%22deposit_range%22%3A%5B0%2C12000%5D%2C%22price_range%22%3A%5B0%2C999999%5D%2C%22trade_range%22%3A%5B0%2C999999%5D%2C%22maintenance_cost_range%22%3A%5B0%2C999999%5D%2C%22room_size%22%3A%5B0%2C999999%5D%2C%22supply_space_range%22%3A%5B0%2C999999%5D%2C%22room_floor_multi%22%3A%5B1%2C2%2C3%2C4%2C5%2C6%2C7%2C-1%2C0%5D%2C%22division%22%3Afalse%2C%22duplex%22%3Afalse%2C%22room_type%22%3A%5B1%2C2%5D%2C%22use_approval_date_range%22%3A%5B0%2C999999%5D%2C%22parking_average_range%22%3A%5B0%2C999999%5D%2C%22household_num_range%22%3A%5B0%2C999999%5D%2C%22parking%22%3Afalse%2C%22short_lease%22%3Afalse%2C%22full_option%22%3Afalse%2C%22built_in%22%3Afalse%2C%22elevator%22%3Afalse%2C%22balcony%22%3Afalse%2C%22safety%22%3Afalse%2C%22pano%22%3Afalse%2C%22deal_type%22%3A%5B0%2C1%5D%7D&location=%5B%5B127.0791414%2C37.4979765%5D%2C%5B127.1278932%2C37.5176536%5D%5D&page=1&version=1&zoom=15"
        };

        for(int i = 0 ; i<urls.length ; i++) {
            doCrawling(urls[i], i);
        }
    }

    private void doCrawling(String url, int locationIdx) {
        JSONParser parser = new JSONParser();
        JSONArray roomList = new JSONArray();
        // 크롤링
        try {
            Document conn = Jsoup.connect(url).get();
            String data = conn.getElementsByTag("body").text();
            JSONObject object = (JSONObject) parser.parse(data);
            roomList = (JSONArray) object.get("rooms");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            System.out.println(" ERROR ");
        }
        // DB 저장된 Room List 가져옴
        List<RoomData> storedRoomList = roomRepository.findAllByLocationIdx(locationIdx);
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
                boolean deleteFlag = false;
                for(int j = 0 ; j<roomList.size() ; j++) {
                    String crawledRoom = ((JSONObject)roomList.get(j)).get("id").toString();
                    String storedRoom = storedRoomList.get(i).getDataKey();
                    // 이미 확인한 매물 패스
                    if(storedRoom.equals(crawledRoom)) {
                        roomList.remove(j);
                        break;
                    }
                    if(j+1 == roomList.size()) {
                        deleteFlag = true;  // 종료된 매물 표시
                    }
                }
                if(deleteFlag == true) {
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

                    String locationLabel = getLocationLabel(locationIdx);
                    String roomDesc = ((JSONObject)room).get("room_desc2").toString();
                    String price = ((JSONObject)room).get("price_title").toString();
                    telegramUtils.sendMessage(locationLabel + " - " +roomDesc + " - " + price);
                }
                roomRepository.saveAllAndFlush(storedRoomList);
            } catch (IOException e) {
                System.out.println(" ERROR ");
            }
        }
    }

    private String getLocationLabel(int locationIdx) {
        switch (locationIdx) {
            case 0:
                return "송파구";
            case 1:
                return "강동구";
            case 2:
                return "군자역주변";
            default:
                System.out.println(" ============== ERROR : 지명되지 않은 지역명 ==========");
                throw new RuntimeException();
        }

    }
}
