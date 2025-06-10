package com.study.petory.common.util;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Service
public class JsonFixerService {

	// 이 메서드는 txt파일을 json파일로 수정하기 위해 존재합니다
	// IOException 예외는 읽으려는 파일이 없거나 쓰기가 실패할 경우 발생합니다
	public void fixJsonFile(String rawFilePath, String fixedFilePath) throws IOException {

		// YAMLFactory는 JSON 형식처럼 들어가 있지만 따옴표가 없는 등 결함이 있는 경우 Jackson에서 자동으로 보완해주는 클래스입니다
		// 이것을 사용하기 위해 build.gradle에 의존성 주입 해줘야 합니다
		ObjectMapper forgivingMapper = new ObjectMapper(new YAMLFactory());

		// 원본 파일(txt)을 tree 구조로 읽는 과정
		JsonNode root = forgivingMapper.readTree(new File(rawFilePath));

		// 위에서 읽어온 txt 파일 내부에서 bookmarkList에 해당하는 배열만 추출하는 과정
		JsonNode bookmarkList = root.path("bookmarkList");

		// ObjectMapper는 Json과 java 객체 간 변환을 자동으로 해주는 jackson 라이브러리의 클래스!
		// bookmarkList 배열에 해당하는 부분을 JSON 형식으로 새 파일에 저장하는 과정
		ObjectMapper strictMapper = new ObjectMapper();
		strictMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fixedFilePath), bookmarkList);
	}
}

