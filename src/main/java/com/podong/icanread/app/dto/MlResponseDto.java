package com.podong.icanread.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@NoArgsConstructor
public class MlResponseDto {
    ArrayList<String> textList;

    public void setTextList(ArrayList<String> textList) {
        this.textList = textList;
    }
}
