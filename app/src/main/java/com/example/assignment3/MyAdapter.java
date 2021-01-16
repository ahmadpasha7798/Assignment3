package com.example.assignment3;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    int a=0;
    List<Todo_item> items;
    private MyViewHolder.itemClickListener ICL;
    private MyViewHolder.itemLongClickListener ILCL;

    public MyAdapter(List items, MyViewHolder.itemClickListener ICL, MyViewHolder.itemLongClickListener ILCL) {
        this.items = items;
        this.ICL=ICL;
        this.ILCL=ILCL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View  view =layoutInflater.inflate(R.layout.recycler,parent,false);
        //view.setOnClickListener(new myOnClickListener());
        return new MyViewHolder(view,ICL,ILCL );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(items.get(position).Title);
        holder.tvDesc.setText(items.get(position).Description);
        holder.tvStatus.setText(items.get(position).Status);
        if(items.get(position).Status.equals("Pending"))
        {
            holder.cbStatus.setImageResource(R.drawable.pending);
            holder.tvStatus.setTextColor(Color.RED);
        }
        else if(items.get(position).Status.equals("Completed"))
        {
            holder.cbStatus.setImageResource(R.drawable.checklist);
            holder.tvStatus.setTextColor(Color.GREEN);
        }
        else if(items.get(position).Status.equals("Postponed"))
        {
            holder.cbStatus.setImageResource(R.drawable.outline_next_plan_black_36dp);
            holder.tvStatus.setTextColor(Color.BLUE);
        }
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MyViewHolder extends ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView tvTitle;
        TextView tvDesc;
        TextView tvStatus;
        ImageView cbStatus;

        itemLongClickListener ILCL;
        itemClickListener ICL;

        public MyViewHolder(@NonNull View itemView, itemClickListener ICL, itemLongClickListener ILCL) {
            super(itemView);
            this.ICL=ICL;
            this.ILCL=ILCL;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            tvTitle=itemView.findViewById(R.id.Title);
            tvDesc=itemView.findViewById(R.id.Des);
            tvStatus=itemView.findViewById(R.id.Status);
            cbStatus=itemView.findViewById(R.id.Status_image);


        }

        @Override
        public void onClick(View v) {
            ICL.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return ILCL.onItemLongClick(getAdapterPosition());
        }

        public interface itemClickListener{
            void onItemClick(int position);
        }
        public interface  itemLongClickListener{
            boolean onItemLongClick(int position);
        }
    }
    /*class myOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            RecyclerView recyclerView=v.findViewById(R.id.recyclerView);
            RecyclerView.ViewHolder holder=recyclerView.getChildViewHolder(v);
            int position=holder.getAdapterPosition();
            a=(a+1)%3;
            if(a==0)
            {
                items.get(position).Status="Pending";
            }
            else if (a==1) {
                items.get(position).Status="Postponed";
            }
            else if(a==2){
                items.get(position).Status="Completed";
            }
        }
    }*/
}

