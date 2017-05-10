package com.github.codetanzania.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * This open311Service fetches open311Service-requests by using the profile of the
 * currently signed-in user.
 *
 * We use the http header If-Modified-Since to ask for the server to give
 * us newer content because the application catches old requests in a local
 * SQLite Database.
 *
 * This approach enables the application to work offline as well
 */
public class ServiceRequestsLoader extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ServiceRequestsLoader(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
