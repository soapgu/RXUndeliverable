package com.shgbit.rxundeliverable;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private TextView msg;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.msg = findViewById(R.id.tv_msg);
        this.findViewById(R.id.btn_launch).setOnClickListener( v->{
            disposables.add( this.mergedRx( true )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( t-> {
                                Logger.i("on next %s",t);
                                msg.setText( t );
                            },
                            throwable -> {
                                Logger.i("on error:%s",throwable.getMessage());
                                msg.setText( "on error"+throwable.getMessage() );
                            },
                            ()-> msg.setText("Rx Complete")));
        } );
    }

    private Observable<String> mergedRx(boolean error ){

        return Observable.merge( delayResult(1,error).toObservable(),
                delayResult(1,error).toObservable(),
                delayResult(1,error).toObservable(),
                delayResult(1,error).toObservable());
    }

    private Single<String> delayResult( int second , boolean error ){
        return Single.<String>create( emitter -> {
            Logger.i("ready emit");
            Thread.sleep(second * 1000L);
            Logger.i("begin to emit");
            if(error){
                emitter.onError( new Exception("some logic error") );
            }else {
                emitter.onSuccess(String.format("%s ok",second));
            }
        } ).subscribeOn(Schedulers.io());
    }
}