package com.study.petory.domain.dailyQna.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.dailyQna.Repository.QuestionRepository;
import com.study.petory.domain.dailyQna.dto.request.QuestionCreateRequestDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetAllResponseDto;
import com.study.petory.domain.dailyQna.dto.response.QuestionGetOneResponseDto;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.user.service.UserService;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

	private final QuestionRepository questionRepository;
	private final UserService userService;

	/*
	테스트 코드 전용
	todo 삭제 예정
	 */
	@Override
	public void setQuestion() {
		LocalDate date = LocalDate.of(2024, 1, 1);
		for (int i = 1; i <= 366; i++) {
			String monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"));
			Question question = new Question(
				"질문 " + i,
				monthDay
			);
			date = date.plusDays(1);
			questionRepository.save(question);
		}
	}

	// id로 질문을 조회
	@Override
	public Question findQuestionByQuestionIdOrElseThrow(Long questionId) {
		return questionRepository.findById(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
	}

	// 해당 일자에 해당하는 질문이 있는지 확인
	@Override
	public boolean existsByDate(String date) {
		if (questionRepository.existsByDate(date)) {
			throw new CustomException(ErrorCode.DATE_IS_EXIST);
		}
		return true;
	}

	// 질문을 저장
	@Override
	@Transactional
	public void saveQuestion(Long userId, QuestionCreateRequestDto request) {
		existsByDate(request.getDate());
		// 수정 예정
		// if (!userService.권한검증메서드(userId)) {
		// 	throw new CustomException(ErrorCode.FORBIDDEN);
		// }
		questionRepository.save(Question.builder()
			.question(request.getQuestion())
			.date(request.getDate())
			.build()
		);
	}

	@Override
	public Page<QuestionGetAllResponseDto> getAllQuestion(Long userId, int page) {
		// 수정 예정
		// if (!userService.권한검증메서드(userId)) {
		// 	throw new CustomException(ErrorCode.FORBIDDEN);
		// }
		int p = 0;
		if (page > 1) {
			p = page - 1;
		}
		PageRequest pageable = PageRequest.of(p, 50, Sort.by("date").ascending());

		return questionRepository.findQuestionByPage(pageable);
	}

	@Override
	@Transactional
	public QuestionGetOneResponseDto getOneQuestion(Long userId, Long questionId) {
		// 수정 예정
		// if (!userService.권한검증메서드(userId)) {
		// 	throw new CustomException(ErrorCode.FORBIDDEN);
		// }
		Question question = questionRepository.findById(questionId)
			.orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
		return QuestionGetOneResponseDto.from(question);
	}
}
