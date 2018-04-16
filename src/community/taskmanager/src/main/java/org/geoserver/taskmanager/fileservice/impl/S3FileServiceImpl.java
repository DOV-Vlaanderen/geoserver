/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.fileservice.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CopyPartRequest;
import com.amazonaws.services.s3.model.CopyPartResult;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.FileUtils;
import org.geoserver.taskmanager.fileservice.FileService;
import org.geotools.util.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * S3 remote file storage.
 *
 * @author Timothy De Bock - timothy.debock.github@gmail.com
 */
public class S3FileServiceImpl implements FileService {


    private static final Logger LOGGER = Logging.getLogger(S3FileServiceImpl.class);

    private static final long serialVersionUID = -5960841858385823283L;

    private static String ENCODING = "aws-chunked";

    private String alias;

    private String endpoint;

    private String user;

    private String password;

    public static String S3_NAME_PREFIX = "s3-";

    public S3FileServiceImpl() {
    }

    public S3FileServiceImpl(String endpoint, String user, String password, String alias) {
        this.endpoint = endpoint;
        this.user = user;
        this.password = password;
        this.alias = alias;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getName() {
        return S3_NAME_PREFIX + alias;
    }

    @Override
    public String getDescription() {
        return "S3 Service: " + alias;
    }

    @Override
    public boolean checkFileExists(String filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }
        try {
            return getS3Client().doesObjectExist(getBucketName(filePath), getFileName(filePath));
        } catch (AmazonClientException e) {
            throw new IOException(e);
        }

    }

    @Override
    public String create(String filePath, InputStream content) throws IOException {
        //Check parameters
        if (content == null) {
            throw new IOException("Content of a file can not be null.");
        }
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }

        if (checkFileExists(filePath)) {
            throw new IOException("The file already exists");
        }
        File scratchFile = File.createTempFile("prefix", String.valueOf(System.currentTimeMillis()));
        try {
            if (!getS3Client().doesBucketExist(getBucketName(filePath))) {
                getS3Client().createBucket(getBucketName(filePath));
            }

            FileUtils.copyInputStreamToFile(content, scratchFile);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentEncoding(ENCODING);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    getBucketName(filePath),
                    getFileName(filePath),
                    scratchFile);

            putObjectRequest.withMetadata(metadata);

            getS3Client().putObject(putObjectRequest);
        } catch (AmazonClientException e) {
            throw new IOException(e);
        } finally {
            if (scratchFile.exists()) {
                scratchFile.delete();
            }
        }
        return alias + "://" + filePath.toString();
    }

    @Override
    public boolean delete(String filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }
        if (checkFileExists(filePath)) {
            try {
                getS3Client().deleteObject(getBucketName(filePath), getFileName(filePath));
            } catch (AmazonClientException e) {
                throw new IOException(e);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public InputStream read(String filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }
        GetObjectRequest objectRequest = new GetObjectRequest(getBucketName(filePath), getFileName(filePath));
        try {
            return getS3Client().getObject(objectRequest).getObjectContent();
        } catch (AmazonClientException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<String> listSubfolders() throws IOException {
        try {
            List<Bucket> buckets = getS3Client().listBuckets();
            ArrayList<String> paths = new ArrayList<>();
            for (Bucket bucket : buckets) {
                paths.add(bucket.getName());
            }
            return paths;
        } catch (AmazonClientException e) {
            throw new IOException(e);
        }
    }


    public void rename(String filePathSource, String filePathTarget) throws IOException {
        copy(filePathSource, filePathTarget);
        delete(filePathSource);
    }

    private void copy(String filePathSource, String filePathTarget) throws IOException {
        AmazonS3 s3Client = getS3Client();

        // Create lists to hold copy responses
        List<CopyPartResult> copyResponses = new ArrayList<>();

        // Step 2: Initialize
        InitiateMultipartUploadRequest initiateRequest =
                new InitiateMultipartUploadRequest(  getBucketName(filePathTarget),getFileName(filePathTarget));

        InitiateMultipartUploadResult initResult = s3Client.initiateMultipartUpload(initiateRequest);

        try {

            // Get object size.
            GetObjectMetadataRequest metadataRequest =
                    new GetObjectMetadataRequest(getBucketName(filePathSource), getFileName(filePathSource));

            ObjectMetadata metadataResult = s3Client.getObjectMetadata(metadataRequest);
            long objectSize = metadataResult.getContentLength(); // in bytes

            // Step 4. Copy parts.
            long partSize = 5 * 1024 * 1024; // 5 MB
            long bytePos = 0;

            //String uploadId = initResult.getUploadId();

            for (int i = 1; bytePos < objectSize; i++)
            {
                // Step 5. Save copy response.
                long lastByte = bytePos + partSize - 1 >= objectSize ? objectSize - 1 : bytePos + partSize - 1;
                CopyPartRequest copyRequest = new CopyPartRequest()
                                .withDestinationBucketName(getBucketName(filePathTarget))
                                .withDestinationKey(getFileName(filePathTarget))
                                .withSourceBucketName(getBucketName(filePathSource))
                                .withSourceKey(getFileName(filePathSource))
                                .withUploadId(initResult.getUploadId())
                                .withFirstByte(bytePos)
                                .withLastByte(lastByte)
                                .withPartNumber(i);

                copyResponses.add(s3Client.copyPart(copyRequest));
                bytePos += partSize;
            }
            // Step 7. Complete copy operation.
            CompleteMultipartUploadRequest completeRequest = new
                    CompleteMultipartUploadRequest(
                    getBucketName(filePathTarget),
                    getFileName(filePathTarget),
                    initResult.getUploadId(),
                    GetETags(copyResponses));
            s3Client.completeMultipartUpload(completeRequest);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            throw new IOException(e);
        }
    }

    private AmazonS3 getS3Client() {
        if (endpoint == null) {
            throw new IllegalArgumentException("The endpoint is required, add a property: alias.s3.endpoint");
        }
        if (user == null) {
            throw new IllegalArgumentException("The user is required, add a property: alias.s3.user");
        }
        if (password == null) {
            throw new IllegalArgumentException("The password is required, add a property: alias.s3.password");
        }

        AmazonS3 s3;
        //custom endpoint

        s3 = new AmazonS3Client(new BasicAWSCredentials(user, password));

        final S3ClientOptions clientOptions = S3ClientOptions.builder().setPathStyleAccess(true).build();
        s3.setS3ClientOptions(clientOptions);
        String endpoint = this.endpoint;
        if (!endpoint.endsWith("/")) {
            endpoint = endpoint + "/";
        }
        s3.setEndpoint(endpoint);

        return s3;
    }

    private static String getBucketName(String filePath) {
        int indexSlashPosition = filePath.indexOf('/');
        if (indexSlashPosition > 0) {
            return filePath.substring(0, indexSlashPosition);
        }
        return "";
    }
    
    private static String getFileName(String filePath) {
        int indexSlashPosition = filePath.indexOf('/');
        if (indexSlashPosition >= 0) {
            return filePath.substring(indexSlashPosition + 1);
        }
        return "";
    }


    private List<PartETag> GetETags(List<CopyPartResult> responses) {
        List<PartETag> etags = new ArrayList<>();
        for (CopyPartResult response : responses)
        {
            etags.add(new PartETag(response.getPartNumber(), response.getETag()));
        }
        return etags;
    }

}
