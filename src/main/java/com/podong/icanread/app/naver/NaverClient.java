package com.podong.icanread.app.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class NaverClient {
    final String BASE_URL = "https://openapi.naver.com/v1/search/encyc.json";

    String menuName;
    String meaning;
    String imageURL;
    private String clientId;
    private String clientSecret;

    public NaverClient(String menuName, String clientId, String clientSecret) throws ParseException {
        this.menuName = menuName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        naverSearchEncyclopediaResult();
    }

    public void naverSearchEncyclopediaResult() throws ParseException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/encyc.json")
                .queryParam("query", menuName)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        // Header를 사용
        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        ResponseEntity<String> searchResult = restTemplate.exchange(req, String.class);
        String data = searchResult.getBody();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(data);
        JSONArray parseItems = (JSONArray) jsonObject.get("items");
        JSONObject firstItem = (JSONObject) parseItems.get(0);
        meaning = firstItem.get("description").toString();
        imageURL = firstItem.get("thumbnail").toString();
    }
    public String getImageURL(){
        return imageURL;
    }
    public String getMeaning(){
        return meaning;
    }
}
