package io.costax.amazons3;

import com.amazonaws.SdkBaseException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.InputStream;
import java.net.URL;

/**
 * - Bucket names should not contain upper-case letters
 * - Bucket names should not contain underscores (_)
 * - Bucket names should not end with a dash
 * - Bucket names should be between 3 and 63 characters long
 * - Bucket names cannot contain dashes next to periods (e.g., my-.bucket.com and my.-bucket are invalid)
 * - Bucket names cannot contain periods - Due to our S3 client utilizing SSL/HTTPS, Amazon documentation indicates that a bucket name cannot contain a period,
 * otherwise you will not be able to upload files from our S3 browser in the dashboard.
 * <p>
 * Valid Examples
 * <p>
 * - my-eu-bucket-3
 * - my-project-x
 * - 4my-group
 */
public class Example {

    private static final Configurations S3 = new Configurations();

    private static final AmazonS3 amazonS3 = amazon3();

    public static void main(String[] args) {
        InputStream resourceAsStream = Example.class.getResourceAsStream("/mochqq.jpg");
        uploadObject(resourceAsStream, "myfile.jpg", "application/jpg");
    }

    static void uploadObject(InputStream inputStream, String fileObjKeyName, String contentType) {

        var objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.addUserMetadata("x-amz-meta-title", "someTitle");

        PutObjectRequest request = new PutObjectRequest(S3.getBucket(), fileObjKeyName, inputStream, objectMetadata);
        request.withCannedAcl(CannedAccessControlList.PublicRead);

        PutObjectResult putObjectResult = amazonS3.putObject(request);

    }


    public static URL getFile(final String fileName) {
        try {

            String filePath = fileName;

            URL url = amazonS3.getUrl(S3.getBucket(), filePath);

            return url;
        } catch (SdkBaseException e) {
            throw new RuntimeException("Cannot read file.", e);
        }
    }

    private static AmazonS3 amazon3() {
        final Configurations s3 = new Configurations();

        BasicAWSCredentials credentials = new BasicAWSCredentials(
                s3.getAccessKeyId(),
                s3.getAccessKeySecret());

        final AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration("http://localhost:4572", s3.getRegion());

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))

                .enablePathStyleAccess()
                //.withRegion(s3.getRegion())
                .build();
    }
}
