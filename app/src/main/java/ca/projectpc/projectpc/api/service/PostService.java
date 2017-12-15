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

import android.support.annotation.Nullable;

import java.util.List;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.result.ArrayResult;
import ca.projectpc.projectpc.api.service.result.BasicIdResult;
import ca.projectpc.projectpc.api.service.result.DataResult;

public class PostService extends Service {
    private class CreatePostParameters {
        String title;
        String category;
        List<String> tags;
        Double price;
        String currency;
        String body;
        String location;
        Double latitude;
        Double longitude;
    }

    private class UploadImageParameters {
        String postId;
        Boolean thumbnail;
        String imageData;
    }

    private class RemoveImageParameters {
        String postId;
        String imageId;
    }

    private class SetThumbnailImageParameters {
        String postId;
        String imageId;
    }

    private class SetListedParameters {
        String postId;
        boolean listed;
    }

    private class UpdatePostParameters {
        String postId;
        String title;
        List<String> tags;
        Double price;
        String currency;
        String body;
        String location;
        Double latitude;
        Double longitude;
    }

    private class DownloadImageParameters {
        String imageId;
    }

    public class DownloadImageResult {
        public String imageData;
    }

    private class GetAllPostsForCategoryParameters {
        String category;
        List<String> tags;
    }

    private class GetPostsForCategoryParameters {
        String category;
        List<String> tags;
        Integer start;
        Integer count;
    }

    private class RemovePostParameters {
        String postId;
    }

    private class GetPostParameters {
        String postId;
    }

    public class PostStatus {
        public static final int Unlisted = 0;
        public static final int Listed = 1;
        public static final int Deleted = 2;
        public static final int Sold = 3;
    }

    public class Post extends DataResult {
        public String authorId;
        public String authorEmail;
        public Integer status;
        public String title;
        public String category;
        public String[] tags;
        public Double price;
        public String currency;
        public String[] imageIds;
        public String thumbnailId;
        public String thumbnailImageId;
        public String body;
        public String location;
        public Double latitude;
        public Double longitude;
    }

    public class GetPostsResult extends ArrayResult<Post> {
    }

    public class GetPostResult extends Post {
    }

    public ServiceTask createPost(String title, String category, List<String> tags, Double price,
                                  String currency, String body, String location,
                                  @Nullable Double latitude, @Nullable Double longitude,
                                  final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        CreatePostParameters parameters = new CreatePostParameters();
        parameters.title = title;
        parameters.category = category;
        parameters.tags = tags;
        parameters.price = price;
        parameters.currency = currency;
        parameters.body = body;
        parameters.location = location;
        parameters.latitude = latitude;
        parameters.longitude = longitude;

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

    public ServiceTask removeImage(String postId, String imageId,
                                   final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        RemoveImageParameters parameters = new RemoveImageParameters();
        parameters.postId = postId;
        parameters.imageId = imageId;

        return sendRequest("DELETE", "/post/removeImage", parameters, RemoveImageParameters.class,
                BasicIdResult.class, null, callback);
    }

    public ServiceTask setThumbnailImage(String postId, String imageId,
                                         final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        SetThumbnailImageParameters parameters = new SetThumbnailImageParameters();
        parameters.postId = postId;
        parameters.imageId = imageId;

        return sendRequest("DELETE", "/post/setThumbnailImage", parameters,
                SetThumbnailImageParameters.class, BasicIdResult.class, null, callback);
    }

    public ServiceTask setListed(String postId, boolean listed,
                                 IServiceCallback<BasicIdResult> callback)
            throws Exception {
        SetListedParameters parameters = new SetListedParameters();
        parameters.postId = postId;
        parameters.listed = listed;

        return sendRequest("POST", "/post/setListed", parameters,
                SetListedParameters.class, BasicIdResult.class, null, callback);
    }

    public ServiceTask removePost(String postId, IServiceCallback<BasicIdResult> callback)
            throws Exception {
        RemovePostParameters parameters = new RemovePostParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/post/delete", parameters,
                RemovePostParameters.class, BasicIdResult.class, null, callback);
    }

    public ServiceTask updatePost(String postId, @Nullable String title,
                                  @Nullable List<String> tags, @Nullable Double price,
                                  @Nullable String currency, @Nullable String body,
                                  @Nullable String location, @Nullable Double latitude,
                                  @Nullable Double longitude,
                                  IServiceCallback<BasicIdResult> callback)
            throws Exception {
        UpdatePostParameters parameters = new UpdatePostParameters();
        parameters.postId = postId;
        parameters.title = title;
        parameters.tags = tags;
        parameters.price = price;
        parameters.currency = currency;
        parameters.body = body;
        parameters.location = location;
        parameters.latitude = latitude;
        parameters.longitude = longitude;

        return sendRequest("POST", "/post/update", parameters, UpdatePostParameters.class,
                BasicIdResult.class, null, callback);
    }

    public ServiceTask downloadImage(String imageId,
                                     IServiceCallback<DownloadImageResult> callback)
            throws Exception {
        DownloadImageParameters parameters = new DownloadImageParameters();
        parameters.imageId = imageId;

        return sendRequest("POST", "/post/downloadImage", parameters, DownloadImageParameters.class,
                DownloadImageResult.class, null, callback);
    }

    public ServiceTask getAllPostsForCategory(String category, @Nullable List<String> tags,
                                              IServiceCallback<GetPostsResult> callback)
            throws Exception {
        GetAllPostsForCategoryParameters parameters = new GetAllPostsForCategoryParameters();
        parameters.category = category;
        parameters.tags = tags;

        return sendRequest("POST", "/post/getAllPostsForCategory", parameters,
                GetAllPostsForCategoryParameters.class, GetPostsResult.class, null, callback);
    }

    public ServiceTask getPostsForCategory(String category, @Nullable List<String> tags,
                                           int start, int count,
                                           IServiceCallback<GetPostsResult> callback)
            throws Exception {
        GetPostsForCategoryParameters parameters = new GetPostsForCategoryParameters();
        parameters.category = category;
        parameters.tags = tags;
        parameters.start = start;
        parameters.count = count;

        return sendRequest("POST", "/post/getPostsForCategory", parameters,
                GetPostsForCategoryParameters.class, GetPostsResult.class, null, callback);
    }

    public ServiceTask getMyPosts(IServiceCallback<GetPostsResult> callback)
            throws Exception {
        return sendRequest("POST", "/post/getMyPosts", GetPostsResult.class, null, callback);
    }

    public ServiceTask getPost(String postId, IServiceCallback<GetPostResult> callback)
            throws Exception {
        GetPostParameters parameters = new GetPostParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/post/getPost", parameters, GetPostParameters.class,
                GetPostResult.class, null, callback);
    }
}
