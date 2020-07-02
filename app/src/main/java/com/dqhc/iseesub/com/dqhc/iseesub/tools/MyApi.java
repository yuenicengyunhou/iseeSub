package com.dqhc.iseesub.com.dqhc.iseesub.tools;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MyApi {

    @POST("getPushRecords")
    Call<ResponseBody> getMessages(@Query("phone") String phoneNum, @Query("currentPage") String currentPage);

//    @Multipart
//    @POST("uploadTask")
//    Call<ResponseBody> uploadVideo(@Query("teachingCustomerHomeworkId") String id, @Query("Authorization") String token, @Query("file") String mm, @Query("_compressVideoname") String videoName, @Query("mimeType") String mimetype, @Part MultipartBody.Part file);

    //    @POST("uploadTask")
//    Call<ResponseBody> uploadVideo(@PartMap Map<String, RequestBody> params);
    @Multipart/*@Part("description")RequestBody description,*/
    @POST("uploadTask")                                                                                                                                                                                                                 /*"_data\";filename=\"video.mp4\""*/
    Call<ResponseBody> upload(@Query("teachingCustomerHomeworkId") String id, @Query("Authorization") String token, @Part MultipartBody.Part file);

//    @POST("uploadTask")
//    Call<ResponseBody> uploadByte(@PartMap Map<String ,Object> params);

    @POST("getTeachingAudio")
    Call<ResponseBody> getTeachingAudio(@Query("courseId") String courseId, @Query("currentPage") Integer currentPage);

    Call<ResponseBody> getTeachingAudio();

    @POST("initDeviceInfo")
    Call<ResponseBody> initDeviceInfo(@Query("authorization") String authorization, @Query("deviceId") String deviceId, @Query("deviceType") int deviceType);

    @POST("getUptodateTvVersion")
    Call<ResponseBody> getUptodateAppVersion(@Query("deviceName") String deviceName, @Query("deviceNumber") String deviceNumber);


    @POST("loadCampusInfo")
    Call<ResponseBody> getLoadCampusInfo(@Query("tvBoxNumber") String tvBoxNumber, @Query("lal") String lal, @Query("ip") String ip);

    @POST("destroyDeviceInfo")
    Call<ResponseBody> destroyDeviceInfo(@Query("authorization") String authorization);

    @POST("updateAppCount")
    Call<ResponseBody> updateAppCount(@Query("versionId") String versionId);

    @POST("app.xz?m=updatePackage")
    Call<ResponseBody> update(@Query("token") String token, @Query("key") String key);


}
