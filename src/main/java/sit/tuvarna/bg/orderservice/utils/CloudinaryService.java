package sit.tuvarna.bg.orderservice.utils;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.tuvarna.bg.orderservice.exceptions.CloudServiceException;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {


    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public String uploadBase64Image(String base64Image) {
        try {
            // Remove "data:image/png;base64," prefix if present
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Map<String, Object> options  = new HashMap<>();
            options.put("folder","hapka.bg");
            Map uploadResult = cloudinary.uploader().upload(imageBytes,options);
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new CloudServiceException("Image upload failed");
        }
    }
}
