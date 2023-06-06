package team.router.recycle.domain.station;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class StationService {

    private final Response response;
    private final StationRepository stationRepository;

    public StationService(Response response, StationRepository stationRepository) {
        this.response = response;
        this.stationRepository = stationRepository;
    }


    public ResponseEntity<?> initStation() throws IOException {
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        String API_KEY = "/5473736b67687975343450566e4455";
        String MASTER_PATH = "/json/bikeStationMaster";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000", "/3001/4000"};

        for (String target : TARGET_LIST) {
            URL MASTER_URL = new URL(BASE_URL + API_KEY + MASTER_PATH + target);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(MASTER_URL.openStream(), StandardCharsets.UTF_8));
                String result = br.readLine();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.readTree(result).get("bikeStationMaster").get("row").forEach(station -> {
                    try {
                        Station newStation = Station.builder()
                                .stationId(station.get("LENDPLACE_ID").asText())
                                .stationAddress1(station.get("STATN_ADDR1").asText())
                                .stationAddress2(station.get("STATN_ADDR2").asText())
                                .stationLatitude(station.get("STATN_LAT").asDouble())
                                .stationLongitude(station.get("STATN_LNT").asDouble())
                                .build();
                        stationRepository.save(newStation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


                // latitude, longitude "" 거르기
            } catch (Exception e) {
                return response.fail(String.valueOf(e), HttpStatus.BAD_REQUEST);
            }
        }

        return response.success();
    }
}
