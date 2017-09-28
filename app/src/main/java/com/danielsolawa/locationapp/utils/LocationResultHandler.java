package com.danielsolawa.locationapp.utils;

import com.danielsolawa.locationapp.model.LocationInfo;

/**
 * Created by NeverForgive on 2017-08-24.
 */

public interface LocationResultHandler {

    void handleLocationResult(String msg, LocationInfo locationInfo);
}
