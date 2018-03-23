package doaing.order.device;

import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import doaing.order.R;

public class PortConfigurationActivity extends Activity {
	private static final String DEBUG_TAG = "PortConfigurationActivity";

	private RadioButton rbUSB, rbBluetooth, rbEhternet;
	private TextView       tvPortInfo;
	private Button         btCancel;
	private LinearLayout   llEthernet;
	private PortParameters mPortParam;
	private EditText       etIpAddress, etPortNum;
	private RadioGroup rgPort;
	// Return Intent extra

	public static       String EXTRA_DEVICE_ADDRESS   = "device_address";
	public static final int    REQUEST_ENABLE_BT      = 2;
	public static final int    REQUEST_CONNECT_DEVICE = 3;
	public static final int    REQUEST_USB_DEVICE     = 4;

	// Debugging
	public static LinearLayout deviceNamelinearLayout;
	// Member fields
	private ListView lvPairedDevice = null, lvNewDevice = null;
	private TextView tvPairedDevice = null,tvUsb, tvNewDevice = null;
	private LinearLayout linearLayout,dialog_linearLayout;
	private Button btDeviceScan = null;
	private BluetoothAdapter mBluetoothAdapter;
	//private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayList<String> mPairedDevicesArrayList;
	//private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private ArrayList<String> mNewDevicesArrayList;
	private int mPairedPos = -1,mNewPos = -1;
	private ArrayAdapter mPairedDevicesAdapter;
	private mNewArrayAdapter mNewDevicesAdapter;
	private String address="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.dialog_port_configuration);
		mPortParam = new PortParameters();
		initView();
		bluetoothInitView();

	}

	private void bluetoothInitView(){
		// TextView �����?
		tvPairedDevice = (TextView)findViewById(R.id.tvPairedDevices);
		//ListView �����?
		lvPairedDevice = (ListView)findViewById(R.id.lvPairedDevices);
		// TextView �µ�
		tvNewDevice = (TextView)findViewById(R.id.tvNewDevices);
		// ListView �µ�
		lvNewDevice = (ListView)findViewById(R.id.lvNewDevices);
		// Button ɨ���豸
		btDeviceScan = (Button)findViewById(R.id.btBluetoothScan);
		btDeviceScan.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setVisibility(View.GONE);
				discoveryDevice();
			}
		});
		getDeviceList();
	}

	private void initView() {
		llEthernet =  findViewById(R.id.llEthernet);
		tvPortInfo =  findViewById(R.id.tvPortInfo);
		btCancel = findViewById(R.id.btCancel);
		rbUSB =  findViewById(R.id.rbUsb);
		tvUsb = findViewById(R.id.tvUsb);
		rbBluetooth =  findViewById(R.id.rbBluetooth);
		rbEhternet =  findViewById(R.id.rbEthernet);
		etIpAddress =  findViewById(R.id.etIpAddress);
		etPortNum =  findViewById(R.id.etPortNumber);
		rgPort = findViewById(R.id.rgPort);
		dialog_linearLayout = findViewById(R.id.dialog_linearLayout);
		linearLayout = findViewById(R.id.dialog_bluetooth_id);
		rbUSB.setOnClickListener(new USBRaidoOnClickListener());
		rbBluetooth.setOnClickListener(new BluetoothRaidoOnClickListener());
		rbEhternet.setOnClickListener(new EthernetRaidoOnClickListener());
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Ethernet();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mFindBlueToothReceiver);
	}

	private void messageBox(String err) {
		Toast.makeText(getApplicationContext(),
				err, Toast.LENGTH_SHORT).show();
	}

	class USBRaidoOnClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			//TODO Auto-generated method stub
			dialog_linearLayout.setVisibility(View.GONE);
			llEthernet.setVisibility(View.GONE);
			tvPortInfo.setVisibility(View.GONE);
			linearLayout.setVisibility(View.GONE);
			tvUsb.setVisibility(View.VISIBLE);
			mPortParam.setPortType(PortParameters.USB);
