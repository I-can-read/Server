package com.podong.icanread.service.menu;

import com.podong.icanread.app.dto.MenuDto;
import com.podong.icanread.domain.menu.Menu;
import com.podong.icanread.domain.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuDto findByName(String name) {
        Menu entity = menuRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 없습니다. 메뉴명 :" + name));
        return new MenuDto(entity);
    }
}
