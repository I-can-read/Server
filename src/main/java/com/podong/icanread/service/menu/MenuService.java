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

    public MenuDto findByName(String name) throws ParseException {
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
    public MenuDto searchByNaver(String name) throws ParseException {
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
}
