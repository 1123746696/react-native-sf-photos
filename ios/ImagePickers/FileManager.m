//
//  FileManager.m
//  ImagePickers
//
//  Created by SmartFun on 2018/5/25.
//  Copyright © 2018年 SmartFun. All rights reserved.
//

#import "FileManager.h"

@implementation FileManager

+ (FileManager *)manage{
    static FileManager *_obj;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _obj = [FileManager new];
    });
    return _obj;
}

/* 创建文件夹 */
- (void)createDocumentWithPath:(NSString *)path
                  documentName:(NSString *)name
                      callback:(void(^)(NSString *__nullable documentPath))callback
{
    NSFileManager *manager = [NSFileManager defaultManager];
    NSError *error;
    NSString *documentPath = [path stringByAppendingPathComponent:[NSString stringWithFormat:@"%@", name]];
    if (![manager fileExistsAtPath:documentPath]){
        [manager createDirectoryAtPath:documentPath withIntermediateDirectories:YES attributes:nil error:&error];
    }
    NSLog(@"--------------------------------- \n路径文件:%@",documentPath);
    callback(documentPath);
}

/* 判断文件创建时间是否小于当前时间 */
- (BOOL)compareTimeInterval:(NSDate *)date{
    NSDate *currentDate = [NSDate dateWithTimeIntervalSinceNow:0 * 60 * 60];
    NSComparisonResult result = [currentDate compare:date];
    if (result == NSOrderedDescending) {
        return true;
    }
    else if (result == NSOrderedAscending){
        return false;
    }
    return true;
}

- (void)removeDocument:(NSString *)path{
    NSFileManager *manager = [NSFileManager defaultManager];
    NSError *error;
    [manager removeItemAtPath:path error:&error];
}




@end
