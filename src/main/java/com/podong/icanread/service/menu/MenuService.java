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

    public MenuDto findByName(String name) {
        Optional<Menu> entity = menuRepository.findByName(name);
        if (entity.isPresent()){ // 메뉴가 DB에 존재하는 경우
            return new MenuDto(entity.get());
        }
        else{ // 메뉴가 DB에 존재하지 않는 경우
            return searchByWikipedia(name);
        }
    }

    // 위키피디아에서 받아온 메뉴 뜻, 이미지 DB에 저장하고 리턴하는 메소드
    public MenuDto searchByWikipedia(String name){
        WikipediaClient wikipediaClient = new WikipediaClient(name);
        Menu entity = Menu.builder()
                .name(name)
                .meaning(wikipediaClient.getMeaning())
                .image(wikipediaClient.getImageURL())
                .build();
        menuRepository.save(entity);
        return new MenuDto(entity);
    }

    // 네이버에서 받아온 메뉴 뜻, 이미지 DB에 저장하고 리턴하는 메소드
    public MenuDto searchByNaver(String name) throws ParseException {
        NaverClient naverClient = new NaverClient(name, naverClientId, naverClientSecret);
        Menu entity = Menu.builder()
                .name(name)
                .meaning(naverClient.getMeaning())
                .image(naverClient.getImageURL())
                .build();
        menuRepository.save(entity);
        return new MenuDto(entity);
    }
}
