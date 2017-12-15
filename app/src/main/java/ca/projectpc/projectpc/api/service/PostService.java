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

/**
 * Service API to handle posting and editing of ads
 */
public class PostService extends Service {
    /**
     * Subclass to store relevant ad parameters
     */
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

    /**
     * Subclass to store relevant image parameters
     */
    private class UploadImageParameters {
        String postId;
        Boolean thumbnail;
        String imageData;
    }

    /**
     * Subclass to store vars needed to remove image
     */
    private class RemoveImageParameters {
        String postId;
        String imageId;
    }

    /**
     * Subclass to store thumbnail parameters
     */
    private class SetThumbnailImageParameters {
        String postId;
        String imageId;
    }

    /**
     * Subclass to store parameters needed for listing the ad
     */
    private class SetListedParameters {
        String postId;
        boolean listed;
    }

    /**
     * Subclass to store post updating related data
     */
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

    /**
     * Subclass to store download image parameters
     */
    private class DownloadImageParameters {
        String imageId;
    }

    /**
     * Subclass to get result from downloading an image
     */
    public class DownloadImageResult {
        public String imageData;
    }

    /**
     * Subclass to store vars for fetching all posts of a certain category
     */
    private class GetAllPostsForCategoryParameters {
        String category;
        List<String> tags;
    }

    /**
     * Subclass to store vars for getting specific number of posts from a certain category
     */
    private class GetPostsForCategoryParameters {
        String category;
        List<String> tags;
        Integer start;
        Integer count;
    }

    /**
     * Subclass to store var for removing a post
     */
    private class RemovePostParameters {
        String postId;
    }

    /**
     * Subclass to store var for fetching a single post
     */
    private class GetPostParameters {
        String postId;
    }

    /**
     * Subclass to store the status of a post
     */
    public class PostStatus {
        public static final int Unlisted = 0;
        public static final int Listed = 1;
        public static final int Deleted = 2;
        public static final int Sold = 3;
    }

    /**
     * Subclass to store all post related data, including original author
     */
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

    /**
     * Subclass to fetch the results of a post as an ArrayResult
     */
    public class GetPostsResult extends ArrayResult<Post> {
    }

    /**
     * Subclass to fetch the result of a post
     */
    public class GetPostResult extends Post {
    }

    /**
     * Accepting relevant ad data, adding them to PostParameters, and passing
     * them via HTTP POST create request.
     *
     * @param title     Ad title
     * @param category  Ad category
     * @param tags      Ad tags
     * @param price     Ad price
     * @param currency  Currency that the ad price is in
     * @param body      Ad description
     * @param location  Ad Location
     * @param latitude  Nullable lat coords
     * @param longitude Nullable lon coords
     * @param callback  Service callback to receive success or fail result
     * @return The result of the HTTP sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
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

    /**
     * Method to upload images associated with an ad ID by parsing the image into
     * a base64 format and utilizing sendRequest to send HTTP POST uploadImage request.
     *
     * @param postId ID of the ad which is associated with the images
     * @param thumbnail scaled down version of image to be used as thumbnail
     * @param base64Image the image to be uploaded
     * @param callback Service callback to receive success or fail result
     * @return The result of the HTTP sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
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

    /**
     * Remove an image from a post given the post ID and the image ID
     *
     * @param postId ID of the target ad
     * @param imageId ID of the target image
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask removeImage(String postId, String imageId,
                                   final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        RemoveImageParameters parameters = new RemoveImageParameters();
        parameters.postId = postId;
        parameters.imageId = imageId;

        return sendRequest("DELETE", "/post/removeImage", parameters, RemoveImageParameters.class,
                BasicIdResult.class, null, callback);
    }

    /**
     * Method to set the thumbnail of the ad to the appropriately passed image id
     *
     * @param postId ID of the ad
     * @param imageId ID of the image to use as the thumbnail
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask setThumbnailImage(String postId, String imageId,
                                         final IServiceCallback<BasicIdResult> callback)
            throws Exception {
        SetThumbnailImageParameters parameters = new SetThumbnailImageParameters();
        parameters.postId = postId;
        parameters.imageId = imageId;

        return sendRequest("DELETE", "/post/setThumbnailImage", parameters,
                SetThumbnailImageParameters.class, BasicIdResult.class, null, callback);
    }

    /**
     * Method to set the ad as listed publicly by POST setListed
     *
     * @param postId ID of the post to be listed
     * @param listed Whether or not the post is already listed
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask setListed(String postId, boolean listed,
                                 IServiceCallback<BasicIdResult> callback)
            throws Exception {
        SetListedParameters parameters = new SetListedParameters();
        parameters.postId = postId;
        parameters.listed = listed;

        return sendRequest("POST", "/post/setListed", parameters,
                SetListedParameters.class, BasicIdResult.class, null, callback);
    }

    // TODO: Javadocs
    public ServiceTask removePost(String postId, IServiceCallback<BasicIdResult> callback)
            throws Exception {
        RemovePostParameters parameters = new RemovePostParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/post/delete", parameters,
                RemovePostParameters.class, BasicIdResult.class, null, callback);
    }

    /**
     * Method to update a post with new edited data
     *
     * @param postId ID of the post to edit
     * @param title Updated ad title
     * @param tags Updated ad tags
     * @param price Updated ad price
     * @param currency Updated ad currency
     * @param body Updated ad description
     * @param location Updated ad location
     * @param latitude Updated ad lat
     * @param longitude Updated ad lon
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
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

    /**
     * Task to download a specified image for use in replacement.
     *
     * @param imageId ID of the image to be downloaded
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask downloadImage(String imageId,
                                     IServiceCallback<DownloadImageResult> callback)
            throws Exception {
        DownloadImageParameters parameters = new DownloadImageParameters();
        parameters.imageId = imageId;

        return sendRequest("POST", "/post/downloadImage", parameters, DownloadImageParameters.class,
                DownloadImageResult.class, null, callback);
    }

    /**
     * Method to fetch all posts in a given category
     *
     * @param category category to get posts from
     * @param tags Nullable list of tags to get posts from
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask getAllPostsForCategory(String category, @Nullable List<String> tags,
                                              IServiceCallback<GetPostsResult> callback)
            throws Exception {
        GetAllPostsForCategoryParameters parameters = new GetAllPostsForCategoryParameters();
        parameters.category = category;
        parameters.tags = tags;

        return sendRequest("POST", "/post/getAllPostsForCategory", parameters,
                GetAllPostsForCategoryParameters.class, GetPostsResult.class, null, callback);
    }

    /**
     * Method to fetch a specified number of posts from a category,
     * given a start and end point.
     *
     * @param category The category to fetch posts from
     * @param tags tags to fetch relevant posts from
     * @param start Starting point in the list to begin fetch
     * @param count How many posts to fetch after the starting point
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
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

    /**
     * Service task to fetch all posts submitted by the user
     *
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask getMyPosts(IServiceCallback<GetPostsResult> callback)
            throws Exception {
        return sendRequest("POST", "/post/getMyPosts", GetPostsResult.class, null, callback);
    }

    /**
     * Service to fetch one specific post from the database
     *
     * @param postId ID of the post to fetch
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask getPost(String postId, IServiceCallback<GetPostResult> callback)
            throws Exception {
        GetPostParameters parameters = new GetPostParameters();
        parameters.postId = postId;

        return sendRequest("POST", "/post/getPost", parameters, GetPostParameters.class,
                GetPostResult.class, null, callback);
    }
}
