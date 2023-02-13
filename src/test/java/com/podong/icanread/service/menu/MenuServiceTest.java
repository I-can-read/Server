package com.podong.icanread.service.menu;

import com.podong.icanread.app.wikipedia.WikipediaClient;
import com.podong.icanread.domain.menu.Menu;
import com.podong.icanread.domain.menu.MenuRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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
        this.menuService = new MenuService(menuRepository);
    }

    @AfterEach
    public void cleanup() {menuRepository.deleteAll();}

    @Test
    @DisplayName("메뉴 이름으로 DB에서 메뉴 조회 가능한지 체크")
    void mock_test() {
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
}
