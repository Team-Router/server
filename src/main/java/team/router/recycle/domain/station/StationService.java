package team.router.recycle.domain.station;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;

@Service
public class StationService {

    @Value("${SEOUL_API_KEY}")
    private String SEOUL_API_KEY;

    private final Response response;
    private final StationRepository stationRepository;
    private final ExecutorService executorService;

    public StationService(Response response, StationRepository stationRepository) {
        this.response = response;
        this.stationRepository = stationRepository;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public ResponseEntity<?> initStation() {
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        String API_KEY = SEOUL_API_KEY;
        String MASTER_PATH = "/json/tbCycleStationInfo";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000", "/3001/4000"};

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String target : TARGET_LIST) {
            URL MASTER_URL;
            try {
                MASTER_URL = new URL(BASE_URL + API_KEY + MASTER_PATH + target);
            } catch (MalformedURLException e) {
                return response.fail("Invalid URL", HttpStatus.BAD_REQUEST);
            }
            futures.add(CompletableFuture.runAsync(() -> {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(MASTER_URL.openStream(), StandardCharsets.UTF_8))) {
                    String result = br.readLine();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(result).get("stationInfo").get("row");
                    List<Station> stationList = new ArrayList<>();
                    for (JsonNode station : jsonNode) {
                        if (station.get("STA_LAT").asText().startsWith("0") || station.get("STA_LONG").asText()
                                .startsWith("0")) {
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
                        stationList.add(newStation);
                    }
                    stationRepository.saveAll(stationList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return response.success();
    }
}
