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

package ca.projectpc.projectpc.api;

/**
 *
 */
public class ServiceResultCode {
    public static final int Ok = 1000;
    public static final int InternalError = 1001;
    public static final int Unauthorized = 1002;

    public static final int InvalidUserId = 1100;
    public static final int InvalidCredentials = 1101;
    public static final int AlreadyAuthenticated = 1102;
    public static final int UserAlreadyExists = 1103;

    public static final int InvalidPostId = 1200;
    public static final int InvalidImageId = 1201;
    public static final int ImageLimitReached = 1202;

    public static final int NotImplemented = 9999;
}