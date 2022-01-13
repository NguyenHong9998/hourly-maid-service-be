package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String upload(MultipartFile file);

    ResponseEntity<ByteArrayResource> downloadImg(String publicId, boolean isAvatar);
}
