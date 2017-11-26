package ca.projectpc.projectpc.api.services;

import android.util.Base64;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;

public class PostService extends Service {
    private class UploadImageParameters {
        String fileName;
        String imageData;
    }

    public class UploadImageResult {
        public String id;
        public String fileName;
    }

    public ServiceTask uploadImage(String fileName, byte[] buffer,
                                   final IServiceCallback<UploadImageResult> callback)
            throws Exception {
        UploadImageParameters parameters = new UploadImageParameters();
        parameters.fileName = fileName;
        parameters.imageData = Base64.encodeToString(buffer, Base64.DEFAULT);

        return sendRequest("POST", "/post/upload", parameters, UploadImageParameters.class,
                UploadImageResult.class, null, callback);
    }
}
