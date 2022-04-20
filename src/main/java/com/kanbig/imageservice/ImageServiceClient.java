
package com.kanbig.imageservice;

import com.google.protobuf.ByteString;
import com.kanbig.imageservice.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link ImageServiceClient}.
 */
public class ImageServiceClient {
    private static final Logger logger = Logger.getLogger(ImageServiceClient.class.getName());

    private final ManagedChannel channel;

    private final KanbigImageServiceGrpc.KanbigImageServiceBlockingStub blockingStub;

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public ImageServiceClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build());
    }

    /**
     * Construct client for accessing HelloWorld server using the existing channel.
     */
    ImageServiceClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = KanbigImageServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * function Enroll(db_id, filename, personID, name, client, callback) {
     * var image;
     * var fs = require("fs");
     * var path = require("path");
     * var face_id = -1;
     * var data = fs.readFileSync(filename);
     * image = {
     * data: data,
     * filename: path.basename(filename)
     * }
     * var imageRequest = {
     * cam_id: 1,
     * list_id: db_id,//db_id
     * image_chunk: image,
     * personID: personID,
     * name: name
     * };
     * client.Enroll(imageRequest, function (err, response) {
     * const end = process.hrtime.bigint();
     * let number = Number(end - start);
     * <p>
     * console.log(`Benchmark took ${number / 1000000000.0} seconds`);
     * console.log('Enroll response:', response);
     * face_id = response.face_id;
     * callback(face_id);
     * });
     * return face_id;
     * }
     */
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public void getFeature(String filename) {
        logger.info("Will try to getFeature " + filename + " ...");
        byte[] fileContent;
        Image image;
        ImageRequest request = null;
        try {
            File file = new File(filename);
            fileContent = Files.readAllBytes(file.toPath());
            image = Image.newBuilder().setFilename(file.getName()).setData(ByteString.copyFrom(fileContent)).build();
            request = ImageRequest.newBuilder().setCamId(0).setImageChunk(image).setListId(0).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FeatureResponse response;
        try {
            response = blockingStub.getFeature(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Enrolling: " + bytesToHex(response.getFeature().toByteArray()));
    }

    public void enroll(int db_id, String filename, String personID, String name) {
        logger.info("Will try to enroll " + name + " ...");
        byte[] fileContent;
        Image image;
        ImageRequest request = null;
        try {
            File file = new File(filename);
            fileContent = Files.readAllBytes(file.toPath());
            image = Image.newBuilder().setFilename(file.getName()).setData(ByteString.copyFrom(fileContent)).build();
            request = ImageRequest.newBuilder().setCamId(0).setName(name).setPersonID(personID).setImageChunk(image).setListId(db_id).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OpResponse response;
        try {
            response = blockingStub.enroll(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Enrolling: " + response.getFaceId());
    }

    /**
     * function CompareOnDb(db_id, face_id, filename, client) {
     * var image;
     * var fs = require("fs");
     * var path = require("path");
     * data = fs.readFileSync(filename);
     * image = {
     * data: data,
     * filename: path.basename(filename)
     * }
     * var compareRequest = {
     * face_id: face_id,
     * list_id: db_id,//db_id
     * img: image,
     * option: 9 //option == 9 will cause the server return liveness score of the image
     * };
     * const mystart = process.hrtime.bigint();
     * client.CompareOnDb(compareRequest, function (err, response) {
     * const end = process.hrtime.bigint();
     * let s = Number(end-mystart)
     * let number = Number(end - start);
     * console.log(`CompareOnDb took ${s / 1000000000.0} seconds`);
     * console.log(`Benchmark took ${number / 1000000000.0} seconds`);
     * console.log('CompareOnDb response:', response);
     * });
     * }
     */
    public void compareOnDb(int db_id, int face_id, String filename) {
        logger.info("Will try to compareOnDb " + filename + " ...");
        byte[] fileContent;
        Image image;
        CompareOnDbRequest request = null;
        try {
            File file = new File(filename);
            fileContent = Files.readAllBytes(file.toPath());
            image = Image.newBuilder().setFilename(file.getName()).setData(ByteString.copyFrom(fileContent)).build();
            request = CompareOnDbRequest.newBuilder().setFaceId(face_id).setImg(image).setListId(db_id).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FaceCompareResponse response;
        try {
            response = blockingStub.compareOnDb(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("compareOnDb: " + response.getScore());
    }

    public void findVehicles(String filename) {
        logger.info("Will try to getFeature " + filename + " ...");
        byte[] fileContent;
        Image image;
        ImageRequest request = null;
        try {
            File file = new File(filename);
            fileContent = Files.readAllBytes(file.toPath());
            image = Image.newBuilder().setFilename(file.getName()).setData(ByteString.copyFrom(fileContent)).build();
            request = ImageRequest.newBuilder().setCamId(0).setListId(0).setImageChunk(image).setOption(1).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VehicleResponse response;
        try {
            response = blockingStub.findVehicles(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        for (VehicleInfo v : response.getIdentitiesList()) {

          logger.log(Level.INFO, v.toString());
        }
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        ImageServiceClient client = new ImageServiceClient("127.0.0.1", 50051);
        try {
            /* Access a service running on the local machine on port 50051 */
            String user = "world";
            if (args.length > 0) {
                user = args[0]; /* Use the arg as the name to greet if provided */
            }
            for (int i = 0; i < 1; i++) {
                client.findVehicles("/home/charlie/CLionProjects/faceservice/images/b1.jpg");
//                client.enroll(0, "/home/charlie/CLionProjects/faceservice/images/10000003.jpg", "10000003", "noname1");
//                client.compareOnDb(0, 0, "/home/charlie/CLionProjects/faceservice/images/10000003.jpg");
            }
        } finally {
            client.shutdown();
        }
    }
}
