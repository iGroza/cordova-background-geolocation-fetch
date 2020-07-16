//
//  CDVBackgroundGeolocationFetch-Swift.h
//
//  Created by iGroza on 08.06.2020.
//  Copyright © 2020 iGroza. All rights reserved.
//

@class CDVBackgroundGeolocationFetch;

@interface CDVBackgroundGeolocationFetch : CDVPlugin
-(void)ready:(CDVInvokedUrlCommand*)command;
-(void)startTracking:(CDVInvokedUrlCommand*)command;
-(void)stopTracking:(CDVInvokedUrlCommand*)command;
-(void)postCurrentLocation:(CDVInvokedUrlCommand*)command;
-(void)postPushResult:(NSString*)messageId;
@end