//			getUsbDevice();
		}
	}

	//默认为蓝牙打印机
	private void RbBluetooth(){
		dialog_linearLayout.setVisibility(View.VISIBLE);
		linearLayout.setVisibility(View.VISIBLE);
		llEthernet.setVisibility(View.GONE);
		tvPortInfo.setVisibility(View.GONE);
		tvUsb.setVisibility(View.GONE);
		mPortParam.setPortType(PortParameters.BLUETOOTH);
		getBluetoothDevice();
	}

	class BluetoothRaidoOnClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			RbBluetooth();
		}
	}


	private void Ethernet(){
		tvPortInfo.setVisibility(View.GONE);
		llEthernet.setVisibility(View.VISIBLE);
		linearLayout.setVisibility(View.GONE);
		tvUsb.setVisibility(View.GONE);
		dialog_linearLayout.setVisibility(View.VISIBLE);
		mPortParam.setPortType(PortParameters.ETHERNET);
	}
	class EthernetRaidoOnClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Ethernet();
		}
	}

	protected void getUsbDevice() {
		Intent intent = new Intent(PortConfigurationActivity.this,
				UsbDeviceList.class);
		startActivityForResult(intent, REQUEST_USB_DEVICE);
	}

	public void getBluetoothDevice() {
		// Get local Bluetooth adapter
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		// If the adapter is null, then Bluetooth is not supported
		if (bluetoothAdapter == null) {
			messageBox("Bluetooth is not supported by the device");
		} else {
			// If BT is not on, request that it be enabled.
			// setupChat() will then be called during onActivityResult
			if (!bluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent,
						REQUEST_ENABLE_BT);
			} else {
//				Intent intent = new Intent(PortConfigurationActivity.this,
//						BluetoothDeviceList.class);
//				startActivityForResult(intent,
//						REQUEST_CONNECT_DEVICE);
				linearLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//    Log.d(DEBUG_TAG, "requestCode" + requestCode + "=>" + "resultCode"
//        + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				// bluetooth is opened
				// select bluetooth device fome list
//				Intent intent = new Intent(PortConfigurationActivity.this,
//						BluetoothDeviceList.class);
//				startActivityForResult(intent,
//						REQUEST_CONNECT_DEVICE);
				linearLayout.setVisibility(View.VISIBLE);
			} else {
				// bluetooth is not open
				Toast.makeText(this, R.string.bluetooth_is_not_enabled,
						Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == REQUEST_CONNECT_DEVICE) {
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						EXTRA_DEVICE_ADDRESS);
				// fill in some parameters
				tvPortInfo.setVisibility(View.VISIBLE);
				tvPortInfo.setText(getString(R.string.bluetooth_address) + address);
				dialog_linearLayout.setVisibility(View.VISIBLE);
				mPortParam.setBluetoothAddr(address);
			}
		} else if (requestCode == REQUEST_USB_DEVICE) {
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						EXTRA_DEVICE_ADDRESS);
				// fill in some parameters
				tvPortInfo.setVisibility(View.VISIBLE);
				tvPortInfo.setText(getString(R.string.usb_address) + address);
				dialog_linearLayout.setVisibility(View.VISIBLE);
				mPortParam.setUsbDeviceName(address);
			}
		}
	}

	public void okButtonClicked(View view) {
		Intent intent = new Intent(this, PrinterConnectDialog.class);
		Bundle bundle = new Bundle();

		String ipAddress = etIpAddress.getText().toString();
		String portNum = etPortNum.getText().toString();
		if(PortParameters.ETHERNET == mPortParam.getPortType() ){
			mPortParam.setIpAddr(ipAddress);
			mPortParam.setPortNumber(Integer.valueOf(portNum));
		}
		bundle.putInt(GpPrintService.PORT_TYPE, mPortParam.getPortType());
		bundle.putString(GpPrintService.IP_ADDR, mPortParam.getIpAddr());
		bundle.putInt(GpPrintService.PORT_NUMBER, mPortParam.getPortNumber());
		bundle.putString(GpPrintService.BLUETOOT_ADDR, mPortParam.getBluetoothAddr());
		bundle.putString(GpPrintService.USB_DEVICE_NAME, mPortParam.getUsbDeviceName());
		intent.putExtras(bundle);
		this.setResult(Activity.RESULT_OK, intent);
		this.finish();
	}

	protected void getDeviceList() {
		mPairedDevicesArrayList = new ArrayList<>();
		mNewDevicesArrayList = new ArrayList<>();
		mNewDevicesAdapter = new mNewArrayAdapter();
		mPairedDevicesAdapter = new ArrayAdapter();
		// Initialize array adapters. One for already paired devices and
		// one for newly discovered devices
//		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
//				R.layout.bluetooth_device_name_item);
//		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
//				R.layout.bluetooth_device_name_item);
		mPairedDevicesAdapter.setList(mPairedDevicesArrayList);
		lvPairedDevice.setAdapter(mPairedDevicesAdapter);
		lvPairedDevice.setOnItemClickListener(mDeviceClickListener);
		mNewDevicesAdapter.setList(mNewDevicesArrayList);
		lvNewDevice.setAdapter(mNewDevicesAdapter);
		lvNewDevice.setOnItemClickListener(mNewDeviceClickListener);
		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mFindBlueToothReceiver, filter);
		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mFindBlueToothReceiver, filter);
		// Get the local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			tvPairedDevice.setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
//				mPairedDevicesArrayAdapter.add(device.getName() + "    "
//						+ device.getAddress());
				mPairedDevicesArrayList.add(device.getName() + "    "
						+ device.getAddress());
			}
		} else {
			String noDevices = getResources().getText(R.string.none_paired)
					.toString();
			//mPairedDevicesArrayAdapter.add(noDevices);
			mPairedDevicesArrayList.add(noDevices);
		}
	}

	// changes the title when discovery is finished
	private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//					mNewDevicesArrayAdapter.add(device.getName() + "    "
