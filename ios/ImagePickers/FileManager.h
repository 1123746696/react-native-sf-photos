//
//  FileManager.h
//  ImagePickers
//
//  Created by SmartFun on 2018/5/25.
//  Copyright © 2018年 SmartFun. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FileManager : NSObject
+ (FileManager *)manage;
- (void)createDocumentWithPath:(NSString *)path
                        documentName:(NSString *)name
                      callback:(void(^)(NSString *__nullable documentPath))callback;
- (BOOL)compareTimeInterval:(NSDate *)date;
- (void)removeDocument:(NSString *)path;
@end
