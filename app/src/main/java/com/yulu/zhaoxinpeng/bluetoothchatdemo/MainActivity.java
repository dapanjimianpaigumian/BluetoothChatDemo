package com.yulu.zhaoxinpeng.bluetoothchatdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.R.string.no;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    @BindView(R.id.btn_discover)
    Button mBtnDiscover;
    @BindView(R.id.devicesList)
    ListView mDevicesListView;
    @BindView(R.id.listchat)
    ListView mChatListView;
    @BindView(R.id.edit_text_out)
    EditText mEditTextOut;
    @BindView(R.id.button_send)
    Button mBtnSend;
    private BluetoothAdapter mBluetoothAdapter;
    private Unbinder bind;
    private ArrayAdapter<String> mDeviceAdapter;
    private ArrayAdapter<String> mChatAdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);

        // 1. 获取蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            //如果适配器为空，说明不支持蓝牙
            Toast.makeText(this, "此设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //2. 开启蓝牙，判断是否已经开启
        if (!mBluetoothAdapter.isEnabled()) {
            //如果未打开蓝牙，那么先提示开启蓝牙，然后开始视图的初始化
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
            initView();
        } else {
            //如果已开启蓝牙，那么直接开始视图的舒适化
            initView();
        }

        //动态注册广播接收器
        IntentFilter mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//找到
        registerReceiver(mReceiver, mIntentFilter);
        //mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//扫描结束
        //registerReceiver(mReceiver, mIntentFilter);

    }

    //视图初始化
    private void initView() {
        //创建发现设备列表
        mDeviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDevicesListView.setAdapter(mDeviceAdapter);
        mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 将本地的蓝牙和点击的蓝牙建立连接
            }
        });

        //创建聊天设备列表
        mChatAdater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mChatListView.setAdapter(mChatAdater);
    }


    // 广播接收器：接收搜索到的蓝牙设备
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("action",action);
            // 判断是不是搜索设备的Action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 拿到得到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e("device",device+"");
                mDeviceAdapter.add(device.getName()+"\n"+device.getAddress());
                mDeviceAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 判断：如果当前没有设备，可以提示一下
                if (mDeviceAdapter.getCount()==0){
                    Toast.makeText(context, "设备未找到", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    //开启设备可检测性，并开始搜索其他设备
    @OnClick({R.id.btn_discover})
    public void initChat() {

        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

            //Toast.makeText(this, "开启被检测状态", Toast.LENGTH_SHORT).show();
            // 打开可检测性
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }

        Toast.makeText(this, "开始扫描周围蓝牙设备", Toast.LENGTH_SHORT).show();
        // 开启扫描
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

    }

    @OnClick(R.id.button_send)
    public void onClickview(){
        // TODO 发送消息建立通讯
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_BLUETOOTH_ENABLE:
                initView();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
