//
//  CDVBackgroundGeolocationFetch.swift
//
//  Created by iGroza on 08.06.2020.
//  Copyright © 2020 iGroza. All rights reserved.
//

import Foundation

@objc(CDVBackgroundGeolocationFetch) class CDVBackgroundGeolocationFetch : CDVPlugin {
    var locationManager: LocationManager? = LocationManager()
       
    @objc(ready:)
    func ready(command: CDVInvokedUrlCommand){
        LocationConfig.shared.update(command)
        
        if self.locationManager == nil {
            self.locationManager  = LocationManager()
        }
        
        self.locationManager!.requestAlwaysAuthorization{state in
            let pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: state
            )
            
            self.commandDelegate!.send(
                pluginResult,
                callbackId: command.callbackId
            )
        }
    }
    
    @objc(startTracking:)
    func startTracking(command: CDVInvokedUrlCommand) {
        self.locationManager!.startTracking()
    }
    
    @objc(stopTracking:)
    func stopTracking(command: CDVInvokedUrlCommand) {
        locationManager!.startTracking()
    }
}

