package com.example.restposkitchen.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.restposkitchen.DatabaseHandler;
import com.example.restposkitchen.MainActivity;
import com.example.restposkitchen.model.KitchenSettingsModel;
import com.example.restposkitchen.model.Orders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.example.restposkitchen.MainActivity.cloudOrderList;
import static com.example.restposkitchen.MainActivity.filteredOrders;
import static com.example.restposkitchen.MainActivity.orderNo;
import static com.example.restposkitchen.MainActivity.ordersList;
import static com.example.restposkitchen.MainActivity.socketOrderList;
import static com.example.restposkitchen.MainActivity.textChecker;

public class KitchenJSONPresenter implements Response.Listener<JSONArray>, Response.ErrorListener {

    private RequestQueue requestQueue;
    private Context context;
    private MainActivity mainActivity;
    private UTF8GetKitchenOrders utf8JsonRequest;
    private String urlKitchenOrders;

    private UTFU8pdatingOrders updateObjectRequest;
    private String urlUpdateOrders;// = "10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno=302&compyear=2018&posno=1&orderno=10\n";

    private DatabaseHandler databaseHandler;

    private UTF8TestInternetConnection utf8TestInternetConnection;
    private String urlInternetTesting;

    public KitchenJSONPresenter(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.requestQueue = Volley.newRequestQueue(context);
        this.databaseHandler = new DatabaseHandler(context);

    }

    public void sendKitchenRequest() {
        mainActivity.showLoading();
        urlKitchenOrders = KitchenSettingsModel.URL + "GetRestKitchenData?compno="
                + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                + "&POSNO=" + KitchenSettingsModel.POS_NO + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO;
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("compno", KitchenSettingsModel.COMPANY_NO);
//            jsonObject.put("compyear", KitchenSettingsModel.COMPANY_YEAR);
//            jsonObject.put("POSNO", KitchenSettingsModel.POS_NO);
//            jsonObject.put("SCREENNO", KitchenSettingsModel.COMPANY_NO);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put(jsonObject);
        utf8JsonRequest = new UTF8GetKitchenOrders(Request.Method.GET, urlKitchenOrders, null, this, this);
        requestQueue.add(utf8JsonRequest);

    }

    public class UTF8GetKitchenOrders extends JsonRequest<JSONArray> {
        public UTF8GetKitchenOrders(int method, String url, @Nullable String requestBody, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, requestBody, listener, errorListener);
        }

        @Override
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String utf8String = new String(response.data, "UTF-8");
                return Response.success(new JSONArray(utf8String), HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                // log error
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                // log error
                return Response.error(new ParseError(e));
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mainActivity.dismissLoading();

        filteredOrders.clear();
        orderNo.clear();
        ordersList.clear();
        socketOrderList.clear();
        cloudOrderList.clear();
        String stringError = "" + error;
        Log.e("getkitchen:", stringError);
        if (stringError.contains("No Company defined, or company dosenot active.")
                | stringError.contains("ServerError")) {
            Toast.makeText(context, "Please verify that the settings information \n is correct, or check your company!", Toast.LENGTH_LONG).show();
        } else if (stringError.contains("No kitchen data found")) {
            Toast.makeText(context, "No orders yet or check screen number!", Toast.LENGTH_SHORT).show();
        } //else {
//            textChecker.setText("3");
//            textChecker.setText("1");
//            Toast.makeText(context, "Starting offline connection", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "Please check internet connection!", Toast.LENGTH_SHORT).show(); /*/
//        }
//        textChecker.setText("1");

    }

