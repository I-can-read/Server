package com.podong.icanread.domain.menu;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@DynamicInsert
@Table(name="MENU")
public class Menu {
    // Menu 테이블 기본키(PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 테이블 칼럼 - 메뉴 이름
    @Column(length = 50, nullable = false)
    private String name;

    // 테이블 칼럼 - 메뉴 뜻풀이
    @Column(nullable = false)
    private String meaning;

    // 테이블 칼럼 - 메뉴 이미지 URL
    @Column(nullable = false)
    private String image;

    @Builder
    public Menu(String name, String meaning, String image){
        this.name = name;
        this.meaning = meaning;
        this.image = image;
    }
}
