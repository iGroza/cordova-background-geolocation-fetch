//
//  HTTPLocationRequest.swift
//
//  Created by iGroza on 08.06.2020.
//  Copyright © 2020 iGroza. All rights reserved.
//

import Foundation
import CoreLocation

class HTTPLocationManager: NSObject{
    
    static func postLocation(location: CLLocation){
        let url = URL(string: LocationConfig.shared.url!)!
        var request = URLRequest(url: url)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.httpMethod = "POST"
                
        let parameters: [String: Any] = [
            "uuid": LocationConfig.shared.uuid,
            "location": [
                "coordinates": [location.coordinate.longitude, location.coordinate.latitude],
                "longitude": location.coordinate.longitude,
                "latitude": location.coordinate.latitude,
                "timestamp": location.timestamp.debugDescription
            ],
            "extras": LocationConfig.shared.extras ?? "null"
        ]
        
        request.httpBody = Dictionary.toJson(dic: parameters)
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            guard let data = data,
                let response = response as? HTTPURLResponse,
                error == nil else {
                    print("error", error ?? "Unknown error")
                    return
            }
                        
            print("============================================================[ RESPONSE ]============================================================")
            print("response date: \(Date().debugDescription)")
            print(response.debugDescription)
        }
        
        task.resume()
    }
}

extension Dictionary {
    static func toJson(dic: Dictionary) -> Data? {
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: dic, options: .prettyPrinted)
            return jsonData
        } catch {
            print(error.localizedDescription)
            return nil
        }
    }
}
