package com.dfc.agsolutions.activity;

import com.dfc.agsolutions.model.CheckNumberModel;
import com.dfc.agsolutions.model.CreateServiceListDataModel;
import com.dfc.agsolutions.model.CreatTripModel;
import com.dfc.agsolutions.model.CreatePaymentDataModel;
import com.dfc.agsolutions.model.CurrantTripDataModel;
import com.dfc.agsolutions.model.DebitTypeDataModel;
import com.dfc.agsolutions.model.DeletModel;
import com.dfc.agsolutions.model.DeleteModel;
import com.dfc.agsolutions.model.DriverListDataModel;
import com.dfc.agsolutions.model.ExpensesListDataModel;
import com.dfc.agsolutions.model.FetchAgencyDataModel;
import com.dfc.agsolutions.model.FetchAllVehicleDataModel;
import com.dfc.agsolutions.model.FetchBHSDDataModel;
import com.dfc.agsolutions.model.FetchDriverDataModel;
import com.dfc.agsolutions.model.FetchVendorDataModel;
import com.dfc.agsolutions.model.FetchVehicleDataModel;
import com.dfc.agsolutions.model.GarageDataModel;
import com.dfc.agsolutions.model.MyResponseData;
import com.dfc.agsolutions.model.OngoingTruckTypeModel;
import com.dfc.agsolutions.model.PreviousHistoryDataModel;
import com.dfc.agsolutions.model.ProfileModel;
import com.dfc.agsolutions.model.RequestBodyData;
import com.dfc.agsolutions.model.ResponseArrayModel;
import com.dfc.agsolutions.model.ResponseTodoCount;
import com.dfc.agsolutions.model.ServiceFetchVehicleDataModel;
import com.dfc.agsolutions.model.ServiceStatusDataModel;
import com.dfc.agsolutions.model.ServiceTypeDataModel;
import com.dfc.agsolutions.model.ServiceSubFinalModel;
import com.dfc.agsolutions.model.TodoListDataModel;
import com.dfc.agsolutions.model.TripCurrantDataModel;
import com.dfc.agsolutions.model.TruckTypeModel;
import com.dfc.agsolutions.model.UpdateTripModel;
import com.dfc.agsolutions.model.VehicleDetailsModel;
import com.dfc.agsolutions.model.VehicleHistoryResponse;
import com.dfc.agsolutions.model.VoucherTypeDataModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @POST("check-mobile")
    Call<CheckNumberModel> getCheckMobile(@Query("mobile") String mobile);

    @POST("login")
    Call<MyResponseData> getLogin(@Query("mobile") String mobile,
                                  @Query("password") String password);

    @POST("fetch-branch")
    Call<ResponseArrayModel> getBranch();

    @POST("fetch-todo-count")
    Call<ResponseTodoCount> getToDoCount(@Query("branch_name") String branch_name);

    @POST("fetch-profile")
    Call<ProfileModel> get_profile();

    @POST("fetch-vehicle-list")
    Call<TruckTypeModel> getVehicleList(@Query("branch_name") String branch_name,
                                        @Query("truck_type") String truck_type);

    @POST("update-profile-status")
    Call<DeletModel> getDeleteAccount();

    @POST("fetch-remaing-trip-vehicle")
    Call<FetchVehicleDataModel> fetchVehicleList(@Query("branch_name") String branch_name);


    @POST("fetch-driver-trip-history")
    Call<PreviousHistoryDataModel> getPreviousHistory(@Query("trip_type") String branch_name);

    @POST("fetch-todo")
    Call<TodoListDataModel> getToDoList(@Query("branch_name") String branch_name,
                                        @Query("todo_type") String todo_type);

    @POST("update-todo")
    Call<TodoListDataModel> getUpdateList(@Query("todo_id") String todo_id,
                                          @Query("branch_name") String branch_name);

    @POST("create-todo")
    Call<TodoListDataModel> getCompleteUpdateList(@Query("todo_id") String todo_id,
                                                  @Query("branch_name") String branch_name);

    @POST("fetch-driver-current-trip")
    Call<CurrantTripDataModel> getDriverCurrantTrip();

    @POST("fetch-voucher-type")
    Call<VoucherTypeDataModel> getVoucherType();

    @POST("fetch-service-type")
    Call<ServiceTypeDataModel> getServiceType();

    @POST("create-service-sub-temp")
    Call<CreateServiceListDataModel> getServiceSubType(@Query("service_ref") String service_date,
                                                       @Query("temp_service_sub_type") String service_year,
                                                       @Query("temp_service_sub_amount") String service_truck_no);

    @POST("create-service")
    Call<ServiceStatusDataModel> getServiceStatus(@Query("service_date") String service_date,
                                                  @Query("service_year") String service_year,
                                                  @Query("service_truck_no") String service_truck_no,
                                                  @Query("service_garage") String service_garage,
                                                  @Query("service_km") String service_km,
                                                  @Query("service_amount") String service_amount,
                                                  @Query("service_remarks") String service_remarks);

    @POST("create-service")
    Call<ServiceStatusDataModel> getServiceStatus(@Body RequestBodyData requestBody);


    @POST("fetch-payment-debit")
    Call<DebitTypeDataModel> getDebitType(@Query("payment_details_voucher_type") String payment_details_voucher_type,
                                          @Query("branch_name") String branch_name);

    @POST("create-payment-details")
    Call<CreatePaymentDataModel> createPayment(@Query("payment_details_date")
                                               String payment_details_date,
                                               @Query("payment_details_mode_type")
                                               String payment_details_mode_type,
                                               @Query("payment_details_voucher_type")
                                               String payment_details_voucher_type,
                                               @Query("payment_details_debit") String payment_details_debit,
                                               @Query("payment_details_amount") String payment_details_amount,
                                               @Query("branch_name") String branch_name,
                                               @Query("payment_details_transaction") String payment_details_transaction,
                                               @Query("payment_details_narration") String payment_details_narration);

    @POST("fetch-payment-details")
    Call<ExpensesListDataModel> get_ExpensesList(@Query("branch_name") String branch_name);


    @POST("update-driver-current-trip")
    Call<TripCurrantDataModel> get_TripCurrant(@Query("trip_id") String trip_id,
                                               @Query("trip_status") String trip_status);

    @POST("fetch-vendors")
    Call<GarageDataModel> get_Garage(@Query("branch_name") String trip_id,
                                     @Query("vendor_type") String trip_status);

    @POST("fetch-driver")
    Call<DriverListDataModel> getDriverList(@Query("branch_name") String branch_name);

    @POST("fetch-service-vehicle")
    Call<ServiceFetchVehicleDataModel> getServiceFetchVehicleList(@Query("branch_name") String branch_name);

    @POST("fetch-drivers")
    Call<FetchDriverDataModel> fetchDriver(@Query("branch_name") String branch_name);

    @POST("fetch-vendors")
    Call<FetchVendorDataModel> fetchAgent(@Query("branch_name") String branch_name,
                                          @Query("vendor_type") String vendor_type);

    @POST("fetch-agency")
    Call<FetchAgencyDataModel> fetchAgency(@Query("branch_name") String branch_name);

    @POST("fetch-vehicle-bhsd")
    Call<FetchBHSDDataModel> fetchVehicleBHSD(@Query("trip_vehicle") String trip_vehicle);

    @POST("fetch-vehicle-list")
    Call<OngoingTruckTypeModel> getVehicleListOngoing(@Query("branch_name") String branch_name,
                                                      @Query("truck_type") String truck_type);

    @POST("fetch-all-vehicle-list")
    Call<FetchAllVehicleDataModel> getAllVehicleList(@Query("branch_name") String branch_name);


    @POST("create-trip")
    Call<CreatTripModel> createTrip(@Query("trip_year") String trip_year
            , @Query("trip_branch") String trip_branch,
                                    @Query("trip_date") String trip_date,
                                    @Query("trip_vehicle") String trip_vehicle,
                                    @Query("trip_driver") String trip_driver,
                                    @Query("trip_agency") String trip_agency,
                                    @Query("trip_hsd") String trip_hsd,
                                    @Query("trip_hsd_supplied") String trip_hsd_supplied,
                                    @Query("trip_advance") String trip_advance,
                                    @Query("trip_supplier") String trip_supplier,
                                    @Query("trip_remarks") String trip_remarks,
                                    @Query("trip_bhsd") String trip_bhsd);

    @POST("get_update_trip")
    Call<UpdateTripModel> updateTrip(@Query("trip_id") String trip_id,
                                     @Query("trip_advance") String trip_advance,
                                     @Query("trip_bhsd") String trip_bhsd,
                                     @Query("trip_remarks") String trip_remarks);

    @POST("fetch-vehicle-details")
    Call<VehicleDetailsModel> getVehicleDetails(@Query("reg_no") String reg_no);


    @POST("fetch-service-final")
    Call<ServiceSubFinalModel> fetchServiceFinal(@Query("service_type") String service_type);

    @POST("delete-service-type")
    Call<DeleteModel> deleteServiceType(@Query("service_type") String service_type,
                                        @Query("branch_name") String branch_name);

    @POST("edit-service-type")
    Call<CreateServiceListDataModel> editServiceType(
            @Query("service_type") String service_type,
            @Query("branch_name") String branch_name,
            @Query("service_type_name") String service_type_name,
            @Query("service_type_remarks") String service_type_remarks
    );

    @POST("fetch-vehicle-status")
    Call<OngoingTruckTypeModel> getVehicleStatus(
            @Query("reg_no") String reg_no,
            @Query("branch_name") String branch_name,
            @Query("trip_status") String trip_status
    );

    @POST("fetch-vehicle-trip-history")
    Call<JsonObject> getVehicleHistory(
            @Query("trip_type") String trip_type,
            @Query("vehicle_no") String trip_vehicle
    );

}