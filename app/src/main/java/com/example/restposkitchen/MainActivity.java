package com.example.restposkitchen;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.printservice.PrintService;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity implements View.OnDragListener, View.OnClickListener {//implements View.OnTouchListener {

    //    public static TextView networkStateFollower; // check network and network state
//    public static String internetState;
    public PrintPic printPic;
    private Handler handler = new Handler();
    Handler mHandler = new Handler();
    private ProgressDialog progressDialog;
    private MediaPlayer mediaPlayer;
    private Timer _Request_Trip_Timer;
    private DatabaseHandler databaseHandler;
    private KitchenJSONPresenter presenter;
    private GridView dndGridView;
    private OrdersAdapter adapter;
    private ImageButton deleteButton, settingsButton;
    private TextView languageArabic, languageEnglish;
    public static TextView textChecker;
    boolean checkSound = true;
    //    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private String LANGUAGE_SHARED_PREF = "language";

    public static ArrayList<List<Orders>> filteredOrders = new ArrayList<>();
    public static List<Orders> ordersList = new ArrayList<>();
    public static List<Orders> socketOrderList = new ArrayList<>();
    public static List<Orders> cloudOrderList = new ArrayList<>();
    public static ArrayList<Integer> orderNo = new ArrayList<>();
    public static List<String> deletedOrders = new ArrayList<>();
    private List<String> socketOrders = new ArrayList<>();
    private List<String> dateSort2 = new ArrayList<>();
    //    private List<Bitmap> bitmapList = new ArrayList<>();
    private List<String> arabicList = new ArrayList<>();

    private String domain, point, orderListAsString;
    private int position = 0;
    static int sizeBefore = 0, sizeAfter = 0;

    boolean isFound = false; // used for compare cloud and socket values

    private Dialog dialog, passwordDialog;
    private EditText companyNo, companyYear, posNo, screenNo, timerDuration, passwordField, ipAddressForReceiver, stageNo, getOrdersURL;
    private RadioGroup radioGroup;
    private RadioButton doneRadioButton, transferRadioButton, printRadioButton;
    private Button saveSettings, cleanKitchen, cancelSettings, checkPassword;
    private LinearLayout stageNoLinear, ipAddressLinear;

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
                && !TextUtils.isEmpty(KitchenSettingsModel.POS_NO) && !TextUtils.isEmpty(KitchenSettingsModel.SCREEN_NO)
                && !TextUtils.isEmpty(KitchenSettingsModel.STAGE_TYPE) && !TextUtils.isEmpty(KitchenSettingsModel.URL)
                && !TextUtils.isEmpty(KitchenSettingsModel.STAGE_NO)) {
            KitchenSettingsModel.FILLED = true;
            companyNo.setText(KitchenSettingsModel.COMPANY_NO);
            companyNo.setSelection(companyNo.getText().length());// change writing indicator position
            companyYear.setText(KitchenSettingsModel.COMPANY_YEAR);
            posNo.setText(KitchenSettingsModel.POS_NO);
            screenNo.setText(KitchenSettingsModel.SCREEN_NO);
            timerDuration.setText(KitchenSettingsModel.TIMER_DURATION);
            getOrdersURL.setText(KitchenSettingsModel.URL);
            stageNo.setText(KitchenSettingsModel.STAGE_NO);

            if (KitchenSettingsModel.STAGE_TYPE.equals("transfer")) {
//                Log.e("check", "transfer");
                ipAddressLinear.setVisibility(View.VISIBLE);
                transferRadioButton.setChecked(true);
                ipAddressForReceiver.setText(KitchenSettingsModel.IP_OF_RECEIVER);

            } else if (KitchenSettingsModel.STAGE_TYPE.equals("print")) {
//                Log.e("check", "transfer");
                ipAddressLinear.setVisibility(View.VISIBLE);
                printRadioButton.setChecked(true);
                ipAddressForReceiver.setText(KitchenSettingsModel.IP_OF_RECEIVER);

            } else if (KitchenSettingsModel.STAGE_TYPE.equals("done")) {
//                Log.e("check", "done");
                ipAddressLinear.setVisibility(View.GONE);
                doneRadioButton.setChecked(true);
            }

        } else {
            ipAddressLinear.setVisibility(View.GONE);
            doneRadioButton.setChecked(true);
            KitchenSettingsModel.STAGE_TYPE = "done";
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
//                            String domain = "http://10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno="
//                                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
//                                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + deletedOrders.get(i)
//                                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO;
                            String domain = KitchenSettingsModel.URL + "UpdateRestKitchenScreen?compno="
                                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + point
                                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO + "&cashno=" + filteredOrders.get(position).get(0).getCashNumber();

                            Log.e("clean kitchen", "" + domain);


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

//        prefs = getSharedPreferences(LANGUAGE_SHARED_PREF, MODE_PRIVATE);
//        String name = prefs.getString("lang", "en");//"No name defined" is the default value.
//        if (name.equals("en"))
//            setAppLanguage("en");
//        else
//            setAppLanguage("ar");
        arabicList.add("الطلب");
//        arabicList.add("طاولة");
//        arabicList.add("سفري");
        arabicList.add("رقم الطاولة");
        arabicList.add("القسم");
        arabicList.add("ملاحظات  الكمي        المادة");
//        arabicList.add("الكمية");
//        arabicList.add("ملاحظات");


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
        radioGroup = dialog.findViewById(R.id.kitchen_settings_radioGroup);
        doneRadioButton = dialog.findViewById(R.id.kitchen_settings_done);
        transferRadioButton = dialog.findViewById(R.id.kitchen_settings_transfer);
        printRadioButton = dialog.findViewById(R.id.kitchen_settings_print);
        ipAddressForReceiver = dialog.findViewById(R.id.kitchen_settings_IP_address);
        stageNo = dialog.findViewById(R.id.kitchen_settings_stageNo);
        getOrdersURL = dialog.findViewById(R.id.kitchen_settings_url);
        stageNoLinear = dialog.findViewById(R.id.kitchen_settings_stageNo_linear);
        ipAddressLinear = dialog.findViewById(R.id.kitchen_settings_IP_linear);
        saveSettings = dialog.findViewById(R.id.kitchen_settings_save);
        cleanKitchen = dialog.findViewById(R.id.kitchen_settings_clean_kitchen);
        cancelSettings = dialog.findViewById(R.id.kitchen_settings_cancel);

        textChecker = findViewById(R.id.main_state_checker);
//        networkStateFollower = findViewById(R.id.main_network_follower);
//        internetState = findViewById(R.id.main_network_follower);
        languageArabic = findViewById(R.id.main_language_arabic);
        languageEnglish = findViewById(R.id.main_language_english);
        settingsButton = findViewById(R.id.orders_list_settings_button);
        settingsButton.setOnClickListener(this);
        languageEnglish.setOnClickListener(this);
        languageArabic.setOnClickListener(this);

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

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
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
        removeDuplicateOrders(); // remove duplicate orders from socket and cloud

        if (ordersList.size() != 0 && checkSound) {
            sizeAfter = ordersList.size();
            if (sizeAfter > sizeBefore)
                mediaPlayer.start();
        }
        if (ordersList.size() != 0) {
            sortByTime();

            for (int m = 0; m < ordersList.size(); m++) {
                orderNo.add(Integer.parseInt(ordersList.get(m).getOrderNumber()));
            }
            removeDuplicates(orderNo);

            // Method to get all orders for specific table
            for (int j = 0; j < orderNo.size(); j++) {
                List<Orders> ordersForOneTable = new ArrayList<>();
//                Log.e("main:oneTable j", "" + orderNo.get(j));

                for (int k = 0; k < ordersList.size(); k++) { //KitchenJSONPresenter.ordersList.size()
//                    Log.e("without name", "" + KitchenJSONPresenter.ordersList.get(k).getOrderNumber());
                    if (orderNo.get(j) == Integer.parseInt(ordersList.get(k).getOrderNumber())) {//KitchenJSONPresenter.ordersList.get(k).getOrderNumber()
                        ordersForOneTable.add(ordersList.get(k));//KitchenJSONPresenter.ordersList.get(k)

//                        Log.e("main:oneTable k", "" + ordersList.get(k).getOrderNumber());
                    }
                }
                Collections.sort(ordersForOneTable, new OrderByItemCodeComparator());
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

//        Log.e("socket size:", "" + socketOrderList.size());
//        Log.e("cloud size:", "" + cloudOrderList.size());

        if (socketOrderList.size() != 0 && cloudOrderList.size() != 0) {
            for (int i = 0; i < cloudOrderList.size(); i++) {
                isFound = false;
                String checkCloudOrder = "" + cloudOrderList.get(i).getOrderNumber()
                        + cloudOrderList.get(i).getCashNumber()
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
//                Log.e("cloud order", checkCloudOrder);
//{"PRICE":2,"ITEMCODE":"1111","SECTIONNO":-1,"POSNO":1,"QTY":2,"ORDERTYPE":0,"NOTE":"","ORDERNO":"268","TABLENO":-1,"ITEMNAME":"mall soop","ISUPDATE":0}
                //26801111mall soop22.01-1-10
                //26801111mall soop22.01-1-10
                for (int j = 0; j < socketOrderList.size(); j++) {
                    String checkSocketOrder = "" + socketOrderList.get(j).getOrderNumber()
                            + socketOrderList.get(j).getCashNumber()
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
//                    Log.e("checkSocketOrder", checkSocketOrder);
//                    Log.e("boolean:", "" + checkCloudOrder.equals(checkSocketOrder));
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

    public class OrderByItemCodeComparator implements Comparator<Orders> {
        public int compare(Orders order1, Orders order2) {
            return order1.getItemCode().compareTo(order2.getItemCode());
        }
    }

    public void sortByTime() {
        Log.e("ordersList...", "" + ordersList.size());
        dateSort2.clear();
        for (int i = 0; i < ordersList.size(); i++) {
//            String newValue = (((ordersList.get(i).getDateIn() + "").replaceAll("ص", "am")).replaceAll("م", "pm"));
//            String[] splitString = newValue.split("\\s+", 3);
//            String[] splitString2 = splitString[1].split(":", 3);
            if (!ordersList.get(i).getDateIn().equals("TRINDATE")) {
                String[] splitString = ordersList.get(i).getDateIn().split("\\s+", 2);
//                Log.e("splitname...", "" + ordersList.get(i).getItemName());
//                Log.e("splitString...", "" + ordersList.get(i).getDateIn());

                String[] splitString2 = splitString[1].split(":", 3);

                dateSort2.add(splitString2[0] + splitString2[1] + splitString2[2]);
            }
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

//        for (int k = 0; k < ordersList.size(); k++) {
//            Log.e("final", "" + ordersList.get(k).getOrderNumber() + ordersList.get(k).getDateIn());
//        }
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
//            Log.e("a", KitchenSettingsModel.STAGE_TYPE);
//            Log.e("b", "" + KitchenSettingsModel.STAGE_TYPE.equals("done"));
//            Log.e("c", "" + KitchenSettingsModel.STAGE_TYPE.equals("transfer"));

            if (KitchenSettingsModel.STAGE_TYPE.equals("done")) {
                domain = KitchenSettingsModel.URL + "UpdateRestKitchenScreen?compno="
                        + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                        + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + point
                        + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO + "&cashno=" + filteredOrders.get(position).get(0).getCashNumber();
            } else if (KitchenSettingsModel.STAGE_TYPE.equals("transfer") || KitchenSettingsModel.STAGE_TYPE.equals("print")) {

                if (KitchenSettingsModel.STAGE_TYPE.equals("transfer")) {
                    KitchenSettingsModel.PORT_NO = 9002;
                    KitchenSettingsModel.FLAG = 1;
                } else if (KitchenSettingsModel.STAGE_TYPE.equals("print")) {
                    KitchenSettingsModel.PORT_NO = 9100;
                    KitchenSettingsModel.FLAG = 2;
                }

                domain = KitchenSettingsModel.URL + "RestTransferKitchen?compno="
                        + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                        + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + point
                        + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO + "&cashno=" + filteredOrders.get(position).get(0).getCashNumber();


                fillSocketOrdersToSend(point);
            }

            checkSound = false;
            databaseHandler.deleteFromSocketAndCloud(point);
            presenter.updateOrdersRequest(domain);
            filteredOrders.remove(filteredOrders.get(position));
            hideDeleteOrders();
            Toast.makeText(this, "item deleted", Toast.LENGTH_SHORT).show();
//            Log.e("item ", " deleted");
            textChecker.setText("3");
        }
        return true;
    }

    public void deleteKitchenOrder(final int position) {
//        Log.e("item ", " respo");

        textChecker.setText("5");
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setIcon(R.drawable.ic_delete_white_24dp)
                .setMessage("Are you want delete order number " + filteredOrders.get(position).get(0).getOrderNumber() + " ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        point = filteredOrders.get(position).get(0).getOrderNumber();
                        Log.e("stage type", KitchenSettingsModel.STAGE_TYPE);
                        if (KitchenSettingsModel.STAGE_TYPE.equals("done")) {
                            Log.e("stage type", "done");
                            domain = KitchenSettingsModel.URL + "UpdateRestKitchenScreen?compno="
                                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + point
                                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO + "&cashno=" + filteredOrders.get(position).get(0).getCashNumber();
                        } else if (KitchenSettingsModel.STAGE_TYPE.equals("transfer") || KitchenSettingsModel.STAGE_TYPE.equals("print")) {
                            Log.e("stage type", KitchenSettingsModel.STAGE_TYPE);
                            if (KitchenSettingsModel.STAGE_TYPE.equals("transfer")) {
                                KitchenSettingsModel.PORT_NO = 9002;
                                KitchenSettingsModel.FLAG = 1;
                            } else if (KitchenSettingsModel.STAGE_TYPE.equals("print")) {
                                KitchenSettingsModel.PORT_NO = 9100;
                                KitchenSettingsModel.FLAG = 2;
                            }

                            domain = KitchenSettingsModel.URL + "RestTransferKitchen?compno="
                                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + point
                                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO + "&cashno=" + filteredOrders.get(position).get(0).getCashNumber();
                            fillSocketOrdersToSend(filteredOrders.get(position).get(0).getOrderNumber());
                        }

//                        Log.e("point", "" + domain);

                        checkSound = false;
                        databaseHandler.deleteFromSocketAndCloud(point);
                        presenter.updateOrdersRequest(domain);
                        filteredOrders.remove(filteredOrders.get(position));
                        Toast.makeText(MainActivity.this, "item deleted", Toast.LENGTH_SHORT).show();
//                        Log.e("item ", " deleted");
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

    @SuppressLint("ResourceType")
    public void fillSocketOrdersToSend(String orderNo) {
        List<Orders> list = databaseHandler.getOrderBy(orderNo);
//        Log.e("db list", "" + list.size());
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("TRINDATE", list.get(i).getDateIn());
                jsonObject.put("CASHNO", list.get(i).getCashNumber());
                jsonObject.put("ORDERNO", list.get(i).getOrderNumber());
                jsonObject.put("ORDERTYPE", list.get(i).getOrderType());
                jsonObject.put("ITEMCODE", list.get(i).getItemCode());
                jsonObject.put("ITEMNAME", list.get(i).getItemName());
                jsonObject.put("QTY", list.get(i).getQuantity());
                jsonObject.put("PRICE", list.get(i).getPrice());
                jsonObject.put("POSNO", list.get(i).getPosNumber());
                jsonObject.put("TABLENO", list.get(i).getTableNumber());
                jsonObject.put("SECTIONNO", list.get(i).getSection());
                jsonObject.put("ISUPDATE", list.get(i).getIsUpdated());
//                        jsonObject.put("", "0");
                jsonObject.put("NOTE", list.get(i).getNote());
                jsonObject.put("STGNO", list.get(i).getStageNo());

//                Log.e("level", jsonObject.toString());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (KitchenSettingsModel.FLAG == 2 && KitchenSettingsModel.LANGUAGE.equals("ar")) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.print_container_layout);

            LinearLayout linearLayout = dialog.findViewById(R.id.print_container_parent);
            TableLayout tableContainer = dialog.findViewById(R.id.print_container_table_container);
            TextView orderNoTextView = dialog.findViewById(R.id.print_container_order_no);
            TextView tableNoTextView = dialog.findViewById(R.id.print_container_table_no);
            TextView sectionTextView = dialog.findViewById(R.id.print_container_section_no);
            TextView orderTypeTextView = dialog.findViewById(R.id.print_container_orderType);

            String orderType, tableNo, sectionNo;
            try {
                if (jsonArray.getJSONObject(0).getInt("ORDERTYPE") == 0) {
                    orderType = "سفري";
                    tableNo = "-";
                    sectionNo = "-";
                } else {
                    orderType = "طاولة";
                    tableNo = "" + jsonArray.getJSONObject(0).getInt("TABLENO");
                    sectionNo = jsonArray.getJSONObject(0).getString("SECTIONNO");
                }

                orderNoTextView.setText("" + jsonArray.getJSONObject(0).getString("ORDERNO"));
                orderTypeTextView.setText(orderType);
                tableNoTextView.setText(tableNo);
                sectionTextView.setText(sectionNo);

                TableRow subjectRow = new TableRow(MainActivity.this);
                TableRow.LayoutParams layoutParams5 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT
                        , TableRow.LayoutParams.WRAP_CONTENT);
                subjectRow.setLayoutParams(layoutParams5);

                TableRow.LayoutParams viewsLayoutParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT
                        , 30, 1.0f);

                TextView noteSubject = new TextView(MainActivity.this);
                noteSubject.setText("ملاحظات");
                noteSubject.setGravity(Gravity.CENTER);
                noteSubject.setTextSize(18);
                noteSubject.setLayoutParams(viewsLayoutParams1);
                subjectRow.addView(noteSubject);

                TextView qtySubject = new TextView(MainActivity.this);
                qtySubject.setText("الكمية");
                qtySubject.setGravity(Gravity.CENTER);
                qtySubject.setTextSize(18);
                qtySubject.setLayoutParams(viewsLayoutParams1);
                subjectRow.addView(qtySubject);

                TextView itemSubject = new TextView(MainActivity.this);
                itemSubject.setText("المادة");
                itemSubject.setGravity(Gravity.CENTER);
                itemSubject.setTextSize(18);
                itemSubject.setLayoutParams(viewsLayoutParams1);
                subjectRow.addView(itemSubject);

//                TableRow subjectRow2 = new TableRow(MainActivity.this);
//                TableRow.LayoutParams layoutParams22 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT
//                        , TableRow.LayoutParams.WRAP_CONTENT);
//                subjectRow2.setLayoutParams(layoutParams22);
//
//                TextView lineView = new TextView(MainActivity.this);
//                lineView.setText("---------------------------------------------------------------------------------------------------------");
//                lineView.setLayoutParams(noteLayoutParams1);
//                subjectRow2.addView(lineView);

                tableContainer.addView(subjectRow);
//                tableContainer.addView(subjectRow2);

                for (int m = 0; m < jsonArray.length(); m++) {
                    JSONObject orderObject = jsonArray.getJSONObject(m);
                    String name = orderObject.getString("ITEMNAME");
                    String qty = String.valueOf(orderObject.getInt("QTY"));
                    String note = "";
//                    while (name.length() < 40) {
//                        name += " ";
//                    }
//                    name+= orderObject.getString("ITEMNAME");
//                    while (qty.length() < 8) {
//                        qty += " ";
//                    }
//                    qty += String.valueOf(orderObject.getInt("QTY"));

//                    TableRow itemRow = new TableRow(MainActivity.this);
//                    TextView nameTextView = new TextView(MainActivity.this);
//                    nameTextView.setText(orderObject.getString("NOTE") + qty + name);
//                    itemRow.addView(nameTextView);
//                    tableContainer.addView(itemRow);
                    if (orderObject.getString("ISUPDATE").equals("1"))
                        note = "تعديل";
                    else
                        note = "";

                    TableRow itemRow = new TableRow(MainActivity.this);
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT
                            , TableRow.LayoutParams.WRAP_CONTENT);
                    itemRow.setLayoutParams(layoutParams);

                    TableRow.LayoutParams viewsLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT
                            , 50, 1.0f);

                    TextView noteTextView = new TextView(MainActivity.this);
                    noteTextView.setText(note);//orderObject.getString("NOTE"));
                    noteTextView.setGravity(Gravity.CENTER);
                    noteTextView.setTextSize(18);
                    noteTextView.setLayoutParams(viewsLayoutParams);
                    itemRow.addView(noteTextView);

                    TextView qtyTextView = new TextView(MainActivity.this);
                    qtyTextView.setText(qty);
                    qtyTextView.setGravity(Gravity.CENTER);
                    qtyTextView.setTextSize(18);
                    qtyTextView.setLayoutParams(viewsLayoutParams);
                    itemRow.addView(qtyTextView);

                    TextView nameTextView = new TextView(MainActivity.this);
                    nameTextView.setText(name);
                    nameTextView.setGravity(Gravity.CENTER);
                    nameTextView.setTextSize(18);
                    nameTextView.setLayoutParams(viewsLayoutParams);
                    itemRow.addView(nameTextView);

                    tableContainer.addView(itemRow);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());
            sendSocketToNextStage(jsonArray, linearLayout);

        } else
            sendSocketToNextStage(jsonArray, null);

    }

    public void sendSocketToNextStage(final JSONArray jsonArray, final LinearLayout linearLayout2) {
//        Log.e("level", "2");
//        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket(KitchenSettingsModel.IP_OF_RECEIVER, KitchenSettingsModel.PORT_NO);
                    OutputStream out = s.getOutputStream();
                    PrintWriter output = new PrintWriter(out);
//                    Bitmap b=StringToBitMap("رقم الطاولة");
                    if (KitchenSettingsModel.FLAG == 1) {// transfer
                        output.println(jsonArray.toString());
                    } else if (KitchenSettingsModel.FLAG == 2) { // print
                        if (KitchenSettingsModel.LANGUAGE.equals("en")) {
//                            Toast.makeText(MainActivity.this, "Current language is English", Toast.LENGTH_SHORT).show();
                            String orderType, tableNo, sectionNo, note;
                            if (jsonArray.getJSONObject(0).getInt("ORDERTYPE") == 0) {
                                orderType = "Take Away";
                                tableNo = "-";
                                sectionNo = "-";
                            } else {
                                orderType = "Dine In";
                                tableNo = "" + jsonArray.getJSONObject(0).getInt("TABLENO");
                                sectionNo = jsonArray.getJSONObject(0).getString("SECTIONNO");
                            }

                            String orderString = "Order: " + jsonArray.getJSONObject(0).getString("ORDERNO")
                                    + " / " + orderType
                                    + "\nTable No: " + tableNo
                                    + "\nSection No: " + sectionNo
                                    + "\n__________________________________"
                                    + "\nitem               qty     note\n"
                                    + "__________________________________\n";

                            for (int m = 0; m < jsonArray.length(); m++) {
                                JSONObject orderObject = jsonArray.getJSONObject(m);
                                String name = orderObject.getString("ITEMNAME");
                                String qty = String.valueOf(orderObject.getInt("QTY"));
                                if (orderObject.getString("ISUPDATE").equals("1"))
                                    note = "Updated";
                                else
                                    note = "";
                                while (name.length() < 20) {
                                    name += " ";
                                }
                                while (qty.length() < 8) {
                                    qty += " ";
                                }
                                orderString += name
                                        + qty
                                        + note
                                        + "\n";
                            }
//                            orderObject.getString("NOTE")
                            orderString += "\n\n\n";
                            output.println(orderString);
                            output.flush();
                            output.println(new char[]{0x1D, 0x56, 0x41, 0x10});
                            output.flush();
                        } else if (KitchenSettingsModel.LANGUAGE.equals("ar")) {
//
                            LinearLayout linearLayout = linearLayout2;
                            linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                            linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());
                            Bitmap bitmap = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            linearLayout.draw(canvas);
//                            canvas.drawText(orderString, 0, height / 2, tp);
                            printPic = PrintPic.getInstance();
                            printPic.init(bitmap);
                            byte[] bitmapdata = printPic.printDraw();
                            out.write(bitmapdata);
                            out.flush();
                            output.println(new char[]{0x1D, 0x56, 0x41, 0x10});
                            output.flush();

                        } else
                            Toast.makeText(MainActivity.this, "Can't determine language!", Toast.LENGTH_SHORT).show();
//                    output.println(b);

                    }
//                    output.println("27" +'t'+"255");
//                    output.println("27"+'p'+ '0' + "50"+ "200");

                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
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
//                            Log.e("socket ", "is worked");
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

                            Orders orders = new Orders(object.getString("TRINDATE")
                                    , object.getString("CASHNO")
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
                                    , object.getInt("STGNO")
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
        switch (v.getId()) {
            case R.id.orders_list_settings_button:
                if (v.getId() == R.id.orders_list_settings_button) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    passwordField.setText("");

                    checkPassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(passwordField.getText())) {
                                if (passwordField.getText().toString().equals("123456")) {

                                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                                            switch (checkedId) {
                                                case R.id.kitchen_settings_done:
//                                            Log.e("radio", "done");
                                                    ipAddressLinear.setVisibility(View.GONE);
                                                    doneRadioButton.setChecked(true);
                                                    KitchenSettingsModel.STAGE_TYPE = "done";

                                                    break;
                                                case R.id.kitchen_settings_transfer:
//                                            Log.e("radio", "transfer");
                                                    ipAddressLinear.setVisibility(View.VISIBLE);
                                                    transferRadioButton.setChecked(true);
                                                    KitchenSettingsModel.STAGE_TYPE = "transfer";
                                                    break;
                                                case R.id.kitchen_settings_print:
//                                            Log.e("radio", "transfer");
                                                    ipAddressLinear.setVisibility(View.VISIBLE);
                                                    printRadioButton.setChecked(true);
                                                    KitchenSettingsModel.STAGE_TYPE = "print";
                                                    break;
                                            }
                                        }
                                    });

                                    saveSettings.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (!TextUtils.isEmpty(companyNo.getText()) && !TextUtils.isEmpty(companyYear.getText())
                                                    && !TextUtils.isEmpty(posNo.getText()) && !TextUtils.isEmpty(screenNo.getText())
                                                    && !TextUtils.isEmpty(timerDuration.getText()) && !TextUtils.isEmpty(getOrdersURL.getText())
                                                    && !TextUtils.isEmpty(stageNo.getText())) {
                                                if (timerDuration.getText().toString().matches("[0-9]+")) {
                                                    if (KitchenSettingsModel.STAGE_TYPE.equals("transfer") || KitchenSettingsModel.STAGE_TYPE.equals("print")) {
                                                        if (!TextUtils.isEmpty(ipAddressForReceiver.getText().toString())) {
                                                            final Pattern IP_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                                                                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                                                                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                                                                    + "|[1-9][0-9]|[0-9]))");
                                                            Matcher matcher = IP_ADDRESS.matcher(ipAddressForReceiver.getText().toString());//"127.0.0.1"
                                                            if (matcher.matches()) {
//                                                        Log.e("pattern", "true");
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
                                                                KitchenSettingsModel.URL = "" + getOrdersURL.getText().toString();
                                                                KitchenSettingsModel.STAGE_NO = "" + stageNo.getText().toString();
                                                                KitchenSettingsModel.IP_OF_RECEIVER = "" + ipAddressForReceiver.getText().toString();
                                                                KitchenSettingsModel.FILLED = true;
                                                                databaseHandler.addkitchenSettings();
                                                                dialog.dismiss();
                                                                finish();
                                                                startActivity(getIntent());
                                                            } else {
//                                                        Log.e("pattern", "false");
                                                                Toast.makeText(MainActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Please fill empty fields!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else { // done
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
                                                        KitchenSettingsModel.URL = "" + getOrdersURL.getText().toString();
                                                        KitchenSettingsModel.STAGE_NO = "" + stageNo.getText().toString();
                                                        KitchenSettingsModel.IP_OF_RECEIVER = "no address";
                                                        KitchenSettingsModel.FILLED = true;
                                                        databaseHandler.addkitchenSettings();
                                                        dialog.dismiss();
                                                        finish();
                                                        startActivity(getIntent());
                                                    }
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

//                                                            "http://10.0.0.16:8080/WSKitchenScreen/FSAppServiceDLL.dll/UpdateRestKitchenScreen?compno="
//                                                                    + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
//                                                                    + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + filteredOrders.get(i).get(0).getOrderNumber()
//                                                                    + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO
                                                                    String domain = KitchenSettingsModel.URL + "UpdateRestKitchenScreen?compno="
                                                                            + KitchenSettingsModel.COMPANY_NO + "&compyear=" + KitchenSettingsModel.COMPANY_YEAR
                                                                            + "&posno=" + KitchenSettingsModel.POS_NO + "&orderno=" + filteredOrders.get(i).get(0).getOrderNumber()
                                                                            + "&SCREENNO=" + KitchenSettingsModel.SCREEN_NO + "&cashno=" + filteredOrders.get(i).get(0).getCashNumber();

                                                                    Log.e("clean kitchen", "" + domain);

                                                                    presenter.updateOrdersRequest(domain);
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
                break;
            case R.id.main_language_arabic:
                LocaleAppUtils.setLocale(new Locale("ar"));
                LocaleAppUtils.setConfigChange(this);
                setAppLanguage("ar");
                finish();
                startActivity(getIntent());
                break;
            case R.id.main_language_english:
                LocaleAppUtils.setLocale(new Locale("en"));
                LocaleAppUtils.setConfigChange(this);
                setAppLanguage("en");
                finish();
                startActivity(getIntent());
                break;

        }
    }

    public void setAppLanguage(String language) {
        KitchenSettingsModel.LANGUAGE = language;
        SharedPreferences.Editor editor = getSharedPreferences(LANGUAGE_SHARED_PREF, MODE_PRIVATE).edit();
        editor.putString("lang", language);
        editor.apply();
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

