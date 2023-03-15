package com.podong.icanread.controller;

import com.podong.icanread.app.dto.MlResponseDto;
import com.podong.icanread.app.dto.MenuDto;
import com.podong.icanread.app.exception.CustomException;
import com.podong.icanread.app.ml.MlClient;
import com.podong.icanread.service.menu.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.podong.icanread.app.exception.ErrorCode.NOT_FOUND_FILE;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MenuController {
    private final MenuService menuService;
    private final MlClient mlClient;

    // 메뉴 이름으로 메뉴 조회 테스트용 API
    @GetMapping("/api/v1/menu")
    public MenuDto findByName(@RequestParam("name") String name) {
        return menuService.findByName(name);
    }

    // Naver Search API 결과 테스트
    @GetMapping("/api/v1/naver")
    public MenuDto naverSearch(@RequestParam("name") String name) {
        return menuService.searchByNaver(name);
    }

    // 재구성된 메뉴 리스트 조회 테스트 API (FE <-> BE <-> ML)
    @PostMapping("/api/v1/menu/list")
    public List<MenuDto> reorganizedMenuList(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()){
            return menuService.extractedTextListFromML(file);
        }
        else{
            throw new CustomException(NOT_FOUND_FILE);
        }
    }

    // 이미지 request body로 뒀을 때, 재구성된 메뉴 리스트 조회 테스트 API (BE <-> ML OK)
    @PostMapping("/api/v1/ml/test")
    public ArrayList<String> textList(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()){
            return mlClient.receiveTextListFromMl(file).getTextList();
        }
        else{
            throw new CustomException(NOT_FOUND_FILE);
        }
    }

    // ML 서버에 요청할 API 역할
    @PostMapping("/api/v1/menu/extract")
    public MlResponseDto checkMlServer(@RequestParam("file") MultipartFile file) {
        return mlClient.mlServer(file);
    }
}
