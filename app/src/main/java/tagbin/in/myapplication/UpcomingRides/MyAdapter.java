package tagbin.in.myapplication.UpcomingRides;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tagbin.in.myapplication.R;

/**
 * Created by admin pc on 16-12-2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyviewHolder> {

    private ArrayList<DataItems> infoList = new ArrayList<>();
    private LayoutInflater mInflater;
    Context context;
    DataItems currentItem;
    int pos=0;

    public MyAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context=context;
    }

    public void setData(ArrayList<DataItems> list,Boolean b) {
        this.infoList = list;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }


    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.myrow, parent, false);
        MyviewHolder viewHolder = new MyviewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, final int position) {
         currentItem = infoList.get(position);
        holder.pick.setText(currentItem.getPick());
        holder.time.setText(currentItem.getTime());
        holder.to_loc.setText(currentItem.getTimetostart());

        if (currentItem.getStatus().equals("pending")){
            holder.cab_no.setText("Pending");
            holder.icon.setImageResource(R.drawable.pending);
            holder.colbar.setBackgroundColor(Color.parseColor("#FFFFEE00"));
        }else if (currentItem.getStatus().equals("accept")){
            holder.cab_no.setText("Ride Accepted");
            holder.icon.setImageResource(R.drawable.success);
            holder.colbar.setBackgroundColor(Color.parseColor("#FF36FA00"));
        }else if (currentItem.getStatus().equals("Trip started")){
            holder.cab_no.setText("Trip started");
            holder.icon.setImageResource(R.drawable.tripaccepted);
            holder.colbar.setBackgroundColor(Color.parseColor("#FF5722"));
        }
    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }
    public String getCount() {
        String count= Integer.toString(infoList.size());
        return count;
    }

    static class MyviewHolder extends RecyclerView.ViewHolder {
        TextView cab_no;
        TextView time;
        TextView to_loc;
        TextView pick;
        ImageView icon;
        View colbar;


        public MyviewHolder(View itemView) {
            super(itemView);
            cab_no = (TextView) itemView.findViewById(R.id.mcab_no);
            time = (TextView) itemView.findViewById(R.id.mtime);
            to_loc = (TextView) itemView.findViewById(R.id.mto_location);
            pick = (TextView) itemView.findViewById(R.id.pick);
            icon= (ImageView) itemView.findViewById(R.id.icon);
            colbar=  itemView.findViewById(R.id.colbar);

        }
    }
}
