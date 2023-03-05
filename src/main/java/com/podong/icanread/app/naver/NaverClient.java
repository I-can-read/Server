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

    public NaverClient(String menuName, String clientId, String clientSecret) {
        this.menuName = menuName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        naverSearchEncyclopediaResult();
    }

    public void naverSearchEncyclopediaResult() {
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

        try{
            ResponseEntity<String> searchResult = restTemplate.exchange(req, String.class);
            String data = searchResult.getBody();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(data);
            try{
                JSONArray parseItems = (JSONArray) jsonObject.get("items");
                JSONObject firstItem = (JSONObject) parseItems.get(0);
                meaning = firstItem.get("description").toString();
                if (meaning.contains("<")){ // 태그 존재할 경우 태그 제외
                    meaning = meaning.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
                }
                if (meaning.contains("[영양성분]")){ // 영양성분일 경우 빈스트링으로 처리
                    meaning = "";
                }
                imageURL = firstItem.get("thumbnail").toString();
            } catch (IndexOutOfBoundsException | NullPointerException e){
                changeNullToEmptyString(meaning, imageURL);
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
    }
    public String getImageURL(){
        return imageURL;
    }
    public String getMeaning(){
        return meaning;
    }
    private void changeNullToEmptyString(String imageURL, String meaning){
        this.imageURL = imageURL == null ? "" : imageURL;
        this.meaning = meaning == null ? "" : meaning;
    }
}
