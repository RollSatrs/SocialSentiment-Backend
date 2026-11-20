package com.socialsentiment.backend.analysis;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.regex.Pattern;

@Service
public class InstagramUrlService {
    private static final Pattern PATH = Pattern.compile("^/(p|reel|tv)/[A-Za-z0-9_-]+/?$");
    public String normalize(String url) {
        try {
            URI uri = URI.create(url.trim());
            String host = uri.getHost();
            if (!"https".equalsIgnoreCase(uri.getScheme()) || host == null
                || !(host.equalsIgnoreCase("instagram.com") || host.equalsIgnoreCase("www.instagram.com"))
                || uri.getRawUserInfo() != null || uri.getPort() != -1
                || !PATH.matcher(uri.getPath()).matches()) throw new IllegalArgumentException();
            return "https://www.instagram.com" + uri.getPath();
        } catch (RuntimeException ex) {
            throw new InvalidAnalysisException("Укажите корректную HTTPS-ссылку на Instagram-пост или Reel.");
        }
    }
}
