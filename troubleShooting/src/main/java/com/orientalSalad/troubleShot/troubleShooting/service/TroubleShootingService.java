package com.orientalSalad.troubleShot.troubleShooting.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.orientalSalad.troubleShot.global.constant.Pagination;
import com.orientalSalad.troubleShot.global.utill.ObjectConverter;
import com.orientalSalad.troubleShot.tag.serivice.TagService;
import com.orientalSalad.troubleShot.troubleShooting.dto.RequestTroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.SearchTroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingReplyDTO;
import com.orientalSalad.troubleShot.troubleShooting.entity.TroubleShootingEntity;
import com.orientalSalad.troubleShot.troubleShooting.entity.TroubleShootingLikeEntity;
import com.orientalSalad.troubleShot.troubleShooting.entity.TroubleShootingReplyEntity;
import com.orientalSalad.troubleShot.troubleShooting.entity.TroubleShootingReplyLikeEntity;
import com.orientalSalad.troubleShot.troubleShooting.mapper.TroubleShootingMapper;
import com.orientalSalad.troubleShot.troubleShooting.repository.TroubleShootingLikeRepository;
import com.orientalSalad.troubleShot.troubleShooting.repository.TroubleShootingReplyLikeRepository;
import com.orientalSalad.troubleShot.troubleShooting.repository.TroubleShootingReplyRepository;
import com.orientalSalad.troubleShot.troubleShooting.repository.TroubleShootingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TroubleShootingService {
	private final TroubleShootingMapper troubleShootingMapper;
	private final TroubleShootingRepository troubleShootingRepository;
	private final TroubleShootingReplyRepository troubleShootingReplyRepository;
	private final TroubleShootingLikeRepository troubleShootingLikeRepository;
	private final TroubleShootingReplyLikeRepository troubleShootingReplyLikeRepository;
	private final ObjectConverter<TroubleShootingDTO, TroubleShootingEntity> troubleShootingConverter;
	private final ObjectConverter<TroubleShootingReplyDTO, TroubleShootingReplyEntity> troubleShootingReplyConverter;
	private final TagService tagService;

	public boolean insertTroubleShooting(RequestTroubleShootingDTO requestTroubleShootingDTO){
		TroubleShootingEntity troubleShootingEntity = troubleShootingConverter.toEntity(requestTroubleShootingDTO.getTroubleShooting());
		troubleShootingEntity = troubleShootingRepository.save(troubleShootingEntity);

		tagService.attachTag(requestTroubleShootingDTO.getTroubleShooting().getTags(),troubleShootingEntity.getSeq());

		return true;
	}
	public boolean updateTroubleShooting(RequestTroubleShootingDTO requestTroubleShootingDTO) throws Exception{
		//작성자와 로그인 유저 확인
		if(requestTroubleShootingDTO.getLoginSeq() != requestTroubleShootingDTO.getTroubleShooting().getWriter().getSeq()){
			throw new Exception("작성자와 로그인유저가 다릅니다.");
		}

		TroubleShootingDTO troubleShootingDTO = requestTroubleShootingDTO.getTroubleShooting();

		TroubleShootingEntity troubleShootingEntity = troubleShootingRepository.findBySeq(troubleShootingDTO.getSeq());
		troubleShootingEntity.update(troubleShootingDTO);

		troubleShootingEntity = troubleShootingRepository.save(troubleShootingEntity);

		tagService.updateTag(troubleShootingDTO.getTags(),troubleShootingDTO.getSeq());

		return true;
	}
	public boolean deleteTroubleShooting(RequestTroubleShootingDTO requestTroubleShootingDTO) throws Exception{
		//작성자와 로그인 유저 확인
		if(requestTroubleShootingDTO.getLoginSeq() != requestTroubleShootingDTO.getTroubleShooting().getWriter().getSeq()){
			throw new Exception("작성자와 로그인유저가 다릅니다.");
		}

		TroubleShootingDTO troubleShootingDTO = requestTroubleShootingDTO.getTroubleShooting();
	
		//문서 삭제
		TroubleShootingEntity troubleShootingEntity = troubleShootingRepository.findBySeq(troubleShootingDTO.getSeq());
		troubleShootingRepository.delete(troubleShootingEntity);
			
		//태그 삭제
		tagService.deleteTag(troubleShootingDTO.getSeq());

		return true;
	}
	public boolean insertTroubleShooingReply(TroubleShootingReplyDTO troubleShootingReplyDTO){
		TroubleShootingReplyEntity troubleShootingReplyEntity
			= troubleShootingReplyConverter.toEntity(troubleShootingReplyDTO);

		troubleShootingReplyRepository.save(troubleShootingReplyEntity);

		return true;
	}
	public boolean updateTroubleShooingReply(TroubleShootingReplyDTO troubleShootingReplyDTO) throws Exception {
		TroubleShootingReplyEntity replyEntity = troubleShootingReplyRepository.findById(troubleShootingReplyDTO.getSeq()).orElse(null);

		if(replyEntity == null){
			throw new Exception("잘못된 덧글입니다.");
		}

		replyEntity.update(troubleShootingReplyDTO);

		troubleShootingReplyRepository.save(replyEntity);

		return true;
	}
	public boolean deleteTroubleShooingReply(TroubleShootingReplyDTO troubleShootingReplyDTO) throws Exception {
		TroubleShootingReplyEntity replyEntity = troubleShootingReplyRepository.findById(troubleShootingReplyDTO.getSeq()).orElse(null);

		if(replyEntity == null){
			throw new Exception("잘못된 덧글입니다.");
		}

		troubleShootingReplyRepository.delete(replyEntity);

		return true;
	}
	public TroubleShootingDTO findTroubleShootingBySeq(long seq) throws Exception {
		TroubleShootingDTO troubleShootingDTO = troubleShootingMapper.selectTroubleShootingBySeq(seq);

		if(troubleShootingDTO == null){
			throw new Exception(seq+"번 게시물은 없습니다.");
		}

		log.info(troubleShootingDTO.toString());
		troubleShootingDTO.setViewCount(troubleShootingDTO.getViewCount()+1);
		//조회수 증가
		troubleShootingMapper.updateTroubleShootingView(troubleShootingDTO.getSeq());
		
		return troubleShootingDTO;
	}
	public List<TroubleShootingDTO> findTroubleShootingList(
		SearchTroubleShootingDTO searchParam) throws Exception {
		//페이지 번호 기본값
		if(searchParam.getPageNo() == 0){
			searchParam.setPageNo(Pagination.PAGE_NO);
		}

		//페이지 크기 기본값
		if(searchParam.getPageSize() == 0){
			searchParam.setPageSize(Pagination.PAGE_SIZE);
		}

		if(searchParam.getTags() != null){
			Collections.sort(searchParam.getTags());
		}

		int tagSize = searchParam.getTags() !=null ? searchParam.getTags().size() : 0;

		log.info(searchParam.toString());

		List<TroubleShootingDTO> troubleShootingDTOList
			= troubleShootingMapper.selectTroubleShootingList(searchParam,searchParam.getTags(),tagSize);

		return troubleShootingDTOList;
	}
	public Long countTroubleShootingList(SearchTroubleShootingDTO searchParam) throws Exception {
		int tageSize = 0;

		if(searchParam.getTags() != null){
			Collections.sort(searchParam.getTags());
			tageSize = searchParam.getTags().size();
		}

		Long count = troubleShootingMapper.countTroubleShootingList(searchParam,searchParam.getTags(),tageSize);

		return count;
	}
	public boolean updateReplyLike(Long userSeq,Long troubleShootingSeq, Long replySeq) throws Exception {
		TroubleShootingReplyLikeEntity replyLike =
			troubleShootingReplyLikeRepository.findByTroubleSeqEqualsAndReplySeqAndUserSeq(
				troubleShootingSeq,replySeq,userSeq);
		//좋아요 하기
		if(replyLike == null){
			replyLike =	TroubleShootingReplyLikeEntity.builder()
				.troubleSeq(troubleShootingSeq)
				.replySeq(replySeq)
				.userSeq(userSeq)
				.build();
			troubleShootingReplyLikeRepository.save(replyLike);
		}
		//좋아요 취소
		else{
			troubleShootingReplyLikeRepository.delete(replyLike);
		}

		return true;
	}
	public boolean updateTroubleShootingLike(Long userSeq,Long troubleShootingSeq) throws Exception {
		TroubleShootingLikeEntity troubleShootingLike =
			troubleShootingLikeRepository.findByTroubleSeqEqualsAndUserSeq(
				troubleShootingSeq,userSeq);
		//좋아요 하기
		if(troubleShootingLike == null){
			troubleShootingLike =	TroubleShootingLikeEntity.builder()
				.troubleSeq(troubleShootingSeq)
				.userSeq(userSeq)
				.build();
			troubleShootingLikeRepository.save(troubleShootingLike);
		}
		//좋아요 취소
		else{
			troubleShootingLikeRepository.delete(troubleShootingLike);
		}

		return true;
	}
}
