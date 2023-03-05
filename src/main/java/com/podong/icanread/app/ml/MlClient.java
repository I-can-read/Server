package com.podong.icanread.app.ml;

import com.podong.icanread.app.dto.MlResponseDto;
import com.podong.icanread.app.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;

import static com.podong.icanread.app.exception.ErrorCode.NOT_FOUND_TEXT_LIST;

@Slf4j
@Service
@RequiredArgsConstructor
public class MlClient {
    // ML 서버에 이미지 보내고, Text List 받아오기
    public MlResponseDto receiveTextListFromMl(@RequestParam("file") MultipartFile file) throws IOException{
        WebClient client = WebClient.create("http://localhost:8080"); // ML 서버 주소로 나중에 수정하기
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        return client.post()
                .uri("/menu/extract")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(MlResponseDto.class)
                .timeout(Duration.ofMillis(1000))
                .blockOptional().orElseThrow(
                        () -> new CustomException(NOT_FOUND_TEXT_LIST)
                );
    }

    // ML 서버 역할
    public MlResponseDto mlServer(@RequestParam("file") MultipartFile file) {
        ArrayList<String> textList = new ArrayList<>();
        textList.add("아메리카노");
        textList.add("카페모카");
        textList.add("카푸치노");
        MlResponseDto mlResponseDto = new MlResponseDto();
        mlResponseDto.setTextList(textList);
        return mlResponseDto;
    }

    // MultipartFile 자체 대신 MultipartMap에 MultipartFile의 바이트를 추가하기 위한 클래스
    static class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // we do not want to generally read the whole stream into memory ...
        }
    }
}
