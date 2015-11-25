package com.felhr.serialportexample;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
	private UsbService usbService;

	private TextView display;
	private EditText editText;
	private Button sendButton;
	private MyHandler mHandler;
	private static Groups _groups;
	private Spinner spr;
    private Map<String,String> lstFields;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MyHandler(this);

        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(this);
		display.setMovementMethod(new android.text.method.ScrollingMovementMethod());
		_groups = new Groups(Groups.HDWType.UU1, 12);
		spr = (Spinner)findViewById(R.id.protocol_spinner);
		spr.setSelection(1);
    }

    @Override
	public void onResume()
	{
    	super.onResume();
    	setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
	}

    @Override
    public void onPause()
    {
    	super.onPause();
    	unregisterReceiver(mUsbReceiver);
    	unbindService(usbConnection);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_home)
        {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
            return true;
        }
		else if(id == R.id.action_sp1) {
			GenerateUI("SP1");
			//Toast.makeText(this, "Setting", Toast.LENGTH_LONG).show();
			return true;
		}
        return super.onOptionsItemSelected(item);
    }

	public void GenerateUI(String sp)
	{
			//_groups.p
        lstFields = new HashMap<String, String>() {

		};

		setContentView(R.layout.spcontainer);

		IGroup grp =  _groups.GroupsList.get(sp);
		Field[] Fields = grp.getClass().getFields();
		TableLayout tl = (TableLayout) findViewById(R.id.tblSP);
		Object Val = null;
		for (Field field : Fields) {
			if(!field.isAnnotationPresent(Groups.GroupsAttributes.class)) {


				TableRow tr = new TableRow(this);
				tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

				tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
				TextView tv = new TextView(this);
				tv.setText(field.getName());
				EditText et = new EditText(this);
                et.setOnFocusChangeListener(myEditTextFocus);
				et.setTag(field.getName());
				field.setAccessible(true);
				if (field.isAccessible()) {
					try {
						Val = field.get(grp);
						if(Val != null) {
							lstFields.put(field.getName(), Val.toString());
						}
						else{
							lstFields.put(field.getName(), "");
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

				}

				tr.addView(tv);
                String result = "";
                if (field.getType() == String.class) {
				} else if (field.getType() == int.class) {
					int a = (int) Val;
					 result = String.valueOf(a);
				} else if (field.getType() == double.class) {
					double a = (double) Val;
					 result = String.valueOf(a);

				} else if (field.getType() == float.class) {
					float a = (float) Val;
					 result = String.valueOf(a);
				}
			 	else if (field.getType() == long.class) {
					long a = (long) Val;
					result = String.valueOf(a);
				}
                else if (field.getType() == short.class) {
                    short a = (short) Val;
                     result = String.valueOf(a);

                }
				else if (field.getType() == Enum.class) {

				}

                et.setText(result);
                tr.addView(et);
			}

		}

	}


    private View.OnFocusChangeListener myEditTextFocus =  new View.OnFocusChangeListener() {
        public void onFocusChange(View view, boolean gainFocus) {
            //onFocus
            if (!gainFocus) {

				//Log.d("aaa111a", "aaa");

               IGroup grp =  _groups.GroupsList.get("SP1");

				EditText editText= (EditText) view;
				//Log.d("aaaa", editText.getTag().toString());
				//Log.d("bbbb", editText.getText().toString());

				lstFields.put(editText.getTag().toString(), editText.getText().toString());
				BuildMessage(grp);
            }
        };
    };

    private void BuildMessage(IGroup grp)
    {
	//	Log.d("14789","rttrt");
		Field[] Fields = grp.getClass().getFields();
		//Log.d("aa","kaka");
		for (String key : lstFields.keySet()) {
			String val =	lstFields.get(key);
			for (Field field : Fields) {

				if(field.getName() == key)
				{
					if (field.getType() == String.class) {
						try {
							field.set(grp, val);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else if (field.getType() == int.class) {
						int a = Integer.parseInt(val) ;
						try {
							field.set(grp, a);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else if (field.getType() == double.class) {
						double a = Double.parseDouble(val) ;
						try {
							field.set(grp, a);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}

					} else if (field.getType() == float.class) {
						float a = Float.parseFloat(val);
						try {
							field.set(grp, a);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					else if (field.getType() == long.class) {
						long a = Long.parseLong(val);
						try {
							field.set(grp, a);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					else if (field.getType() == short.class) {
						short a = Short.parseShort(val);
						try {
							field.set(grp, a);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}

					}
					else if (field.getType() == Enum.class) {

					}


					//Log.d(field.getName(),val);
					/*try {

						field.set(grp,val);
					} catch (IllegalAccessException e) {
						Log.d("123456",e.getMessage());
						e.printStackTrace();
					}*/
				}
			}
			//Log.d("bbbbbbbb", grp.toString());
			// do what you wish with key and value here
		}

		EditText et = (EditText) findViewById(R.id.editText111);
		et.setText(grp.toString());
		/*for(String tmp:lstFields)
		{
			for (Field field : Fields) {
				if(field.getName() == tmp){
					//field.set(grp, tmp)
				}
			}
		}*/
    }

	@Override
	public void onClick(View v)
	{
		if(!editText.getText().toString().equals(""))
		{
			String data = editText.getText().toString();
			if(usbService != null) // if UsbService was correctly binded, Send data
				usbService.write(data.getBytes());
		}
	}

	private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras)
	{
		if(UsbService.SERVICE_CONNECTED == false)
		{
			Intent startService = new Intent(this, service);
			if(extras != null && !extras.isEmpty())
			{
				Set<String> keys = extras.keySet();
				for(String key: keys)
				{
					String extra = extras.getString(key);
					startService.putExtra(key, extra);
				}
			}
			startService(startService);
		}
		Intent bindingIntent = new Intent(this, service);
		bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	private void setFilters()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
		filter.addAction(UsbService.ACTION_NO_USB);
		filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
		filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
		filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
		registerReceiver(mUsbReceiver, filter);
	}

	/*
	 * This handler will be passed to UsbService. Dara received from serial port is displayed through this handler
	 */
	private static class MyHandler extends Handler
	{
		private final WeakReference<MainActivity> mActivity;

		public MyHandler(MainActivity activity)
		{
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case UsbService.MESSAGE_FROM_SERIAL_PORT:
				String data = (String) msg.obj;
				mActivity.get().display.append(data);
				break;
			}
		}
	}

	/*
	 * Notifications from UsbService will be received here.
	 */
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context arg0, Intent arg1)
		{
			if(arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_GRANTED)) // USB PERMISSION GRANTED
			{
				Toast.makeText(arg0, "USB Ready 1", Toast.LENGTH_SHORT).show();
			}else if(arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)) // USB PERMISSION NOT GRANTED
			{
				Toast.makeText(arg0, "USB Permission not granted", Toast.LENGTH_SHORT).show();
			}else if(arg1.getAction().equals(UsbService.ACTION_NO_USB)) // NO USB CONNECTED
			{
				Toast.makeText(arg0, "No USB connected", Toast.LENGTH_SHORT).show();
			}else if(arg1.getAction().equals(UsbService.ACTION_USB_DISCONNECTED)) // USB DISCONNECTED
			{
				Toast.makeText(arg0, "USB disconnected", Toast.LENGTH_SHORT).show();
			}else if(arg1.getAction().equals(UsbService.ACTION_USB_NOT_SUPPORTED)) // USB NOT SUPPORTED
			{
				Toast.makeText(arg0, "USB device not supported", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private final ServiceConnection usbConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1)
		{
			usbService = ((UsbService.UsbBinder) arg1).getService();
			usbService.setHandler(mHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			usbService = null;
		}
	};

}