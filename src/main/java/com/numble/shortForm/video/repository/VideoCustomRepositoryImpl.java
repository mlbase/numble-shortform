package com.numble.shortForm.video.repository;

import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.user.entity.QUsers;
import com.numble.shortForm.video.dto.response.QVideoResponseDto;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.dto.response.VideoResponseDto;
import com.numble.shortForm.video.entity.VideoLike;
import com.numble.shortForm.video.sort.VideoSort;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.numble.shortForm.user.entity.QUsers.users;
import static com.numble.shortForm.video.entity.QVideo.video;

@RequiredArgsConstructor
public class VideoCustomRepositoryImpl implements VideoCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<VideoResponseDto> retrieveAll(Pageable pageable) {
        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size()
                )).from(video)
                .leftJoin(video.users,users)
                .orderBy(video.showId.desc())
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(fetch,pageable,fetch.size());
    }

    @Override
    public VideoResponseDto retrieveDetail(Long videoId, Long userId) {

        return queryFactory.select(new QVideoResponseDto(
                video.id,
                users.id,
                users.nickname,
                video.showId,
                video.title,
                video.thumbnail,
                video.isBlock,
                video.view,
                video.created_at,
                video.duration,
                video.videoLikes.size(),
                video.description,
                users.id.eq(userId)
        )).from(video)
                .leftJoin(video.users,users)
                .where(video.id.eq(videoId))
                .fetchOne();

    }

    @Override
    public Result retrieveMyVideo(String userEmail, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.isNotNull()
                )).from(video)
                .leftJoin(video.users,users)
                .orderBy(video.created_at.desc())
                .where(users.email.eq(userEmail))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .orderBy(VideoSort.sort(pageable))
                .fetch();

        int size = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size()
                )).from(video)
                .leftJoin(video.users, users)
                .where(users.email.eq(userEmail))
                .offset((pageable.getPageNumber() + 1) * pageable.getPageSize())
                .limit(1)
                .fetch().size();

       return new Result(size >0 ?true : false,fetch,fetch.size());

    }

    //메인비디오 추천 반환
    @Override
    public List<VideoResponseDto> getVideoByTag(Long videoId) {

     return null;

    }

    @Override
    public Page<VideoResponseDto> searchVideoQuery(String query,Pageable pageable,Long userId) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.id.eq(userId)
                )).from(video)
                .leftJoin(video.users,users)
                .where(video.title.contains(query).or(video.description.contains(query)))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .orderBy(VideoSort.sort(pageable))
                .fetch();

        return new PageImpl<>(fetch,pageable,fetch.size());
    }

    @Override
    public Page<VideoResponseDto> retrieveMainVideo(Pageable pageable,Long userId) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.id.eq(userId)
                )).from(video)
                .leftJoin(video.users, users)
                .orderBy(VideoSort.sort(pageable))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(fetch,pageable,fetch.size());
    }

    @Override
    public Page<VideoResponseDto> retrieveMainVideoNotLogin(Pageable pageable) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        video.isNull()
                )).from(video)
                .leftJoin(video.users, users)
                .orderBy(VideoSort.sort(pageable))
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(fetch,pageable,fetch.size());
    }

    @Override
    public Page<VideoResponseDto> retrieveConcernVideo(List<Long> videoids,Long videoId, Pageable pageable,Long userId) {

        List<VideoResponseDto> fetch = queryFactory.select(new QVideoResponseDto(
                        video.id,
                        users.id,
                        users.nickname,
                        video.showId,
                        video.title,
                        video.thumbnail,
                        video.isBlock,
                        video.view,
                        video.created_at,
                        video.duration,
                        video.videoLikes.size(),
                        video.description,
                        users.id.eq(userId)
                )).from(video)
                .leftJoin(video.users, users)
                .where(video.id.in(videoids).or(video.isNotNull()).and(video.id.ne(videoId)))
                .orderBy(video.videoLikes.size().desc())
                .offset(pageable.getPageNumber()* pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(fetch,pageable,fetch.size());
    }

}
