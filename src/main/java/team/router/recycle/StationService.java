package team.router.recycle;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final Response response;

    public StationService(Response response) {
        this.response = response;
    }

    public ResponseEntity<?> initStation() {
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        String API_KEY = "/5473736b67687975343450566e4455";
        String MASTER_PATH = "/json/bikeStationMaster";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000", "/3001/4000"};

        for (String target : TARGET_LIST) {
            System.out.println(BASE_URL + API_KEY + MASTER_PATH + target);
        }

        return response.success();
    }
}
