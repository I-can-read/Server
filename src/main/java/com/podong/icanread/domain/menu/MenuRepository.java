package com.podong.icanread.domain.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    // 메뉴 이름으로 DB에 있는지 조회
    @Query("SELECT m FROM Menu m WHERE name = :name")
    Optional<Menu> findByName(@Param("name") String name);
}
