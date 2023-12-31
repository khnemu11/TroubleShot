package com.orientalSalad.troubleShot.GPTController.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.orientalSalad.troubleShot.GPTController.dto.RequestContextDTO;
import com.orientalSalad.troubleShot.GPTController.dto.ResponseMessageDTO;
import com.orientalSalad.troubleShot.GPTController.service.GPTService;
import com.orientalSalad.troubleShot.global.dto.RequestDTO;
import com.orientalSalad.troubleShot.global.dto.ResponseGPTCountDTO;
import com.orientalSalad.troubleShot.global.dto.ResultDTO;
import com.orientalSalad.troubleShot.global.utill.Authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "gpt API")
@Controller
@RequestMapping("/gpt")
@RequiredArgsConstructor
@Slf4j
public class GPTController {
	private final GPTService GPTService;
	private final Authentication authentication;
	@Operation(summary = "문서 피드백",description = "입력 DTO :RequestContextDTO, "
		+ "300자 이내의 조언을 줌 "
		+ "\n주의 : gpt-4라서 시간이 오래 걸릴수 있음(최대 50초 제한)")
	@PostMapping("/readme-feedback")
	public ResponseEntity<?> contextFeedBack(@RequestBody RequestContextDTO requestContextDTO,
		HttpServletRequest request) throws Exception {
		log.info("====== 문서 피드백 시작 =====");
		log.info(requestContextDTO.toString());

		authentication.checkLogin(request,requestContextDTO);

		 String order = "\n위의 글을 더 좋은 트러블슈팅 문서로 바꿔주고 싶어. "
			+ "어느 부분을 수정해야 할까? "
			+ "이때 너의 조언은 200자가 넘으면 안돼";

		String context = GPTService.getMessage(requestContextDTO,order);

		ResponseMessageDTO resultDTO = ResponseMessageDTO.builder()
			.context(context)
			.success(true)
			.message("gpt 문서 조언을 성공했습니다")
			.build();

		log.info("====== 문서 피드백 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "에러 조언",description = "입력 DTO :RequestContextDTO, "
		+ "300자 이내의 조언을 줌 "
		+ "\n주의 : gpt-4라서 시간이 오래 걸릴수 있음(최대 50초 제한)")
	@PostMapping("/error-feedback")
	public ResponseEntity<?> errorFeedBack(@RequestBody RequestContextDTO requestContextDTO,
		HttpServletRequest request) throws Exception {
		log.info("====== 에러 코드 조언 시작 =====");

		String order = "\n위의 에러가 발생해. "
			+ "어느 부분이 문제일까? "
			+ "이때 너의 조언은 300자가 넘으면 안돼";

		authentication.checkLogin(request,requestContextDTO);

		String context = GPTService.getMessage(requestContextDTO,order);

		ResponseMessageDTO resultDTO = ResponseMessageDTO.builder()
			.context(context)
			.success(true)
			.message("에러 코드 조언을 성공했습니다")
			.build();

		log.info("====== 에러 코드 조언 끝 =====");
		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
	@Operation(summary = "유저의 남은 GPT 요청 횟수")
	@GetMapping("/count")
	public ResponseEntity<?> getCount(@ModelAttribute RequestDTO requestDTO,
		HttpServletRequest request) throws Exception {
		log.info("====== 남은 요청 횟수 불러오기 시작 =====");

		int count = GPTService.getCount(requestDTO);

		ResponseGPTCountDTO resultDTO = ResponseGPTCountDTO.builder()
			.count(count)
			.success(true)
			.message("남은 요청 횟수 불러오기를 성공했습니다")
			.build();

		log.info("====== 남은 요청 횟수 불러오기 끝 =====");

		return new ResponseEntity<ResultDTO>(resultDTO, HttpStatus.OK);
	}
}
