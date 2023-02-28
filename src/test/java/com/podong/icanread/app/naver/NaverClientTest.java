package com.podong.icanread.app.naver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NaverClientTest {
    @Test
    @DisplayName("네이버 백과사전 검색 API에서 받아오는 데이터 필터링 테스트")
    public void testMeaningFiltering_fromNaverSearchApi(){
        String meaning = "적당량의 뜨거운 물을 섞는 방식이 연한 커피를 즐기는 미국에서 시작된 것이라 하여 ‘<b>아메리카노</b>’라 부른다. 우리나라에서도 가장 인기 있는 메뉴 중 하나이다. 에스프레소에 쓰이는 원두와 물의 양에 따라... ";
        // 태그 제외
        meaning = meaning.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
        System.out.println(getTwoSentences(meaning));
    }

    // 2문장까지만 가져오기
    private String getTwoSentences (String str) {
        Pattern pattern = Pattern.compile("(.*?)[.](.*?)[.]");
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return str.substring(0, matcher.end());
        }
        return null;
    }
}
