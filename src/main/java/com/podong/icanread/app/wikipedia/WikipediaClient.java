package com.podong.icanread.app.wikipedia;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
@Slf4j
public class WikipediaClient {
    final String BASE_URL = "https://ko.wikipedia.org/api/rest_v1/page/summary/";
    String menuName;
    String displayTitle = "";
    String imageURL = "";
    String meaning = "";

    public WikipediaClient(String menuName){
        this.menuName = menuName;
        getMeaningAndImage();
    }
    private void getMeaningAndImage(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL+menuName)
                .get()
                .build();
        try{
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(data);

            // 제목 가져오기
            displayTitle = (String) jsonObject.get("displaytitle");
            try{
                // 이미지 URL 가져오기
                JSONObject jsonObjectOriginalImage = (JSONObject) jsonObject.get("originalimage");
                imageURL = (String) jsonObjectOriginalImage.get("source");

                // 뜻 가져오기
                meaning = (String) jsonObject.get("extract");
            } catch (NullPointerException e){
                changeNullToEmptyString(imageURL, meaning);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    private void changeNullToEmptyString(String imageURL, String meaning){
        this.imageURL = imageURL == null ? "" : imageURL;
        this.meaning = meaning == null ? "" : meaning;
    }
    public String getImageURL(){
        return imageURL;
    }
    public String getMeaning(){
        return meaning;
    }
}
