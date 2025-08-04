package com.anipick.backend.image.mapper;

import com.anipick.backend.image.domain.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface ImageMapper {
    void insertImage(Image image);

    Optional<Image> findByImageId(@Param("imageId") Long imageId);

    Optional<Image> findByUserId(@Param("userId") Long userId);
}
