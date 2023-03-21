package com.podong.icanread.app.ml;

import com.podong.icanread.app.dto.MlResponseDto;
import com.podong.icanread.app.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${ml.url}")
    private String mlUrl;

    // ML 서버에 이미지 보내고, Text List 받아오기
    public MlResponseDto receiveTextListFromMl(@RequestParam("file") MultipartFile file) throws IOException{
        WebClient client = WebClient.create(mlUrl);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        MlResponseDto mlResponseDto = client.post()
                .uri("/api/v1/menu/extract")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(MlResponseDto.class)
                .timeout(Duration.ofMillis(5000))
                .blockOptional().orElseThrow(
                        () -> new CustomException(NOT_FOUND_TEXT_LIST)
                );
        file.getInputStream().close();
        return mlResponseDto;
    }

    // ML 서버 역할
    public MlResponseDto mlServer(@RequestParam("file") MultipartFile file) {
        ArrayList<String> textList = new ArrayList<>();
        textList.add("에스프레소");
        textList.add("아메리카노");
        textList.add("카푸치노");
        textList.add("카페모카");
        textList.add("화이트 카페모카");
        textList.add("카라멜 마키아또");
        textList.add("콜드브루");
        textList.add("아인슈페너");
        textList.add("아포가토");
        textList.add("카페 라떼");
        textList.add("바닐라 라떼");
        textList.add("고구마 라떼");

        textList.add("그린티 라떼");
        textList.add("초코 라떼");
        textList.add("헤이즐넛 라떼");
        textList.add("콜드브루 라떼");
        textList.add("그린티");
        textList.add("아이스티");
        textList.add("얼그레이");
        textList.add("캐모마일");
        textList.add("페퍼민트");
        textList.add("로즈마리");
        textList.add("자스민차");
        textList.add("유자차");

        textList.add("레몬차");
        textList.add("에이드");
        textList.add("레몬 에이드");
        textList.add("자몽 에이드");
        textList.add("청포도 에이드");
        textList.add("블루레몬 에이드");
        textList.add("프라페");
        textList.add("망고 스무디");
        textList.add("딸기 스무디");
        textList.add("블루베리 스무디");
        textList.add("요거트 스무디");
        textList.add("키위 스무디");

        textList.add("딸기바나나 주스");
        textList.add("키위 주스");
        textList.add("오렌지 주스");
        textList.add("토마토 주스");
        MlResponseDto mlResponseDto = new MlResponseDto();
        mlResponseDto.setTextList(textList);
        return mlResponseDto;
    }

    // MultipartFile 자체 대신 MultipartMap에 MultipartFile의 바이트를 추가하기 위한 클래스
    static class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) throws IOException {
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
