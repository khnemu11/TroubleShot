package com.orientalSalad.troubleShot.troubleShooting.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.orientalSalad.troubleShot.global.dto.RequestDTO;
import com.orientalSalad.troubleShot.global.dto.ResultDTO;
import com.orientalSalad.troubleShot.global.utill.Authentication;
import com.orientalSalad.troubleShot.troubleShooting.dto.RequestTroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.RequestTroubleShootingReplyDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.ResponseTroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.ResponseTroubleShootingListDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.SearchTroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingAnswerDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingReplyDTO;
import com.orientalSalad.troubleShot.troubleShooting.service.TroubleShootingAnswerService;
import com.orientalSalad.troubleShot.troubleShooting.service.TroubleShootingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Tag(name = "트러블 슈팅 API")
@Controller
@RequestMapping("/trouble-shootings")
@RequiredArgsConstructor
@Log4j2
public class TroubleShootingController {
	private final TroubleShootingService troubleShootingService;
	private final TroubleShootingAnswerService troubleShootingAnswerService;
	private final Authentication authentication;

	@Operation(summary = "트러블 슈팅 문서 등록",description = "입력 DTO :RequestTroubleShootingDTO")
	@PostMapping("")
	public ResponseEntity<?> insertTroubleShooting(
		HttpServletRequest request, @RequestBody RequestTroubleShootingDTO requestTroubleShootingDTO) throws Exception {
		log.info("====== 트러블 슈팅 문서 등록 시작 =====");
		log.info(requestTroubleShootingDTO.toString());

		//로그인 확인
		authentication.checkLogin(request,requestTroubleShootingDTO);

		//트러블슈팅 문서 등록
		troubleShootingService.insertTroubleShooting(requestTroubleShootingDTO);

		ResultDTO resultDTO = ResultDTO.builder()
			.success(true)
			.message("트러블 슈팅 문서 등록이 성공했습니다.")
			.build();
		
		log.info("====== 트러블 슈팅 문서 등록 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "트러블 슈팅 문서 삭제",description = "입력 DTO :RequestTroubleShootingDTO")
	@DeleteMapping("/{seq}")
	public ResponseEntity<?> deleteTroubleShooting(
		HttpServletRequest request,
		@ModelAttribute RequestDTO requestDTO,
		@PathVariable(name = "seq") Long seq) throws
		Exception {
		log.info("====== 트러블 슈팅 문서 삭제 시작 =====");
		log.info(requestDTO.toString());
		//로그인 확인
		authentication.checkLogin(request,requestDTO);
		//pk 설정
		RequestTroubleShootingDTO requestTroubleShootingDTO
			= RequestTroubleShootingDTO.builder()
			.loginSeq(requestDTO.getLoginSeq())
			.type(requestDTO.getType())
			.troubleShooting(TroubleShootingDTO.builder()
				.seq(seq)
				.build())
			.build();
		//트러블슈팅 문서 삭제
		troubleShootingService.deleteTroubleShooting(requestTroubleShootingDTO);

		ResultDTO resultDTO = ResultDTO.builder()
			.success(true)
			.message(seq+"번 트러블 슈팅 문서 삭제를 성공했습니다.")
			.build();

		log.info("====== 트러블 슈팅 문서 삭제 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "트러블 슈팅 문서 수정",description = "입력 DTO :RequestTroubleShootingDTO")
	@PutMapping("/{seq}")
	public ResponseEntity<?> updateTroubleShooting(
		HttpServletRequest request,
		@RequestBody RequestTroubleShootingDTO requestTroubleShootingDTO,
		@PathVariable(name = "seq") Long seq) throws
		Exception {
		log.info("====== 트러블 슈팅 문서 수정 시작 =====");
		log.info(requestTroubleShootingDTO);
		//로그인 확인
		authentication.checkLogin(request,requestTroubleShootingDTO);
		//pk 설정
		requestTroubleShootingDTO.getTroubleShooting().setSeq(seq);
		//트러블슈팅 문서 수정
		troubleShootingService.updateTroubleShooting(requestTroubleShootingDTO);

		ResultDTO resultDTO = ResultDTO.builder()
			.success(true)
			.message("트러블 슈팅 문서 수정이 성공했습니다.")
			.build();

		log.info("====== 트러블 슈팅 문서 수정 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "트러블 슈팅 단일 문서 상세 내용 검색",description = "입력 DTO :RequestDTO")
	@GetMapping("/{seq}")
	public ResponseEntity<?> findTroubleShooting(@PathVariable(name = "seq") long seq,
		@ModelAttribute RequestDTO requestDTO) throws Exception {
		log.info("====== 트러블 슈팅 문서 pk 탐색 시작 =====");
		log.info("pk : "+seq);
		log.info("request : "+requestDTO.toString());
		//
		TroubleShootingDTO troubleShootingDTO = troubleShootingService.findTroubleShootingBySeq(seq,requestDTO);

		if(troubleShootingDTO == null){
			throw new Exception(seq+"번 트러블 슈팅 문서가 없습니다.");
		}

		//솔루션 정렬
		if(troubleShootingDTO.getAnswers() != null){
			// log.info("솔루션 정렬");
			List<TroubleShootingAnswerDTO> answerDTOList = new ArrayList<>(troubleShootingDTO.getAnswers());
			// log.info(answerDTOList);
			answerDTOList.sort((o1, o2) -> {
				if(o1.getLikeCount() == o2.getLikeCount()){
					if(o1.getCreateTime().isBefore(o2.getCreateTime())){
						return 1;
					}else{
						return -1;
					}
				}
				return o2.getLikeCount() - o1.getLikeCount();
			});
			// log.info(answerDTOList);
			Set<TroubleShootingAnswerDTO> answerSet = new LinkedHashSet(answerDTOList);
			// log.info(answerSet);

			troubleShootingDTO.setAnswers(answerSet);
		}
		// log.info(troubleShootingDTO.getAnswers());
		ResponseTroubleShootingDTO resultDTO = ResponseTroubleShootingDTO.builder()
			.success(true)
			.message(seq+"번 트러블 슈팅 문서 검색을 성공했습니다.")
			.troubleShooting(troubleShootingDTO)
			.build();
		
		// log.info("====== 트러블 슈팅 문서 pk 탐색 끝 =====");
		return new ResponseEntity<ResponseTroubleShootingDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "트러블 슈팅 문서 목록 검색",description = "입력 DTO :SearchTroubleShootingDTO")
	@GetMapping("")
	public ResponseEntity<?> findTroubleShootingList(
		@ModelAttribute SearchTroubleShootingDTO searchParam) throws
		Exception {
		log.info("====== 트러블 슈팅 문서 목록 검색 시작 =====");
		log.info(searchParam);

		List<TroubleShootingDTO> troubleShootingDTOList = troubleShootingService.findTroubleShootingList(searchParam);
		Long totalCount = troubleShootingService.countTroubleShootingList(searchParam);

		ResponseTroubleShootingListDTO resultDTO = ResponseTroubleShootingListDTO.builder()
			.success(true)
			.message("트러블 슈팅 문서 목록 검색을 성공했습니다.")
			.totalCount(totalCount)
			.troubleShootingList(troubleShootingDTOList)
			.build();

		log.info("====== 트러블 슈팅 문서 목록 검색 끝 =====");
		return new ResponseEntity<ResponseTroubleShootingListDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "트러블 슈팅 덧글 달기",description = "입력 DTO :requestTroubleShootingReplyDTO")
	@PostMapping("/{seq}/reply")
	public ResponseEntity<?> insertReply(@PathVariable(name = "seq") long seq,
		@RequestBody RequestTroubleShootingReplyDTO requestTroubleShootingReplyDTO,
		HttpServletRequest request) throws Exception {
		log.info("====== 트러블 슈팅 문서 덧글 달기 시작 =====");
		//로그인 확인
		authentication.checkLogin(request,requestTroubleShootingReplyDTO);

		troubleShootingService.insertTroubleShooingReply(requestTroubleShootingReplyDTO);

		ResultDTO resultDTO = ResultDTO.builder()
			.success(true)
			.message("트러블 슈팅 문서 덧글 등록이 성공했습니다.")
			.build();

		log.info("====== 트러블 슈팅 문서 덧글 달기 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "트러블 슈팅 덧글 수정",description = "입력 DTO :RequestTroubleShootingReplyDTO")
	@PutMapping("/{troubleSeq}/reply/{replySeq}")
	public ResponseEntity<?> updateReply(
		@PathVariable(name = "troubleSeq") long troubleSeq,
		@PathVariable(name = "replySeq") long replySeq,
		@RequestBody RequestTroubleShootingReplyDTO requestTroubleShootingReplyDTO,
		HttpServletRequest request) throws Exception {
		log.info("====== 트러블 슈팅 문서 덧글 수정 시작 =====");

		requestTroubleShootingReplyDTO.getTroubleShootingReply().setTroubleSeq(troubleSeq);
		requestTroubleShootingReplyDTO.getTroubleShootingReply().setSeq(replySeq);
		//로그인 확인
		authentication.checkLogin(request,requestTroubleShootingReplyDTO);

		troubleShootingService.updateTroubleShooingReply(requestTroubleShootingReplyDTO);

		ResultDTO resultDTO = ResultDTO.builder()
			.success(true)
			.message("트러블 슈팅 문서 덧글 등록이 성공했습니다.")
			.build();

		log.info("====== 트러블 슈팅 문서 덧글 수정 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "트러블 슈팅 덧글 삭제",description = "입력 DTO :RequestDTO")
	@DeleteMapping("/{troubleSeq}/reply/{replySeq}")
	public ResponseEntity<?> deleteReply(
		@PathVariable(name = "troubleSeq") long troubleSeq,
		@PathVariable(name = "replySeq") long replySeq,
		@ModelAttribute RequestDTO requestDTO,
		HttpServletRequest request) throws Exception {
		log.info("====== 트러블 슈팅 문서 덧글 삭제 시작 =====");
		log.info(requestDTO.toString());
		//덧글 요청 객체 생성
		RequestTroubleShootingReplyDTO requestTroubleShootingReplyDTO
			= RequestTroubleShootingReplyDTO.builder()
			.loginSeq(requestDTO.getLoginSeq())
			.type(requestDTO.getType())
			.troubleShootingReply(TroubleShootingReplyDTO.builder()
				.seq(replySeq)
				.troubleSeq(troubleSeq)
				.build())
			.build();
		log.info(requestTroubleShootingReplyDTO.toString());
		//로그인 확인
		authentication.checkLogin(request,requestTroubleShootingReplyDTO);
		
		//덧글 삭제
		troubleShootingService.deleteTroubleShooingReply(requestTroubleShootingReplyDTO);

		ResultDTO resultDTO = ResultDTO.builder()
			.success(true)
			.message("트러블 슈팅 문서 덧글 삭제가 성공했습니다.")
			.build();

		log.info("====== 트러블 슈팅 문서 덧글 삭제 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
}
