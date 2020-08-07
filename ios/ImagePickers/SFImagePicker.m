//
//  SFImagePicker.m
//  SFShare
//
//  Created by SmartFun on 2018/4/11.
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import "SFImagePicker.h"
#import "HXPhotoPicker.h"
#import <React/RCTConvert.h>
#import "FileManager.h"

#import "HXPhotoModel.h"

@interface SFImagePicker()<HXAlbumListViewControllerDelegate,UIImagePickerControllerDelegate>
@property (nonatomic, strong) NSString *type;
@property (nonatomic, assign) NSInteger number;
@property (nonatomic, assign) BOOL isSingle;
@property (nonatomic, assign) BOOL isCrop;
@property (strong, nonatomic) HXPhotoManager *manager;
@property (strong, nonatomic) HXDatePhotoToolManager *toolManager;
@property (strong, nonatomic) UIColor *bottomViewBgColor;

@property (strong, nonatomic) NSMutableArray *selectArray;
@end

@implementation SFImagePicker

RCT_EXPORT_MODULE();

- (NSMutableArray *)selectArray{
    if (!_selectArray) {
        _selectArray = [NSMutableArray array];
    }
    return _selectArray;
}

- (void)setTypeOfManager{
    if (self.manager)
        return;
    if ([self.type isEqualToString:@"all"])
    {
        self.manager = [[HXPhotoManager alloc] initWithType:HXPhotoManagerSelectedTypePhotoAndVideo];
    }
    else if ([self.type isEqualToString:@"photos"])
    {
        self.manager = [[HXPhotoManager alloc] initWithType:HXPhotoManagerSelectedTypePhoto];
    }
    else if ([self.type isEqualToString:@"videos"])
    {
        self.manager = [[HXPhotoManager alloc] initWithType:HXPhotoManagerSelectedTypeVideo];
    }
}

- (void)setModeWithPicAndVideo{
    HXPhotoConfiguration *config = self.manager.configuration;
    config.photoCanEdit = YES;
    config.sectionHeaderTranslucent = false;
    config.maxNum = self.number;
    config.videoMaxNum = 1;
    config.photoMaxNum = self.number;
    config.videoMaximumDuration = 12;
    config.videoMaxDuration = 12;
}

- (void)setSingleMode{
    if (!self.manager)
        self.manager = [[HXPhotoManager alloc] initWithType:HXPhotoManagerSelectedTypePhoto];
    HXPhotoConfiguration *config = self.manager.configuration;
    config.singleSelected = YES;
    config.singleJumpEdit = self.isCrop;
    config.movableCropBox = YES;
    config.movableCropBoxEditSize = YES;
}

- (void)setSingleOfManage{
    if (self.isCrop) {
        [self setSingleMode];
    }
    else {
        [self setModeWithPicAndVideo];
    }
}

RCT_EXPORT_METHOD(deletePhoto:(NSInteger)index){
    [self.manager afterSelectedListdeletePhotoModel:self.selectArray[index]];
}

RCT_EXPORT_METHOD(clearList){
    [self.manager clearSelectedList];
}

