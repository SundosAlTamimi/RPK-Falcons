package com.example.restposkitchen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.ColorSpace;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restposkitchen.model.KitchenSettingsModel;
import com.example.restposkitchen.model.Orders;
import com.example.restposkitchen.presenter.KitchenJSONPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnDragListener, View.OnClickListener {//implements View.OnTouchListener {

    //    public static TextView networkStateFollower; // check network and network state
    public static String internetState;
    final Handler handler = new Handler();
    private ProgressDialog progressDialog;
    private MediaPlayer mediaPlayer;
    private Timer _Request_Trip_Timer;

    public static ArrayList<List<Orders>> filteredOrders = new ArrayList<>();
    private List<String> socketOrders = new ArrayList<>();
    public static List<Orders> ordersList = new ArrayList<>();
    public static List<Orders> socketOrderList = new ArrayList<>();
    public static List<Orders> cloudOrderList = new ArrayList<>();
    public static ArrayList<Integer> orderNo = new ArrayList<>();
    public static List<String> deletedOrders = new ArrayList<>();
    public static Set<String> orderNoWithoutDup;
    List<String> dateSort2 = new ArrayList<>();
    List<Integer> dateSort3 = new ArrayList<>();

    private DatabaseHandler databaseHandler;
    private KitchenJSONPresenter presenter;
    private GridView dndGridView;
    private OrdersAdapter adapter;
    private ImageButton deleteButton, settingsButton;
    public static TextView textChecker;
    boolean checkSound = true;

    private String domain, point, orderListAsString;
    private int position = 0;
    static int sizeBefore = 0, sizeAfter = 0;

    boolean isFound = false; // used for compare cloud and socket values

    private Dialog dialog, passwordDialog;
    private EditText companyNo, companyYear, posNo, screenNo, timerDuration, passwordField;
    private Button saveSettings, cleanKitchen, cancelSettings, checkPassword;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializationAtStart();
        mediaPlayer = MediaPlayer.create(this, R.raw.appointed);
        databaseHandler.getkitchenSettings();
        startServerSocket();

        if (!TextUtils.isEmpty(KitchenSettingsModel.COMPANY_NO) && !TextUtils.isEmpty(KitchenSettingsModel.COMPANY_YEAR)
                && !TextUtils.isEmpty(KitchenSettingsModel.POS_NO) && !TextUtils.isEmpty(KitchenSettingsModel.SCREEN_NO)) {
            KitchenSettingsModel.FILLED = true;
            companyNo.setText(KitchenSettingsModel.COMPANY_NO);
            companyNo.setSelection(companyNo.getText().length());// change writing indicator position
            companyYear.setText(KitchenSettingsModel.COMPANY_YEAR);
            posNo.setText(KitchenSettingsModel.POS_NO);
            screenNo.setText(KitchenSettingsModel.SCREEN_NO);
            timerDuration.setText(KitchenSettingsModel.TIMER_DURATION);

        } else {
            KitchenSettingsModel.FILLED = false;
            Toast.makeText(this, "Please check settings first!", Toast.LENGTH_SHORT).show();
        }

        textChecker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textChecker.getText().toString().equals("1")) {//&& isAdapterFinished) { // when cloud is working
                    Log.e("text changed ", "1");
                    deletedOrders = databaseHandler.getBindingOrders();
                    Log.e("main:deletedOrders", "" + deletedOrders.size());
                    if (deletedOrders.size() != 0) {
                        for (int i = 0; i < deletedOrders.size(); i++) {
                            String domain = "http://10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno="
                                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + deletedOrders.get(i)
                                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO;
                            presenter.updateOrdersRequest(domain);
                            databaseHandler.deleteFromBindingList(deletedOrders.get(i));
                        }
                    }
                    textChecker.setText("2");
                } else if (textChecker.getText().toString().equals("2")) {// && isAdapterFinished) {
                    Log.e("text changed ", "2");
                    presenter.sendKitchenRequest();
                } else if (textChecker.getText().toString().equals("3")) {// && isAdapterFinished) {
                    Log.e("text changed ", "3");
//                    Log.e("socket order size: ", "" + socketOrderList.size());
                    filterOrdersByTableNo();
                    adapter = new OrdersAdapter(MainActivity.this, filteredOrders, MainActivity.this);
                    dndGridView.setAdapter(adapter);
                    textChecker.setText("0");
                } else if (textChecker.getText().toString().equals("4")) {
                    if (point != null) {
                        databaseHandler.addBindingOrders(point);
                        Log.e("add binding", "" + point);
                        point = null;
                    }
                    textChecker.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        _Request_Trip_Timer = new Timer();
        _Request_Trip_Timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                if (ordersList.size() != 0) {
                sizeBefore = ordersList.size();
//                }
                presenter.checkInternetConnection();
//                startServerSocket();
            }
        }, 0, (Integer.parseInt(KitchenSettingsModel.TIMER_DURATION) * 1000));// First time start after 0 milli second and repeated after 30 second

    }

    public void initializationAtStart() {
//        MyReceiver = new MyReceiver();
        databaseHandler = new DatabaseHandler(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        presenter = new KitchenJSONPresenter(this, MainActivity.this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please Waiting...");
        progressDialog.setCancelable(false);

        passwordDialog = new Dialog(MainActivity.this);
        passwordDialog.setContentView(R.layout.fragment_password_setting);
        passwordDialog.setCanceledOnTouchOutside(true);
        passwordField = passwordDialog.findViewById(R.id.setting_password_editText);
        checkPassword = passwordDialog.findViewById(R.id.setting_password_enter);

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.fragment_kitchen_settings);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Settings");
        companyNo = dialog.findViewById(R.id.kitchen_settings_company_no);
        companyYear = dialog.findViewById(R.id.kitchen_settings_company_year);
        posNo = dialog.findViewById(R.id.kitchen_settings_pos_no);
        screenNo = dialog.findViewById(R.id.kitchen_settings_screen_no);
        timerDuration = dialog.findViewById(R.id.kitchen_settings_timer);
        saveSettings = dialog.findViewById(R.id.kitchen_settings_save);
        cleanKitchen = dialog.findViewById(R.id.kitchen_settings_clean_kitchen);
        cancelSettings = dialog.findViewById(R.id.kitchen_settings_cancel);

        textChecker = findViewById(R.id.main_state_checker);
//        networkStateFollower = findViewById(R.id.main_network_follower);
//        internetState = findViewById(R.id.main_network_follower);
        settingsButton = findViewById(R.id.orders_list_settings_button);
        settingsButton.setOnClickListener(this);

        dndGridView = findViewById(R.id.gridView_dnd_container);
        dndGridView.setNumColumns(3);
        dndGridView.setVerticalScrollBarEnabled(false);
//        presenter = new KitchenJSONPresenter(this);

        deleteButton = findViewById(R.id.orders_list_delete_button);
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setOnDragListener(this);
    }

    @Override
    public void onBackPressed() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    void showData() {
//        List<Orders> orders = databaseHandler.getOrders();
//        Log.e("size is ", "" + orders.size());
        List<Orders> ordersSocket = databaseHandler.getOrdersFromSocket();
        Log.e("size is ", "" + ordersSocket.size());
//        Log.e("ITEMNAME ", "" + orders.get(0).getItemName());
//        Log.e("Done ", "" + orders.get(0).getDone());
//        Log.e("KitchenNumber ", "" + orders.get(0).getKitchenNumber());

    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        // Create a new LinkedHashSet
        Set<T> set = new LinkedHashSet<>();
        // Add the elements to set
        set.addAll(list);
        // Clear the list
        list.clear();
        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);

        // return the list
        return list;
    }

    public ArrayList<List<Orders>> filterOrdersByTableNo() {
        removeDuplicateOrders();

        if (ordersList.size() != 0 && checkSound) {
            sizeAfter = ordersList.size();
            if (sizeAfter > sizeBefore)
                mediaPlayer.start();
        }
        if (ordersList.size() != 0) {
            convertToEnglish();

            for (int m = 0; m < ordersList.size(); m++) {
                orderNo.add(Integer.parseInt(ordersList.get(m).getOrderNumber()));
            }
            removeDuplicates(orderNo);
//            orderNoWithoutDup = new HashSet<>(orderNo);
//            orderNo.clear();
//            orderNo.addAll(orderNoWithoutDup);

//            Collections.sort(orderNo);
            // Method to get all orders for specific table
            for (int j = 0; j < orderNo.size(); j++) {
                List<Orders> ordersForOneTable = new ArrayList<>();
                Log.e("main:oneTable j", "" + orderNo.get(j));

                for (int k = 0; k < ordersList.size(); k++) { //KitchenJSONPresenter.ordersList.size()
//                    Log.e("without name", "" + KitchenJSONPresenter.ordersList.get(k).getOrderNumber());
                    if (orderNo.get(j)== Integer.parseInt(ordersList.get(k).getOrderNumber())) {//KitchenJSONPresenter.ordersList.get(k).getOrderNumber()
                        ordersForOneTable.add(ordersList.get(k));//KitchenJSONPresenter.ordersList.get(k)

                        Log.e("main:oneTable k", "" + ordersList.get(k).getOrderNumber());
                    }
                }
//                Collections.sort(ordersForOneTable);
                filteredOrders.add(ordersForOneTable);
            }
        }
        Log.e("main:filtered: ", "" + filteredOrders.size());
        checkSound = true;
        return filteredOrders;
    }

    public void removeDuplicateOrders() {
        filteredOrders.clear();
        ordersList.clear();
        orderNo.clear();
        cloudOrderList.clear();
        socketOrderList.clear();
        socketOrderList = databaseHandler.getOrdersFromSocket();
        cloudOrderList = databaseHandler.getOrders();

        Log.e("socket size:", "" + socketOrderList.size());
        Log.e("cloud size:", "" + cloudOrderList.size());

        if (socketOrderList.size() != 0 && cloudOrderList.size() != 0) {
            for (int i = 0; i < cloudOrderList.size(); i++) {
                isFound = false;
                String checkCloudOrder = "" + cloudOrderList.get(i).getOrderNumber()
                        + cloudOrderList.get(i).getOrderType()
                        + cloudOrderList.get(i).getItemCode()
                        + cloudOrderList.get(i).getItemName()
                        + cloudOrderList.get(i).getQuantity()
                        + cloudOrderList.get(i).getPrice()
                        + cloudOrderList.get(i).getPosNumber()
                        + cloudOrderList.get(i).getTableNumber()
                        + cloudOrderList.get(i).getSection()
                        + cloudOrderList.get(i).getIsUpdated()
                        + cloudOrderList.get(i).getNote();
                Log.e("cloud order", checkCloudOrder);
//{"PRICE":2,"ITEMCODE":"1111","SECTIONNO":-1,"POSNO":1,"QTY":2,"ORDERTYPE":0,"NOTE":"","ORDERNO":"268","TABLENO":-1,"ITEMNAME":"mall soop","ISUPDATE":0}
                //26801111mall soop22.01-1-10
                //26801111mall soop22.01-1-10
                for (int j = 0; j < socketOrderList.size(); j++) {
                    String checkSocketOrder = "" + socketOrderList.get(j).getOrderNumber()
                            + socketOrderList.get(j).getOrderType()
                            + socketOrderList.get(j).getItemCode()
                            + socketOrderList.get(j).getItemName()
                            + socketOrderList.get(j).getQuantity()
                            + socketOrderList.get(j).getPrice()
                            + socketOrderList.get(j).getPosNumber()
                            + socketOrderList.get(j).getTableNumber()
                            + socketOrderList.get(j).getSection()
                            + socketOrderList.get(j).getIsUpdated()
                            + socketOrderList.get(j).getNote();
                    Log.e("checkSocketOrder", checkSocketOrder);
                    Log.e("boolean:", "" + checkCloudOrder.equals(checkSocketOrder));
                    if (checkCloudOrder.equals(checkSocketOrder)) {
                        isFound = true;
                        ordersList.add(cloudOrderList.get(i));
                        socketOrderList.remove(j);
                        j = socketOrderList.size();

                    }
                }

                if (!isFound) { // if there is items in the cloud not exist in the socket
                    ordersList.add(cloudOrderList.get(i));
                    isFound = false;
                }
            }

            if (socketOrderList.size() != 0) { // if there is items in the socket not exist in the cloud
                for (int k = 0; k < socketOrderList.size(); k++) {
                    ordersList.add(socketOrderList.get(k));
                }
            }

        } else if (socketOrderList.size() != 0) {
            ordersList = socketOrderList;
        } else if (cloudOrderList.size() != 0) {
            ordersList = cloudOrderList;
        }

    }

    public void convertToEnglish() {
        Log.e("ordersList...", "" + ordersList.size());
        dateSort2.clear();
        for (int i = 0; i < ordersList.size(); i++) {
//            String newValue = (((ordersList.get(i).getDateIn() + "").replaceAll("ص", "am")).replaceAll("م", "pm"));
//            String[] splitString = newValue.split("\\s+", 3);
//            String[] splitString2 = splitString[1].split(":", 3);
            String[] splitString = ordersList.get(i).getDateIn().split("\\s+", 2);
            Log.e("splitString...", "" + splitString);
            String[] splitString2 = splitString[1].split(":", 3);

            dateSort2.add(splitString2[0] + splitString2[1] + splitString2[2]);
//            if (splitString[2].contains("pm")) {
//                int val = Integer.parseInt(splitString2[0]) + 12;
//                dateSort2.add("" + val + splitString2[1] + splitString2[2]);
//            } else {
//                dateSort2.add(splitString2[0] + splitString2[1] + splitString2[2]);
//            }
        }
        Log.e("dateSort2...", "" + dateSort2.size());

        sortList();
    }

    void sortList() {
        for (int k = 0; k < dateSort2.size(); k++) {
//            dateSort3.add(Integer.parseInt(dateSort2.get(k)));
            ordersList.get(k).setDateIn(dateSort2.get(k));
        }
//        Collections.sort(dateSort3);
        Collections.sort(ordersList, new DateInComparator());

        for (int k = 0; k < ordersList.size(); k++) {
            Log.e("final", "" + ordersList.get(k).getOrderNumber() + ordersList.get(k).getDateIn());
        }
    }

    public class DateInComparator implements Comparator<Orders> {
        public int compare(Orders order1, Orders order2) {
            return order1.getDateIn().compareTo(order2.getDateIn());
        }
    }

    public void showDeleteOrderButton(ArrayList<List<Orders>> filteredOrders, int position) {
        this.filteredOrders = filteredOrders;
        this.position = position;
        deleteButton.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                10);
        animate.setDuration(50);
        animate.setFillAfter(true);
        deleteButton.startAnimation(animate);
    }

    public void hideDeleteOrders() {
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                10,
                0);
        animate.setDuration(50);
