package io.costax.amazons3;

/**
 * ## the access-key-id and access-key-secret are personal informations,
 * # therefore, its should not be shared in the code, they should be passed through the environment variables.
 * #food4u.storage.s3.access-key-id=
 * #food4u.storage.s3.access-key-secret=
 * food4u.storage.s3.bucket=food4u-test
 * food4u.storage.s3.region=us-east-1
 * food4u.storage.s3.photos-directory=catalog
 */
public class Configurations {

    private String accessKeyId = "dummy";
    private String accessKeySecret = "dummy";
    private String bucket = "test";
    private String region = "us-east-1";
    private String photosDirectory = "catalog";


    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucket() {
        return bucket;
    }

    public String getRegion() {
        return region;
    }

    public String getPhotosDirectory() {
        return photosDirectory;
    }
}