//							+ device.getAddress());
					mNewDevicesArrayList.add(device.getName() + "    "
							+ device.getAddress());
					Log.e("Port",""+mNewDevicesArrayList.size());
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_bluetooth_device);
				Log.i("tag", "finish discovery" +mNewDevicesArrayList.size());
				if (mNewDevicesArrayList.size() == 0) {
					String noDevices = getResources().getText(
							R.string.none_bluetooth_device_found).toString();
					//mNewDevicesArrayAdapter.add(noDevices);
					mNewDevicesArrayList.add(noDevices);
				}
			}
			mNewDevicesAdapter.notifyDataSetChanged();
		}
	};
	private void discoveryDevice() {
		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scaning);
		// Turn on sub-title for new devices
		tvNewDevice.setVisibility(View.VISIBLE);

		lvNewDevice.setVisibility(View.VISIBLE);
		// If we're already discovering, stop it
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
		// Request discover from BluetoothAdapter
		mBluetoothAdapter.startDiscovery();
	}

	// The on-click listener for all devices in the ListViews
	private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect
			mBluetoothAdapter.cancelDiscovery();
			// Get the device MAC address, which is the last 17 chars in the View
			String info = ((TextView) v).getText().toString();
			String noDevices = getResources().getText(R.string.none_paired).toString();
			String noNewDevice = getResources().getText(R.string.none_bluetooth_device_found).toString();
			Log.i("tag", info);
			mPairedDevicesAdapter.setPos(arg2);
			mNewPos = -1;
			mNewDevicesAdapter.notifyDataSetChanged();
			mPairedDevicesAdapter.notifyDataSetChanged();
			if (!info.equals(noDevices) && ! info.equals(noNewDevice)) {
				address = info.substring(info.length() - 17);
				// Create the result Intent and include the MAC address
//				Intent intent = new Intent();
//				intent.putExtra(PortConfigurationActivity.EXTRA_DEVICE_ADDRESS, address);
//				// Set result and finish this Activity
//				setResult(Activity.RESULT_OK, intent);
//				finish();
				if (!address.equals("")){
					mPortParam.setBluetoothAddr(address);
					Log.e("Port","0-0---"+address);
				}
			}
		}
	};

	private AdapterView.OnItemClickListener mNewDeviceClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect
			mBluetoothAdapter.cancelDiscovery();
			// Get the device MAC address, which is the last 17 chars in the View
			String info = ((TextView) v).getText().toString();
			String noDevices = getResources().getText(R.string.none_paired).toString();
			String noNewDevice = getResources().getText(R.string.none_bluetooth_device_found).toString();
			Log.i("tag", info);
			mNewPos = arg2;
			mPairedPos = -1;
			mPairedDevicesAdapter.notifyDataSetChanged();
			mNewDevicesAdapter.notifyDataSetChanged();
			if (!info.equals(noDevices) && ! info.equals(noNewDevice)) {
				address = info.substring(info.length() - 17);
				// Create the result Intent and include the MAC address
//				Intent intent = new Intent();
//				intent.putExtra(PortConfigurationActivity.EXTRA_DEVICE_ADDRESS, address);
//				// Set result and finish this Activity
//				setResult(Activity.RESULT_OK, intent);
//				finish();
				if (!address.equals("")){
					mPortParam.setBluetoothAddr(address);
					Log.e("Port","0-0---"+address);
				}
			}
		}
	};


	class ArrayAdapter extends BaseAdapter{

		private List<String> list;
		private void setList(List<String> list)
		{
			this.list = list;
		}
		private ViewHolder viewHolder;
		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null){
				convertView = LayoutInflater.from(PortConfigurationActivity.this).inflate(R.layout.bluetooth_device_name_item,null);
				viewHolder = new ViewHolder();
				viewHolder.textView = convertView.findViewById(R.id.tv_bluetooth);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (mPairedPos == position){
				viewHolder.textView.setBackgroundColor(getResources().getColor(R.color.cyan));
			}else{
				viewHolder.textView.setBackgroundColor(getResources().getColor(R.color.white));
			}
			viewHolder.textView.setText(list.get(position));
			return convertView;
		}

		public void setPos(int pos){
			mPairedPos=pos;
			Log.e("Port",""+mPairedPos+"---"+pos);

		}

		class ViewHolder{
			TextView textView;
		}
	}

	class mNewArrayAdapter extends BaseAdapter{

		private List<String> list;
		private void setList(List<String> list)
		{
			this.list = list;
		}
		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null){
				convertView = LayoutInflater.from(PortConfigurationActivity.this).inflate(R.layout.bluetooth_device_name_item,null);
				viewHolder = new ViewHolder();
				viewHolder.textView = convertView.findViewById(R.id.tv_bluetooth);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (mNewPos == position){
				viewHolder.textView.setBackgroundColor(getResources().getColor(R.color.cyan));
			}else{
				viewHolder.textView.setBackgroundColor(getResources().getColor(R.color.white));
			}
			viewHolder.textView.setText(list.get(position));
			return convertView;
		}

		class ViewHolder{
			TextView textView;
		}
	}
}

