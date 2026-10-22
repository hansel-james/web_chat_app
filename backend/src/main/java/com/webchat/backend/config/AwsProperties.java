package com.webchat.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    private final S3 s3 = new S3();

    public S3 getS3() {
        return s3;
    }

    public static class S3 {
        private String endpointUrl;
        private String accessKeyId;
        private String secretAccessKey;
        private String region;

        public String getEndpointUrl() {
            return endpointUrl;
        }

        public void setEndpointUrl(String endpointUrl) {
            this.endpointUrl = endpointUrl;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}