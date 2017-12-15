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

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;

/**
 * Generic class for system services, storing the service
 * version and setting up HTTP GET version request.
 */
public class SystemService extends Service {
    public class VersionResult {
        public String version;
    }

    /**
     * Fetch the version of the service task
     *
     * @param callback Service callback to receive success or fail result
     * @return The result of the sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
    public ServiceTask getVersion(IServiceCallback<VersionResult> callback) throws Exception {
        return sendRequest("GET", "/system/version", VersionResult.class, null, callback);
    }
}