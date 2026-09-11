package com.anipick.backend.image.storage;

import org.springframework.core.io.Resource;

public interface ImageStorage {

    String save(byte[] data, String fileName);

    Resource load(String key);
}
