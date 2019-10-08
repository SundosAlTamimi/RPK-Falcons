package com.example.restposkitchen;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.restposkitchen.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<List<Orders>> filteredOrders;
    public List<Orders> id;
    private MainActivity mainActivity;

    public OrdersAdapter(Context context, ArrayList<List<Orders>> filteredOrders, MainActivity mainActivity) {
        this.context = context;
        this.filteredOrders = filteredOrders;
        this.mainActivity = mainActivity;
        Log.e("adapter:filtered: ", "" + filteredOrders.size());

    }

    @Override
    public int getCount() {
        return filteredOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredOrders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        Log.e("getView", "DONE");
        convertView = LayoutInflater.from(context).inflate(R.layout.orders_row, null);//parent, false);

        LinearLayout linearLayout = convertView.findViewById(R.id.orders_row_layout_container);
        TableLayout tableLayout = convertView.findViewById(R.id.orderRow_table_container);
        TextView orderNoTextView = convertView.findViewById(R.id.orderRow_order_no);
        TextView tableNoTextView = convertView.findViewById(R.id.orderRow_table_no);
        TextView sectionTextView = convertView.findViewById(R.id.orderRow_section_no);
        ImageView orderTypeImage = convertView.findViewById(R.id.orderRow_orderType_image);
        TextView orderTypeTextView = convertView.findViewById(R.id.orderRow_orderType_text);
        TextView updatedOrder = convertView.findViewById(R.id.orderRow_isUpdated);
        ImageButton deleteOrder = convertView.findViewById(R.id.orderRow_delete_order);

        if (filteredOrders.size() != 0) {
            orderNoTextView.setText("C" + filteredOrders.get(position).get(0).getCashNumber()+ "-"+ filteredOrders.get(position).get(0).getOrderNumber());

            if (filteredOrders.get(position).get(0).getOrderType() == 0) {
                tableNoTextView.setText("---");
                sectionTextView.setText("---");
                orderTypeImage.setImageResource(R.drawable.take_away_hover);
                orderTypeTextView.setText(" Take Away ");
            } else { //1
                tableNoTextView.setText("" + filteredOrders.get(position).get(0).getTableNumber());
                sectionTextView.setText("" + filteredOrders.get(position).get(0).getSection());
                orderTypeImage.setImageResource(R.drawable.dine_in_hover);
                orderTypeTextView.setText(" Dine In ");
            }
        }
//        Log.e("inner size ", "" + filteredOrders.get(position).size());
        updatedOrder.setText("No Update");
        for (int j = 0; j < filteredOrders.get(position).size(); j++) {
            final int jj = j;
//            Log.e("jj ", "" + position + " " + jj);
            TableRow row = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            TextView itemName = new TextView(context);
            itemName.setText("" + filteredOrders.get(position).get(j).getItemName());
//              Log.e("item name", "" + filteredOrders.get(position).get(j).getItemName());
            itemName.setTextColor(ContextCompat.getColor(context, R.color.black));
            itemName.setBackgroundResource(R.color.layer3);
            itemName.setGravity(Gravity.CENTER);
            TableRow.LayoutParams itemNameParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
            itemNameParam.setMargins(0, 2, 0, 2);
            itemName.setLayoutParams(itemNameParam);


            TextView quantity = new TextView(context);
            quantity.setText("" + filteredOrders.get(position).get(j).getQuantity());
            quantity.setTextColor(ContextCompat.getColor(context, R.color.black));
            quantity.setBackgroundResource(R.color.layer3);
            quantity.setGravity(Gravity.CENTER);
            TableRow.LayoutParams quantityParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            quantityParam.setMargins(2, 2, 2, 2);
            quantity.setLayoutParams(quantityParam);
            TextView note = new TextView(context);
//            Log.e("jjjj ", filteredOrders.get(position).get(jj).getNote());

            if (!filteredOrders.get(position).get(j).getNote().isEmpty() && !filteredOrders.get(position).get(j).getNote().equals("")
                    && !filteredOrders.get(position).get(j).getNote().equals("f") && !filteredOrders.get(position).get(j).getNote().equals("m")) {
//                Log.e("pos2 ", "" + pos);
                note.setText("note");
                note.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                note.setTextColor(ContextCompat.getColor(context, R.color.blue_ice));
                note.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.e("pos3 ", "" +( pos--));
//                        Log.e("jj ", "" + jj);
                        Context wrapper = new ContextThemeWrapper(context, R.style.YOURSTYLE);
                        PopupMenu menu = new PopupMenu(wrapper, v);
                        menu.getMenu().add(filteredOrders.get(position).get(jj).getNote());
//                        Log.e("jj ", "" + position +" "+ jj);
                        menu.show();
                    }
                });
            } else {
                note.setText("---");
                note.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
//            note.setText(filteredOrders.get(position).get(j).getNote());
            note.setBackgroundResource(R.color.layer3);
            note.setGravity(Gravity.CENTER);
            TableRow.LayoutParams noteParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            noteParam.setMargins(0, 2, 0, 2);
            note.setLayoutParams(noteParam);

            if (filteredOrders.get(position).get(j).getIsUpdated().equals("1")) {
//                itemName.setTextColor(ContextCompat.getColor(context, R.color.exit));
                itemName.setBackgroundResource(R.color.floor);
                quantity.setBackgroundResource(R.color.floor);
                note.setBackgroundResource(R.color.floor);
                updatedOrder.setText("New Update");
                updatedOrder.setTextColor(ContextCompat.getColor(context, R.color.exit));
            }

            if (filteredOrders.get(position).get(j).getNote().equals("m")
                    || filteredOrders.get(position).get(j).getNote().equals("f")) {
                itemName.setTextColor(ContextCompat.getColor(context, R.color.green));
                quantity.setTextColor(ContextCompat.getColor(context, R.color.green));
                note.setTextColor(ContextCompat.getColor(context, R.color.green));
            }

            row.addView(itemName);
            row.addView(quantity);
            row.addView(note);

            tableLayout.addView(row);
        }

        tableLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // prevent any other scroll from applied
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDrag(data, shadowBuilder, v, 0);
                        mainActivity.showDeleteOrderButton(filteredOrders, position);
                        break;
                    case MotionEvent.ACTION_UP:
                        mainActivity.hideDeleteOrders();
                        break;
                    default:
                        Log.e("State ", "" + event.getAction());
                }
                return false;
            }
        });

        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("delete", "reponsed");
                mainActivity.deleteKitchenOrder(position);
            }
        });

//        notifyDataSetChanged();
        return convertView;
    }


}
