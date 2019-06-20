package com.hqumath.androidmvvm.ui.activitylist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.hqumath.androidmvvm.base.BaseViewModel;
import com.hqumath.androidmvvm.data.MyApiService;
import com.hqumath.androidmvvm.data.MyRepository;
import com.hqumath.androidmvvm.entity.ActivityEntity;
import com.hqumath.androidmvvm.entity.CommitEntity;
import com.hqumath.androidmvvm.http.BaseApi;
import com.hqumath.androidmvvm.http.HandlerException;
import com.hqumath.androidmvvm.http.HttpOnNextListener;
import com.hqumath.androidmvvm.http.RetrofitClient;
import com.hqumath.androidmvvm.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;

/**
 * ****************************************************************
 * 文件名称: ActivityListViewModel
 * 作    者: Created by gyd
 * 创建时间: 2019/6/4 16:54
 * 文件描述:
 * 注意事项:
 * 版权声明:
 * ****************************************************************
 */
public class ActivityListViewModel extends BaseViewModel<MyRepository> {
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MediatorLiveData<List<CommitEntity>> list = new MediatorLiveData<>();

    public ActivityListViewModel(@NonNull Application application) {
        super(application);
        model = MyRepository.getInstance();
    }

    public void getActivityList() {
        RetrofitClient.getInstance().sendHttpRequest(new BaseApi(new HttpOnNextListener() {
            @Override
            public void onSubscribe() {
                isLoading.setValue(true);
            }

            @Override
            public void onNext(Object o) {
                list.setValue((List<CommitEntity>)o);
            }

            @Override
            public void onError(HandlerException.ResponseThrowable e) {
                isLoading.setValue(false);
                ToastUtil.toast(getApplication(), e.getMessage());

            }

            @Override
            public void onComplete() {
                isLoading.setValue(false);
            }
        }, getLifecycleProvider()) {
            @Override
            public Observable getObservable(Retrofit retrofit) {
                Map<String, Object> map = new HashMap<>();
                map.put("per_page", 10);
                map.put("page", 1);
                map.put("sha", "master");
                return retrofit.create(MyApiService.class).getActivityList(map);
            }
        });
    }

    public LiveData<List<CommitEntity>> getData() {
        return list;
    }
}