RCT_EXPORT_METHOD(getPhotos:(NSDictionary *)dic
                    call:(RCTResponseSenderBlock)callback)
{
    
//    self.type =  [dic[@"type"] integerValue];
    self.type = dic[@"type"];
    self.isCrop = [dic[@"isCrop"] boolValue];
    self.number = [dic[@"number"] integerValue];
    self.isSingle = [dic[@"isSingle"] boolValue];
    [self setTypeOfManager];
    [self setSingleOfManage];
    

    NSLog(@"%@",self.manager.afterSelectedArray);
    
    
    UIViewController *root = RCTPresentedViewController();

    dispatch_async(dispatch_get_main_queue(), ^{
        [root hx_presentAlbumListViewControllerWithManager:self.manager
                                                      done:^(NSArray<HXPhotoModel *> *allList,
                                                             NSArray<HXPhotoModel *> *photoList,
                                                             NSArray<HXPhotoModel *> *videoList,
                                                             BOOL original,
                                                             HXAlbumListViewController *viewController)
         {
             NSMutableArray *allArr = [NSMutableArray array];
//             NSMutableArray *imgArr  = [NSMutableArray array];
//             NSMutableArray *videoArr = [NSMutableArray array];
             if ([self.type isEqualToString:@"photos"] && self.isCrop) {
                 NSString *path = NSTemporaryDirectory();
                 NSString *documentPath = [path stringByAppendingPathComponent:[NSString stringWithFormat:@"album"]];
                 NSError *err;
                 NSArray *arr = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:documentPath error:&err];
                 NSLog(@"%@",arr);
                 NSString *temp = arr.firstObject;
                 temp = [temp substringFromIndex:6];
                 temp = [temp substringToIndex:temp.length - 4];
                 NSTimeInterval interval = temp.integerValue;
                 NSDate *date = [NSDate dateWithTimeIntervalSince1970:interval/1000];
                 BOOL result =  [[FileManager manage] compareTimeInterval:date];
                 if (result) {
                     [[FileManager manage] removeDocument:[NSString stringWithFormat:@"%@/%@",documentPath,arr.firstObject]];
                 }
                 
                 [[FileManager manage] createDocumentWithPath:path documentName:@"album" callback:^(NSString * _Nullable documentPath){
                     HXPhotoModel *model = allList.firstObject;
                     CGFloat width = [UIScreen mainScreen].bounds.size.width;
                     CGFloat height = [UIScreen mainScreen].bounds.size.height;
                     CGFloat imgWidth = model.imageSize.width;
                     CGFloat imgHeight = model.imageSize.height;
                     CGSize size;
                     if (imgHeight > imgWidth / 9 * 17) {
                         size = CGSizeMake(width, height);
                     }else {
                         size = CGSizeMake(model.endImageSize.width * 1.5, model.endImageSize.height * 1.5);
                     }
                     if (self.isCrop) {
                         NSData *imageData =  UIImagePNGRepresentation(model.previewPhoto);
                         NSDate *date = [NSDate dateWithTimeIntervalSinceNow:0];
                         NSDate *nextDay = [NSDate dateWithTimeInterval:1*60*60 sinceDate:date];
                         NSTimeInterval time = [nextDay timeIntervalSince1970] * 1000;
                         NSString *timeString = [NSString stringWithFormat:@"%.0f", time];
                         NSString *filePath = [documentPath stringByAppendingPathComponent:[NSString stringWithFormat:@"image_%@.png", timeString]];
                         BOOL a = [imageData writeToFile:filePath atomically:YES];
                         NSLog(@"头像写入-->: %@ \n 是否成功: %@",filePath, a ? @"yes" : @"false");
                         NSMutableDictionary *param = [NSMutableDictionary dictionary];
//                         [param setObject:@"" forKey:@"thumb_image"];
                         [param setObject:filePath forKey:@"imageUrl"];
                         [param setObject:@"img" forKey:@"type"];
//                         [param setObject:@"0" forKey:@"index"];
                         [param setObject:@"" forKey:@"videoUrl"];
//                         [param setObject:[NSString stringWithFormat:@"%f", size.width] forKey:@"width"];
//                         [param setObject:[NSString stringWithFormat:@"%f", size.height] forKey:@"height"];
//                         [imgArr addObject:param];
                         [allArr addObject:param];
                         callback(@[[NSNull null], allArr]);
                     }
                     else{
                         PHImageRequestOptions *option = [[PHImageRequestOptions alloc] init];
                         option.deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat;
                         option.resizeMode = PHImageRequestOptionsResizeModeFast;
                         option.networkAccessAllowed = NO;
                         option.synchronous = YES;
                         [[PHImageManager defaultManager] requestImageForAsset:allList.firstObject.asset targetSize:model.imageSize contentMode:PHImageContentModeAspectFit options:option resultHandler:^(UIImage * _Nullable result, NSDictionary * _Nullable info) {
                             NSData *imageData =  UIImagePNGRepresentation(result);
                             NSDate *date = [NSDate dateWithTimeIntervalSinceNow:0];
                             NSDate *nextDay = [NSDate dateWithTimeInterval:1*60*60 sinceDate:date];
                             NSTimeInterval time = [nextDay timeIntervalSince1970] * 1000;
                             NSString *timeString = [NSString stringWithFormat:@"%.0f", time];
                             NSString *filePath = [documentPath stringByAppendingPathComponent:[NSString stringWithFormat:@"image_%@.png", timeString]];
                             BOOL a = [imageData writeToFile:filePath atomically:YES];
                             NSLog(@"头像写入-->: %@ \n 是否成功: %@",filePath, a ? @"yes" : @"false");
                             NSMutableDictionary *param = [NSMutableDictionary dictionary];
//                             [param setObject:@"" forKey:@"thumb_image"];
                             [param setObject:filePath forKey:@"imageUrl"];
                             [param setObject:@"img" forKey:@"type"];
//                             [param setObject:@"0" forKey:@"index"];
                             [param setObject:@"" forKey:@"videoUrl"];
//                             [param setObject:[NSString stringWithFormat:@"%f", size.width] forKey:@"width"];
//                             [param setObject:[NSString stringWithFormat:@"%f", size.height] forKey:@"height"];
//                             [imgArr addObject:param];
                             [allArr addObject:param];
                             callback(@[[NSNull null], allArr]);
                         }];
                     }
                 }];
             }
             if (!self.isCrop)
             {
                 NSString *path = NSTemporaryDirectory();
                 NSString *documentPath = [path stringByAppendingPathComponent:[NSString stringWithFormat:@"album"]];
                 NSError *err;
                 NSArray *arr = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:documentPath error:&err];
                 NSLog(@"%@",arr);
                 for (int i = 0; i < arr.count; i++) {
                     NSString *temp = arr[i];
                     temp = [temp substringFromIndex:6];
                     temp = [temp substringToIndex:temp.length - 4];
                     NSTimeInterval interval = temp.integerValue;
                     NSDate *date = [NSDate dateWithTimeIntervalSince1970:interval/1000];
                     BOOL result =  [[FileManager manage] compareTimeInterval:date];
                     NSLog(@"%d",result);
                     if (result) {
                         [[FileManager manage] removeDocument:[NSString stringWithFormat:@"%@/%@",documentPath,arr[i]]];
                     }
                 }
                 [[FileManager manage] createDocumentWithPath:path documentName:@"album" callback:^(NSString * _Nullable documentPath){
                     NSLog(@"%@ \n", documentPath);
                     for (int i = 0; i < allList.count; i++)
                     {
                         HXPhotoModel *model = allList[i];
                         
                         [self.selectArray addObject:model];
                         
                         if (model.type == HXPhotoManagerSelectedTypePhoto)
                         {
                             CGFloat width = [UIScreen mainScreen].bounds.size.width;
                             CGFloat height = [UIScreen mainScreen].bounds.size.height;
                             CGFloat imgWidth = model.imageSize.width;
                             CGFloat imgHeight = model.imageSize.height;
                             CGSize size;
                             if (imgHeight > imgWidth / 9 * 17) {
                                 size = CGSizeMake(width, height);
                             }else {
                                 size = CGSizeMake(model.endImageSize.width * 1.5, model.endImageSize.height * 1.5);
                             }
                             PHImageRequestOptions *option = [[PHImageRequestOptions alloc] init];
                             option.deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat;
                             option.resizeMode = PHImageRequestOptionsResizeModeFast;
                             option.networkAccessAllowed = NO;
                             option.synchronous = YES;
                             [[PHImageManager defaultManager] requestImageForAsset:allList[i].asset targetSize:size contentMode:PHImageContentModeAspectFit options:option resultHandler:^(UIImage * _Nullable result, NSDictionary * _Nullable info) {
                                 NSLog(@"--------- imageSize --------: \n %f_%f", result.size.width,result.size.height);
                                 NSLog(@"--------- info ----------: \n %@",info);
                                 NSData *imageData =  UIImagePNGRepresentation(result);
                                 NSDate *date = [NSDate dateWithTimeIntervalSinceNow:0];
                                 NSDate *nextDay = [NSDate dateWithTimeInterval:1*60*60 sinceDate:date];
                                 NSTimeInterval time = [nextDay timeIntervalSince1970] * 1000;
                                 NSString *timeString = [NSString stringWithFormat:@"%.0f", time];
                                 NSString *filePath = [documentPath stringByAppendingPathComponent:[NSString stringWithFormat:@"image_%@.png", timeString]];
                                 BOOL a = [imageData writeToFile:filePath atomically:YES];
                                 NSLog(@"图片写入-->: %@ \n 是否成功: %@",filePath, a ? @"yes" : @"false");
//                                 NSData *thumbData =  UIImagePNGRepresentation(model.thumbPhoto);
//                                 NSString *baseDate = [thumbData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
                                 NSMutableDictionary *param = [NSMutableDictionary dictionary];
                                 [param setObject:@"img" forKey:@"type"];
                                 [param setObject:filePath forKey:@"imageUrl"];
                                 [param setObject:@"" forKey:@"videoUrl"];
//                                 [param setObject:baseDate forKey:@"thumb_image"];
//                                 [param setObject:[NSString stringWithFormat:@"%d",i] forKey:@"index"];
//                                 [param setObject:[NSString stringWithFormat:@"%f", size.width] forKey:@"width"];
//                                 [param setObject:[NSString stringWithFormat:@"%f", size.height] forKey:@"height"];
//                                 [imgArr addObject:param];
                                 [allArr addObject:param];
                                 if (allList.count - 1 == i) {
                                     NSLog(@"%@",allArr);
                                     callback(@[[NSNull null], allArr]);
                                 }
                             }];
                         }
                         else 
                         {
                             PHVideoRequestOptions *options = [[PHVideoRequestOptions alloc] init];
                             options.deliveryMode = PHVideoRequestOptionsDeliveryModeAutomatic;
                             [[PHImageManager defaultManager] requestAVAssetForVideo:allList[i].asset options:options resultHandler:^(AVAsset * _Nullable asset, AVAudioMix * _Nullable audioMix, NSDictionary * _Nullable info) {
                                 NSLog(@"-------- video -------- \n %@ \n  %@ \n %@", asset, audioMix, info);
                                 if (asset && [asset isKindOfClass:[AVURLAsset class]])
                                 {
                                     NSURL *url = ((AVURLAsset *)asset).URL;
                                     NSData *data = [NSData dataWithContentsOfURL:url];
                                     NSDate *date = [NSDate dateWithTimeIntervalSinceNow:0];
                                     NSDate *nextDay = [NSDate dateWithTimeInterval:1*60*60 sinceDate:date];
                                     NSTimeInterval time = [nextDay timeIntervalSince1970] * 1000;
                                     NSString *timeString = [NSString stringWithFormat:@"%.0f", time];
                                     NSString *filePath = [documentPath stringByAppendingPathComponent:[NSString stringWithFormat:@"video_%@.mp4", timeString]];
                                     BOOL a = [data writeToFile:filePath atomically:YES];
                                     NSLog(@"视频数据写入-->: %@ \n 是否成功: %@",filePath, a ? @"yes" : @"false");
                                     NSData *imageData =  UIImagePNGRepresentation(allList[i].thumbPhoto);
                                     NSString *baseDate = [imageData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
                                     NSMutableDictionary *param = [NSMutableDictionary dictionary];
                                     [param setObject:@"video" forKey:@"type"];
//                                     [param setObject:baseDate forKey:@"thumb_image"];
                                     [param setObject:baseDate forKey:@"imageUrl"];
                                     [param setObject:filePath forKey:@"videoUrl"];
//                                     [param setObject:[NSString stringWithFormat:@"%d",i] forKey:@"index"];
//                                     [videoArr addObject:param];
                                     [allArr addObject:param];
                                     if (allList.count - 1 == i) {
                                         NSLog(@"%@",allArr);
                                         callback(@[[NSNull null], allArr]);
//                                         callback(@[ @{@"allList": allArr,@"photoList":imgArr,@"videoList":videoArr}]);
                                     }
                                 }
                             }];
                         }
                     }
                 }];
             }
         } cancel:^(HXAlbumListViewController *viewController) {
             
         }];
    });

  
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
     dispatch_async(dispatch_get_main_queue(), ^{
         [picker dismissViewControllerAnimated:YES completion:nil];
     });
}
@end
