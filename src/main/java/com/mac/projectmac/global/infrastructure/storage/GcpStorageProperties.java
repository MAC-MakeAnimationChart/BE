package com.mac.projectmac.global.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gcp")
public class GcpStorageProperties {

    private String projectId;
    private final Storage storage = new Storage();
    private final Credentials credentials = new Credentials();

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Storage getStorage() {
        return storage;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public static class Storage {

        private String bucket;
        private String dataSourcePrefix = "data_source";
        private String problemDatasetPrefix = "problem_dataset";

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getDataSourcePrefix() {
            return dataSourcePrefix;
        }

        public void setDataSourcePrefix(String dataSourcePrefix) {
            this.dataSourcePrefix = dataSourcePrefix;
        }

        public String getProblemDatasetPrefix() {
            return problemDatasetPrefix;
        }

        public void setProblemDatasetPrefix(String problemDatasetPrefix) {
            this.problemDatasetPrefix = problemDatasetPrefix;
        }
    }

    public static class Credentials {
        private String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
