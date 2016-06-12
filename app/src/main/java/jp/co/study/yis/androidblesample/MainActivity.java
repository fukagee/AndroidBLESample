package jp.co.study.yis.androidblesample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    final  int REQUEST_BLEPERMISSIONS = 1;
    final  int SDKVER_MARSHMALLOW = 23;
    final int REQUEST_ENABLE_BT=1001;
    private BluetoothAdapter mBluetoothAdapter;

    // パーミンッションチェック
    // @param permission Manifest.permission.*に定義してあるパーミッション
    private void checkBLEPermission()
    {
        // デバイスがBLEに対応していなければトースト表示.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE未対応のデバイスです", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Android6.0以降なら権限確認.
        if(Build.VERSION.SDK_INT >= SDKVER_MARSHMALLOW)
        {
            // 権限が許可されていない場合はリクエスト.
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                // リクエストの結果はonRequestPermissionResultに帰ってくる
                requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_BLEPERMISSIONS);
            }
        }
        // 権限が許可されていた場合 BlueTooth関係のオブジェクトのセットアップ
        SettingUpBlueTooth(false);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // 権限リクエストの結果を取得する.
        if (requestCode == REQUEST_BLEPERMISSIONS) {
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "BlueToothのパーミッションが必要です。",
                        Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            // 権限がある場合はBlueTooth関係のオブジェクトをセットアップ
            SettingUpBlueTooth(false);
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




    private void SettingUpBlueTooth(boolean isRetry)
    {
        /*
         * BlueToothAdapterの取得
         */
        final BluetoothManager bluetoothManager =
            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            if(!isRetry) {
                // 最初の呼び出してBlueToothAdapterが取得できなければBlueToothの有効化を促す。
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else {
                // BlueToothAdapterが取得できなければ終了
                Toast.makeText(MainActivity.this, "BlueToothを有効にしてください。", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    }


    // startActivityForResult で起動させたアクティビティが
    // finish() により破棄されたときにコールされる
    // requestCode : startActivityForResult の第二引数で指定した値が渡される
    // resultCode : 起動先のActivity.setResult の第一引数が渡される
    // Intent data : 起動先Activityから送られてくる Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // Bluetoothが有効にされたら再度mBluetoothAdapterの取得を試みる。
                    SettingUpBlueTooth(true);
                } else if (resultCode == RESULT_CANCELED) {
                    // 権限が付与されなかった。
                    finish();
                }
                // BLE Toothのセットアップが完了
                BlueToothSettingUpSucceeded();
                break;

            default:
                break;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] permiSsions = {Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN};


        Button scanButton = (Button)findViewById(R.id.ScanButton);
        assert scanButton != null; // NullExceptionのコード分析警告の対策。
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(v == null)
                {
                    return;
                }
            }
        });

        // BLEのパーミッションがあるかをチェックし、なければアプリを終了する。
        checkBLEPermission();

    }

    // Bluetoothの
    private void BlueToothSettingUpSucceeded()
    {
        Button button = (Button)findViewById(R.id.ScanButton);
        if(button == null){
            // BlueToothAdapterが取得できなければ終了
            Toast.makeText(MainActivity.this, "内部エラー。", Toast.LENGTH_SHORT).show();
            finish();
        }

        button.setEnabled(true);
    }


}
