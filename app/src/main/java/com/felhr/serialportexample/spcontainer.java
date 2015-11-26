package com.felhr.serialportexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class spcontainer extends Activity {
    private Map<String,String> lstFields;
    private static Groups _groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spcontainer);
        _groups = new Groups(Groups.HDWType.UU1, 12);
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
}
