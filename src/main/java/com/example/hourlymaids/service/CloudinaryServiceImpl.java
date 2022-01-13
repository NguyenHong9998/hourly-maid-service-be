package com.example.hourlymaids.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.hourlymaids.entity.UserEntity;
import com.example.hourlymaids.repository.UserRepository;
import com.example.hourlymaids.util.CloudinaryUtils;
import com.example.hourlymaids.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    Cloudinary cloudinary = Singleton.getCloudinary();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CloudinaryUtils cloudinaryUtils;

    @Override
    public String upload(MultipartFile file) {
        return cloudinaryUtils.uploadFile(file);
    }


    @Override
    public ResponseEntity<ByteArrayResource> downloadImg(String publicId, boolean isAvatar) {

        // Generates the URL
        String format = "jpg";
        Transformation transformation = new Transformation().crop("fill");
        if (isAvatar) {
            // transformation = transformation.gravity("face").radius("max");
            transformation = transformation.radius("max");
            format = "png";
        }
        String cloudUrl = cloudinary.url().secure(true).format(format)
                .transformation(transformation)
                .publicId(publicId)
                .generate();

        try {
            // Get a ByteArrayResource from the URL
            URL url = new URL(cloudUrl);
            InputStream inputStream = url.openStream();
            byte[] out = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            ByteArrayResource resource = new ByteArrayResource(out);

            // Creates the headers
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-disposition", "attachment; filename=image.jpg");
            responseHeaders.add("Content-Type", "image/jpeg");
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .contentLength(out.length)
                    // .contentType(MediaType.parseMediaType(mimeType))
                    .body(resource);

        } catch (Exception ex) {
            return null;
        }
    }
}
