package gcboard.gcboardrefactoring.domain.comment.application.service;

import gcboard.gcboardrefactoring.domain.comment.domain.entity.CommentEntity;
import gcboard.gcboardrefactoring.domain.comment.domain.repository.CommentRepository;
import gcboard.gcboardrefactoring.domain.comment.exception.CommentAccessDeniedException;
import gcboard.gcboardrefactoring.domain.comment.exception.CommentAcceptDepthException;
import gcboard.gcboardrefactoring.domain.comment.exception.CommentAlreadyDeletedException;
import gcboard.gcboardrefactoring.domain.comment.exception.CommentDepthExceededException;
import gcboard.gcboardrefactoring.domain.comment.exception.CommentNotFoundException;
import gcboard.gcboardrefactoring.domain.comment.exception.CommentPostMismatchException;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.request.CommentCreateRequestDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.request.CommentCursorRequestDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.request.CommentUpdateRequestDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.response.CommentResponseDto;
import gcboard.gcboardrefactoring.domain.comment.presentation.dto.response.CommentTreeResponseDto;
import gcboard.gcboardrefactoring.domain.post.domain.entity.PostEntity;
import gcboard.gcboardrefactoring.domain.post.domain.repository.PostRepository;
import gcboard.gcboardrefactoring.domain.post.exception.PostAcceptAccessDeniedException;
import gcboard.gcboardrefactoring.domain.post.exception.PostAcceptTargetMismatchException;
import gcboard.gcboardrefactoring.domain.post.exception.PostNotFoundException;
import gcboard.gcboardrefactoring.global.cursor.response.CursorResponseDto;
import gcboard.gcboardrefactoring.global.security.user.GcBoardUserDetails;
import gcboard.gcboardrefactoring.global.validation.ProfanityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final int PATH_SEGMENT_LENGTH = 8;
    private static final int MAX_COMMENT_DEPTH = 2;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ProfanityValidator profanityValidator;

    @Transactional
    public CommentResponseDto createComment(Long postId, GcBoardUserDetails gcBoardUserDetails, CommentCreateRequestDto requestDto, Long parentCommentId) {
        PostEntity postEntity = findPostOrThrow(postId);
        profanityValidator.validateClean(requestDto.content());

        CommentEntity parent = null;
        Long rootId = null;
        int depth = 0;

        if (parentCommentId != null) {
            parent = findCommentOrThrow(parentCommentId);
            validateCommentBelongsToPost(parent, postId);
            rootId = parent.getRootId();
            depth = parent.getDepth() + 1;
            if (depth >= MAX_COMMENT_DEPTH) {
                throw new CommentDepthExceededException(MAX_COMMENT_DEPTH);
            }
        }

        CommentEntity commentEntity = CommentEntity.builder()
                .post(postEntity)
                .parent(parent)
                .rootId(rootId)
                .depth(depth)
                .content(requestDto.content())
                .authorNickname(gcBoardUserDetails.getUsername())
                .build();

        commentRepository.save(commentEntity);

        Long resolvedRootId = (rootId != null) ? rootId : commentEntity.getId();
        String path = buildPath(parent, commentEntity.getId());
        commentEntity.updateRootAndPath(resolvedRootId, path);

        return CommentResponseDto.from(commentEntity);
    }

    @Transactional(readOnly = true)
    public CursorResponseDto<CommentTreeResponseDto> getComments(Long postId, CommentCursorRequestDto cursorRequestDto, Long parentCommentId) {
        findPostOrThrow(postId);
        long limit = cursorRequestDto.count() + 1;
        CommentEntity parent = null;
        String parentPathPrefix = "";
        if (parentCommentId != null) {
            parent = findCommentOrThrow(parentCommentId);
            validateCommentBelongsToPost(parent, postId);
            parentPathPrefix = parent.getPath() + "/";
        }
        List<CommentTreeResponseDto> comments = commentRepository.findByPostIdWithCursor(postId, parentPathPrefix, cursorRequestDto.cursorPath(), limit);

        boolean hasMore = comments.size() > cursorRequestDto.count();
        List<CommentTreeResponseDto> content = hasMore ? comments.subList(0, cursorRequestDto.count().intValue()) : comments;
        List<CommentTreeResponseDto> masked = content.stream()
                .map(CommentTreeResponseDto::maskDeleted)
                .toList();

        return new CursorResponseDto<CommentTreeResponseDto>(
                masked,
                masked.size(),
                !hasMore
        );
    }

    @Transactional
    public CommentResponseDto updateComment(Long postId, Long commentId, GcBoardUserDetails gcBoardUserDetails, CommentUpdateRequestDto requestDto) {
        CommentEntity commentEntity = findCommentOrThrow(commentId);
        validateCommentBelongsToPost(commentEntity, postId);
        validateAuthor(commentEntity, gcBoardUserDetails.getUsername());
        validateNotDeleted(commentEntity);
        profanityValidator.validateClean(requestDto.content());

        commentEntity.updateContent(requestDto.content());
        return CommentResponseDto.from(commentEntity);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, GcBoardUserDetails gcBoardUserDetails) {
        CommentEntity commentEntity = findCommentOrThrow(commentId);
        validateCommentBelongsToPost(commentEntity, postId);
        validateAuthor(commentEntity, gcBoardUserDetails.getUsername());
        validateNotDeleted(commentEntity);

        commentEntity.softDelete();
        if (commentEntity.getPost().getAcceptedCommentId() != null
                && commentEntity.getPost().getAcceptedCommentId().equals(commentEntity.getId())) {
            commentEntity.getPost().unacceptComment();
        }
    }

    @Transactional
    public void acceptComment(Long postId, Long commentId, GcBoardUserDetails gcBoardUserDetails) {
        PostEntity postEntity = findPostOrThrow(postId);
        CommentEntity commentEntity = findCommentOrThrow(commentId);
        validateCommentBelongsToPost(commentEntity, postId);
        validateAuthorIsPostOwner(postEntity, gcBoardUserDetails.getUsername());
        validateAcceptDepth(commentEntity);
        validateNotDeleted(commentEntity);

        postEntity.acceptComment(commentEntity.getId());
    }

    @Transactional
    public void unacceptComment(Long postId, Long commentId, GcBoardUserDetails gcBoardUserDetails) {
        PostEntity postEntity = findPostOrThrow(postId);
        CommentEntity commentEntity = findCommentOrThrow(commentId);
        validateCommentBelongsToPost(commentEntity, postId);
        validateAuthorIsPostOwner(postEntity, gcBoardUserDetails.getUsername());

        if (postEntity.getAcceptedCommentId() == null || !postEntity.getAcceptedCommentId().equals(commentEntity.getId())) {
            throw new PostAcceptTargetMismatchException();
        }

        postEntity.unacceptComment();
    }

    private CommentEntity findCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    private PostEntity findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    private void validateCommentBelongsToPost(CommentEntity commentEntity, Long postId) {
        if (!commentEntity.getPost().getId().equals(postId)) {
            throw new CommentPostMismatchException();
        }
    }

    private void validateAuthor(CommentEntity commentEntity, String nickname) {
        if (!commentEntity.getAuthorNickname().equals(nickname)) {
            throw new CommentAccessDeniedException();
        }
    }

    private void validateAcceptDepth(CommentEntity commentEntity) {
        if (commentEntity.getDepth() > 0) {
            throw new CommentAcceptDepthException();
        }
    }

    private void validateNotDeleted(CommentEntity commentEntity) {
        if (Boolean.TRUE.equals(commentEntity.getIsDeleted())) {
            throw new CommentAlreadyDeletedException();
        }
    }

    private void validateAuthorIsPostOwner(PostEntity postEntity, String nickname) {
        if (!postEntity.getAuthorNickname().equals(nickname)) {
            throw new PostAcceptAccessDeniedException();
        }
    }

    private String buildPath(CommentEntity parent, Long commentId) {
        String segment = toPaddedHex(commentId);
        if (parent == null) {
            return segment;
        }
        return parent.getPath() + "/" + segment;
    }

    private String toPaddedHex(Long id) {
        return String.format("%0" + PATH_SEGMENT_LENGTH + "x", id);
    }
}
