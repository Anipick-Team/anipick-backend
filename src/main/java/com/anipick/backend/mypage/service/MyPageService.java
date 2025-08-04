package com.anipick.backend.mypage.service;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.domain.SortOption;
import com.anipick.backend.common.dto.CursorDto;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.image.domain.Image;
import com.anipick.backend.mypage.domain.MyPageDefaults;
import com.anipick.backend.mypage.dto.*;
import com.anipick.backend.image.mapper.ImageMapper;
import com.anipick.backend.mypage.mapper.MyPageMapper;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.domain.UserAnimeOfStatus;
import com.anipick.backend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final MyPageMapper myPageMapper;
    private final UserMapper userMapper;
    private final ImageMapper imageMapper;

    public MyPageResponse getMyPage(Long userId) {
        User user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL));

        Long watchListCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.WATCHLIST.toString());
        Long watchingCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.WATCHING.toString());
        Long finishedCount = myPageMapper.getMyWatchCount(userId, UserAnimeOfStatus.FINISHED.toString());

        WatchCountDto watchCountDto = WatchCountDto.from(watchListCount, watchingCount, finishedCount);
        List<LikedAnimesDto> likedAnimesDto = myPageMapper.getMyLikedAnimes(userId, null, MyPageDefaults.DEFAULT_PAGE_SIZE);
        List<LikedPersonsDto> likedPersonsDto = myPageMapper.getMyLikedPersons(userId, null, MyPageDefaults.DEFAULT_PAGE_SIZE);

        return MyPageResponse.from(user.getNickname(), user.getProfileImageUrl(), watchCountDto, likedAnimesDto, likedPersonsDto);
    }

    @Transactional
    public ImageIdResponse updateProfileImage(CustomUserDetails user, MultipartFile profileImageFile) {
        String originalFilename = profileImageFile.getOriginalFilename();
        String uploadImageUrl;
        Image image;

        try {
            BufferedImage bufferedImage = ImageIO.read(profileImageFile.getInputStream());
            if(bufferedImage == null) {
                throw new CustomException(ErrorCode.INVAILD_IMAGE_EXTENSION);
            }

            byte[] compressedBytes = compressImageWithThumbnailator(profileImageFile);
            uploadImageUrl = getUploadImageUrl(originalFilename, user.getUserId());

            File directory = new File(uploadDir);
            if(!directory.exists()) {
                directory.mkdirs();
            }

            File outputFile = new File(directory, uploadImageUrl);
            try(FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                fileOutputStream.write(compressedBytes);
            }
            log.info("uploadImageUrl : {}", uploadImageUrl);

            userMapper.updateUserProfileImage(user.getUserId(), uploadImageUrl);

            log.info("output file : {}", outputFile.getAbsolutePath());

            image = Image.builder()
                    .authId(user.getUserId())
                    .imageName(originalFilename)
                    .imagePath(outputFile.getAbsolutePath())
                    .build();
            imageMapper.insertImage(image);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ImageIdResponse.from(image.getImageId());
    }

    public ImageResponse getProfileImage(CustomUserDetails user, Long imageId) {
        if(user == null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Image image = imageMapper.findByImageId(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_DATA_NOT_FOUND));
        String imagePath = image.getImagePath();
        byte[] imageBytes;

        try {
            Path filePath = Paths.get(imagePath);
            log.info("filePath : {}", filePath);
            imageBytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ImageResponse.from(imageBytes);
    }

    public WatchListAnimesResponse getMyAnimesWatchList(Long userId, String status, Long lastId, Integer size) {
        Long count = myPageMapper.getMyWatchCount(userId, status);
        List<WatchListAnimesDto> watchListAnimes = myPageMapper.getMyWatchListAnimes(userId, status, lastId, size);
        Long newLastId;

        if(watchListAnimes.isEmpty()) {
            newLastId = null;
        } else {
            newLastId = watchListAnimes.getLast().getUserAnimeStatusId();
        }

        CursorDto cursorDto = CursorDto.of(newLastId);

        return WatchListAnimesResponse.from(count, cursorDto, watchListAnimes);
    }

    public WatchingAnimesResponse getMyAnimesWatching(Long userId, String status, Long lastId, Integer size) {
        Long count = myPageMapper.getMyWatchCount(userId, status);
        List<WatchingAnimesDto> watchingAnimes = myPageMapper.getMyWatchingAnimes(userId, status, lastId, size);
        Long newLastId;

        if (watchingAnimes.isEmpty()) {
            newLastId = null;
        } else {
            newLastId = watchingAnimes.getLast().getUserAnimeStatusId();
        }

        CursorDto cursorDto = CursorDto.of(newLastId);

        return WatchingAnimesResponse.from(count, cursorDto, watchingAnimes);
    }

    public FinishedAnimesResponse getMyAnimesFinished(Long userId, String status, Long lastId, Integer size) {
        Long count = myPageMapper.getMyWatchCount(userId, status);
        List<FinishedAnimesDto> finishedAnimes = myPageMapper.getMyFinishedAnimes(userId, status, lastId, size);
        Long newLastId;

        if(finishedAnimes.isEmpty()) {
            newLastId = null;
        } else {
            newLastId = finishedAnimes.getLast().getUserAnimeStatusId();
        }

        CursorDto cursorDto = CursorDto.of(newLastId);

        return FinishedAnimesResponse.from(count, cursorDto, finishedAnimes);
    }

    public RatedAnimesResponse getMyAnimesRated(Long userId, Long lastId, Long lastCount, Double lastRating, Integer size, String sort, Boolean reviewOnly) {
        Long count = myPageMapper.getMyReviewCount(userId);
        SortOption sortOption = SortOption.of(sort);

        List<AnimesReviewDto> animesReviews;
        Long newLastId;
        Long newLastLikeCount;
        Double newLastRating;

        if(reviewOnly) {
            animesReviews = myPageMapper.getMyAnimesReviewsOnly(userId, lastId, size, sortOption.getCode(), lastCount, lastRating);
        } else {
            animesReviews = myPageMapper.getMyAnimesReviewsAll(userId, lastId, size, sortOption.getCode(), lastCount, lastRating);
        }

        if(animesReviews.isEmpty()) {
            newLastId = null;
            newLastLikeCount = 0L;
            newLastRating = 0.0;
        } else {
            newLastId = animesReviews.getLast().getReviewId();
            newLastLikeCount = animesReviews.getLast().getLikeCount();
            newLastRating = animesReviews.getLast().getRating();
        }

        CursorDto cursorDto = getCursorBySortOption(newLastId, newLastLikeCount, newLastRating, sort, sortOption);

        return RatedAnimesResponse.from(count, cursorDto, animesReviews);
    }

    public LikedAnimesResponse getMyAnimesLiked(Long userId, Long lastId, Integer size) {
        Long count = myPageMapper.getMyAnimesLikeCount(userId);
        List<LikedAnimesDto> likedAnimes = myPageMapper.getMyLikedAnimes(userId, lastId, size);
        Long newLastId;

        if(likedAnimes.isEmpty()) {
            newLastId = null;
        } else {
            newLastId = likedAnimes.getLast().getAnimeLikeId();
        }

        CursorDto cursorDto = CursorDto.of(newLastId);

        return LikedAnimesResponse.from(count, cursorDto, likedAnimes);
    }

    public LikedPersonsResponse getMyPersonsLiked(Long userId, Long lastId, Integer size) {
        Long count = myPageMapper.getMyPersonsLikeCount(userId);
        List<LikedPersonsDto> likedPersons = myPageMapper.getMyLikedPersons(userId, lastId, size);
        Long newLastId;

        if(likedPersons.isEmpty()) {
            newLastId = null;
        } else {
            newLastId = likedPersons.getLast().getUserLikedVoiceActorId();
        }

        CursorDto cursorDto = CursorDto.of(newLastId);

        return LikedPersonsResponse.from(count, cursorDto, likedPersons);
    }

    private CursorDto getCursorBySortOption(Long lastId, Long lastCount, Double lastRating, String sort, SortOption sortOption) {
        CursorDto cursorDto;
        switch (sortOption) {
            case LATEST:
                cursorDto = CursorDto.of(sort, lastId);
                break;
            case LIKES:
                cursorDto = CursorDto.of(sort, lastId, String.valueOf(lastCount));
                break;
            case RATING_ASC:
                cursorDto = CursorDto.of(sort, lastId, String.valueOf(lastRating));
                break;
            case RATING_DESC:
                cursorDto = CursorDto.of(sort, lastId, String.valueOf(lastRating));
                break;
            default:
                cursorDto = CursorDto.of(sort, lastId);
        }

        return cursorDto;
    }

    private String getUploadImageUrl(String fileName, Long userId) {
        String baseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        return userId + System.currentTimeMillis() + baseName + "." + extension;
    }

    private byte[] compressImageWithThumbnailator(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(800, 800)
                .outputQuality(0.7)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}