//      animate.setFillAfter(true);
        deleteButton.startAnimation(animate);
        deleteButton.setVisibility(View.INVISIBLE);
    }

    public boolean onDrag(View v, DragEvent event) {
        textChecker.setText("5");

        if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED)
            deleteButton.setBackgroundResource(R.color.exit);

        if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
            deleteButton.setBackgroundResource(R.drawable.delete_button_style);
            hideDeleteOrders();
        }

        if (event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
            deleteButton.setBackgroundResource(R.drawable.delete_button_style);
            hideDeleteOrders();
        }

        if (event.getAction() == DragEvent.ACTION_DROP) {
            point = filteredOrders.get(position).get(0).getOrderNumber();
            domain = "http://10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno="
                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + point
                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO;

            checkSound = false;
            databaseHandler.deleteFromSocketAndCloud(point);
            presenter.updateOrdersRequest(domain);
            filteredOrders.remove(filteredOrders.get(position));
            hideDeleteOrders();
            Toast.makeText(this, "item deleted", Toast.LENGTH_SHORT).show();
            Log.e("item ", " deleted");
            textChecker.setText("3");
        }
        return true;
    }

    public void deleteKitchenOrder(final int position) {
        Log.e("item ", " respo");

        textChecker.setText("5");
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setMessage("Are you want delete order number " + filteredOrders.get(position).get(0).getOrderNumber() + " ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        point = filteredOrders.get(position).get(0).getOrderNumber();
                        domain = "http://10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno="
                                + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                                + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + point
                                + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO;

                        checkSound = false;
                        databaseHandler.deleteFromSocketAndCloud(point);
                        presenter.updateOrdersRequest(domain);
                        filteredOrders.remove(filteredOrders.get(position));
                        Toast.makeText(MainActivity.this, "item deleted", Toast.LENGTH_SHORT).show();
                        Log.e("item ", " deleted");
                        textChecker.setText("3");
                    }
                });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    public void startServerSocket() {

        Thread thread = new Thread(new Runnable() {
            private String stringData = null;

            //            private JSONObject jsonObject = new JSONObject();
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(9002);
                    while (true) {
                        //Server is waiting for client here, if needed
                        Socket s = serverSocket.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream());

                        stringData = input.readLine();
                        if (stringData != null) {
                            Log.e("socket ", "is worked");
//                            output.println(stringData);
//                            output.flush();
                            filterSocketAndServerOrders(stringData);
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        updateUI(stringData);
//                        if (stringData.equalsIgnoreCase("STOP")) {
//                            end = true;
//                            output.close();
//                            s.close();
//                            break;
//                        }

                        stringData = null;
                        output.close();
                        s.close();
                    }
//                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }

    public void filterSocketAndServerOrders(final String socketOrder) { //final Orders socketOrder
        handler.post(new Runnable() {
            @Override
            public void run() {
                socketOrders.clear();
                Log.e("socketOrder ", socketOrder);
                socketOrders.add(socketOrder);
                Log.e("socket size", "" + socketOrders.size());
                for (int k = 0; k < socketOrders.size(); k++) {
                    try {
                        JSONArray jsonArray = new JSONArray(socketOrders.get(k));
                        Log.e("array length", "" + jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Orders orders = new Orders("TRINDATE"
                                    , object.getString("ORDERNO")
                                    , Integer.parseInt(object.getString("ORDERTYPE"))
                                    , object.getString("ITEMCODE")
                                    , object.getString("ITEMNAME")
                                    , Integer.parseInt(object.getString("QTY"))
                                    , Double.parseDouble(object.getString("PRICE"))
                                    , Integer.parseInt(object.getString("POSNO"))
                                    , Integer.parseInt(object.getString("TABLENO"))
                                    , object.getString("SECTIONNO")
                                    , object.getString("ISUPDATE")
                                    , "0"
                                    , object.getString("NOTE")
                            );
                            Log.e("socket order ", orders.getOrderNumber());
                            databaseHandler.addOrdersBySocket(orders);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                showData();
//                if (textChecker.getText().toString().equals("3"))
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!textChecker.getText().toString().equals("5"))
                            textChecker.setText("3");
                    }
                });
            }
        });
//        socketOrders.clear();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.orders_list_settings_button) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            passwordField.setText("");

            checkPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(passwordField.getText())) {
                        if (passwordField.getText().toString().equals("123456")) {
                            saveSettings.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!TextUtils.isEmpty(companyNo.getText()) && !TextUtils.isEmpty(companyYear.getText())
                                            && !TextUtils.isEmpty(posNo.getText()) && !TextUtils.isEmpty(screenNo.getText())
                                            && !TextUtils.isEmpty(timerDuration.getText())) {

                                        databaseHandler.deletekitchenSettings();
                                        filteredOrders.clear();
                                        orderNo.clear();
                                        ordersList.clear();
                                        cloudOrderList.clear();
                                        socketOrderList.clear();
                                        KitchenSettingsModel.COMPANY_NO = "" + companyNo.getText().toString();
                                        KitchenSettingsModel.COMPANY_YEAR = "" + companyYear.getText().toString();
                                        KitchenSettingsModel.POS_NO = "" + posNo.getText().toString();
                                        KitchenSettingsModel.SCREEN_NO = "" + screenNo.getText().toString();
                                        KitchenSettingsModel.TIMER_DURATION = "" + timerDuration.getText().toString();
                                        if (KitchenSettingsModel.TIMER_DURATION.matches("[0-9]+")) {
                                            KitchenSettingsModel.FILLED = true;
                                            databaseHandler.addkitchenSettings();
                                            dialog.dismiss();
                                            finish();
                                            startActivity(getIntent());
                                        } else {
                                            timerDuration.setError("Characters and symbols \n are not valid!");
                                        }

                                    } else
                                        Toast.makeText(MainActivity.this, "Please fill empty fields!", Toast.LENGTH_SHORT).show();
                                }
                            });

                            cancelSettings.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    passwordDialog.dismiss();
                                }
                            });

                            cleanKitchen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                            .setIcon(R.drawable.ic_warning_black_24dp)
                                            .setTitle("Attention!")
                                            .setMessage("Are you sure you want to clean the kitchen? \n this will lead to lose the data!!")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    databaseHandler.deleteAllOrders();
                                                    databaseHandler.deleteAllFromSocketOrders();
