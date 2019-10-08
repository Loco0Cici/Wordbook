package com.example.sunduoduo.words;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main2Activity extends Activity{
    private EditText searchText;
    private Button btn_search;
    private TextView searchresult;
    private String YouDaoUrl="";

    private String YouDaoBaseUrl="http://fanyi.youdao.com/openapi.do";
    private String YouDaoKeyFrom="haobaoshui";
    private String YouDaokey="1650542691";
    private String YouDaoType="data";
    private String YouDaoDoctype="json";
    private String YouDaoVersion="1.1";



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        searchText=(EditText)findViewById(R.id.searchcontent);
        btn_search=(Button)findViewById(R.id.btn_search);
        searchresult=(TextView)findViewById(R.id.searchresult);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String YouDaoSearchContent=searchText.getText().toString().trim();
                if(YouDaoSearchContent.equals("")){
                    Toast.makeText(Main2Activity.this, "请输入要查询的内容！", Toast.LENGTH_LONG).show();
                }else{

                    YouDaoUrl=YouDaoBaseUrl+"?keyfrom="+YouDaoKeyFrom+"&key="+YouDaokey+"&type="+YouDaoType+
                            "&doctype="+YouDaoDoctype+"&version="+YouDaoVersion+"&q="+YouDaoSearchContent;
                    try{

                        //生成一个新线程
                        Thread t = new NetworkThread();
                        //让这一个线程开始运行
                        t.start();

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


    }



    class NetworkThread extends Thread{
        public void run(){

            Looper.prepare();
            Log.v("新线程", YouDaoUrl);

            try {
                Analyze analyze = new Analyze();
                analyze.AnalyzingOfJson(YouDaoUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class Analyze extends Activity {
        public  void AnalyzingOfJson(String youDaoUrl) throws Exception {
            // TODO Auto-generated method stub

            HttpGet httpGet= new HttpGet(youDaoUrl);
            HttpResponse httpResponse= new DefaultHttpClient().execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode()==200){
                // String result= EntityUtils.toString(httpResponse.getEntity());
                BufferedReader input1= new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent())) ;
                StringBuilder sb= new StringBuilder();

                for(String s=input1.readLine();s!=null;s=input1.readLine()){
                    sb.append(s);
                }
                String result=sb.toString();
                System.out.println("result="+result);
                JSONArray jsonArray= new JSONArray("["+result+"]");
                // String message=null;
                StringBuilder message=new StringBuilder();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject= jsonArray.getJSONObject(i);
                    if(jsonObject!=null){
                        String errorCode=jsonObject.getString("errorCode");

                        if(errorCode.equals("0")){
                            String query= jsonObject.getString("query");
                            //message=query;
                            message.append(query);
                            String translation=jsonObject.getString("translation");
                            // message+="\t"+translation;
                            message.append("\t"+translation);
                            //有道词典-基本词典
                            if(jsonObject.has("basic")){
                                JSONObject basic=jsonObject.getJSONObject("basic");
                                if(basic.has("phonetic")){
                                    String phonetic=basic.getString("phonetic");
                                    // message+="\n\t"+phonetic;
                                    message.append("\n\t音标：["+phonetic+"]");
                                }
                                if(basic.has("explains")){
                                    String explains=basic.getString("explains");
                                    //message+="\n\t"+explains;
                                    message.append("\n\t"+explains);
                                }
                            }
                            if(jsonObject.has("web")){
                                String web=jsonObject.getString("web");
                                JSONArray webstring= new JSONArray("["+web+"]");
                                // message+="\n网络释义：";
                                message.append("\n网络释义：");
                                JSONArray webArray= webstring.getJSONArray(0);
                                int count=0;
                                while(!webArray.isNull(count)){
                                    if(webArray.getJSONObject(count).has("key")){
                                        String key=webArray.getJSONObject(count).getString("key");
                                        //message+="\n\t<"+(count+1)+">"+key;
                                        message.append("\n\t<"+(count+1)+">"+key);

                                    }
                                    if(webArray.getJSONObject(count).has("value")){
                                        String value=webArray.getJSONObject(count).getString("value");
                                        //message+="\n\t "+value;
                                        message.append("\n\t "+value);

                                    }
                                    count++;
                                }

                            }


                        }
                        if(errorCode.equals("20")){
                            Toast.makeText(getApplicationContext(), "要翻译的文本过长", Toast.LENGTH_LONG).show();
                        }
                        if(errorCode.equals("30")){
                            Toast.makeText(getApplicationContext(), "无法进行有效的翻译 ", Toast.LENGTH_LONG).show();
                        }
                        if(errorCode.equals("40")){
                            Toast.makeText(getApplicationContext(), "不支持语言类型", Toast.LENGTH_LONG).show();
                        }
                        if(errorCode.equals("50")){
                            Toast.makeText(getApplicationContext(), "无效的Key", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                }
                searchresult.setText(message.toString());

            }
            else{
                Toast.makeText(getApplicationContext(), "提取异常", Toast.LENGTH_LONG).show();
            }
        }
    }


}