    @Override
    public void onResponse(JSONArray response) {

        filteredOrders.clear();
        orderNo.clear();
        ordersList.clear();
        socketOrderList.clear();
        cloudOrderList.clear();

        Log.e("length", "" + response.length());
        if (response.length() > 0) {
            JSONObject object;
            for (int i = 0; i < response.length(); i++) {
                try {
                    object = response.getJSONObject(i);
//                String s = encodingJson(object.getString("ITEMNAME"));

                    Orders orders = new Orders(object.getString("TRINDATE")
                            , object.getString("ORDERNO")
                            , object.getInt("ORDERTYPE")
                            , object.getString("ITEMCODE")
                            , object.getString("ITEMNAME")
                            , object.getInt("QTY")
                            , object.getDouble("PRICE")
                            , object.getInt("POSNO")
                            , object.getInt("TABLENO")
                            , object.getString("SECTIONNO")
                            , object.getString("ISUPDATE")
                            , "0"
                            , object.getString("NOTE")
                    );
                    cloudOrderList.add(orders);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            checkConnectionType = 1;
//            Log.e("presenter 2:", "" + checkConnectionType);

            new StoreKitchenData().execute();

        } else {
            mainActivity.dismissLoading();
            Toast.makeText(context, "The Orders Menu is Empty", Toast.LENGTH_SHORT).show();
        }

    }

    private class StoreKitchenData extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mainActivity.dismissLoading();
            Toast.makeText(context, "Orders successfully stored", Toast.LENGTH_LONG).show();
            textChecker.setText("3");
        }

        @Override
        protected String doInBackground(String... strings) {
            databaseHandler.deleteAllOrders();
            for (int i = 0; i < cloudOrderList.size(); i++) {
                databaseHandler.addOrders(cloudOrderList.get(i));
            }
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void updateOrdersRequest(String domain) {
        mainActivity.showLoading();
        urlUpdateOrders = domain;//"10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno=302&compyear=2018&posno=1&orderno=10\n";
        updateObjectRequest = new UTFU8pdatingOrders(Request.Method.GET, urlUpdateOrders, null, new UpdatingKitchenOrderClass(), new UpdatingKitchenOrderClass());
        requestQueue.add(updateObjectRequest);
    }

    class UTFU8pdatingOrders extends JsonRequest<JSONObject> {
        public UTFU8pdatingOrders(int method, String url, @Nullable String requestBody, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, requestBody, listener, errorListener);
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                String utf8String = new String(response.data, "UTF-8");
                return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                // log error
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                // log error
                return Response.error(new ParseError(e));
            }
        }
    }

    class UpdatingKitchenOrderClass implements Response.Listener<JSONObject>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mainActivity.dismissLoading();
            String stringError = "" + error;
            if (!stringError.contains("voucher Update successfully.")) {
                textChecker.setText("4");
                Log.e("updateOrdersRequest", "" + error);
            }
        }

        @Override
        public void onResponse(JSONObject response) {
            mainActivity.dismissLoading();

            try {
                int errorCode = response.getInt("ErrorCode");
                String errorDescreption = response.getString("ErrorDescreption");
                Toast.makeText(context, "Order deleted successfully", Toast.LENGTH_SHORT).show();

                Log.e("ErrorCode", "" + errorCode);
                Log.e("ErrorDescreption", errorDescreption);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void checkInternetConnection() {

//        progressDialog.show();
//        arrayRequest = new JsonArrayRequest(Request.Method.GET, urlKitchenOrders, null, this, this);

        urlInternetTesting = KitchenSettingsModel.URL + "GetRestKitchenData?compno="
                + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                + "&POSNO=" + KitchenSettingsModel.POS_NO + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO;
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("compno", KitchenSettingsModel.COMPANY_NO);
//            jsonObject.put("compyear", KitchenSettingsModel.COMPANY_YEAR);
//            jsonObject.put("POSNO", KitchenSettingsModel.POS_NO);
//            jsonObject.put("SCREENNO", KitchenSettingsModel.COMPANY_NO);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put(jsonObject);
        utf8TestInternetConnection = new UTF8TestInternetConnection(Request.Method.GET, urlInternetTesting, null, new TestInternetConnectionClass(), new TestInternetConnectionClass());
        requestQueue.add(utf8TestInternetConnection);
    }

    public class UTF8TestInternetConnection extends JsonRequest<JSONArray> {
        public UTF8TestInternetConnection(int method, String url, @Nullable String requestBody, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, requestBody, listener, errorListener);
        }

        @Override
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String utf8String = new String(response.data, "UTF-8");
                return Response.success(new JSONArray(utf8String), HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                // log error
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                // log error
                return Response.error(new ParseError(e));
            }
        }
    }

    class TestInternetConnectionClass implements Response.Listener<JSONArray>, Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            mainActivity.dismissLoading();
//            textChecker.setText("5");
//            MainActivity.internetState = "0";
            String stringError = "" + error;
            if (stringError.contains("No Company defined, or company dosenot active.")
                    | stringError.contains("ServerError")) {
                Toast.makeText(context, "Please verify that the settings information \n is correct, or check your company!", Toast.LENGTH_LONG).show();
                textChecker.setText("3");
            } else if (stringError.contains("No kitchen data found")) {
//                Toast.makeText(context, "No orders yet", Toast.LENGTH_SHORT).show();
                textChecker.setText("1");

            } else {
                Toast.makeText(context, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
            Log.e("TestInternetConnection", "0: " + error);
        }

        @Override
        public void onResponse(JSONArray response) {
            mainActivity.dismissLoading();
            textChecker.setText("1");
//            MainActivity.internetState = "200";
//            Log.e("TestInternetConnection", "" + MainActivity.internetState);
        }
    }


}
