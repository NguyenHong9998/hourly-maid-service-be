package com.example.hourlymaids.util;

import com.cloudinary.Cloudinary;


import com.cloudinary.SingletonManager;
import com.cloudinary.utils.ObjectUtils;
import com.example.hourlymaids.constant.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class CloudinaryUtils {
    @Value("${cloudinary.name}")
    private String cloudinaryName;

    @Value("${cloudinary.key}")
    private String cloudinaryKey;

    @Value("${cloudinary.secret}")
    private String cloudinarySecret;

    private Cloudinary cloudinary;

    public void initCloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinaryName);
        config.put("api_key", cloudinaryKey);
        config.put("api_secret", cloudinarySecret);
        cloudinary = new Cloudinary(config);
    }

    public String uploadFile(MultipartFile file) {
        initCloudinary();
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String publicId = uploadResult.get("url").toString();
            return publicId;
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), "500", HttpStatus.BAD_REQUEST);
        }
    }

}
