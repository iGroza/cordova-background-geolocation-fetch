//
//  LocationConfig.swift
//
//  Created by iGroza on 08.06.2020.
//  Copyright © 2020 iGroza. All rights reserved.
//

import Foundation

struct LocationConfig {
    public var url: String?
    public var uuid: String?
    public var extras: NSDictionary?
    
    public var LOCATION_UPDATES_TIME: Double = 0 // in seconds
    public var LOCATION_DISTANSE_FILTER: Double = 0 // in meters
    
    static var shared = LocationConfig()
    private init() {}
    
    func update(_ command: CDVInvokedUrlCommand){
        let jsonData = command.arguments[0] as! NSDictionary
        LocationConfig.shared.url = jsonData["url"] as! String
        LocationConfig.shared.uuid = jsonData["uuid"] as! String
        LocationConfig.shared.extras = jsonData["extras"] as? NSDictionary
        LocationConfig.shared.LOCATION_UPDATES_TIME = jsonData["LOCATION_UPDATES_TIME"] as? Double ?? 1.0
        LocationConfig.shared.LOCATION_DISTANSE_FILTER = jsonData["LOCATION_DISTANSE_FILTER"] as? Double ?? 1.0
    }
}
