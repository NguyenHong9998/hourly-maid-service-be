package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/cloud")
public class CloudinaryController {
    @Autowired
    CloudinaryService cloudinaryService;

    @PostMapping("/upload-avatar")
    public ResponseEntity<ResponseDataAPI> upload(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.upload(file);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(url).build());
    }


    @GetMapping("/downloadAvatar/{publicId}/{size}")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadAvatar(@PathVariable String publicId) {
        return cloudinaryService.downloadImg(publicId,true);
    }
}
