package com.study.petory.common.util;

import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

// CommandLineRunner는 main 메서드 실행 시 특정 코드를 자동으로 실행하고 싶을 때 사용하는 인터페이스!
// 현재 txt 파일에는 folder와 bookmarkList 등의 필요없는 정보들이 있기 때문에 모두 없애고 필요한 배열 부분만 가져오기 위해 사용
@Component
@RequiredArgsConstructor
public class JsonFixRunner implements CommandLineRunner {

	private final JsonFixerService jsonFixerService;

	@Override
	public void run(String... args) throws Exception {
		// // 1. resources/data 경로 기준으로 폴더 객체 생성(data 폴더 안을 스캔하기 위한 준비)
		// String dirPath = System.getProperty("user.dir") + "/src/main/resources/data";
		// File folder = new File(dirPath);
		//
		// // 2. .txt 파일만 필터링
		// File[] fileArrays = folder.listFiles((dir, name) -> name.endsWith(".txt"));
		//
		// for (File file : fileArrays) {
		// 	String rawPath = file.getPath();
		// 	String fixedPath = rawPath.replace(".txt", "-fixed.json");
		//
		// 	try {
		// 		jsonFixerService.fixJsonFile(rawPath, fixedPath);
		// 	} catch (Exception e) {
		// 		throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
		// 	}
		// }
	}
}
