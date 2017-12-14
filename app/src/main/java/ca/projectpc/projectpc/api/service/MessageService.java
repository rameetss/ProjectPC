package ca.projectpc.projectpc.api.service;

import java.util.Date;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.result.ArrayResult;
import ca.projectpc.projectpc.api.service.result.BasicIdResult;
import ca.projectpc.projectpc.api.service.result.DataResult;

public class MessageService extends Service {
    private class CreateMessageParameters {
        String postId;
        String targetId;
        String body;
    }

    private class GetMessagesForPostParameters {
        String postId;
    }

    private class GetMessagesSinceParameters {
        Date time;
    }

    public class Message extends DataResult {
        public String postId;
        public String senderId;
        public String senderName;
        public String targetId;
        public String targetName;
        public String body;
    }

    public class GetMessagesResult extends ArrayResult<Message> {
        // No extra variables
    }

    public ServiceTask createMessage(String postId, String targetId, String body,
                                     IServiceCallback<BasicIdResult> callback)
            throws Exception {
        CreateMessageParameters parameters = new CreateMessageParameters();
        parameters.postId = postId;
        parameters.targetId = targetId;
        parameters.body = body;

        return sendRequest("POST", "/message/create", parameters, CreateMessageParameters.class,
                BasicIdResult.class, null, callback);
    }

    public ServiceTask getMessagesForPost(String postId,
                                          IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        GetMessagesForPostParameters parameters = new GetMessagesForPostParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/message/getMessagesForPost", parameters,
                GetMessagesForPostParameters.class, GetMessagesResult.class, null, callback);
    }

    public ServiceTask getAllMessages(IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        return sendRequest("POST", "/message/getAllMessages", GetMessagesResult.class, null,
                callback);
    }

    public ServiceTask getMessagesSince(Date time, IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        GetMessagesSinceParameters parameters = new GetMessagesSinceParameters();
        parameters.time = time;

        return sendRequest("POST", "/message/getMessagesSince", parameters,
                GetMessagesSinceParameters.class, GetMessagesResult.class, null, callback);
    }
}
