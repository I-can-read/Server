package com.podong.icanread.app.dto;

import com.podong.icanread.domain.menu.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuDto {
    private String name;
    private String meaning;
    private String image;

    public MenuDto(Menu entity){
        this.name = entity.getName();
        this.meaning = entity.getMeaning();
        this.image = entity.getImage();
    }
}
