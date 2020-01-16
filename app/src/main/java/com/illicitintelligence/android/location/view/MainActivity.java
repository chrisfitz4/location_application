package com.illicitintelligence.android.location.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.illicitintelligence.android.location.R;
import com.illicitintelligence.android.location.model.ReturnType;
import com.illicitintelligence.android.location.network.RetrofitCreator;
import com.illicitintelligence.android.location.network.RetrofitService;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView explanation;
    private TextView longLat;
    private TextView address;
    private CompositeDisposable disposable = new CompositeDisposable();
    private LocationManager locationManager;
    public static final int REQUEST_CODE = 707;
    private Handler handler = new Handler();
    private RetrofitCreator retrofitObject;
    Observable<ReturnType> returnTypeObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        explanation = findViewById(R.id.explanation);
        longLat = findViewById(R.id.long_lat_tv);
        address = findViewById(R.id.address_tv);
        retrofitObject = new RetrofitCreator();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setUpLocationListener();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
            if(permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    setUpLocationListener();
                }else{
                    if(Build.VERSION.SDK_INT>=23) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                            //explanation.setVisibility(View.VISIBLE);
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                setUpLocationListener();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.setPriority(Thread.MIN_PRIORITY);
                            thread.run();
                            //explanation.setVisibility(View.GONE);
                        }else {
                            setUpLocationListener();
                        }
                    }else{
                        setUpLocationListener();
                    }
                }
            }
        }
    }

    private void setUpLocationListener(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    10,
                    this);
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
    }


    @Override
    public void onLocationChanged(final Location location) {
        Log.d("TAG_X", "onLocationChanged: "+location.getLatitude()+", "+location.getLongitude());
        longLat.setText(fromHtml(getString(R.string.long_lat_string,
                "<b>"+((Math.round(location.getLatitude()*1000000)/1000000.0))+"</b>"+"<br>",
                "<b>"+((Math.round(location.getLongitude()*1000000)/1000000.0))+"</b>")));
        disposable.add(
                retrofitObject.searchLocation(location.getLatitude()+","+location.getLongitude())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(locationResponse->{
                            address.setText(locationResponse.getResults().get(0).getFormattedAddress());
                            },
                            throwable-> {
                                Log.d("TAG_X", "onLocationChanged: "+throwable.getMessage());
                        })
        );
    }

    private Spanned fromHtml(String toFormat){
        Spanned fromHtml = null;
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.N) {
            fromHtml = Html.fromHtml(toFormat, Html.FROM_HTML_MODE_LEGACY);
        }else{
            fromHtml = Html.fromHtml(toFormat);
        }
        return fromHtml;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
