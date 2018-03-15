/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
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
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.FileUtils;
import org.geoserver.taskmanager.fileservice.FileService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * S3 remote file storage.
 *
 * @author Timothy De Bock
 */
public class S3FileServiceImpl implements FileService {

    private static String ENCODING = "aws-chunked";

    private String alias;

    private String endpoint;

    private String user;

    private String password;

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


    @Override
    public String getName() {
        return "S3 remote file service: " + endpoint;
    }


    @Override
    public boolean checkFileExists(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }
        try {
            return getS3Client().doesObjectExist(getBucketName(filePath), filePath.getFileName().toString());
        } catch (AmazonClientException e) {
            throw new IOException(e);
        }

    }


    @Override
    public String create(Path filePath, InputStream content) throws IOException {
        //Check parameters
        if (content == null) {
            throw new IOException("Content of a file can not be null.");
        }
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }

        if (checkFileExists(filePath)) {
            throw new IOException("The file allready exists");
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
                    filePath.getFileName().toString(),
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
    public boolean delete(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }
        if (checkFileExists(filePath)) {
            try {
                getS3Client().deleteObject(getBucketName(filePath), filePath.getFileName().toString());
            } catch (AmazonClientException e) {
                throw new IOException(e);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public InputStream read(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Name of a file can not be null.");
        }
        GetObjectRequest objectRequest = new GetObjectRequest(getBucketName(filePath), filePath.getFileName().toString());
        try {
            return getS3Client().getObject(objectRequest).getObjectContent();
        } catch (AmazonClientException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<Path> listSubfolders() throws IOException {
        try {
            List<Bucket> buckets = getS3Client().listBuckets();
            ArrayList<Path> paths = new ArrayList<>();
            for (Bucket bucket : buckets) {
                paths.add(Paths.get(bucket.getName()));
            }
            return paths;
        } catch (AmazonClientException e) {
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

    private String getBucketName(Path filePath) {
        if (filePath.getParent() != null) {
            return filePath.getParent().toString();
        }
        return "";
    }

}
