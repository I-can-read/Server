package com.podong.icanread.service.menu;

import com.podong.icanread.app.dto.MenuDto;
import com.podong.icanread.app.ml.MlClient;
import com.podong.icanread.app.wikipedia.WikipediaClient;
import com.podong.icanread.domain.menu.Menu;
import com.podong.icanread.domain.menu.MenuRepository;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    MenuRepository menuRepository;

    MenuService menuService;

    @Autowired
    WikipediaClient wikipediaClient;

    @BeforeEach
    void setup() {
        this.menuService = new MenuService(menuRepository, new MlClient());
    }

    @AfterEach
    public void cleanup() {menuRepository.deleteAll();}

    @Test
    @DisplayName("메뉴 이름으로 DB에서 메뉴 조회 가능한지 체크")
    void mock_test() throws ParseException {
        Optional<Menu> menu = Optional.of(Menu.builder().name("아메리카노").meaning("에스프레소에 뜨거운 물을 더한 커피").image("https://kfcapi.inicis.com/kfcs_api_img/KFCS/goods/DL_1444725_20220704182019850.png").build());

        when(menuRepository.findByName(anyString())).thenReturn(menu);

        assertEquals(menuService.findByName("아메리카노").getName(), "아메리카노");

        // 목 객체의 findByName 한 번 실행되었는지 검증
        verify(menuRepository, times(1)).findByName("아메리카노");

        // findAll이 한번도 실행하지 않았는지 검증
        verify(menuRepository, never()).findAll();

        // 해당 Mock 이 더 이상 interactional 발생되지 않아야 한다.
        verifyNoMoreInteractions(menuRepository);
    }

    @Test
    @DisplayName("위키피디아에서 메뉴 뜻과 이미지 잘 받아오는지 확인")
    public void wikipediaTest() {
        wikipediaClient = new WikipediaClient("카페라떼");
        System.out.println(wikipediaClient.getMeaning());
        System.out.println(wikipediaClient.getImageURL());
    }

    @Test
    @DisplayName("Text List 받아서 하나씩 검색해오는지 확인")
    public void changeTextList_toMenuList() throws ParseException {
        String[] data = {"에스프레소", "아메리카노", "카페 모카", "카페 라떼", "카푸치노", "캐러멜 마키아토", "민트 초콜릿",
                "에스프레소", "바닐라 라떼", "차가운 음료", "오렌지 주스", "애플 주스", "아이스 라떼", "아이스 모카", "아이스 티",
                "디저트", "레드 벨벳 케이크", "마카롱", "크루아상", "치즈 케이크", "애플 파이", "피칸 파이", "초콜릿 케이크",
                "민트 컵케이크", "샌드위치", "달걀 & 햄", "오몰렛", "햄 & 치즈", "햄버거", "치즈 버거"};
        ArrayList<String> restructuredList = new ArrayList<>(Arrays.asList(data));
        for (String s : restructuredList) {
            System.out.println(s);
        }
    }

    @Test
    @DisplayName("ML에서 받아온 텍스트 메뉴인지 검증")
    public void excludeMenu_onlyNumber() {
        String[] data = {"에스프레소", "아메리카노", "빵", "10000", "10,000", "2", "10.0", "2,400", "24,0", "에24", "카페 모카", "바닐라 라떼", "레드 벨벳 케이크", "100", "홍차", "녹차"};
        final String menuPrice = "[0-9]+[.,]?[0-9]+";
        for (String menuName : data){
            String menuNameWithSpacesRemoved = menuName.replaceAll("\\s", ""); // 공백 제거
            boolean isTea = menuName.equals("홍차") || menuName.equals("녹차");
            boolean isShortOrPrice = menuNameWithSpacesRemoved.length() < 3 || menuNameWithSpacesRemoved.matches(menuPrice);
            if (isTea || !isShortOrPrice){ // 3글자 미만 텍스트 또는 가격일 경우 제외 (홍차, 녹차는 예외적으로 포함)
                System.out.println(menuName);
            }
        }
    }

    @Test
    @DisplayName("스무디가 포함된 경우 스무디 뜻 가져와서 스무디 앞 단어 포함해 재구성하기")
    public void reconstructMeaning() {
        String smoothieMeaning = "name는 ingredient와(과) 우유, 요거트 등을 섞어 만든 달콤한 음료이다.";
        String name = "사과 스무디";
        Matcher matcher = Pattern.compile("스무디").matcher(name);
        String ingredient = "";
        while(matcher.find()) {
            ingredient = name.substring(0, matcher.start()); // 스무디의 재료
            ingredient = ingredient.replaceAll("\\s", ""); // 공백 제거
            System.out.println(ingredient);
        }
        String nameReplace = smoothieMeaning.replaceAll("name", name);
        String ingredientReplace = nameReplace.replaceAll("ingredient", ingredient);
        System.out.println(ingredientReplace);
    }
}
