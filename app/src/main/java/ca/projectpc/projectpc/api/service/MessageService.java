/*
 * ProjectPC
 *
 * Copyright (C) 2017 ProjectPC. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */

package ca.projectpc.projectpc.api.service;

import java.util.Date;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.result.ArrayResult;
import ca.projectpc.projectpc.api.service.result.BasicIdResult;
import ca.projectpc.projectpc.api.service.result.DataResult;

/**
 * Message service class to handle sending and receiving messages.
 */
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

    /**
     * Method to create a sending message from parameter data using
     * HTTP POST create request.
     *
     * @param postId   ID of the post user wants to send a message to
     * @param targetId ID of the target user
     * @param body     The message to be sent
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
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

    /**
     * Method for fetching messages related to a specified post.
     *
     * @param postId ID of the post to fetch messages for
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask getMessagesForPost(String postId,
                                          IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        GetMessagesForPostParameters parameters = new GetMessagesForPostParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/message/getMessagesForPost", parameters,
                GetMessagesForPostParameters.class, GetMessagesResult.class, null, callback);
    }

    /**
     * Method to fetch all messages
     *
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask getAllMessages(IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        return sendRequest("POST", "/message/getAllMessages", GetMessagesResult.class, null,
                callback);
    }

    /**
     * Method to fetch all message starting from a given date
     *
     * @param time The date with which to start fetching from
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask getMessagesSince(Date time, IServiceCallback<GetMessagesResult> callback)
            throws Exception {
        GetMessagesSinceParameters parameters = new GetMessagesSinceParameters();
        parameters.time = time;

        return sendRequest("POST", "/message/getMessagesSince", parameters,
                GetMessagesSinceParameters.class, GetMessagesResult.class, null, callback);
    }
}