//                                                    databaseHandler.deleteAllBindingOrders();

                                                    if (filteredOrders.size() != 0) {
                                                        for (int i = 0; i < filteredOrders.size(); i++) {
                                                            presenter.updateOrdersRequest("http://10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno="
                                                                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                                                                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + filteredOrders.get(i).get(0).getOrderNumber()
                                                                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO);
                                                        }
                                                    }

                                                    filteredOrders.clear();
                                                    ordersList.clear();
                                                    orderNo.clear();
                                                    socketOrders.clear();
                                                    socketOrderList.clear();
                                                    cloudOrderList.clear();
//                                                    deletedOrders.clear();
                                                    textChecker.setText("3");
                                                    Toast.makeText(MainActivity.this, "Cleaned successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.show();
                                }
                            });

                            dialog.show();

                        } else {
                            Toast.makeText(MainActivity.this, "Not authorized!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Enter Admin Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            passwordDialog.show();

        }
    }

    public void showLoading() {

        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!MainActivity.this.isFinishing()) {
                    progressDialog.show();
                }

            }
        });
    }

    public void dismissLoading() {

        this.runOnUiThread(new Runnable() {
            public void run() {
                if (!MainActivity.this.isFinishing()) {
                    progressDialog.dismiss();
                }

            }
        });
    }
}

