//
//  LocationManager.swift
//
//  Created by iGroza on 08.06.2020.
//  Copyright © 2020 iGroza. All rights reserved.
//

import Foundation
import CoreLocation

class LocationManager: NSObject {
    var mLocationManager: CLLocationManager?
    var lastUptateLocation: Date
    var authorizationCallback: ((_ state: String) -> Void)?
    
    override init(){
        self.mLocationManager = CLLocationManager()
        self.lastUptateLocation = Date()
    }
    
    private func configureLocationManager(){
        self.mLocationManager?.delegate = self
        self.mLocationManager?.distanceFilter =  LocationConfig.shared.LOCATION_DISTANSE_FILTER
        self.mLocationManager?.allowsBackgroundLocationUpdates = true
    }
    
    func requestAlwaysAuthorization(_ authorizationCallback: @escaping (_ state: String) -> Void){
        self.authorizationCallback = authorizationCallback
        self.configureLocationManager()
        self.mLocationManager?.requestAlwaysAuthorization()
    }
    
    func startTracking(){
        self.mLocationManager!.startUpdatingLocation()
    }
    
    func stopTracking(){
        self.mLocationManager!.stopUpdatingLocation()
    }
    
    func postCurrentLocation() {
        HTTPLocationManager.postLocation(location: (mLocationManager?.location)!)
    }
}

extension LocationManager: CLLocationManagerDelegate {
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        var statusString: String = "undefined"
        
        switch status {
        case .authorizedAlways:
            statusString = "authorizedAlways"
        case .authorizedWhenInUse:
            statusString = "authorizedWhenInUse"
        case .denied:
            statusString = "denied"
        case .notDetermined:
            statusString = "notDetermined"
        case .restricted:
            statusString = "restricted"
        }
        
        self.authorizationCallback!(statusString)
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("CLLocation", locations)
        guard let location = locations.first else {
            return
        }
        
        if lastUptateLocation.addingTimeInterval(LocationConfig.shared.LOCATION_UPDATES_TIME) < Date()  {
            HTTPLocationManager.postLocation(location: location)
            lastUptateLocation = Date()
        }
    }
}

