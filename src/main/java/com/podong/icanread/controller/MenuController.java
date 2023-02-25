package com.podong.icanread.controller;

import com.podong.icanread.app.dto.MenuDto;
import com.podong.icanread.service.menu.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MenuController {
    private final MenuService menuService;

    // 메뉴 이름으로 메뉴 조회 테스트용 API
    @GetMapping("/api/v1/menu")
    public MenuDto findByName(@RequestParam("name") String name){
        return menuService.findByName(name);
    }

    // Naver Search API 결과 테스트
    @GetMapping("/api/v1/naver")
    public MenuDto naverSearch(@RequestParam("name") String name) throws ParseException {
        return menuService.searchByNaver(name);
    }
}
