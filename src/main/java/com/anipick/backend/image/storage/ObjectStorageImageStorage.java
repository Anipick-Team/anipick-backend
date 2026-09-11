package com.anipick.backend.image.storage;

import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@ConditionalOnProperty(name = "oci.object-storage.enabled", havingValue = "true")
public class ObjectStorageImageStorage implements ImageStorage {

    private final ObjectStorageClient client;
    private final String namespace;
    private final String bucket;
    private final String prefix;

    public ObjectStorageImageStorage(
            @Value("${oci.object-storage.namespace}") String namespace,
            @Value("${oci.object-storage.bucket}") String bucket,
            @Value("${oci.object-storage.prefix}") String prefix
    ) {
        this.namespace = namespace;
        this.bucket = bucket;
        this.prefix = prefix;
        this.client = ObjectStorageClient.builder()
                .build(InstancePrincipalsAuthenticationDetailsProvider.builder().build());
    }

    @Override
    public String save(byte[] data, String fileName) {
        String key = prefix + fileName;
        client.putObject(PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucket)
                .objectName(key)
                .contentLength((long) data.length)
                .putObjectBody(new ByteArrayInputStream(data))
                .build());
        return key;
    }

    @Override
    public Resource load(String key) {
        GetObjectResponse response = client.getObject(GetObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucket)
                .objectName(key)
                .build());

        String fileName = key.substring(key.lastIndexOf('/') + 1);
        long contentLength = response.getContentLength();

        return new InputStreamResource(response.getInputStream()) {
            @Override
            public String getFilename() {
                return fileName;
            }

            @Override
            public long contentLength() {
                return contentLength;
            }
        };
    }

    @PreDestroy
    public void close() {
        client.close();
    }
}
