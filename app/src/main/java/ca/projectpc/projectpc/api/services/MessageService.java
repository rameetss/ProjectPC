package ca.projectpc.projectpc.api.services;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;

public class MessageService extends Service {
    private class CreateMessageParameters {
        String postId;
        String targetId;
        String body;
    }

    private class GetMessagesParameters {
        String postId;
    }

    public class BasicIdResult {
        public String id;
    }

    public class Message {
        public String postId;
        public String senderId;
        public String targetId;
        public String body;
    }

    public class GetMessagesResult {
        public String postId;
        public Message[] messages;
    }

    public ServiceTask create(String postId, String targetId, String body,
                              final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        CreateMessageParameters parameters = new CreateMessageParameters();
        parameters.postId = postId;
        parameters.targetId = targetId;
        parameters.body = body;

        return sendRequest("POST", "/message/create", parameters, CreateMessageParameters.class,
                BasicIdResult.class, null, callback);
    }

    public ServiceTask get(String postId, final IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        GetMessagesParameters parameters = new GetMessagesParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/message/get", parameters, GetMessagesParameters.class,
                GetMessagesResult.class, null, callback);
    }
}
