package tagbin.in.myapplication.Volley;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tagbin.in.myapplication.NotifyService;

/**
 * Created by admin pc on 01-12-2015.
 */
public class MakeRequest {
    Context context;
    String currenturl;


    public MakeRequest(Context mycon,String url){
        context = mycon;
       currenturl= url;

    }

    public void makeRequest(final String name,final String num,final String cabno,final String pass,final String conPass) {


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, currenturl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {



                        Log.v("writtem:%n %s", response.toString());




                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("error:%n %s", error.toString());


                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("username", num);
                params.put("cab_no", cabno);
                params.put("password", pass);
                params.put("rpassword", conPass);

                return params;
            }


        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
//        requestQueue.cancelAll(tag);

    }

}

