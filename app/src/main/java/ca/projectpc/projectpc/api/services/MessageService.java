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

    private class GetMessagesForPostParameters {
        String postId;
    }

    public class BasicIdResult {
        public String id;
    }

    public class Message {
        public String postId;
        public String senderId;
        public String senderName;
        public String targetId;
        public String targetName;
        public String body;
    }

    public class GetMessagesResult {
        public Message[] messages;
    }

    public ServiceTask createMessage(String postId, String targetId, String body,
                                     final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        CreateMessageParameters parameters = new CreateMessageParameters();
        parameters.postId = postId;
        parameters.targetId = targetId;
        parameters.body = body;

        return sendRequest("POST", "/message/create", parameters, CreateMessageParameters.class,
                BasicIdResult.class, null, callback);
    }

    public ServiceTask getMessagesForPost(String postId,
                                          final IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        GetMessagesForPostParameters parameters = new GetMessagesForPostParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/message/getMessagesForPost", parameters,
                GetMessagesForPostParameters.class, GetMessagesResult.class, null, callback);
    }

    public ServiceTask getAllMessages(final IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        return sendRequest("POST", "/message/getAllMessages", GetMessagesResult.class, null,
                callback);
    }
}
