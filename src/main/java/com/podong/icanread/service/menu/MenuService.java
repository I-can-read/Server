package com.podong.icanread.service.menu;

import com.podong.icanread.app.dto.MenuDto;
import com.podong.icanread.app.naver.NaverClient;
import com.podong.icanread.app.wikipedia.WikipediaClient;
import com.podong.icanread.domain.menu.Menu;
import com.podong.icanread.domain.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    @Value("${naver.id}")
    private String naverClientId;

    @Value("${naver.secret}")
    private String naverClientSecret;

    public MenuDto findByName(String name) {
        Optional<Menu> entity = menuRepository.findByName(name);
        if (entity.isPresent()){ // 메뉴가 DB에 존재하는 경우
            return new MenuDto(entity.get());
        }
        else{ // 메뉴가 DB에 존재하지 않는 경우
            WikipediaClient wikipediaClient = new WikipediaClient(name);
            if (!wikipediaClient.isCheckDataNull()){ // 1순위 Wikipedia API 사용
                return searchByWikipedia(wikipediaClient, name);
            }
            else { // 2순위 Naver Encyclopedia Search API 사용
                return searchByNaver(name);
            }
        }
    }

    // Wikipedia API 연동
    public MenuDto searchByWikipedia(WikipediaClient wikipediaClient, String name){
        return saveData(name, wikipediaClient.getMeaning(), wikipediaClient.getImageURL());
    }

    // Naver Encyclopedia Search API 연동
    public MenuDto searchByNaver(String name) {
        NaverClient naverClient = new NaverClient(name, naverClientId, naverClientSecret);
        return saveData(name, naverClient.getMeaning(), naverClient.getImageURL());
    }

    // DB에 메뉴 저장
    private MenuDto saveData(String name, String meaning, String image) {
        Menu entity = Menu.builder()
                .name(name)
                .meaning(meaning)
                .image(image)
                .build();
        menuRepository.save(entity);
        return new MenuDto(entity);
    }

    // ML에서 텍스트 리스트 받아오는 로직 (수정 예정)
    public List<MenuDto> extractedTextListFromML() {
        String[] data = {"에스프레소", "아메리카노", "카페 모카", "카페 라떼", "카푸치노", "캐러멜 마키아토", "민트 초콜릿",
                "에스프레소", "바닐라 라떼", "차가운 음료", "오렌지 주스", "애플 주스", "아이스 라떼", "아이스 모카", "아이스 티",
                "디저트", "레드 벨벳 케이크", "마카롱", "크루아상", "치즈 케이크", "애플 파이", "피칸 파이", "초콜릿 케이크",
                "민트 컵케이크", "샌드위치", "달걀 & 햄", "오몰렛", "햄 & 치즈", "햄버거", "치즈 버거"};
        return makeMenuList(new ArrayList<>(Arrays.asList(data)));
    }

    // ML에서 텍스트 리스트 받아오면 뜻풀이, 이미지 추가한 메뉴판 새로 만들기
    public List<MenuDto> makeMenuList(ArrayList<String> data) {
        ArrayList<MenuDto> restructuredList = new ArrayList<>();
        for (String menuName : data){
            restructuredList.add(findByName(menuName));
        }
        return restructuredList;
    }
}
