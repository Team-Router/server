package team.router.recycle.domain.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final ExecutorService executorService;
    private final StationClient client;

    public StationService(StationRepository stationRepository, StationClient client) {
        this.stationRepository = stationRepository;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.client = client;
    }

    public void initStation() {
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000", "/3001/4000"};

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String target : TARGET_LIST) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    String result = client.getStationInfo(target);

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(result).get("stationInfo").get("row");
                    List<Station> stationList = new ArrayList<>();
                    for (JsonNode station : jsonNode) {
                        if (station.get("STA_LAT").asText().startsWith("0") || station.get("STA_LONG").asText()
                                .startsWith("0")) {
                            continue;
                        }
                        stationList.add(Station.builder()
                                .stationNumber(station.get("RENT_NO").asText())
                                .stationName(station.get("RENT_NM").asText())
                                .stationNumberName(station.get("RENT_ID_NM").asText())
                                .stationAddress1(station.get("STA_ADD1").asText())
                                .stationAddress2(station.get("STA_ADD2").asText())
                                .stationLatitude(station.get("STA_LAT").asDouble())
                                .stationLongitude(station.get("STA_LONG").asDouble())
                                .build());
                    }
                    stationRepository.saveAll(stationList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            System.out.println("Station init complete");
        });
    }
}
