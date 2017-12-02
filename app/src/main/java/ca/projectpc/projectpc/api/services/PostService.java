package ca.projectpc.projectpc.api.services;

import java.util.List;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;

public class PostService extends Service {
    private class CreatePostParameters {
        String title;
        String category;
        List<String> tags;
        Double price;
        String currency;
        String body;
    }

    private class UploadImageParameters {
        String postId;
        boolean thumbnail;
        String imageData;
    }

    private class DownloadImageParameters {
        String imageId;
    }

    public class BasicIdResult {
        public String id;
    }

    public class DownloadImageResult {
        public String imageData;
    }

    public ServiceTask createPost(String title, String category, List<String> tags, Double price,
                                  String currency, String body,
                                  final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        CreatePostParameters parameters = new CreatePostParameters();
        parameters.title = title;
        parameters.category = category;
        parameters.tags = tags;
        parameters.price = price;
        parameters.currency = currency;
        parameters.body = body;

        return sendRequest("POST", "/post/create", parameters, CreatePostParameters.class,
                BasicIdResult.class, null, callback);
    }

    public ServiceTask uploadImage(String postId, boolean thumbnail, String base64Image,
                                   final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        UploadImageParameters parameters = new UploadImageParameters();
        parameters.postId = postId;
        parameters.thumbnail = thumbnail;
        parameters.imageData = base64Image;

        return sendRequest("POST", "/post/uploadImage", parameters, UploadImageParameters.class,
                BasicIdResult.class, null, callback);
    }

    public ServiceTask downloadImage(String imageId,
                                     final IServiceCallback<DownloadImageResult> callback)
        throws Exception {
        DownloadImageParameters parameters = new DownloadImageParameters();
        parameters.imageId = imageId;

        return sendRequest("POST", "/post/downloadImage", parameters, DownloadImageParameters.class,
                DownloadImageResult.class, null, callback);
    }
}
