package team.router.recycle.domain.station;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
//        String MASTER_PATH = "/json/bikeStationMaster";
        String MASTER_PATH = "/json/tbCycleStationInfo";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000", "/3001/4000"};

        for (String target : TARGET_LIST) {
            URL MASTER_URL = new URL(BASE_URL + API_KEY + MASTER_PATH + target);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(MASTER_URL.openStream(), StandardCharsets.UTF_8));
                String result = br.readLine();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(result).get("stationInfo").get("row");
                for (JsonNode station : jsonNode) {
                    if ("0.0".equals(station.get("STA_LAT").asText()) || "0.0".equals(station.get("STA_LONG").asText())) {
                        continue;
                    }
                    Station newStation = Station.builder()
                            .stationNumber(station.get("RENT_NO").asText())
                            .stationName(station.get("RENT_NM").asText())
                            .stationNumberName(station.get("RENT_ID_NM").asText())
                            .stationAddress1(station.get("STA_ADD1").asText())
                            .stationAddress2(station.get("STA_ADD2").asText())
                            .stationLatitude(station.get("STA_LAT").asDouble())
                            .stationLongitude(station.get("STA_LONG").asDouble())
                            .build();
                    stationRepository.save(newStation);
                }
            } catch (Exception e) {
                return response.fail(String.valueOf(e), HttpStatus.BAD_REQUEST);
            }
        }

        return response.success();
    }
}
