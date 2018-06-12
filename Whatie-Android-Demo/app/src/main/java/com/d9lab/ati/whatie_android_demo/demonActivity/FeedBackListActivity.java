package com.d9lab.ati.whatie_android_demo.demonActivity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d9lab.ati.whatie_android_demo.R;
import com.d9lab.ati.whatie_android_demo.application.BaseRecyclerAdapter;
import com.d9lab.ati.whatie_android_demo.application.RecyclerViewHolder;
import com.d9lab.ati.whatiesdk.bean.BaseListResponse;
import com.d9lab.ati.whatiesdk.bean.FeedBack;
import com.d9lab.ati.whatiesdk.callback.FeedbacksCallback;
import com.d9lab.ati.whatiesdk.ehome.EHomeInterface;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by 神火 on 2018/6/12.
 */

public class FeedBackListActivity extends BaseActivity {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_title_left)
    ImageView ivTitleLeft;
    @Bind(R.id.ll_title_left)
    LinearLayout llTitleLeft;
    @Bind(R.id.iv_title_right)
    ImageView ivTitleRight;
    @Bind(R.id.tv_title_right)
    TextView tvTitleRight;
    @Bind(R.id.ll_title_right)
    LinearLayout llTitleRight;
    @Bind(R.id.rv_feedback_list)
    RecyclerView rvFeedbackList;
    @Bind(R.id.content)
    FrameLayout flContent;

    private BaseRecyclerAdapter<FeedBack> mAdapter;
    private List<FeedBack> mFeedBacks = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.act_feedback_list;
    }

    @Override
    protected void initViews() {
        tvTitle.setText("Feedback");
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        rvFeedbackList.setLayoutManager(manager);
        tvTitleRight.setText("Add");
        tvTitleRight.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFeedBacks();
    }

    private void getFeedBacks() {
        EHomeInterface.getINSTANCE().getAllFeedBacks(mContext, 1, 100, new FeedbacksCallback() {
            @Override
            public void onSuccess(Response<BaseListResponse<FeedBack>> response) {
                mFeedBacks.clear();
                if (response.body().isSuccess()) {
                    if (response.body().getPage().getList() != null) {
                        mFeedBacks.addAll(response.body().getPage().getList());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "Feedback List is null", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Response<BaseListResponse<FeedBack>> response) {
                super.onError(response);
                Toast.makeText(mContext, "Get feedback List failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void initEvents() {
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,AddFeedBackActivity.class));
            }
        });

    }

    @Override
    protected void initDatas() {
        mAdapter = new BaseRecyclerAdapter<FeedBack>(mContext, mFeedBacks) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_feedback;
            }

            @Override
            public void bindData(RecyclerViewHolder holder, int position, FeedBack item) {
                holder.setText(R.id.tv_feedback_content, item.getContent());

            }


        };

        rvFeedbackList.setAdapter(mAdapter);
    }
}
