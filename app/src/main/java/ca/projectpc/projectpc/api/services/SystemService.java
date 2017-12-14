package ca.projectpc.projectpc.api.services;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;

public class SystemService extends Service {
    public class VersionResult {
        public String version;
    }

    public ServiceTask getVersion(final IServiceCallback<VersionResult> callback) throws Exception {
        return sendRequest("GET", "/system/version", VersionResult.class, null, callback);
    }
}