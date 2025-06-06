package com.study.petory.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3TestController {

	private final S3Service s3Service;

	public String uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			String imageUrl = s3Service.uploadImage(file);
			return "파일이 성공적으로 업로드되었습니다! imageUrl: " + imageUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return "파일 업로드가 실패했습니다!";
		}
	}
}
