package com.example.bwie.zhoukao20170917;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.bwie.zhoukao20170917.Bean.Data;
import com.example.bwie.zhoukao20170917.adapter.RecyclerAdapter;
import com.example.bwie.zhoukao20170917.utils.ItemDecration;
import com.example.bwie.zhoukao20170917.utils.OkHttpUtils;
import com.example.bwie.zhoukao20170917.utils.OnItemonclicklinear;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private List<Data.DataBean> list = new ArrayList<>();
    private int page = 1;
    private String url = "http://www.yulin520.com/a2a/impressApi/news/mergeList?sign=C7548DE604BCB8A17592EFB9006F9265&pageSize=20&gender=2&ts=1871746850&page=";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //初始化控件
        initView();
        
        //加载源数据
        loadData();
    }

    /**
     * 加载源数据
     */
    private void loadData() {

        //通过类名直接调用静态方法获取单例对象再调用getBeanOfOK()方法传入参数通过接口回调获取数据
        OkHttpUtils.getInstance().getBeanOfOk(this, url + page, Data.class,
                new OkHttpUtils.CallBack<Data>() {
                    @Override
                    public void getData(Data testBean) {

                        if (testBean != null) {
                            //如果不为空用本地list接收
                            list.addAll(testBean.getData());
                            //数据一旦回调成功初始化数据源和适配器
                            initAdapter();
                        }
                    }
                });
    }

    private void initAdapter() {

        adapter = new RecyclerAdapter(list,this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemonclicklinear(new OnItemonclicklinear() {
            @Override
            public void onClickListener(View v, int position) {

                Data.DataBean bean = list.get(position);
                Toast.makeText(MainActivity.this, bean.getUserName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        //线性布局管理及设置
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemDecration(this));
        //加载更多的数据，上拉刷新，下拉加载
        initData();
    }

    /**
     * 加载更多的数据，上拉刷新，下拉加载
     */
    private void initData() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page ++;
                loadData();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition==list.size()-1){
                    page++;
                    loadData();
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }
}
