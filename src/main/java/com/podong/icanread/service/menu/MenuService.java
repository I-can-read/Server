package com.podong.icanread.service.menu;

import com.podong.icanread.app.dto.MenuDto;
import com.podong.icanread.app.ml.MlClient;
import com.podong.icanread.app.naver.NaverClient;
import com.podong.icanread.app.wikipedia.WikipediaClient;
import com.podong.icanread.domain.menu.Menu;
import com.podong.icanread.domain.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MlClient mlClient;
    @Value("${naver.id}")
    private String naverClientId;

    @Value("${naver.secret}")
    private String naverClientSecret;

    public MenuDto findByName(String name) {
        String menuNameWithSpacesRemoved = removeSpaces(name);
        Optional<Menu> entity = menuRepository.findByName(menuNameWithSpacesRemoved);
        if (entity.isPresent()){ // 메뉴가 DB에 존재하는 경우
            return new MenuDto(name, entity.get().getMeaning(), entity.get().getImage());
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
        String menuNameWithSpacesRemoved = removeSpaces(name);
        Menu entity = Menu.builder()
                .name(menuNameWithSpacesRemoved)
                .meaning(meaning)
                .image(image)
                .build();
        menuRepository.save(entity);
        return new MenuDto(name, meaning, image);
    }

    // ML에서 텍스트 리스트 받아오기
    public List<MenuDto> extractedTextListFromML(MultipartFile file) throws IOException {
        return makeMenuList(mlClient.receiveTextListFromMl(file).getTextList());
    }

    // ML에서 텍스트 리스트 받아오면 뜻풀이, 이미지 추가한 메뉴판 새로 만들기
    public List<MenuDto> makeMenuList(ArrayList<String> data) {
        ArrayList<MenuDto> restructuredList = new ArrayList<>();
        final String menuPrice = "[0-9]+[.,]?[0-9]+";
        for (String menuName : data){
            String menuNameWithSpacesRemoved = removeSpaces(menuName);
            boolean isTea = menuName.equals("홍차") || menuName.equals("녹차");
            boolean isShortOrPrice = menuNameWithSpacesRemoved.length() < 3 || menuNameWithSpacesRemoved.matches(menuPrice);
            if (isTea || !isShortOrPrice){ // 3글자 미만 텍스트 또는 가격일 경우 제외 (홍차, 녹차는 예외적으로 포함)
                restructuredList.add(findByName(menuName));
            }
        }
        return restructuredList;
    }

    // 공백 제거
    public String removeSpaces(String name){
        return name.replaceAll("\\s", "");
    }
}
