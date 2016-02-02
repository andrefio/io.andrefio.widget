package io.andref.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.andref.example.models.President;
import io.andref.widget.AnimatedCheckBox;

import java.util.ArrayList;

public class CircleViewActivity extends AppCompatActivity
{
    private static final String DEMOCRATIC = "Democratic";
    private static final String REPUBLICAN = "Republican";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        Adapter adapter = new Adapter();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        ArrayList<President> presidents;

        public Adapter()
        {
            presidents = new ArrayList<>();
            presidents.add(new President("Barack Obama", DEMOCRATIC, R.drawable.img_44));
            presidents.add(new President("George W. Bush", REPUBLICAN, R.drawable.img_43));
            presidents.add(new President("Bill Clinton", DEMOCRATIC, R.drawable.img_42));
            presidents.add(new President("George H. W. Bush", REPUBLICAN, R.drawable.img_41));
            presidents.add(new President("Ronald Reagan", REPUBLICAN, R.drawable.img_40));
            presidents.add(new President("Jimmy Carter", DEMOCRATIC, R.drawable.img_39));
            presidents.add(new President("Gerald Ford", REPUBLICAN, R.drawable.img_38));
            presidents.add(new President("Richard Nixon", REPUBLICAN, R.drawable.img_37));
            presidents.add(new President("Lyndon B. Johnson", DEMOCRATIC, R.drawable.img_36));
            presidents.add(new President("John F. Kennedy", DEMOCRATIC, R.drawable.img_35));
        }


        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_list_item_2_lines_with_avatar, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position)
        {
            if (presidents.size() > position)
            {
                President president = presidents.get(position);
                if (president != null)
                {
                    holder.animatedCheckBox.setImageResource(president.getResourceId());
                    holder.textView1.setText(president.getLine1());
                    holder.textView2.setText(president.getLine2());
                }
            }
        }

        @Override
        public int getItemCount()
        {
            return presidents.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            AnimatedCheckBox animatedCheckBox;
            TextView textView1;
            TextView textView2;

            public ViewHolder(View itemView)
            {
                super(itemView);

                animatedCheckBox = (AnimatedCheckBox) itemView.findViewById(R.id.animated_check_box);
                textView1 = (TextView) itemView.findViewById(R.id.text_view_1);
                textView2 = (TextView) itemView.findViewById(R.id.text_view_2);
            }
        }
    }
}